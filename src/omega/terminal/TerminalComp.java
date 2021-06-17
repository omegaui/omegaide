package omega.terminal;
import omega.*;
import omega.token.factory.*;

public class TerminalComp {
     private Terminal terminal;

     public void showTerminal(){
          terminal = new Terminal();
          ShellTokenMaker.apply(terminal.getOutputArea());
          Screen.getScreen().getOperationPanel().addTab("Shell", terminal, terminal::exit);
          terminal.launchTerminal();
     }

     public Terminal getTerminal(){
     	return terminal;
     }
}

