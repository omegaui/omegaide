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
import omega.Screen;

import java.io.File;

import java.awt.image.BufferedImage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import omega.tree.Branch;

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

	public static FlexPanel create(Component c, String name, Runnable closeAction, Runnable focusAction, String toolTip, OPopupWindow popUp) {
		TextComp closeButton = new TextComp("x", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR4, closeAction);
		closeButton.setPreferredSize(new Dimension(15, 15));
		closeButton.setFont(UBUNTU_PX12);
		closeButton.setArc(2, 2);

		String text = !toolTip.startsWith("src") ? ("{" + name + "}") : name;
		
		Graphics graphics = omega.Screen.getScreen().getGraphics();
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(UBUNTU_PX14);
		int width = g.getFontMetrics().stringWidth(text) + 10;

		TextComp comp = new TextComp(text, toolTip, TOOLMENU_GRADIENT, c2, Branch.getColor(name), null);
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
		
		putAnimationLayer(comp, getLineAnimationLayer(1), ACTION_MOUSE_ENTERED);
		
		String baseName = getBaseName(name);
          TextComp iconButton = null;
          
          Color alpha = new Color(FORE.getRed(), FORE.getGreen(), FORE.getBlue(), 40);
          BufferedImage image = null;
          if(c instanceof Editor)
               image = getPreferredImage(((Editor)c).currentFile);
          else
               image = IconManager.fluentshellImage;

          iconButton = new TextComp(image, 25, 25, toolTip, alpha, c2, FORE, ()->{});
          iconButton.setArc(2, 2);
		iconButton.setPreferredSize(new Dimension(baseName.length() > 2 ? (baseName.length() > 3 ? 40 : 30) : 20, 28));
		
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
     
     public static BufferedImage getPreferredImage(File file){
          if(file.isDirectory()){
               File[] files = file.listFiles();
               
			if(files != null && files.length != 0){
				for(File fx : files){
					if(fx.getName().equals(".projectInfo"))
						return IconManager.fluentfolderImage;
				}
			}
               return IconManager.fluentplainfolderImage;
          }
          if(file.getName().contains(".")){
               String ext = file.getName().substring(file.getName().lastIndexOf('.'));
               if(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".bmp")
               || ext.equals(".gif") || ext.equals(".svg") || ext.equals(".ico") || ext.equals(".jp2"))
                    return IconManager.fluentimagefileImage;
               else if(ext.equals(".txt") || ext.equals(".java") || ext.equals(".cpp") || ext.equals(".py") || ext.equals(".rs") || ext.equals(".class") || ext.equals(".groovy"))
                    return IconManager.fluentfileImage;
               else if(ext.equals(".js") || ext.equals(".html") || ext.equals(".php") || ext.equals(".css"))
                    return IconManager.fluentwebImage;
               else if(ext.equals(".sh") || ext.equals(".run") || ext.equals(".dll") || ext.equals(".so"))
                    return IconManager.fluentshellImage;
               else if(ext.equalsIgnoreCase(".appimage") || ext.equals(".deb"))
                    return IconManager.fluentlinuxImage;
               else if(ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".exe") || ext.equals(".msi"))
                    return IconManager.fluentwindowsImage;
               else if(ext.equals(".dmg"))
                    return IconManager.fluentmacImage;
          }
          return IconManager.fluentanyfileImage;
     }
}

