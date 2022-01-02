/**
* StructureView
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
import omega.ui.component.Editor;

import omega.io.IconManager;
import omega.io.UIManager;

import omega.instant.support.java.assist.SourceReader;
import omega.instant.support.java.assist.DataMember;
import omega.instant.support.java.assist.ByteReader;
import omega.instant.support.java.assist.Assembly;

import omega.instant.support.java.framework.CodeFramework;

import omega.instant.support.java.management.JDKManager;
import omega.instant.support.java.management.Import;

import omegaui.component.TextComp;
import omegaui.component.FlexPanel;
import omegaui.component.NoCaretField;

import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import java.util.LinkedList;
import java.util.Scanner;

import java.awt.geom.RoundRectangle2D;

import omega.Screen;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class StructureView extends JDialog {
	public static final short DEASSEMBLE_DYNAMICALLY = 0;
	public static final short DEASSEMBLE_HEADLESSLY = 1;
	public short deassembleType = DEASSEMBLE_DYNAMICALLY;
	public TextComp iconComp;
	public TextComp titleComp;
	public TextComp closeComp;
	public TextComp smallLogComp;
	public TextComp fullLogComp;
	
	public FlexPanel primeContainerPanel;
	
	public NoCaretField searchField;
	
	public FlexPanel searchPanel;
	public JScrollPane searchScrollPanel;
	public JPanel containerPanel;
	
	public JScrollPane contentScrollPanel;
	public JPanel contentPanel;
	
	public JScrollPane textAreaScrollPane;
	public RSyntaxTextArea textArea;
	
	public LinkedList<TextComp> options = new LinkedList<>();
	public LinkedList<TextComp> memberComps = new LinkedList<>();
	
	public StructureView(Screen screen){
		super(screen, false);
		setUndecorated(true);
		setResizable(false);
		setSize(700, 500);
		setLocationRelativeTo(null);
		JPanel panel = new JPanel(null);
		panel.setBackground(back1);
		setContentPane(panel);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
		init();
	}
	
	public void init(){
		iconComp = new TextComp(IconManager.fluentstructureImage, 25, 25, back1, back1, back1, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setArc(0, 0);
		iconComp.setClickable(false);
		add(iconComp);
		
		smallLogComp = new TextComp(IconManager.fluentrocketImage, 25, 25, "Deassemble Dynamically", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->{
			deassembleType = DEASSEMBLE_DYNAMICALLY;
			fullLogComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g){
				super.draw(g);
				if(deassembleType == DEASSEMBLE_DYNAMICALLY){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 2, getWidth(), 2);
				}
			}
		};
		smallLogComp.setBounds(30, 0, 30, 30);
		smallLogComp.setArc(0, 0);
		add(smallLogComp);
		
		fullLogComp = new TextComp(IconManager.fluentshellImage, 25, 25, "Deassemble Headlessly (Uses System's Default javap command)", TOOLMENU_COLOR1_SHADE, back1, TOOLMENU_COLOR1, ()->{
			deassembleType = DEASSEMBLE_HEADLESSLY;
			smallLogComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g){
				super.draw(g);
				if(deassembleType == DEASSEMBLE_HEADLESSLY){
					g.setColor(color3);
					g.fillRect(0, getHeight() - 2, getWidth(), 2);
				}
			}
		};
		fullLogComp.setBounds(60, 0, 30, 30);
		fullLogComp.setArc(0, 0);
		add(fullLogComp);
		
		titleComp = new TextComp("View Java Code Structures", back1, back1, glow, null);
		titleComp.setBounds(90, 0, getWidth() - 120, 30);
		titleComp.setArc(0, 0);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);
		
		closeComp = new TextComp(IconManager.fluentcloseImage, 25, 25, TOOLMENU_COLOR2_SHADE, back1, back1, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setArc(0, 0);
		add(closeComp);
		
		primeContainerPanel = new FlexPanel(null, c2, null);
		primeContainerPanel.setBounds(5, 40, getWidth() - 10, getHeight() - 40);
		primeContainerPanel.setArc(20, 20);
		add(primeContainerPanel);
		
		searchField = new NoCaretField("", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		searchField.setBounds(primeContainerPanel.getWidth()/2 - 125, 5, 250, 25);
		searchField.setFont(PX14);
		searchField.setOnAction(()->search(searchField.getText()));
		primeContainerPanel.add(searchField);
		addKeyListener(searchField);
		
		searchPanel = new FlexPanel(null, back2, null);
		searchPanel.setBounds(getWidth()/2 - 200, 40, 400, 250);
		searchPanel.setArc(10, 10);
		
		searchScrollPanel = new JScrollPane(containerPanel = new JPanel(null));
		searchScrollPanel.setBounds(5, 5, searchPanel.getWidth() - 10, searchPanel.getHeight() - 10);
		searchScrollPanel.setBackground(c2);
		searchScrollPanel.setBorder(null);
		
		containerPanel.setBackground(c2);
		
		searchPanel.add(searchScrollPanel);
		
		contentScrollPanel = new JScrollPane(contentPanel = new JPanel(null));
		contentScrollPanel.setBounds(5, 30, primeContainerPanel.getWidth() - 10, primeContainerPanel.getHeight() - 35);
		contentScrollPanel.setBackground(c2);
		contentScrollPanel.setBorder(null);
		
		contentPanel.setBackground(c2);
		
		textAreaScrollPane = new JScrollPane(textArea = new RSyntaxTextArea());
		textAreaScrollPane.setBorder(null);
		textAreaScrollPane.setBackground(c2);
		textAreaScrollPane.setBounds(5, 30, primeContainerPanel.getWidth() - 10, primeContainerPanel.getHeight() - 35);
		textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
		Editor.getTheme().apply(textArea);
		textArea.setFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
		textArea.setEditable(false);
	}
	
	public synchronized void search(String text){
		titleComp.setText("Searching...");
		titleComp.setColors(back1, back1, TOOLMENU_COLOR3);
		
		options.forEach(containerPanel::remove);
		options.clear();
		repaint();
		
		LinkedList<Import> matches = new LinkedList<>();
		
		for(Import im : JDKManager.imports){
			if(im.getImport().contains(text))
				matches.add(im);
		}
		if(matches.isEmpty()){
			titleComp.setText("No Matches Found");
			titleComp.setColors(back1, back1, TOOLMENU_COLOR2);
			return;
		}
		titleComp.setText("View Java Code Structures");
		titleComp.setColors(back1, back1, glow);
		
		int block = 0;
		for(Import im : matches){
			TextComp comp = new TextComp(im.getImport(), c1, c2, getSuitableColor(im), ()->genView(im));
			comp.setBounds(0, block, searchScrollPanel.getWidth(), 25);
			comp.setFont(PX14);
			comp.setArc(0, 0);
			comp.alignX = 5;
			containerPanel.add(comp);
			options.add(comp);
			
			block += 25;
		}
		primeContainerPanel.remove(contentScrollPanel);
		primeContainerPanel.remove(textAreaScrollPane);
		primeContainerPanel.add(searchPanel);
		
		containerPanel.setPreferredSize(new Dimension(searchScrollPanel.getWidth(), block));
		containerPanel.setLocation(0, 0);
		containerPanel.setSize(containerPanel.getPreferredSize());
		
		searchScrollPanel.getVerticalScrollBar().setVisible(true);
		searchScrollPanel.getVerticalScrollBar().setValue(0);
		
		layout();
		repaint();
		
		matches.clear();
	}
	
	public void genView(Import im){
		primeContainerPanel.remove(searchPanel);
		primeContainerPanel.remove(contentScrollPanel);
		primeContainerPanel.remove(textAreaScrollPane);
		primeContainerPanel.repaint();
		
		memberComps.forEach(contentPanel::remove);
		memberComps.clear();
		
		int block = 0;
		
		if(deassembleType == DEASSEMBLE_DYNAMICALLY){
			primeContainerPanel.add(contentScrollPanel);
			if(CodeFramework.isSource(im.getImport())){
				titleComp.setText("Viewing " + im.getImport() + " (Dynamic, Type : SourceCode)");
				SourceReader reader = new SourceReader(CodeFramework.getContent(im.getImport()));
				for(DataMember dx : reader.dataMembers){
					TextComp comp = new TextComp(dx.getRepresentableValue(), back1, back1, dx.isMethod() ? TOOLMENU_COLOR5 : TOOLMENU_COLOR2, null);
					comp.setBounds(0, block, contentScrollPanel.getWidth(), 25);
					comp.setToolTipText(dx.getData());
					comp.setFont(PX14);
					comp.setClickable(false);
					comp.setArc(0, 0);
					comp.alignX = 5;
					contentPanel.add(comp);
					memberComps.add(comp);
					
					block += 25;
				}
			}
			else{
				ByteReader reader = null;
				if(Assembly.has(im.getImport()))
					reader = Assembly.getReader(im.getImport());
				else
					reader = Screen.getProjectFile().getJDKManager().prepareReader(im.getImport());
				
				if(reader.dataMembers == null)
					return;
				titleComp.setText("Viewing " + im.getImport());
				for(DataMember dx : reader.dataMembers){
					TextComp comp = new TextComp(dx.getRepresentableValue(), back1, back1, dx.isMethod() ? TOOLMENU_COLOR5 : TOOLMENU_COLOR2, null);
					comp.setBounds(0, block, contentScrollPanel.getWidth(), 25);
					comp.setToolTipText(dx.getData());
					comp.setClickable(false);
					comp.setFont(PX14);
					comp.setArc(0, 0);
					comp.alignX = 5;
					contentPanel.add(comp);
					memberComps.add(comp);
					block += 25;
				}
			}
		}
		
		else if(deassembleType == DEASSEMBLE_HEADLESSLY){
			titleComp.setText("Deassembling Headlessly ...");
			titleComp.repaint();
			primeContainerPanel.add(textAreaScrollPane);
			textArea.setText("");
			block = primeContainerPanel.getHeight();
			if(CodeFramework.isSource(im.getImport())){
				textArea.setText("Deassemble Error\n" + im.getImport() + " is a Source File.\nThus, It cannot be deassembled by javap.");
				return;
			}
			try{
				LinkedList<String> options = new LinkedList<>();
				options.add("javap");
				options.add("-public");
				String depenPath = "";
				
				if(!Screen.getProjectFile().getProjectManager().jars.isEmpty()) {
					for(String d : Screen.getProjectFile().getProjectManager().jars)
						depenPath += d + omega.Screen.PATH_SEPARATOR;
				}
				
				if(!Screen.getProjectFile().getProjectManager().resourceRoots.isEmpty()) {
					for(String d : Screen.getProjectFile().getProjectManager().resourceRoots)
						depenPath += d + omega.Screen.PATH_SEPARATOR;
				}
				
				if(Screen.isNotNull(depenPath)){
					options.add("--class-path");
					options.add(depenPath);
				}
				
				String modulePath = Screen.getProjectFile().getDependencyView().getModulePath();
				if(Screen.isNotNull(modulePath)){
					options.add("--module-path");
					options.add(modulePath);
					if(im.module){
						options.add("--module");
						options.add(im.getModuleName());
					}
				}
				
				options.add(im.getImport());
				
				Process process = new ProcessBuilder(options).directory(new File(Screen.getProjectFile().getProjectPath())).start();
				new Thread(()->{
					try(Scanner errorReader = new Scanner(process.getErrorStream())){
						while(process.isAlive()){
							while(errorReader.hasNextLine()){
								textArea.append(errorReader.nextLine() + "\n");
							}
						}
					}
					catch(Exception ee){
						ee.printStackTrace();
					}
				}).start();
				try(Scanner inputReader = new Scanner(process.getInputStream())){
					while(process.isAlive()){
						while(inputReader.hasNextLine()){
							textArea.append(inputReader.nextLine() + "\n");
						}
					}
				}
				catch(Exception ei){
					ei.printStackTrace();
				}
				titleComp.setText("View Java Code Structures");
			}
			catch(Exception e){
				titleComp.setText("Unable to run javap command!");
				titleComp.setColors(c2, c2, TOOLMENU_COLOR2);
				e.printStackTrace();
			}
		}
		
		if(deassembleType == DEASSEMBLE_DYNAMICALLY){
			contentPanel.setPreferredSize(new Dimension(contentScrollPanel.getWidth(), block));
			contentPanel.setLocation(0, 0);
			contentPanel.setSize(contentPanel.getPreferredSize());
			contentScrollPanel.getVerticalScrollBar().setVisible(true);
			contentScrollPanel.getVerticalScrollBar().setValue(0);
		}
		else if(deassembleType == DEASSEMBLE_HEADLESSLY){
			textAreaScrollPane.getVerticalScrollBar().setVisible(true);
			textAreaScrollPane.getVerticalScrollBar().setValue(0);
		}
		
		layout();
		repaint();
	}
	
	public static Color getSuitableColor(Import im){
		return im.getImport().startsWith("java")? TOOLMENU_COLOR1 : TOOLMENU_COLOR3;
	}
}
