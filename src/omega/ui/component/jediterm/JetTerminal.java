/*
 * JetTerminal
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

package omega.ui.component.jediterm;
import com.jediterm.terminal.ui.settings.SettingsProvider;

import omegaui.component.FlexPanel;

import java.awt.event.FocusListener;

import java.awt.Color;

import java.nio.charset.Charset;

import com.jediterm.pty.PtyProcessTtyConnector;

import com.pty4j.PtyProcess;

import com.jediterm.terminal.TtyConnector;

import omega.Screen;

import java.util.HashMap;
import java.util.Map;

import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class JetTerminal extends JPanel{

	public JediTermWidget widget;

	public PtyProcess process;

	public String[] command;

	public String directory;

	private Runnable onProcessExited;

	public JetTerminal(){
		super(null);
		this.directory = Screen.getProjectFile().getProjectPath();
		init();
	}

	public JetTerminal(SettingsProvider settingsProvider){
		super(null);
		this.directory = Screen.getProjectFile().getProjectPath();
		init(settingsProvider);
	}

	public JetTerminal(String[] command, String directory, SettingsProvider settingsProvider){
		super(null);
		this.command = command;
		this.directory = directory;
		init(settingsProvider);
	}

	public JetTerminal(String[] command, String directory){
		super(null);
		this.command = command;
		this.directory = directory;
		init();
	}

	public void init(){
		setBackground(JetTermSettingsProvider.colors[15]);

		JetTermSettingsProvider jtsp = new JetTermSettingsProvider();
		widget = new JediTermWidget(jtsp);

		if(command == null)
			widget.setTtyConnector(getConnector(Screen.onWindows() ? "cmd.exe" : "/bin/bash"));
		else
			widget.setTtyConnector(getConnector(command));

		add(widget);
	}

	public void init(SettingsProvider jtsp){
		widget = new JediTermWidget(jtsp);

		if(command == null)
			widget.setTtyConnector(getConnector(Screen.onWindows() ? "cmd.exe" : "/bin/bash"));
		else
			widget.setTtyConnector(getConnector(command));

		add(widget);
	}

	public TtyConnector getConnector(String... command){
		try{
			this.command = command;

			Map<String, String> envsX = System.getenv();
			HashMap<String, String> envs = new HashMap<>();
			for(String x : envsX.keySet()){
				envs.put(x, envsX.get(x));
			}

			if(!Screen.onWindows()){
				envs.put("TERM", "xterm");
				process = PtyProcess.exec(command, envs, directory);
			}
			else{
				process = PtyProcess.exec(command, envs, directory, true);
			}
			return new PtyProcessTtyConnector(process, Charset.forName("UTF-8"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void start(){
		widget.start();
		if(onProcessExited != null){
			new Thread(()->{
				while(process.isAlive());
					onProcessExited.run();
			}).start();
		}
	}

	public void exit(){
		if(process != null && process.isAlive())
			process.destroyForcibly();
	}

	public void relocate(){
		widget.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
	}

	@Override
	public void addFocusListener(FocusListener focusListener){
		super.addFocusListener(focusListener);
		widget.getTerminalPanel().addFocusListener(focusListener);
	}

	@Override
	public void layout(){
		relocate();
		super.layout();
	}

	public java.lang.Runnable getOnProcessExited() {
		return onProcessExited;
	}

	public void setOnProcessExited(java.lang.Runnable onProcessExited) {
		this.onProcessExited = onProcessExited;
	}

}
