package omega.terminal;

import omega.Screen;
import omega.utils.Editor;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import omega.token.factory.*;
public class TerminalComp extends JPanel{
     private Terminal terminal;
     public TerminalComp(){
     	super(new BorderLayout());
          terminal = new Terminal();
          add(terminal, BorderLayout.CENTER);
     }

     public void showTerminal(boolean value){
     	if(value){
               if(terminal.shellProcess == null || !terminal.shellProcess.isAlive()) {
                    terminal.setWorkingDirectory(new File(Screen.getFileView().getProjectPath()));
                    terminal.start();
                    terminal.write(File.pathSeparator.equals(":") ? "ls" : "dir");
                    ShellTokenMaker.apply(terminal);
                    Screen.getScreen().getOperationPanel().addTab("Shell", new JScrollPane(terminal), ()->showTerminal(false));
               }
     	}
          else{
               terminal.destroyShell();
               Screen.getScreen().getOperationPanel().removeTab("Shell");
          }
     }

     public Terminal getTerminal(){
     	return terminal;
     }
}

