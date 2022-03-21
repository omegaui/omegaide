/**
 * InstructionWindow
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omega.ui.dialog;
import omega.io.IconManager;

import omegaui.component.FlexPanel;
import omegaui.component.TextComp;

import omega.Screen;
import omega.IDE;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.util.LinkedList;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.GradientPaint;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class InstructionWindow extends JDialog{
	private interface PaintBoard {
		void draw(Graphics2D g, int width, int height);
	}

	private FlexPanel mainPanel;

	private TextComp titleComp;
	private TextComp previousComp;
	private TextComp skipComp;
	private TextComp nextComp;

	private LinkedList<PaintBoard> paintBoards = new LinkedList<>();

	private int pointer = 0;

	private static GradientPaint titlePaint = new GradientPaint(0, 100, TOOLMENU_COLOR1, 400, 300, TOOLMENU_COLOR2);
	private static GradientPaint textPaint = new GradientPaint(0, 100, TOOLMENU_COLOR3, 400, 300, TOOLMENU_COLOR4);
	private static GradientPaint bodyPaint = new GradientPaint(0, 0, TOOLMENU_COLOR1, 500, 400, TOOLMENU_COLOR3);

	private BufferedImage toolMenuImage = getInstructionImage("toolmenu.png");
	private BufferedImage sourceDefenderImage = getInstructionImage("source-defender.png");
	private BufferedImage processWizardImage = getInstructionImage("process-wizard.png");

	public InstructionWindow(Screen screen){
		super(screen, false);
		setTitle("Instructions");
		setIconImage(screen.getIconImage());
		setUndecorated(true);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setResizable(false);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
		loadPaintBoards();
	}

	public void init(){
		titleComp = new TextComp("Instructions", c2, c2, glow, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		titleComp.attachDragger(this);
		add(titleComp);

		mainPanel = new FlexPanel(new BorderLayout(), TOOLMENU_GRADIENT, null){
			@Override
			public void paint(Graphics graphics){
				super.paint(graphics);
				Graphics2D g = (Graphics2D)graphics;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				paintBoards.get(pointer).draw(g, mainPanel.getWidth(), mainPanel.getHeight());
				g.setColor(glow);
				g.setFont(UBUNTU_PX12);
				g.drawString("Page " + (pointer + 1) + " of " + paintBoards.size(), mainPanel.getWidth() - 3 - g.getFontMetrics().stringWidth("Page " + (pointer + 1) + " of " + paintBoards.size()) - 10, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 3);
			}
		};
		mainPanel.setBounds(10, 40, getWidth() - 20, getHeight() - 40 - 40);
		add(mainPanel);

		previousComp = new TextComp("Previous", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::previous);
		previousComp.setBounds(10, getHeight() - 30, 100, 25);
		previousComp.setFont(UBUNTU_PX12);
		previousComp.setArc(2, 2);
		add(previousComp);

		skipComp = new TextComp("Skip", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, this::dispose);
		skipComp.setBounds(10 + previousComp.getWidth() + 2, getHeight() - 30, 100, 25);
		skipComp.setFont(UBUNTU_PX12);
		skipComp.setArc(2, 2);
		add(skipComp);

		nextComp = new TextComp("Next", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::next);
		nextComp.setBounds(getWidth() - 120, getHeight() - 30, 100, 25);
		nextComp.setFont(UBUNTU_PX12);
		nextComp.setArc(2, 2);
		add(nextComp);
	}

	public void loadPaintBoards(){
		paintBoards.add((g, width, height)->{
			g.setColor(glow);
			g.setFont(UBUNTU_PX16);
			g.drawString("Hello, there!", width/2 - g.getFontMetrics().stringWidth("Hello, there!")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() - 100);
			g.setFont(UBUNTU_PX14);
			g.drawString("Read some instructions before proceeding further", width/2 - g.getFontMetrics().stringWidth("Read Some Instructions before proceeding further")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() - 50);
			g.drawImage(IconManager.fluentbulletedlistImage, 20, 20, null);
			g.setPaint(bodyPaint);
			g.drawString("Click 'Next' to continue", width/2 - g.getFontMetrics().stringWidth("Click 'Next' to continue")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
			g.drawString("Click 'Previous' to go back anytime", width/2 - g.getFontMetrics().stringWidth("Click 'Previous' to go back anytime")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 50);
			g.drawString("Click 'Skip' to exit", width/2 - g.getFontMetrics().stringWidth("Click 'Skip' to exit")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 100);
		});

		paintBoards.add((g, width, height)->{
			g.setPaint(titlePaint);
			g.setFont(PX40);
			String text = "ToolMenu";
			g.drawString(text, width/2 - g.getFontMetrics().stringWidth(text)/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(toolMenuImage, width/2 - toolMenuImage.getWidth()/6, 60, toolMenuImage.getWidth()/3, toolMenuImage.getHeight()/3, null);
			g.setFont(PX14);
			g.setColor(glow);
			g.setPaint(bodyPaint);
			g.drawString("This is the ToolMenu of Omega IDE", width/2 - g.getFontMetrics().stringWidth("This is the ToolMenu of Omega IDE")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
			g.drawString("The Entire Project WorkFlow is controlled from here", width/2 - g.getFontMetrics().stringWidth("The Entire Project WorkFlow is controlled from here")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 20);
			g.drawString("From Creating Files to Building Project", width/2 - g.getFontMetrics().stringWidth("From Creating Files to Building Project")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 46);
			g.drawString("From Managing Content-Assist to Controlling Imports", width/2 - g.getFontMetrics().stringWidth("From Managing Content-Assist to Controlling Imports")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 66);
			g.drawString("And Much More. We will take a look at each of them.", width/2 - g.getFontMetrics().stringWidth("And Much More. We will take a look at each of them.")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 86);
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(IconManager.fluentfolderImage, 20, 40, null);
			g.drawImage(IconManager.fluentfileImage, 20, 100, null);
			g.drawImage(IconManager.fluentnewfolderImage, 20, 160, null);
			g.drawImage(IconManager.fluentnewfileImage, 20, 220, null);

			g.setFont(UBUNTU_PX14);

			g.drawString("Click to open another project", 100, 70 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to open a file in the main Tab-Panel", 100, 130 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to create a new java project", 100, 195 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to create a new file", 100, 250 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(IconManager.fluentrunImage, 20, 40, null);
			g.drawImage(IconManager.fluentrocketImage, 20, 100, null);
			g.drawImage(IconManager.fluentbuildImage, 20, 160, null);
			g.drawImage(IconManager.fluentrocketbuildImage, 20, 220, null);

			g.setFont(UBUNTU_PX14);

			g.drawString("Click to Build & Run, Right-Click to Run Without Build", 100, 70 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to start Dynamic Build & Run (Extremely Faster)", 100, 130 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to start Headless Build", 100, 195 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to start Dynamic Build(Accuracy Mode)", 100, 260 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(IconManager.fluentstructureImage, 20, 40, 50, 50, null);
			g.drawImage(IconManager.fluentsearchImage, 20, 100, null);
			g.drawImage(IconManager.fluentconsoleImage, 20, 160, null);

			g.setFont(UBUNTU_PX14);

			g.drawString("Click to Decompile and See Class Data", 100, 70 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to Search Files across the Project", 100, 130 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
			g.drawString("Click to Launch a Terminal (in System or in IDE)", 100, 195 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(IconManager.fluentshellImage, width/2 - IconManager.fluentshellImage.getWidth()/2, 60, null);
			g.setFont(PX14);
			g.setColor(glow);
			g.setPaint(bodyPaint);
			g.drawString("Double Click the IDE's Title to Toggle Maximize State", width/2 - g.getFontMetrics().stringWidth("Double Click the IDE's Title to Toggle Maximize State")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
			g.drawString("Hold Down Blank Area around Tool Menu to Drag the Window", width/2 - g.getFontMetrics().stringWidth("Hold Down Blank Area around Tool Menu to Drag the Window")/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 20);
		});

		paintBoards.add((g, width, height)->{
			g.setPaint(titlePaint);
			g.setFont(PX40);
			String text = "Tools Section";
			g.drawString(text, width/2 - g.getFontMetrics().stringWidth(text)/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(sourceDefenderImage, width/2 - sourceDefenderImage.getWidth()/2, 35, null);
			g.setFont(PX14);
			g.setColor(glow);
			g.setPaint(bodyPaint);
			g.drawString("The Tool Section contains unique features to", 20, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 10);
			g.drawString("drive the workflow better.", 60, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 30);
			g.drawString("One of Which is Source Defender.", 20, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 60);
			g.drawString("It makes automatic backups of currently edited sources", 20, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 90);
			g.drawString("as soon as you active run or save operations.", 60, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 110);
		});

		paintBoards.add((g, width, height)->{
			g.drawImage(processWizardImage, 10, height/2 - processWizardImage.getHeight()/4, processWizardImage.getWidth()/2, processWizardImage.getHeight()/2, null);
			g.setFont(PX14);
			g.setColor(glow);

			int x = 30 + processWizardImage.getWidth()/2;
			g.drawString("Process Wizard", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 30);
			g.setPaint(bodyPaint);
			g.drawString("It lets you to run any file from", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 60);
			g.drawString("inside the IDE, ", x + 20, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 80);
			g.drawString("independent of the project.", x + 20, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 100);
			g.drawString("Just Hit CTRL + SHIFT + L in", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 130);
			g.drawString("the Editor.", x + 20, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 150);
			g.drawString("There are more tools to see.", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 170);
		});

		paintBoards.add((g, width, height)->{
			g.setPaint(titlePaint);
			g.setFont(PX40);
			String text = "Editor KeyBindings";
			g.drawString(text, width/2 - g.getFontMetrics().stringWidth(text)/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		});

		paintBoards.add((g, width, height)->{
			g.setFont(PX14);

			int x = 20;
			g.setColor(glow);
			g.drawString("In Editor KeyBindings", width/2 - g.getFontMetrics().stringWidth("In Editor KeyBindings")/2, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 30);

			g.setPaint(textPaint);
			g.drawString("Ctrl + SHIFT + O - Auto Imports", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 50);
			g.drawString("Ctrl + SHIFT + F - Find And Replace", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 70);
			g.drawString("Ctrl + SHIFT + R - Run Project(Non-Dynamic)", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 90);
			g.drawString("Ctrl + SHIFT + C - Click Editor Image", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 110);
			g.drawString("Ctrl + SHIFT + G - Generate Getter/Setter(s)", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 130);
			g.drawString("Ctrl + SHIFT + I - Override/Implement Methods", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 150);
			g.drawString("Ctrl + SHIFT + L - Launch File", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 170);
			g.drawString("Ctrl + SHIFT + PLUS/EQUAL - Increase Editor Font Size", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 190);
			g.drawString("Ctrl + SHIFT + MINUS - Decrease Editor Font Size", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 210);
			g.drawString("Ctrl + SHIFT + T + PLUS/EQUAL - Increase Editor Tab Size", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 230);
			g.drawString("Ctrl + SHIFT + T + MINUS - Decrease Editor Tab Size", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 250);
			g.drawString("Ctrl + SHIFT + P - Show Search Window", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 270);
			g.drawString("Ctrl + SHIFT + SLASH - Toggle Single-Line Comment", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 290);
		});

		paintBoards.add((g, width, height)->{
			g.setFont(PX14);

			int x = 20;
			g.setColor(glow);
			g.drawString("In Editor KeyBindings", width/2 - g.getFontMetrics().stringWidth("In Editor KeyBindings")/2, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 30);

			g.setPaint(textPaint);
			g.drawString("Ctrl + S - Save Current Editor", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 50);
			g.drawString("Ctrl + I - Auto Indent(Java Only)", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 70);
			g.drawString("Ctrl + D - Duplicate Current Line(Till Caret) or Selection", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 90);
			g.drawString("Ctrl + J - Show Definitions", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 110);
			g.drawString("Ctrl + P - Show Editor Preview", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 130);
			g.drawString("TAB - Triggers Snippets", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 150);
		});
		
		paintBoards.add((g, width, height)->{
			g.setFont(PX14);

			int x = 20;
			g.setColor(glow);
			g.drawString("IDE KeyBindings", width/2 - g.getFontMetrics().stringWidth("In Editor KeyBindings")/2, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 30);

			g.setPaint(textPaint);
			g.drawString("Ctrl + T - Show FileWizard", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 50);
			g.drawString("Ctrl + B - Trigger Headless Build", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 70);
			g.drawString("Ctrl + SHIFT + R - Trigger Headless Run", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 90);
			g.drawString("Ctrl + SHIFT + F1 - Trigger Instant Dynamic Run", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() +110);
			g.drawString("Ctrl + SHIFT + P - Show Search Dialog", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 130);
			g.drawString("Ctrl + SHIFT + M - Show Recents Dialog", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 150);
			g.drawString("Ctrl + O - Open Project", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 170);
			g.drawString("Ctrl + ALT + O - Open File", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 190);
			g.drawString("Ctrl + N - New Java Project", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 210);
			g.drawString("Ctrl + SHIFT + N - New Universal Project", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 230);
			g.drawString("ALT + SHIFT + T - New Terminal", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 250);
			g.drawString("ALT + P - Toggle Process Panel", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 270);
			g.drawString("ALT + R - Refresh File Tree", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 290);
			g.drawString("Ctrl + ALT + S - Show Settings", x, g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 310);
		});

		paintBoards.add((g, width, height)->{
			g.setPaint(titlePaint);
			g.setFont(PX40);
			String text = "Thats All.";
			g.drawString(text, width/2 - g.getFontMetrics().stringWidth(text)/2, height/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		});
	}

	public void previous(){
		if(pointer == 0)
			return;
		pointer--;
		if(pointer <= paintBoards.size() - 1){
			nextComp.setText("Next");
		}
		mainPanel.repaint();
	}

	public void next(){
		if(pointer == paintBoards.size() - 1){
			dispose();
			return;
		}
		pointer++;
		if(pointer == paintBoards.size() - 1){
			nextComp.setText("Finish");
		}
		else{
			nextComp.setText("Next");
		}
		mainPanel.repaint();
	}

	public BufferedImage getInstructionImage(String name){
		try{
			if(isDarkMode())
				name = name.substring(0, name.lastIndexOf('.')) + "_dark.png";
			return ImageIO.read(getClass().getResourceAsStream("/extras/instruction-box/" + name));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

