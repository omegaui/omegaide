package tabPane;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconManager {
     public static BufferedImage newImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage projectImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage fileImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage runImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage buildImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage closeImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage settingsImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage showImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage hideImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage infoImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage classImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage enumImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage interImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage annImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage packImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage methImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage varImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
     public static BufferedImage ideImage = getImage("/omega_ide_icon32.png");
     static{
          paintNewImage(newImage.getGraphics());
          paintProjectImage(projectImage.getGraphics());
          paintFileImage(fileImage.getGraphics());
          paintBuildImage(buildImage.getGraphics());
          paintCloseImage(closeImage.getGraphics());
          paintSettingsImage(settingsImage.getGraphics());
          paintShowImage(showImage.getGraphics());
          paintHideImage(hideImage.getGraphics());
          paintCharImage(infoImage.getGraphics(), "!");
          paintCharImage(classImage.getGraphics(), "C");
          paintCharImage(enumImage.getGraphics(), "E");
          paintCharImage(interImage.getGraphics(), "I");
          paintCharImage(annImage.getGraphics(), "A");
          paintCharImage(packImage.getGraphics(), "P");
          paintCharImage(runImage.getGraphics(), ">");
          paintCharImage(methImage.getGraphics(), "{}");
          paintCharImage(varImage.getGraphics(), "V");
     }

     public static void paintCharImage(Graphics graphics, String ch){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.setFont(settings.Screen.PX16);
     	g.drawString(ch, 8 - g.getFontMetrics().stringWidth(ch)/2, (16 - g.getFontMetrics().getHeight())/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
     }

     public static void paintHideImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.fillOval(4, 4, 8, 8);
          g.setColor(getBackground());
          g.fillOval(6, 6, 6, 6);
     }
     
     public static void paintShowImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.fillOval(2, 2, 12, 12);
     }

     public static void paintSettingsImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.fillOval(2, 2, 12, 12);
          g.setColor(getForeground());
          g.fillOval(4, 4, 8, 8);
     }

     public static void paintCloseImage(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 5, 5);
          g.setColor(getForeground());
          g.drawLine(4, 4, 12, 12);
          g.drawLine(12, 4, 4, 12);
     }

     public static void paintBuildImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 30, 30, 5, 5);
          g.setColor(getForeground());
          g.fillOval(12, 5, 14, 14);
          g.drawLine(13, 15, 5, 24);
          g.drawLine(13, 16, 5, 25);
          g.setColor(getBackground());
          g.fillOval(20, 5, 8, 8);
     }

     public static void paintFileImage(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 6, 6);
          g.setColor(getForeground());
          g.fillRect(4, 4, 2, 1);
          g.fillRect(7, 4, 2, 1);
          g.fillRect(5, 8, 2, 1);
          g.fillRect(8, 8, 2, 1);
          g.fillRect(4, 12, 2, 1);
          g.fillRect(7, 12, 2, 1);
     }

     public static void paintProjectImage(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
     	g.setColor(getBackground());
          g.fillRoundRect(2, 5, 21, 18, 5, 5);
          g.drawRect(2, 1, 13, 4);
          g.drawRect(2, 2, 13, 1);
          g.setColor(getForeground());
          g.fillRect(18 - 8, 8, 6, 8);
          g.fillRect(14 - 8, 11, 14, 1);
     }

     public static void paintNewImage(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRoundRect(0, 0, 16, 16, 6, 6);
          g.setColor(getForeground());
          g.fillRect(8, 2, 1, 11);
          g.fillRect(3, 7, 11, 1);
     }

     public static Color getBackground(){
          return ide.utils.UIManager.c3;
     }
     
     public static Color getForeground(){
         return ide.utils.UIManager.c2;
     }

     public static final Icon show = new ImageIcon(showImage);
     public static final Icon hide = new ImageIcon(hideImage);

     private static final Icon getIcon(String path) {
          if(ide.utils.UIManager.isDarkMode()) {
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
          if(ide.utils.UIManager.isDarkMode()) {
               path = path.substring(0, path.lastIndexOf('.'));
               path = path + "_dark" + ".png";
          }
          try {
               return ImageIO.read(IconManager.class.getResource(path));
          }catch(Exception e) {e.printStackTrace();}
          return null;
     }

	public static final ImageIcon getImageIcon(String path) {
		if(ide.utils.UIManager.isDarkMode()
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

}
