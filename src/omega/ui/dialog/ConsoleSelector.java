/**
  * ConsoleSelector
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
package omega.ui.dialog;
import omega.io.DataManager;

import omegaui.component.TextComp;
import omegaui.component.NoCaretField;

import omega.Screen;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class ConsoleSelector extends JDialog{
	private TextComp titleComp;
	private TextComp applyComp;
	private TextComp cancelComp;
	private NoCaretField nameField;
	public ConsoleSelector(Screen screen){
		super(screen, true);
		setUndecorated(true);
		setTitle("Console Selector");
		setIconImage(screen.getIconImage());
		setSize(400, 70);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(c2);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		init();
	}

	public void init(){
		titleComp = new TextComp("Specify System Terminal Launch Command", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 120, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);

		cancelComp = new TextComp("Cancel", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::dispose);
		cancelComp.setBounds(getWidth() - 120, 0, 60, 30);
		cancelComp.setFont(PX14);
		cancelComp.setArc(0, 0);
		add(cancelComp);

		applyComp = new TextComp("Apply", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::apply);
		applyComp.setBounds(getWidth() - 60, 0, 60, 30);
		applyComp.setFont(PX14);
		applyComp.setArc(0, 0);
		add(applyComp);

		nameField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		nameField.setBounds(10, 40, getWidth() - 20, 30);
		nameField.setFont(PX14);
		nameField.setOnAction(this::apply);
		add(nameField);
		addKeyListener(nameField);
	}

	public void apply(){
		dispose();
		DataManager.setConsoleCommand(nameField.getText() != null ? nameField.getText() : getPlatformTerminal());
	}

	public String getPlatformTerminal(){
		return File.pathSeparator.equals(":") ? "" : "cmd.exe";
	}

	public void launchTerminal(){
		if(DataManager.getConsoleCommand() == null || DataManager.getConsoleCommand().equals(""))
			setVisible(true);
		
		new Thread(()->{
			try{
				Runtime.getRuntime().exec(DataManager.getConsoleCommand());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}).start();
		
	}

	@Override
	public void setVisible(boolean value){
		if(value){
	          nameField.setText(DataManager.getConsoleCommand());
		}
	     super.setVisible(value);
	}
}
