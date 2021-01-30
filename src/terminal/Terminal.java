package terminal;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.event.*;
import java.util.*;
import java.awt.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.io.*;
import javax.swing.*;
public class Terminal extends JComponent{
     private Process process;
     private PrintWriter writer;
     private Scanner inReader;
     private Scanner errReader;
     private JTextField textField;
     public RSyntaxTextArea textArea;
     private static final Font PX18 = new Font("Ubuntu Mono", Font.BOLD, 18);
     private static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);
     private static LinkedList<String> commands = new LinkedList<>();
     private static int pointer;
     public volatile boolean shellAlive;
     public static File currentDir;
     public static final String LAUNCH_COMMAND = "shell-again";
     public static final String USER_HOME = System.getProperty("user.home");
     public Terminal(){
          setLayout(new BorderLayout());
          textArea = new RSyntaxTextArea();
     	textArea.setFont(PX18);
          textArea.setEditable(false);
          textArea.setSyntaxEditingStyle(textArea.SYNTAX_STYLE_UNIX_SHELL);
          add(new JScrollPane(textArea), BorderLayout.CENTER);
          
          textField = new JTextField();
          textField.setFont(PX16);
          add(textField, BorderLayout.SOUTH);
          textField.addKeyListener(new KeyAdapter(){
               @Override
               public void keyPressed(KeyEvent e){
               	if(e.getKeyCode() == KeyEvent.VK_ENTER){
                         e.consume();
                         send(textField.getText());
                         String text = textField.getText();
                         text = text.substring(text.lastIndexOf('\n') + 1).trim();
                         commands.add(text);
                         pointer = commands.size();
                         textField.setText("");
               	}
                    else if(e.getKeyCode() == KeyEvent.VK_UP){
                         if(pointer > 0)
                              pointer--;
                         textField.setText(commands.get(pointer));
                         textArea.setCaretPosition(textArea.getText().length());
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                         if(pointer < commands.size() - 1)
                              pointer++;
                         if(pointer >= commands.size())
                              pointer = 0;
                         textField.setText(commands.get(pointer));
                         textArea.setCaretPosition(textArea.getText().length());
                    }
               }
          });
     }

     public Terminal launch(){
     	if(process != null && process.isAlive() || shellAlive) return this;
          try{
               shellAlive = true;
               String shell = "cmd";
               if(File.separator.equals("/"))
                    shell = "bash";
               process = new ProcessBuilder(shell).start();
               writer = new PrintWriter(process.getOutputStream());
               inReader = new Scanner(process.getInputStream());
               errReader = new Scanner(process.getErrorStream());
               new Thread(()->{
                    while(process.isAlive()){
                         if(inReader.hasNextLine())
                              print(inReader.nextLine());
                    }
                    shellAlive = false;
               }).start();
               new Thread(()->{
                    while(process.isAlive()){
                         if(errReader.hasNextLine())
                              print(errReader.nextLine());
                    }
                    shellAlive = false;
                    print("Shell Closed, To relaunch the shell enter " + LAUNCH_COMMAND);
               }).start();
               if(File.separator.equals("/")){
                    currentDir = new File(ide.Screen.getFileView().getProjectPath() + File.separator + "bin");
                    textArea.setText("Current Directory : "+ ide.Screen.getFileView().getProjectPath() + File.separator + "bin\n\n");
               }
               textField.setText("");
          }catch(Exception e){ 
               shellAlive = false;
               System.err.println(e); 
          }
          return this;
     }

     public void destroyShell(){
          if(!shellAlive) return;
          print("Destroying the Shell and Any of it Running Processess...");
     	process.destroyForcibly();
          print("Shell Destroyed!!!");
     }

     public synchronized void send(String text){
     	try{
               text = text.substring(text.lastIndexOf('\n') + 1).trim();
               if(!shellAlive){
                    if(text.equals(LAUNCH_COMMAND)){
                         print("Relaunching Shell...");
                         launch();
                    }
                    return;
               }
               if(text.equals("clear")){
                    textArea.setText("");
                    return;
               }
               else if(text.equals("exit")){
                    process.destroyForcibly();
                    textArea.setText("");
                    return;
               }
               else if(text.equals("cd ..") && File.separator.equals("/")){
                    String path = currentDir.getAbsolutePath();
                    File originalPath = currentDir;
                    currentDir = new File(path.substring(0, path.lastIndexOf(File.separatorChar)));
                    if(currentDir.exists())
                         setToolTipText(currentDir.getAbsolutePath());
                    else
                         currentDir = originalPath;
               }
               else if(text.equals("cd ~") && File.separator.equals("/")){
                    File originalPath = currentDir;
                    currentDir = new File(USER_HOME);
                    if(currentDir.exists())
                         setToolTipText(currentDir.getAbsolutePath());
                    else
                         currentDir = originalPath;
               }
               else if(text.startsWith("cd ") && File.separator.equals("/")){
                    String path = text;
                    if(path.substring(path.indexOf(' ')).trim().charAt(0) == '/')
                         path = path.substring(path.indexOf(' ') + 1).trim();
                    else
                         path = (currentDir.getAbsolutePath() + "/" + path).trim();

                    if(path.startsWith("~")){
                         path = USER_HOME + path.substring(path.indexOf('~') + 1);
                    }
                    File originalPath = currentDir;
                    currentDir = new File(path);
                    if(currentDir.exists())
                         setToolTipText(currentDir.getAbsolutePath());
                    else
                         currentDir = originalPath;
               }
               if(File.separator.equals("/"))
                    print("OmegaIDE:\""+ currentDir.getAbsolutePath() + "\"$ " + text);
     		writer.println(text);
               writer.flush();
     	}catch(Exception e){ System.err.println(e); }
     }

     public synchronized void print(String text){
     	textArea.append(text + "\n");
          repaint();
          textArea.setCaretPosition(textArea.getText().length());
     }
}
