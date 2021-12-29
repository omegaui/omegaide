/**
* Terminal Instance Manager
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
package omega.ui.component;
import omega.Screen;

import omega.ui.component.jediterm.JetTerminal;

import omega.io.ShellTokenMaker;
import omega.io.IconManager;
public class TerminalComp {

	public int count = 1;
	public String name = "";
	
	public void showTerminal(){
		Terminal terminal = new Terminal();
		ShellTokenMaker.apply(terminal.getOutputArea());
		Screen.getScreen().getOperationPanel().addTab("Terminal" + (count > 1 ? ("(" + (count - 1) + ")") : ""), IconManager.fluentconsoleImage, terminal, ()->{
			count--;
			terminal.exit();
		});
		terminal.launchTerminal();
		count++;
	}
	
	public void showJetTerminal(){
		JetTerminal jetTerminal = new JetTerminal();
		final String name = ("Terminal " + (count > 1 ? ("(" + (count - 1) + ")") : ""));
		jetTerminal.setOnProcessExited(()->{
			count--;
			Screen.getScreen().getOperationPanel().removeTab(name);
		});
		jetTerminal.start();
		Screen.getScreen().getOperationPanel().addTab(name, IconManager.fluentconsoleImage, jetTerminal, ()->{
			count--;
			jetTerminal.exit();
		});
		count++;
	}
	
}

