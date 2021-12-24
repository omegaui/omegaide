/**
* The Tab head component.
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
import omega.tree.FileTreeBranch;

import omega.Screen;

import java.io.File;

import java.awt.image.BufferedImage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import omega.popup.OPopupWindow;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import omega.comp.TextComp;
import omega.comp.FlexPanel;

import java.util.LinkedList;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabComp {
	
	private static LinkedList<TextComp> areas = new LinkedList<>();
	
	public static FlexPanel create(Component c, BufferedImage imageX, String name, Runnable closeAction, Runnable focusAction, String toolTip, OPopupWindow popUp) {
		TextComp closeButton = new TextComp("x", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR4, closeAction);
		closeButton.setPreferredSize(new Dimension(15, 15));
		closeButton.setFont(UBUNTU_PX12);
		closeButton.setArc(2, 2);
		
		String text = !toolTip.startsWith("src") ? ("{" + name + "}") : name;
		
		int width = computeWidth(text, UBUNTU_PX14) + 10;
		
		TextComp comp = new TextComp(text, toolTip, TOOLMENU_GRADIENT, c2, FileTreeBranch.getPreferredColorForFile(new File(name)), null);
		comp.setArc(6, 6);
		final Color FORE = comp.color3;
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				areas.forEach((a)->{
					if(a != comp)
						a.setColors(TOOLMENU_GRADIENT, c2, FORE);
					else
						a.setColors(TOOLMENU_GRADIENT, c2, glow);
					a.repaint();
				});
				focusAction.run();
			}
		};
		comp.setFont(UBUNTU_PX14);
		comp.addMouseListener(mouseAdapter);
		comp.setPreferredSize(new Dimension(width, 28));
		areas.add(comp);
		
		String baseName = getBaseName(name);
		TextComp iconButton = null;
		
		Color alpha = new Color(FORE.getRed(), FORE.getGreen(), FORE.getBlue(), 40);
		BufferedImage image = imageX;
		if(image == null){
			if(c instanceof Editor){
				image = FileTreeBranch.getPreferredImageForFile(((Editor)c).currentFile);
			}
			else
				image = IconManager.fluentshellImage;
		}
		if(popUp == null)
			alpha = c2;
		
		iconButton = new TextComp(image, 25, 25, toolTip, alpha, c2, FORE, ()->{});
		iconButton.setArc(0, 0);
		iconButton.setPreferredSize(new Dimension(baseName.length() > 2 ? (baseName.length() > 3 ? 40 : 30) : 28, 28));
		
		putAnimationLayer(iconButton, getImageSizeAnimationLayer(20, -4, true), ACTION_MOUSE_ENTERED);
		
		if(popUp != null) {
			iconButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					popUp.setVisible(true);
					popUp.setLocation(e.getLocationOnScreen());
				}
			});
		}
		
		FlexPanel panel = new FlexPanel(new FlowLayout(), c2, null);
		panel.setArc(5, 5);
		panel.add(iconButton);
		panel.add(comp);
		panel.add(closeButton);
		panel.addMouseListener(mouseAdapter);
		c.addMouseListener(mouseAdapter);
		areas.forEach((a)->{
			if(a != comp)
				a.setColors(TOOLMENU_GRADIENT, c2, FORE);
			else
				a.setColors(TOOLMENU_GRADIENT, c2, glow);
			a.repaint();
		});
		return panel;
	}
	
	public static String getBaseName(String ext) {
		if(ext.equals("Compilation"))
			return "JVM";
		
		if(ext.equals("Building"))
			return "IDE";
		
		else if(ext.equals("Terminal"))
			return "Shell";
		
		else if(ext.equals("File Operation"))
			return "Task";
		
		if(ext.contains("Run("))
			return "JVM";
		
		if((ext.contains("Run ") || ext.contains("Run") || ext.contains("Build")) )
			return "IDE";
		
		if(!ext.contains("."))
			return "?";
		
		if(ext.equals(".projectInfo") || ext.equals(".sources")|| ext.equals(".args") || ext.equals(".preferences") || ext.equals(".recents") || ext.equals(".firststartup") || ext.equals(".ui") || ext.equals(".plugs") || ext.equals(".snippets"))
			return "IDE";
		
		return "?";
	}
}

