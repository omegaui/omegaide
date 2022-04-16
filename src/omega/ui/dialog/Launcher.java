/**
* Launcher
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
import omega.Screen;

import omega.ui.component.ToolMenu;

import omega.io.IconManager;

import omegaui.component.TextComp;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class Launcher extends JFrame{
	private TextComp titleComp;
	private TextComp iconComp;
	private TextComp gifComp;

	private TextComp openProjectComp;
	private TextComp createNewJavaProjectComp;
	private TextComp createNewProjectComp;
	private TextComp openRecentProjectComp;
	private TextComp allProjectsComp;
	private TextComp aboutComp;
	private TextComp closeComp;
	
	public Launcher(){
		super("Omega IDE " + Screen.VERSION);
		setUndecorated(true);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		init();
	}

	public void init(){
		iconComp = new TextComp(IconManager.ideImage64, 64, 64, c2, TOOLMENU_GRADIENT, c2, null);
		iconComp.setBounds(getWidth()/2 - 40, 5, 80, 80);
		iconComp.setClickable(false);
		iconComp.setArc(10, 10);
		iconComp.attachDragger(this);
		iconComp.setPaintGradientEnabled(true);
		iconComp.setGradientMode(TextComp.GRADIENT_MODE_LINEAR);
		iconComp.setLinearGradientColors(back2, TOOLMENU_GRADIENT, back2);
		iconComp.setLinearGradientFractions(0f, 0.5f, 1f);
		add(iconComp);
		
		titleComp = new TextComp("Omega IDE " + Screen.VERSION, c2, c2, TOOLMENU_COLOR2, null);
		titleComp.setFont(PX16);
		titleComp.setSize(computeWidth(titleComp.getText(), titleComp.getFont()) + 20, 30);
		titleComp.setLocation(getWidth()/2 - titleComp.getWidth()/2, 90);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.setPaintTextGradientEnabled(true);
		titleComp.setGradientColor(TOOLMENU_COLOR1);
		titleComp.attachDragger(this);
		add(titleComp);

		gifComp = new TextComp("", back3, back2, back2, null);
		gifComp.setGifImage(IconManager.fluentdeveloperGif, 98, 98);
		gifComp.setBounds(50, 120 + (getHeight()/2 - (getHeight() - 120)/2), 110, 110);
		gifComp.setClickable(false);
		gifComp.setArc(10, 10);
		gifComp.attachDragger(this);
		add(gifComp);
		
		int x = getWidth()/2 + 10;
		int y = gifComp.getY() - 10;
		
		openProjectComp = new TextComp("Open a Project", TOOLMENU_GRADIENT, TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, ()->{
			Screen.getProjectFile().open("Project");
		});
		openProjectComp.setBounds(getWidth()/2 + 10, y, 200, 25);
		openProjectComp.setFont(PX14);
		openProjectComp.setArc(5, 5);
		add(openProjectComp);
		
		createNewJavaProjectComp = new TextComp("Create New Java Project", TOOLMENU_GRADIENT, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, ()->{
			ToolMenu.javaProjectWizard.setVisible(true);
		});
		createNewJavaProjectComp.setBounds(x, y += 30, 200, 25);
		createNewJavaProjectComp.setFont(PX14);
		createNewJavaProjectComp.setArc(5, 5);
		add(createNewJavaProjectComp);
		
		createNewProjectComp = new TextComp("Create New Project", TOOLMENU_GRADIENT, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, ()->{
			ToolMenu.universalProjectWizard.setVisible(true);
		});
		createNewProjectComp.setBounds(x, y += 30, 200, 25);
		createNewProjectComp.setFont(PX14);
		createNewProjectComp.setArc(5, 5);
		add(createNewProjectComp);
		
		openRecentProjectComp = new TextComp("See Recent Projects", TOOLMENU_GRADIENT, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, ()->{
			ToolMenu.recentsDialog.setVisible(true);
		});
		openRecentProjectComp.setBounds(x, y += 30, 200, 25);
		openRecentProjectComp.setFont(PX14);
		openRecentProjectComp.setArc(5, 5);
		add(openRecentProjectComp);
		
		allProjectsComp = new TextComp("See All Projects", TOOLMENU_GRADIENT, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, ()->{
			ToolMenu.allProjectsPopup.setLocationRelativeTo(null);
			ToolMenu.allProjectsPopup.setVisible(true);
		});
		allProjectsComp.setBounds(x, y += 30, 200, 25);
		allProjectsComp.setFont(PX14);
		allProjectsComp.setArc(5, 5);
		add(allProjectsComp);
		
		aboutComp = new TextComp("About", TOOLMENU_GRADIENT, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR4, ()->{
			ToolMenu.infoScreen.setVisible(true);
		});
		aboutComp.setBounds(x, y += 30, 200, 25);
		aboutComp.setFont(PX14);
		aboutComp.setArc(5, 5);
		add(aboutComp);

		closeComp = new TextComp("Exit", TOOLMENU_COLOR4, TOOLMENU_COLOR2, c2, ()->System.exit(0));
		closeComp.setBounds(gifComp.getX(), gifComp.getY() + gifComp.getHeight() + 15, gifComp.getWidth(), 25);
		closeComp.setFont(PX14);
		closeComp.setArc(5, 5);
		add(closeComp);
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}

	public static void main(String[] args){
		new Launcher().setVisible(true);
	}
}
