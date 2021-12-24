/**
  * The IDE integrated Terminal
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package omega.terminal;

import static omega.utils.UIManager.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import omega.Screen;
import omega.comp.FlexPanel;
import omega.utils.Editor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class Terminal extends JPanel {

  public static String shell = File.pathSeparator.equals(":") ? "sh" : "cmd";
  private String lastText;
  private FlexPanel panel;
  private JScrollPane scrollPane;
  private RSyntaxTextArea outputArea;

  private Process shellProcess;
  private Scanner inputReader;
  private Scanner errorReader;
  private PrintWriter writer;

  public Terminal() {
    super(null);
    setBackground(c2);
    init();
  }

  public Terminal(String command) {
    this();
    this.shell = command;
  }

  public void init() {
    panel = new FlexPanel(null, back1, null);
    panel.setArc(10, 10);
    add(panel);

    outputArea = new RSyntaxTextArea();
    outputArea.setAutoscrolls(true);
    outputArea.setDragEnabled(false);
    outputArea.setAutoIndentEnabled(true);
    outputArea.setHyperlinksEnabled(true);
    outputArea.setHyperlinkForeground(glow);
    outputArea.setAntiAliasingEnabled(true);
    outputArea.setAnimateBracketMatching(true);
    outputArea.setBracketMatchingEnabled(true);
    outputArea.setFont(new Font(fontName, fontState, fontSize));
    try {
      omega.utils.Editor.getTheme().apply(outputArea);
    } catch (Exception e) {
      e.printStackTrace();
    }
    outputArea.addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          int code = e.getKeyCode();
          if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
            e.consume();
            return;
          } else if (code == KeyEvent.VK_ENTER) {
            if (
              writer == null || shellProcess == null || !shellProcess.isAlive()
            ) e.consume();
            String text = outputArea.getText();
            text = text.substring(0, outputArea.getCaretPosition());
            text = text.substring(text.lastIndexOf('\n') + 1);
            if (Screen.onWindows()) outputArea.append("\n");
            writer.println(text);
            writer.flush();
          }
        }
      }
    );
    addKeyListener(outputArea.getKeyListeners()[0]);

    scrollPane = new JScrollPane(outputArea);
    panel.add(scrollPane);
  }

  public void launchTerminal() {
    new Thread(() -> {
      try {
        shellProcess =
          new ProcessBuilder(shell)
            .directory(new File(Screen.getFileView().getProjectPath()))
            .start();
        inputReader = new Scanner(shellProcess.getInputStream());
        errorReader = new Scanner(shellProcess.getErrorStream());
        writer = new PrintWriter(shellProcess.getOutputStream());
        new Thread(() -> {
          while (shellProcess.isAlive()) {
            while (errorReader.hasNextLine()) outputToTerminal(
              errorReader.nextLine()
            );
          }
        })
          .start();
        while (shellProcess.isAlive()) {
          while (inputReader.hasNextLine()) outputToTerminal(
            inputReader.nextLine()
          );
        }
        outputToTerminal("Shell Exited!");
      } catch (Exception e) {
        e.printStackTrace();
      }
    })
      .start();
  }

  public void relocate() {
    panel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
    scrollPane.setBounds(5, 5, panel.getWidth() - 10, panel.getHeight() - 10);
  }

  @Override
  public void layout() {
    relocate();
    super.layout();
  }

  public void outputToTerminal(String text) {
    outputArea.append(text + "\n");
    outputArea.setCaretPosition(outputArea.getText().length());
  }

  public void exit() {
    if (shellProcess != null) shellProcess.destroyForcibly();
  }

  public org.fife.ui.rsyntaxtextarea.RSyntaxTextArea getOutputArea() {
    return outputArea;
  }

  public void setOutputArea(
    org.fife.ui.rsyntaxtextarea.RSyntaxTextArea outputArea
  ) {
    this.outputArea = outputArea;
  }

  public java.lang.Process getShellProcess() {
    return shellProcess;
  }

  public void setShellProcess(java.lang.Process shellProcess) {
    this.shellProcess = shellProcess;
  }
}
