package terminal;
import ide.*;
import java.awt.*;
import javax.swing.*;
public class TerminalComp extends JPanel{
     private Terminal terminal;
     public TerminalComp(){
     	super(new BorderLayout());
          terminal = new Terminal();
          add(terminal, BorderLayout.CENTER);
     }

     public void showTerminal(boolean value){
     	if(value){
               if(!terminal.shellAlive) {
                    terminal.launch();
                    ide.utils.Editor.getTheme().apply(terminal.textArea);
                    Screen.getScreen().getOperationPanel().addTab("Terminal", terminal, ()->showTerminal(false));
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
