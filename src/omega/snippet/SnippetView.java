/**
  * SnippetView
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

package omega.snippet;

import static omega.comp.Animations.*;
import static omega.utils.UIManager.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import omega.Screen;
import omega.comp.FlexPanel;
import omega.comp.NoCaretField;
import omega.comp.TextComp;
import omega.utils.Editor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class SnippetView extends JDialog {

  private TextComp titleComp;
  private TextComp closeComp;

  private FlexPanel snippetPanel;
  private JScrollPane scrollPane;

  private RTextScrollPane textScrollPane;
  private RSyntaxTextArea textArea;

  private NoCaretField textField;
  private TextComp addComp;
  private TextComp removeComp;

  private LinkedList<TextComp> snippetComps = new LinkedList<>();
  private int block = 0;

  private Snippet snip;

  public SnippetView(omega.Screen screen) {
    super(screen, false);
    setUndecorated(true);
    setTitle("Snippet Manager");
    setLayout(null);
    setSize(600, 400);
    setLocationRelativeTo(null);
    setResizable(false);
    setIconImage(screen.getIconImage());
    JPanel panel = new JPanel(null);
    panel.setBackground(c2);
    setContentPane(panel);
    setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
    init();
  }

  private void init() {
    titleComp = new TextComp("Snippet Manager", c2, c2, glow, null);
    titleComp.setBounds(0, 0, getWidth() - 30, 30);
    titleComp.setFont(PX14);
    titleComp.setArc(0, 0);
    titleComp.setClickable(false);
    titleComp.attachDragger(this);
    add(titleComp);

    closeComp =
      new TextComp(
        "x",
        TOOLMENU_COLOR2_SHADE,
        c2,
        TOOLMENU_COLOR2,
        () -> {
          dispose();
          saveView();
        }
      );
    closeComp.setBounds(getWidth() - 30, 0, 30, 30);
    closeComp.setFont(PX14);
    closeComp.setArc(0, 0);
    add(closeComp);

    scrollPane =
      new JScrollPane(snippetPanel = new FlexPanel(null, back2, null));
    scrollPane.setBounds(10, 40, 200, 300);
    snippetPanel.setArc(0, 0);
    add(scrollPane);

    textScrollPane = new RTextScrollPane(textArea = new RSyntaxTextArea());
    textScrollPane.setBounds(220, 40, getWidth() - 230, 300);
    textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_NONE);
    Editor.getTheme().apply(textArea);
    add(textScrollPane);

    textField = new NoCaretField("", TOOLMENU_COLOR2, back1, TOOLMENU_COLOR3);
    textField.setBounds(
      textScrollPane.getX(),
      textScrollPane.getY() + textScrollPane.getHeight() + 5,
      textScrollPane.getWidth() - 200,
      25
    );
    textField.setFont(PX14);
    textField.setToolTipText(
      "Snippet Name should have alphabets, numbers or symbols(except \';\') but without whitespaces"
    );
    add(textField);

    addComp =
      new TextComp(
        "Add/Update",
        TOOLMENU_COLOR4_SHADE,
        back2,
        TOOLMENU_COLOR4,
        () -> {
          if (
            textField.getText().contains(" ") || textField.getText().equals("")
          ) {
            Toolkit.getDefaultToolkit().beep();
            return;
          }
          SnippetBase.add(
            textField.getText(),
            textArea.getText(),
            textArea.getCaretPosition(),
            textArea.getCaretLineNumber()
          );
          setView(SnippetBase.getAll().getLast());
          loadSnippets();
        }
      );
    addComp.setBounds(
      textField.getX() + textField.getWidth() + 2,
      textField.getY(),
      120,
      25
    );
    addComp.setFont(PX14);
    add(addComp);

    removeComp =
      new TextComp(
        "Remove",
        TOOLMENU_COLOR4_SHADE,
        back2,
        TOOLMENU_COLOR4,
        () -> {
          SnippetBase.remove(textField.getText());
          loadSnippets();
          textField.setText("");
          textArea.setText("");
          this.snip = null;
        }
      );
    removeComp.setBounds(
      addComp.getX() + addComp.getWidth() + 2,
      textField.getY(),
      80,
      25
    );
    removeComp.setFont(PX14);
    add(removeComp);
  }

  public void setView(Snippet snip) {
    saveView();
    this.snip = snip;
    textField.setText(snip.base);
    textArea.setText(snip.code);
    textArea.setCaretPosition(snip.caret);
  }

  public void saveView() {
    if (snip == null) return;
    if (!snip.base.equals(textField.getText())) return;
    snip.base = textField.getText();
    snip.code = textArea.getText();
    snip.caret = textArea.getCaretPosition();
    snip.line = textArea.getCaretLineNumber();
    SnippetBase.save();
  }

  private void loadSnippets() {
    snippetComps.forEach(snippetPanel::remove);
    snippetComps.clear();

    block = 0;

    for (Snippet snip : SnippetBase.getAll()) {
      TextComp comp = new TextComp(
        snip.base,
        snip.code,
        TOOLMENU_COLOR1_SHADE,
        c2,
        TOOLMENU_COLOR1,
        () -> setView(snip)
      );
      comp.setBounds(0, block, scrollPane.getWidth() - 2, 25);
      comp.setFont(PX14);
      comp.alignX = 5;
      snippetComps.add(comp);
      snippetPanel.add(comp);

      block += 25;
    }
    if (block == 0) return;

    snippetPanel.setPreferredSize(new Dimension(scrollPane.getWidth(), block));
    scrollPane.getVerticalScrollBar().setVisible(true);
    scrollPane.getVerticalScrollBar().setValue(0);
    repaint();
  }

  @Override
  public void setVisible(boolean value) {
    super.setVisible(value);
    if (value) {
      loadSnippets();
    }
  }
}
