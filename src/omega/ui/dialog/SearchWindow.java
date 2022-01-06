/**
* SearchWindow
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
import omega.io.FileOperationManager;

import omega.ui.component.SearchComp;

import omegaui.component.FlexPanel;
import omegaui.component.NoCaretField;
import omegaui.component.TextComp;

import omega.Screen;

import java.awt.image.BufferedImage;

import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.io.File;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.geom.RoundRectangle2D;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class SearchWindow extends JDialog{
	private Screen screen;
	
	private FlexPanel containerPanel;
	
	private JPanel panel;
	
	private JScrollPane scrollPane;
	
	private NoCaretField field;
	
	private TextComp infoComp;
	
	private LinkedList<File> files;
	
	private int blocks = -50;
	
	private LinkedList<SearchComp> searchComps;
	
	private int pointer;
	
	public SearchWindow(Screen f){
		super(f, false);
		
		this.screen = f;
		
		files = new LinkedList<>();
		searchComps = new LinkedList<>();
		
		setUndecorated(true);
		setTitle("Search Files across the Project");
		setIconImage(f.getIconImage());
		setSize(435, 400);
		setLocationRelativeTo(null);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		setResizable(false);
		
		JPanel panelX = new JPanel(null);
		panelX.setBackground(c2);
		setContentPane(panelX);
		setLayout(null);
		
		field = new NoCaretField("", "Type File Name", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		field.setBounds(0, 30, getWidth(), 30);
		field.setFont(PX16);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!searchComps.isEmpty()) {
					if(e.getKeyCode() == KeyEvent.VK_UP && pointer > 0) {
						searchComps.get(pointer).set(false);
						searchComps.get(--pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - 50);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN && pointer + 1< searchComps.size()) {
						searchComps.get(pointer).set(false);
						searchComps.get(++pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + 50);
					}
					else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						searchComps.get(pointer).mousePressed(null);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER)
					list(field.getText());
			}
		});
		add(field);
		addKeyListener(field);
		
		containerPanel = new FlexPanel(null, back1, null);
		containerPanel.setBounds(5, 65, getWidth() - 10, getHeight() - 70 - 30);
		containerPanel.setArc(10, 10);
		add(containerPanel);
		
		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBackground(back2);
		scrollPane.setBounds(5, 5, containerPanel.getWidth() - 10, containerPanel.getHeight() - 10);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL){
			@Override
			public void setVisible(boolean value){
				super.setVisible(false);
			}
		});
		containerPanel.add(scrollPane);
		
		infoComp = new TextComp("", c2, c2, glow, null);
		infoComp.setBounds(0, getHeight() - 25, getWidth(), 25);
		infoComp.setFont(PX14);
		infoComp.setArc(0, 0);
		infoComp.setClickable(false);
		infoComp.alignX = 10;
		add(infoComp);
		
		TextComp titleComp = new TextComp(getTitle(), back2, back2, glow, field::grabFocus);
		titleComp.setBounds(0, 0, getWidth() - 60, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);
		
		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		TextComp reloadComp = new TextComp("#", "Click to Reload File Tree", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->cleanAndLoad(new File(Screen.getProjectFile().getProjectPath())));
		reloadComp.setBounds(getWidth() - 60, 0, 30, 30);
		reloadComp.setArc(0, 0);
		reloadComp.setFont(PX14);
		add(reloadComp);
		
		panel.setBackground(c2);
	}
	
	public void list(String text){
		searchComps.forEach(panel::remove);
		searchComps.clear();
		blocks = -50;
		files.forEach(file->{
			if(file.getName().contains(text)){
				String ext = file.getName();
				if(ext.contains("."))
					ext = ext.substring(ext.lastIndexOf('.'));
				SearchComp comp = new SearchComp(this, file);
				comp.setBounds(0, blocks += 50, scrollPane.getWidth() - 5, 50);
				comp.initUI();
				panel.add(comp);
				searchComps.add(comp);
			}
		});
		panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 5, blocks + 50));
		scrollPane.repaint();
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);
		repaint();
		if(!searchComps.isEmpty()) {
			searchComps.get(pointer = 0).set(true);
			infoComp.setText(searchComps.size() + " File" + (searchComps.size() > 1 ? "s" : "") + " Found!");
		}
		else{
			infoComp.setText("Not at least One File Found!");
		}
		doLayout();
	}
	
	public void cleanAndLoad(File f){
		this.files.clear();
		load(f);
		FileOperationManager.sort(this.files);
	}
	
	public void load(File f){
		File[] files = f.listFiles();
		if(files == null || files.length == 0) return;
		for(File file : files){
			if(file.isDirectory()) 
				load(file);
			else if(!file.getName().endsWith(".class"))
				this.files.add(file);
		}
	}
	
	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		if(value){
			field.grabFocus();
		}
	}
}

