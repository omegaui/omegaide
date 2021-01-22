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
