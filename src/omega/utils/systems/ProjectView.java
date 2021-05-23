/**
  * <one line to give the program's name and a brief idea of what it does.>
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

package omega.utils.systems;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import omega.Screen;
import omega.utils.UIManager;
import omega.utils.systems.creators.FileOperationManager;
import omega.tree.FileTree;

public class ProjectView extends JDialog{

	private static final long serialVersionUID = 1L;
	public FileTree tree;
	private JFileChooser chooser = new JFileChooser();
	private FileOperationManager fileOperationManager;
	private Screen screen;

	public ProjectView(String title, Screen window) {
		super(window);
		this.screen = window;
		setTitle(title);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setIconImage(window.getIconImage());
		setSize(600, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		initComponents();
		setAlwaysOnTop(true);
		UIManager.setData(chooser);
		setModal(false);
	}

	public FileOperationManager getFileOperationManager(){
		return fileOperationManager;
	}

	private void initComponents() {
		fileOperationManager = new FileOperationManager(getScreen());
		tree = new FileTree(null);
		final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					tree.moveUp();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					tree.moveDown();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					tree.forceLocate();
				}
			}
		};
		addKeyListener(keyListener);
		tree.addKeyListener(keyListener);
	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			organizeProjectViewDefaults();
			if(tree.getRoot() == null || !tree.getRoot().getAbsolutePath().equals(Screen.getFileView().getProjectPath())) {
				tree = new FileTree(Screen.getFileView().getProjectPath());
				tree.gen(tree.getRoot());
			}
			repaint();
		}       
		super.setVisible(value);
	}

	public void organizeProjectViewDefaults() {
		if(!getScreen().screenHasProjectView) {
			getScreen().setToNull();
			add(tree, BorderLayout.CENTER);
		}
		else {
			remove(tree);
			getScreen().setToView();
		}
		repaint();
	}
	
	public FileTree getProjectView() {
		return tree;
	}

	public void reload() {
		tree = tree.reload();
		organizeProjectViewDefaults();
          if(Screen.getFileView().getJDKManager() != null)
               Screen.getFileView().getJDKManager().readSources(tree.getRoot().getAbsolutePath());
		Screen.getFileView().getSearchWindow().cleanAndLoad(new File(Screen.getFileView().getProjectPath()));
	}

	public void setTitleMainClass()
	{
		String mainClass = Screen.getRunView().mainClass;
		if(mainClass == null || mainClass.equals(""))
			setTitle("Project Structure -No Main Class");
		else
			setTitle("Project Structure -Main Class : "+ mainClass);
	}	   
	
	public Screen getScreen() {
		return screen;
	}   
}

