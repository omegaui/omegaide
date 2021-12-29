/**
* SideMenu
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

package omega.ui.panel;
import omegaui.component.TextComp;

import omega.Screen;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omega.io.IconManager.*;
import static omegaui.component.animation.Animations.*;
public class SideMenu extends JPanel {
	private Screen screen;
	
	private TextComp sep;
	public TextComp projectTabComp;
	public TextComp shellComp;
	public TextComp structureComp;
	public TextComp searchComp;
	
	public SideMenu(Screen screen){
		super(null);
		this.screen = screen;
		
		setBackground(c2);
		setPreferredSize(new Dimension(30, 100));
		init();
	}
	
	public void init(){
		sep = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		add(sep);
		
		projectTabComp = new TextComp(fluentprojectstructureImage, 20, 20, back2, back2, TOOLMENU_COLOR1, screen::toggleFileTree);
		projectTabComp.setBounds(0, 0, 30, 25);
		projectTabComp.setArc(2, 2);
		add(projectTabComp);
		
		shellComp = new TextComp(fluentconsoleImage, 20, 20, back2, back2, TOOLMENU_COLOR1, Screen.getTerminalComp()::showJetTerminal);
		shellComp.setBounds(0, 25, 30, 25);
		shellComp.setFont(PX18);
		shellComp.setArc(2, 2);
		add(shellComp);
		
		structureComp = new TextComp(fluentstructureImage, 20, 20, back2, back2, TOOLMENU_COLOR1, ()->screen.getToolMenu().structureView.setVisible(true));
		structureComp.setBounds(0, 50, 30, 25);
		structureComp.setFont(PX18);
		structureComp.setArc(2, 2);
		add(structureComp);
		
		searchComp = new TextComp(fluentsearchImage, 20, 20, back2, back2, TOOLMENU_COLOR1, ()->Screen.getFileView().getSearchWindow().setVisible(true));
		searchComp.setBounds(0, 75, 30, 25);
		searchComp.setArc(2, 2);
		add(searchComp);

		putAnimationLayer(projectTabComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(shellComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(searchComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(structureComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
	}
	
	public void changeLocations(boolean non_java){
		if(non_java)
			searchComp.setBounds(0, 50, 30, 25);
		else{
			structureComp.setBounds(0, 75, 30, 25);
			searchComp.setBounds(0, 50, 30, 25);
		}
		repaint();
	}
	
	@Override
	public void paint(Graphics g){
		sep.setBounds(30, 0, 2, getHeight());
		super.paint(g);
	}
}

