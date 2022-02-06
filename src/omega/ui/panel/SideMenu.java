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
import java.util.LinkedList;

import omega.instant.support.BoundsListener;

import omega.ui.component.ToolMenu;

import omegaui.component.TextComp;

import omega.Screen;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omega.io.IconManager.*;
import static omegaui.component.animation.Animations.*;

public class SideMenu extends JPanel {
	public Screen screen;
	
	public TextComp sep;
	public TextComp projectTabComp;
	public TextComp shellComp;
	public TextComp structureComp;
	public TextComp searchComp;
	
	public TextComp buildPathComp;
	public TextComp settingsComp;

	public LinkedList<BoundsListener> boundListeners = new LinkedList<>();
	
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
		
		searchComp = new TextComp(fluentsearchImage, 20, 20, back2, back2, TOOLMENU_COLOR1, ()->Screen.getProjectFile().getSearchWindow().setVisible(true));
		searchComp.setBounds(0, 75, 30, 25);
		searchComp.setArc(2, 2);
		add(searchComp);

		buildPathComp = new TextComp(fluentbuildpathIcon, 20, 20, back2, back2, TOOLMENU_COLOR1, ()->Screen.getProjectFile().getDependencyView().setVisible(true));
		buildPathComp.setBounds(0, getHeight() - 50, 30, 25);
		buildPathComp.setArc(2, 2);
		add(buildPathComp);

		settingsComp = new TextComp(fluentsettingsImage, 20, 20, back2, back2, TOOLMENU_COLOR1, this::showSettings);
		settingsComp.setBounds(0, getHeight() - 25, 30, 25);
		settingsComp.setArc(2, 2);
		add(settingsComp);

		putAnimationLayer(projectTabComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(shellComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(searchComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(structureComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(buildPathComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(settingsComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
	}

	public void showSettings(){
		if(Screen.getProjectFile().getProjectManager().isLanguageTagNonJava()){
			ToolMenu.showNonJavaSettings();
		}
		else{
			Screen.getScreen().getToolMenu().setPopup.setLocationRelativeTo(Screen.getScreen());
			Screen.getScreen().getToolMenu().setPopup.setVisible(true);
		}
	}
	
	public void resize(boolean non_java){
		if(non_java){
			searchComp.setBounds(0, 50, 30, 25);
			
			structureComp.setVisible(false);
			buildPathComp.setVisible(false);
		}
		else{
			structureComp.setBounds(0, 75, 30, 25);
			searchComp.setBounds(0, 50, 30, 25);
			
			structureComp.setVisible(true);
			buildPathComp.setVisible(true);
		}
		buildPathComp.setBounds(0, getHeight() - 50, 30, 25);
		settingsComp.setBounds(0, getHeight() - 25, 30, 25);
		sep.setBounds(30, 0, 2, getHeight());
		repaint();
	}
	
	@Override
	public void layout(){
		if(Screen.getProjectFile().getProjectManager() != null)
			resize(Screen.getProjectFile().getProjectManager().isLanguageTagNonJava());
		super.layout();
		boundListeners.forEach(bL->bL.onLayout(this));
	}

	public void addBoundsListener(BoundsListener listener){
		boundListeners.add(listener);
	}

	public boolean removeBoundsListener(BoundsListener listener){
		return boundListeners.remove(listener);
	}
}

