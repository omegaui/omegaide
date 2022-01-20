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

package omega.ui.dialog;
import java.awt.geom.RoundRectangle2D;

import omega.io.IconManager;

import omega.ui.panel.BuildPanel;

import omegaui.component.TextComp;

import omega.Screen;

import java.io.File;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class BuildPathManager extends JDialog {
	
	private LinkedList<TextComp> jarComps = new LinkedList<>();
	private LinkedList<TextComp> nativeComps = new LinkedList<>();
	private LinkedList<TextComp> resourceRootComps = new LinkedList<>();
	private LinkedList<TextComp> moduleComps = new LinkedList<>();

	private JPanel mainPanel;

	private TextComp iconComp;
	private TextComp titleComp;
	private TextComp addComp;
	private TextComp remComp;
	private TextComp closeComp;
	
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
	
	public BuildPathManager(Screen screen){
		super(screen);
		setTitle("Build Path Manager");
		setModal(true);
		setResizable(false);
		setUndecorated(true);
		setSize(700, 500);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		setLayout(null);
		init();
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
	}
	
	public void init(){

		iconComp = new TextComp(IconManager.fluentbuildpathIcon, 25, 25, c2, c2, c2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		iconComp.attachDragger(this);
		add(iconComp);
		
		titleComp = new TextComp("Manage Build-Path", c2, c2, glow, null);
		titleComp.setBounds(30, 0, getWidth() - 120, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);
		
		addComp = new TextComp(IconManager.fluentaddlinkImage, 20, 20, TOOLMENU_COLOR5_SHADE, c2, c2, this::addPath);
		addComp.setBounds(getWidth() - 90, 0, 30, 30);
		addComp.setArc(0, 0);
		add(addComp);
		
		remComp = new TextComp(IconManager.fluentremovelinkImage, 20, 20, TOOLMENU_COLOR5_SHADE, c2, c2, this::removePath);
		remComp.setBounds(getWidth() - 60, 0, 30, 30);
		remComp.setArc(0, 0);
		add(remComp);

		closeComp = new TextComp(IconManager.fluentcloseImage, 25, 25, TOOLMENU_COLOR2_SHADE, c2, c2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);

		putAnimationLayer(addComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(remComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		
		mainPanel = new JPanel(null);
		mainPanel.setBounds(0, 30, getWidth(), getHeight() - 30);
		mainPanel.setBackground(c2);
		add(mainPanel);
		
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
		mainPanel.add(jarTab);
		
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
		mainPanel.add(nativeTab);
		
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
		mainPanel.add(resourceRootTab);
		
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
		mainPanel.add(moduleTab);
		
		jarPanel = new BuildPanel("Click \"Add\" to Add Jar Files(.jar) to Classpath");
		nativePanel = new BuildPanel("Click \"Add\" to Add Native Library Roots to Classpath");
		resourceRootPanel = new BuildPanel("Click \"Add\" to Add Resource Roots to Classpath");
		modulePanel = new BuildPanel("Click \"Add\" to Add Module Files(.jar modular) to Modulepath");
		
		jarPanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		nativePanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		resourceRootPanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		modulePanel.setBounds(0, 40, getWidth(), getHeight() - 40 - 30);
		
		mainPanel.add(jarPanel);
		mainPanel.add(nativePanel);
		mainPanel.add(resourceRootPanel);
		mainPanel.add(modulePanel);
	}
	
	public void addPath(){
		if(state == 0){
			fs.setFileExtensions(".jar");
			fs.setTitle("Select Jar Files");
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getProjectFile().getProjectManager().jars.add(file.getAbsolutePath());
				}
			}
		}
		else if(state == 1){
			fs.setFileExtensions(FileSelectionDialog.ALL_EXTENSIONS);
			fs.setTitle("Select Native Library Roots");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getProjectFile().getProjectManager().natives.add(file.getAbsolutePath());
				}
			}
		}
		else if(state == 2){
			fs.setFileExtensions(FileSelectionDialog.ALL_EXTENSIONS);
			fs.setTitle("Select Resource Roots");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getProjectFile().getProjectManager().resourceRoots.add(file.getAbsolutePath());
				}
			}
		}
		else{
			fs.setFileExtensions(".jar");
			fs.setTitle("Select Module Files");
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()){
				for(File file : files){
					Screen.getProjectFile().getProjectManager().modules.add(file.getAbsolutePath());
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
		
		int width = jarPanel.getWidth();
		
		for(String path : Screen.getProjectFile().getProjectManager().jars){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block0, width, 30);
			comp.setFont(PX14);
			comp.alignX = 35;
			comp.setImage(IconManager.fluentmanaImage, 20, 20);
			comp.setImageCoordinates(5, 5);
			comp.setArc(0, 0);
			jarPanel.add(comp);
			jarComps.add(comp);
			block0 += 30;
		}
		
		for(String path : Screen.getProjectFile().getProjectManager().natives){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR5_SHADE, c2, TOOLMENU_COLOR5, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block1, width, 30);
			comp.setFont(PX14);
			comp.alignX = 35;
			comp.setImage(IconManager.getPlatformImage(), 20, 20);
			comp.setImageCoordinates(5, 5);
			comp.setArc(0, 0);
			nativePanel.add(comp);
			nativeComps.add(comp);
			block1 += 30;
		}
		
		for(String path : Screen.getProjectFile().getProjectManager().resourceRoots){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block2, width, 30);
			comp.setFont(PX14);
			comp.alignX = 35;
			comp.setImage(IconManager.fluentresourceImage, 20, 20);
			comp.setImageCoordinates(5, 5);
			comp.setArc(0, 0);
			resourceRootPanel.add(comp);
			resourceRootComps.add(comp);
			block2 += 30;
		}
		
		for(String path : Screen.getProjectFile().getProjectManager().modules){
			String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
			TextComp comp = new TextComp(name, TOOLMENU_COLOR6_SHADE, c2, TOOLMENU_COLOR6, ()->{});
			comp.setRunnable(()->{
				comp.setColors(comp.color1, comp.color3, comp.color2);
			});
			comp.setToolTipText(path);
			comp.setBounds(0, block3, width, 30);
			comp.setFont(PX14);
			comp.alignX = 35;
			comp.setImage(IconManager.fluentmanaImage, 20, 20);
			comp.setImageCoordinates(5, 5);
			comp.setArc(0, 0);
			modulePanel.add(comp);
			moduleComps.add(comp);
			block3 += 30;
		}
		
		jarPanel.setPanelPrefSize(new Dimension(jarPanel.getWidth(), block0));
		nativePanel.setPanelPrefSize(new Dimension(nativePanel.getWidth(), block1));
		resourceRootPanel.setPanelPrefSize(new Dimension(resourceRootPanel.getWidth(), block2));
		modulePanel.setPanelPrefSize(new Dimension(modulePanel.getWidth(), block3));
		repaint();
	}
	
	public void removePath(){
		jarComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getProjectFile().getProjectManager().jars.remove(comp.getToolTipText());
		});
		nativeComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getProjectFile().getProjectManager().natives.remove(comp.getToolTipText());
		});
		resourceRootComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getProjectFile().getProjectManager().resourceRoots.remove(comp.getToolTipText());
		});
		moduleComps.forEach(comp->{
			if(comp.color2 == TOOLMENU_COLOR3)
				Screen.getProjectFile().getProjectManager().modules.remove(comp.getToolTipText());
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
		LinkedList<String> modules = Screen.getProjectFile().getProjectManager().modules;
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
		LinkedList<String> modules = Screen.getProjectFile().getProjectManager().modules;
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
		new Thread(Screen.getProjectFile()::readJDK).start();
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

