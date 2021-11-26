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
package omega.terminal;
import omega.utils.IconManager;

import omega.terminal.jediterm.JetTerminal;

import omega.Screen;

import omega.token.factory.ShellTokenMaker;
public class TerminalComp {

	public int count = 1;
	
	public void showTerminal(){
		Terminal terminal = new Terminal();
		ShellTokenMaker.apply(terminal.getOutputArea());
		Screen.getScreen().getOperationPanel().addTab("Terminal" + (count > 1 ? ((count - 1) + "") : ""), IconManager.fluentconsoleImage, terminal, ()->{
			count--;
			terminal.exit();
		});
		terminal.launchTerminal();
		count++;
	}
	
	public void showJetTerminal(){
		JetTerminal jetTerminal = new JetTerminal();
		jetTerminal.start();
		Screen.getScreen().getOperationPanel().addTab("Terminal" + (count > 1 ? ((count - 1) + "") : ""), IconManager.fluentconsoleImage, jetTerminal, ()->{
			count--;
			jetTerminal.exit();
		});
		count++;
	}
	
}

