/*
 * GitProjectWizard
 * Copyright (C) 2022 Omega UI

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
import java.io.File;

import omega.ui.component.jediterm.JetTerminal;

import omega.io.IconManager;
import omega.io.AppDataManager;

import omegaui.component.TextComp;
import omegaui.component.EdgeComp;
import omegaui.component.NoCaretField;
import omegaui.component.FlexPanel;

import omega.Screen;

import javax.swing.JDialog;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class GitProjectWizard extends JDialog{

	private Screen screen;

	private TextComp iconComp;
	private TextComp titleComp;
	private TextComp closeComp;

	private EdgeComp urlLabel;
	private NoCaretField urlField;
	
	private EdgeComp branchLabel;
	private NoCaretField branchField;

	private TextComp cloneComp;

	private FlexPanel terminalHolderPanel;

	private JetTerminal terminal;
	
	public GitProjectWizard(Screen screen){
		super(screen, false);
		this.screen = screen;
		setTitle("Clone a project using git");
		setUndecorated(true);
		setLayout(null);
		setSize(500, 300);
		setLocationRelativeTo(screen);
		getContentPane().setBackground(c2);
		init();
	}

	public void init(){
		iconComp = new TextComp(IconManager.fluentgithubIcon, 25, 25, c2, c2, c2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setArc(0, 0);
		iconComp.attachDragger(this);
		iconComp.setClickable(false);
		add(iconComp);

		titleComp = new TextComp(getTitle(), c2, c2, glow, null);
		titleComp.setBounds(30, 0, getWidth() - 60, 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		titleComp.setClickable(false);
		add(titleComp);

		closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, c2, c2, c2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);

		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);

		urlLabel = new EdgeComp("repo-url", glow, TOOLMENU_GRADIENT, glow, null);
		urlLabel.setBounds(20, 50, 120, 25);
		urlLabel.setFont(PX14);
		urlLabel.setUseFlatLineAtBack(true);
		add(urlLabel);

		urlField = new NoCaretField("https://github.com/", "enter url here", TOOLMENU_COLOR4, c2, TOOLMENU_COLOR5);
		urlField.setBounds(150, 50, getWidth() - 160, 25);
		urlField.setFont(PX14);
		urlField.setOnAction(this::cloneRepo);
		add(urlField);

		branchLabel = new EdgeComp("branch", glow, TOOLMENU_GRADIENT, glow, null);
		branchLabel.setBounds(20, 80, 120, 25);
		branchLabel.setFont(PX14);
		branchLabel.setUseFlatLineAtBack(true);
		add(branchLabel);

		branchField = new NoCaretField("default", "Click here", TOOLMENU_COLOR4, c2, TOOLMENU_COLOR5);
		branchField.setBounds(150, 80, getWidth() - 160, 25);
		branchField.setFont(PX14);
		branchField.setOnAction(this::cloneRepo);
		add(branchField);

		cloneComp = new TextComp("Clone", TOOLMENU_COLOR6_SHADE, back2, TOOLMENU_COLOR6, this::cloneRepo);
		cloneComp.setBounds(getWidth()/2 - 80/2, 120, 80, 25);
		cloneComp.setFont(PX14);
		cloneComp.setArc(6, 6);
		add(cloneComp);
	}

	public void cloneRepo(){
		new Thread(()->{
			if(urlField.getText().equals(""))
				return;

			if(terminal != null){
				remove(terminal);
				repaint();
			}
		
			String[] cloneCommand = null;

			if(!Screen.isNotNull(branchField.getText()))
				branchField.setText("default");
			
			if(!branchField.getText().equals("default")){
				cloneCommand = new String[]{
					"git", "clone", urlField.getText(), "--branch", branchField.getText(), "--single-branch"
				};
			}
			else{
				cloneCommand = new String[]{
					"git", "clone", urlField.getText()
				};
			}
			terminal = new JetTerminal(cloneCommand, AppDataManager.getWorkspace());
			terminal.setBounds(20, 150, getWidth() - 40, getHeight() - 160);
			add(terminal);

			terminal.setOnProcessExited(()->{
				int exitCode = terminal.process.exitValue();
				if(exitCode == 0){
					String projectName = urlField.getText();
					projectName = projectName.substring(projectName.lastIndexOf('/') + 1);
					if(projectName.endsWith(".git"))
						projectName = projectName.substring(0, projectName.lastIndexOf(".git")).trim();
					String path = AppDataManager.getWorkspace() + File.separator + projectName;
					File dir = new File(path);
					if(dir.exists()){
						dispose();
						screen.loadProject(dir);
					}
				}
			});
			
			terminal.setVisible(false);
			terminal.setVisible(true);
			repaint();
			terminal.start();
		}).start();
	}
}
