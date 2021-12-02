/**
* BuildPathManager
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

package omega.utils;
import omega.Screen;

import java.io.File;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;

import omega.comp.TextComp;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import static omega.utils.UIManager.*;
public class BuildPathManager extends JDialog {
	private LinkedList<TextComp> jarComps = new LinkedList<>();
	private LinkedList<TextComp> nativeComps = new LinkedList<>();
	private LinkedList<TextComp> resourceRootComps = new LinkedList<>();
	private LinkedList<TextComp> moduleComps = new LinkedList<>();
	private BuildPanel jarPanel;
	private BuildPanel nativePanel;
	private BuildPanel resourceRootPanel;
	private BuildPanel modulePanel;
	private TextComp jarTab;
	private TextComp nativeTab;
	private TextComp resourceRootTab;
	private TextComp moduleTab;
	
	private JFileChooser fileChooser;
	
	private int state = 0;
	
	private FileSelectionDialog fs;
	
	public BuildPathManager(omega.Screen screen){
		super(screen);
		setTitle("Build Path Manager");
		setModal(true);
		setResizable(false);
		setLayout(null);
		setUndecorated(true);
		setSize(700, 500);
		setLocationRelativeTo(null);
		setBackground(c2);
		init();
	}
	public void init(){
		fs = new FileSelectionDialog(this);
		
		jarTab = new TextComp("Jars", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->setView(0)){
			@Override
			public void draw(Graphics2D g){
				if(state == 0){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		jarTab.setBounds(0, 0, 175, 40);
		jarTab.setFont(PX16);
		jarTab.setArc(0, 0);
		add(jarTab);
		
		nativeTab = new TextComp("Native Roots", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->setView(1)){
			@Override
			public void draw(Graphics2D g){
				if(state == 1){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		nativeTab.setBounds(175, 0, 175, 40);
		nativeTab.setFont(PX16);
		nativeTab.setArc(0, 0);
		add(nativeTab);
		
		resourceRootTab = new TextComp("Resource Roots", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->setView(2)){
			@Override
			public void draw(Graphics2D g){
				if(state == 2){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		resourceRootTab.setBounds(350, 0, 175, 40);
		resourceRootTab.setFont(PX16);
		resourceRootTab.setArc(0, 0);
		add(resourceRootTab);
		
		moduleTab = new TextComp("Modules", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->setView(3)){
			@Override
			public void draw(Graphics2D g){
				if(state == 3){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 3, getWidth(), 3);
				}
			}
		};
		moduleTab.setBounds(525, 0, 175, 40);
		moduleTab.setFont(PX16);
		moduleTab.setArc(0, 0);
		add(moduleTab);
		
		jarPanel = new BuildPanel("Click \"Add\" to Add Jar Files(.jar) to Classpath");
		nativePanel = new BuildPanel("Click \"Add\" to Add Native Library Roots to Classpath");
		resourceRootPanel = new BuildPanel("Click \"Add\" to Add Resource Roots to Classpath");
		modulePanel = new BuildPanel("Click \"Add\" to Add Module Files(.jar modular) to Modulepath");
		
		jarPanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		nativePanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		resourceRootPanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		modulePanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		
		add(jarPanel);
		add(nativePanel);
		add(resourceRootPanel);
		add(modulePanel);
		
		TextComp closeComp = new TextComp("Close", TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, back3, this::dispose);
		closeComp.setBounds(0, getHeight() - 30, 160, 30);
		closeComp.setFont(PX16);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		TextComp addComp = new TextComp("Add", TOOLMENU_COLOR2_SHADE, back3, TOOLMENU_COLOR2, ()->addPath());
		addComp.setBounds(160, getHeight() - 30, 190, 30);
		addComp.setFont(PX16);
		addComp.setArc(0, 0);
		add(addComp);
		
		TextComp remComp = new TextComp("Remove", TOOLMENU_COLOR2_SHADE, back3, TOOLMENU_COLOR2, ()->removePath());
		remComp.setBounds(160 + 190, getHeight() - 30, 190, 30);
		remComp.setFont(PX16);
		remComp.setArc(0, 0);
		add(remComp);
		
		TextComp titleComp = new TextComp("Build Path Manager", TOOLMENU_COLOR1_SHADE, c2,  TOOLMENU_COLOR1, ()->{});
		titleComp.attachDragger(this);
		titleComp.setBounds(getWidth() - 160, getHeight() - 30, 160, 30);
		titleComp.setFont(PX16);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		add(titleComp);
	}
	public void addPath(){
		if(state == 0){
			fs.setFileExtensions(".jar");
			fs.setTitle("Select Jar Files");
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getFileView().getProjectManager().jars.add(file.getAbsolutePath());
				}
			}
		}
		else if(state == 1){
			fs.setFileExtensions(FileSelectionDialog.ALL_EXTENSIONS);
			fs.setTitle("Select Native Library Roots");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getFileView().getProjectManager().natives.add(file.getAbsolutePath());
				}
			}
		}
		else if(state == 2){
			fs.setFileExtensions(FileSelectionDialog.ALL_EXTENSIONS);
			fs.setTitle("Select Resource Roots");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getFileView().getProjectManager().resourceRoots.add(file.getAbsolutePath());
				}
			}
		}
		else{
			fs.setFileExtensions(".jar");
			fs.setTitle("Select Module Files");
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getFileView().getProjectManager().modules.add(file.getAbsolutePath());
				}
			}
		}
		read();
	}
	public void read(){
		jarComps.forEach(jarPanel::remove);
		nativeComps.forEach(nativePanel::remove);
		resourceRootComps.forEach(resourceRootPanel::remove);
		moduleComps.forEach(modulePanel::remove);
		jarComps.clear();
		nativeComps.clear();
		resourceRootComps.clear();
		moduleComps.clear();
		int block0 = 0;
		int block1 = 0;
		int block2 = 0;
		int block3 = 0;
		int maxW0 = getWidth() - 5;
		int maxW1 = getWidth() - 5;
		int maxW2 = getWidth() - 5;
		int maxW3 = getWidth() - 5;
		Graphics g = Screen.getScreen().getGraphics();
		g.setFont(PX14);
		
		for(String path : Screen.getFileView().getProjectManager().jars){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			int w = g.getFontMetrics().stringWidth(path);
			if(w > maxW0)
				maxW0 = w;
		}
		
		for(String path : Screen.getFileView().getProjectManager().natives){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			int w = g.getFontMetrics().stringWidth(path);
			if(w > maxW1)
				maxW1 = w;
		}
		
		for(String path : Screen.getFileView().getProjectManager().resourceRoots){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			int w = g.getFontMetrics().stringWidth(path);
			if(w > maxW2)
				maxW2 = w;
		}
		
		for(String path : Screen.getFileView().getProjectManager().modules){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			int w = g.getFontMetrics().stringWidth(path);
			if(w > maxW3)
				maxW3 = w;
		}
		
		for(String path : Screen.getFileView().getProjectManager().jars){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block0, maxW0, 30);
			comp.setFont(PX14);
			comp.alignX = 5;
			comp.setArc(0, 0);
			jarPanel.add(comp);
			jarComps.add(comp);
			block0 += 30;
		}
		for(String path : Screen.getFileView().getProjectManager().natives){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block1, maxW1, 30);
			comp.setFont(PX14);
			comp.alignX = 5;
			comp.setArc(0, 0);
			nativePanel.add(comp);
			nativeComps.add(comp);
			block1 += 30;
		}
		for(String path : Screen.getFileView().getProjectManager().resourceRoots){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block2, maxW2, 30);
			comp.setFont(PX14);
			comp.alignX = 5;
			comp.setArc(0, 0);
			resourceRootPanel.add(comp);
			resourceRootComps.add(comp);
			block2 += 30;
		}
		for(String path : Screen.getFileView().getProjectManager().modules){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block3, maxW3, 30);
			comp.setFont(PX14);
			comp.alignX = 5;
			comp.setArc(0, 0);
			modulePanel.add(comp);
			moduleComps.add(comp);
			block3 += 30;
		}
		jarPanel.setPreferredSize(new Dimension(maxW0, block0));
		nativePanel.setPreferredSize(new Dimension(maxW1, block1));
		resourceRootPanel.setPreferredSize(new Dimension(maxW2, block2));
		modulePanel.setPreferredSize(new Dimension(maxW3, block3));
		repaint();
	}
	public void removePath(){
		jarComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getFileView().getProjectManager().jars.remove(comp.getToolTipText());
		});
		nativeComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getFileView().getProjectManager().natives.remove(comp.getToolTipText());
		});
		resourceRootComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getFileView().getProjectManager().resourceRoots.remove(comp.getToolTipText());
		});
		moduleComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getFileView().getProjectManager().modules.remove(comp.getToolTipText());
		});
		read();
	}
	public void setView(int state){
		this.state = state;
		jarPanel.setVisible(state == 0);
		nativePanel.setVisible(state == 1);
		resourceRootPanel.setVisible(state == 2);
		modulePanel.setVisible(state == 3);
	}
	public String getModulePath(){
		LinkedList<String> modules = Screen.getFileView().getProjectManager().modules;
		if(modules.isEmpty())
			return null;
		String path = "";
		LinkedList<String> parentDirs = new LinkedList<>();
		modules.forEach(modulePath->{
			String parentPath = modulePath.substring(0, modulePath.lastIndexOf(File.separatorChar));
			boolean contains = false;
			for(String parent : parentDirs){
				if(parent.equals(parentPath)){
					contains = true;
					break;
				}
			}
			if(!contains)
				parentDirs.add(parentPath);
		});
		
		for(String p : parentDirs)
			path += p + Screen.PATH_SEPARATOR;
		
		return path;
	}
	public String getModules(){
		LinkedList<String> modules = Screen.getFileView().getProjectManager().modules;
		if(modules.isEmpty())
			return null;
		String moduleNames = "";
		for(String path : modules){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
			moduleNames += name + ",";
		}
		moduleNames = moduleNames.substring(0, moduleNames.length() - 1);
		return moduleNames;
	}
	@Override
	public void dispose(){
		super.dispose();
		new Thread(Screen.getFileView()::readJDK).start();
	}
	@Override
	public void setVisible(boolean value){
		if(value){
			read();
			setView(state);
		}
		super.setVisible(value);
	}
}

