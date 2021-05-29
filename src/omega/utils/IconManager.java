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

package omega.utils;
import omega.Screen;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import static omega.utils.UIManager.*;
public class IconManager {

     //Fluent Icon Pack
     public static BufferedImage fluentfolderImage = getFluentIcon("program-50.png");
     public static BufferedImage fluentfileImage = getFluentIcon("code-file-50.png");
     public static BufferedImage fluentnewfolderImage = getFluentIcon("add-folder-50.png");
     public static BufferedImage fluentnewfileImage = getFluentIcon("add-file-50.png");
     public static BufferedImage fluentconsoleImage = getFluentIcon("console-50.png");
     public static BufferedImage fluenthelpImage = getFluentIcon("help-50.png");
     public static BufferedImage fluentinfoImage = getFluentIcon("info-popup-50.png");
     public static BufferedImage fluentsaveImage = getFluentIcon("save-50.png");
     public static BufferedImage fluentyoutubeImage = getFluentIcon("youtube-studio-50.png");
     public static BufferedImage fluentupdateImage = getFluentIcon("installing-updates-50.png");
     public static BufferedImage fluentclearImage = getFluentIcon("broom-50.png");
     public static BufferedImage fluentlaunchImage = getFluentIcon("launch-50.png");
     public static BufferedImage fluentrunImage = getFluentIcon("play-50.png");
     public static BufferedImage fluentbuildImage = getFluentIcon("hammer-50.png");
     public static BufferedImage fluentstructureImage = getFluentIcon("unit-50.png");
     public static BufferedImage fluentsearchImage = getFluentIcon("inspect-code-50.png");
     public static BufferedImage fluentprojectstructureImage = getFluentIcon("goto-50.png");
     public static BufferedImage fluentcloseImage = getFluentIcon("close-window-50.png");
     public static BufferedImage fluentsettingsImage = getFluentIcon("gear-50.png");
     public static BufferedImage fluentanyfileImage = getFluentIcon("file-50.png");
     public static BufferedImage fluentimagefileImage = getFluentIcon("image-file-50.png");
     
     public static BufferedImage fluentlevelupImage = getFluentIcon("up-3-50.png");
     public static BufferedImage fluentplainfolderImage = getFluentIcon("folder-50.png");
     public static BufferedImage fluenthomeImage = getFluentIcon("person-at-home-50.png");
     public static BufferedImage fluentwebImage = getFluentIcon("web-shield-50.png");
     public static BufferedImage fluentwindowsImage = getFluentIcon("windows-10-50.png");
     public static BufferedImage fluentlinuxImage = getFluentIcon("penguin-50.png");
     public static BufferedImage fluentshellImage = getFluentIcon("snail-50.png");
     public static BufferedImage fluentmacImage = getFluentIcon("apple-logo-50.png");
     public static BufferedImage fluentgradleImage = getFluentIcon("elephant-50.png");
     public static BufferedImage fluentsourceImage = getFluentIcon("source-code-50.png");
     public static BufferedImage fluentbinaryImage = getFluentIcon("venn-diagram-50.png");
     

     //The Default Icon Pack -- mixed
     public static BufferedImage newImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage projectImage = fluentfolderImage;
     public static BufferedImage fileImage = fluentfileImage;
     public static BufferedImage runImage = fluentrunImage;
     public static BufferedImage buildImage = fluentbuildImage;
     public static BufferedImage closeImage = fluentcloseImage;
     public static BufferedImage settingsImage = fluentsettingsImage;
     public static BufferedImage showImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage hideImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage infoImage = fluenthelpImage;
     public static BufferedImage classImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage recordImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage enumImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage interImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage annImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage packImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage methImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage varImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage ideImage = getImage("/omega_ide_icon32.png");
     public static BufferedImage ideImage64 = getImage("/omega_ide_icon64.png");
     static{
          paintNewImage(newImage.getGraphics());
          paintShowImage(showImage.getGraphics());
          paintHideImage(hideImage.getGraphics());
          paintCharImage(classImage.getGraphics(), "C");
          paintCharImage(recordImage.getGraphics(), "R");
          paintCharImage(enumImage.getGraphics(), "E");
          paintCharImage(interImage.getGraphics(), "I");
          paintCharImage(annImage.getGraphics(), "A");
          paintCharImage(packImage.getGraphics(), "P");
          paintCharImage(methImage.getGraphics(), "{}");
          paintCharImage(varImage.getGraphics(), "V");
     }

     public static void paintCharImage(Graphics graphics, String ch){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(TOOLMENU_COLOR4);
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.setFont(omega.settings.Screen.PX16);
     	g.drawString(ch, 8 - g.getFontMetrics().stringWidth(ch)/2, (16 - g.getFontMetrics().getHeight())/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
     }

     public static void paintHideImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(TOOLMENU_COLOR4);
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.fillOval(4, 4, 8, 8);
          g.setColor(TOOLMENU_COLOR4);
          g.fillOval(6, 6, 6, 6);
     }
     
     public static void paintShowImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(TOOLMENU_COLOR3);
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.fillOval(2, 2, 12, 12);
     }
     
     public static void paintNewImage(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(TOOLMENU_COLOR1);
          g.fillRoundRect(0, 0, 16, 16, 6, 6);
          g.setColor(getForeground());
          g.fillRect(8, 2, 1, 11);
          g.fillRect(3, 7, 11, 1);
     }

     public static Color getBackground(){
          return omega.utils.UIManager.c3;
     }
     
     public static Color getForeground(){
         return omega.utils.UIManager.c2;
     }

     public static final Icon show = new ImageIcon(showImage);
     public static final Icon hide = new ImageIcon(hideImage);

     private static final Icon getIcon(String path) {
          if(omega.utils.UIManager.isDarkMode()) {
               path = path.substring(0, path.lastIndexOf('.'));
               path = path + "_dark" + ".png";
          }
          try {
               BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
               return new ImageIcon(image);
          }catch(Exception e) {e.printStackTrace();}
          return null;
     }
     
     private static final BufferedImage getImage(String path) {
          if(omega.utils.UIManager.isDarkMode()) {
               path = path.substring(0, path.lastIndexOf('.'));
               path = path + "_dark" + ".png";
          }
          try {
               return ImageIO.read(IconManager.class.getResource(path));
          }catch(Exception e) {e.printStackTrace();}
          return null;
     }

	public static final ImageIcon getImageIcon(String path) {
		if(omega.utils.UIManager.isDarkMode()
				&& !path.contains("Theme")) {
			path = path.substring(0, path.lastIndexOf('.'));
			path = path + "_dark" + ".png";
		}
		try {
			BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
			return new ImageIcon(image);
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}

     public static BufferedImage getFluentIcon(String name){
     	try{
     		return ImageIO.read(IconManager.class.getResourceAsStream("/fluent-icons/icons8-" + name));
     	}
     	catch(Exception e){ 
     	     e.printStackTrace();
	     }
          return null;
     }
}

