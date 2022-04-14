/**
 * SearchComp
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

package omega.ui.component;

import omegaui.component.FlexPanel;
import omegaui.component.TextComp;

import omega.Screen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Color;

import java.awt.image.BufferedImage;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JDialog;

import static omega.ui.component.FileTreeBranch.*;
import static omega.ui.dialog.FileSelectionDialog.*;
import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class SearchComp extends FlexPanel implements MouseListener{
	private JDialog searchWindow;

	private File file;

	private TextComp iconComp;
	private TextComp nameComp;
	private TextComp parentComp;
	private TextComp tagComp;

	private TextComp rightComp;
	private TextComp bottomComp;

	private Runnable clickAction;

	public SearchComp(JDialog window, File file){
		super(null, back1, c2);
		setArc(0, 0);
		setPaintGradientEnabled(true);

		this.searchWindow = window;
		this.file = file;
		this.clickAction = ()->Screen.getScreen().loadFile(file);

		addMouseListener(this);
	}

	public void initUI(){
		iconComp = new TextComp(getPreferredImageForFile(file), getHeight(), getHeight(), ALPHA, ALPHA, ALPHA, null);
		iconComp.setBounds(0, 0, getHeight(), getHeight());
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		iconComp.addMouseListener(this);
		add(iconComp);

		nameComp = new TextComp(file.getName(), file.getAbsolutePath(), ALPHA, ALPHA, getPreferredColorForFile(file), null);
		nameComp.setLocation(iconComp.getX() + iconComp.getWidth() + 4, 2);
		nameComp.setSize(getWidth() - nameComp.getX() - 80, getHeight()/2);
		nameComp.setFont(UBUNTU_PX14);
		nameComp.alignX = 5;
		nameComp.setArc(5, 5);
		nameComp.setClickable(false);
		nameComp.addMouseListener(this);
		add(nameComp);

		String parentName = Screen.getPackName(file.getParentFile());
		if(parentName.trim().equals(""))
			parentName = Screen.getProjectFile().getProjectName();

		parentComp = new TextComp(parentName, ALPHA, ALPHA, TOOLMENU_COLOR4, null);
		parentComp.setBounds(nameComp.getX(), nameComp.getY() + nameComp.getHeight() + 2, nameComp.getWidth(), nameComp.getHeight());
		parentComp.setFont(UBUNTU_PX12);
		parentComp.setArc(5, 5);
		parentComp.alignX = 5;
		parentComp.addMouseListener(this);
		add(parentComp);

		tagComp = new TextComp(getExtension(), back2, ALPHA, nameComp.color3, null);
		tagComp.setBounds(getWidth() - 75, getHeight() - parentComp.getHeight(), 70, parentComp.getHeight());
		tagComp.setFont(UBUNTU_PX12);
		tagComp.setClickable(false);
		tagComp.setArc(3, 3);
		tagComp.addMouseListener(this);
		add(tagComp);

		rightComp = new TextComp("R", "Open on Right-Tab-Panel", back1, back2, TOOLMENU_COLOR4, ()->Screen.getScreen().loadFileOnRightTabPanel(file));
		rightComp.setBounds(getWidth() - 34 - 34, 2, 25, 25);
		rightComp.setFont(UBUNTU_PX12);
		rightComp.setArc(0, 0);
		rightComp.setShowHandCursorOnMouseHover(true);
		add(rightComp);

		bottomComp = new TextComp("B", "Open on Bottom-Tab-Panel", back1, back2, TOOLMENU_COLOR4, ()->Screen.getScreen().loadFileOnBottomTabPanel(file));
		bottomComp.setBounds(getWidth() - 37, 2, 25, 25);
		bottomComp.setFont(UBUNTU_PX12);
		bottomComp.setArc(0, 0);
		bottomComp.setShowHandCursorOnMouseHover(true);
		add(bottomComp);

		if(file.isDirectory()){
			rightComp.setVisible(false);
			bottomComp.setVisible(false);
		}
	}

	public void set(boolean enter){
		if(enter)
			mouseEntered(null);
		else
			mouseExited(null);
	}

	@Override
	public void mouseEntered(MouseEvent e){
		setBackground(back2);
		setAccentColor(back3);
	}

	@Override
	public void mouseExited(MouseEvent e){
		setBackground(back1);
		setAccentColor(c2);
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		searchWindow.dispose();
		if(mouseEvent.getButton() == 3){
			Screen.getProjectFile().getFileTreePanel().navigateTo(file);
		}
		else{
			clickAction.run();
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {

	}

	public java.lang.Runnable getClickAction() {
		return clickAction;
	}

	public void setClickAction(java.lang.Runnable clickAction) {
		this.clickAction = clickAction;
	}

	public java.io.File getFile() {
		return file;
	}

	@Override
	public String getName(){
		return file.getName();
	}

	public String getExtension(){
		String name = file.getName();
		name = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : name;
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);

		if(name.equalsIgnoreCase("sh") || name.equalsIgnoreCase("run") || name.equalsIgnoreCase("bat") || name.equalsIgnoreCase("cmd"))
			name = "Shell";

		return name;
	}

}

