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
import omega.*;
import java.awt.event.*;
import omega.comp.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;

import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class InfoScreen extends JDialog{

     private String title = "Omega IDE";
     private String version = Screen.VERSION;
     private String h1 = "omegaui";
     private String h2 = "github.com/omegaui/omegaide";
     private String p1 = "the blazing fast java IDE";

     private BufferedImage image;
     
     public InfoScreen(Screen screen){
          super(screen);
          try{
               GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
               ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")));
               ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Ubuntu-Bold.ttf")));
          }
          catch(Exception e){ 
               System.err.println(e); 
          }
          setUndecorated(true);
          setBackground(new Color(0, 0, 0, 0));
          setSize(300, 300);
          setLocationRelativeTo(null);
          addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    setVisible(false);
               }
          });
          
          try{
               String ext = isDarkMode() ? "_dark.png" : ".png";
               image = ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon128" + ext));
          }
          catch(Exception e){ 
               System.err.println(e); 
          }
     }

     @Override
     public void paint(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(c2);
          g.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);
          g.setColor(c3);
          g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 80, 80);
          g.drawImage(image, getWidth()/2 - 64, 40, 128, 128, null);
          g.setFont(PX26);
          g.setPaint(new GradientPaint(100, 150, c3, getWidth(), getHeight(), c1));
          g.drawString(title, getWidth()/2 - g.getFontMetrics().stringWidth(title)/2, 160 + g.getFontMetrics().getAscent());
          g.setColor(c3);
          g.setFont(PX16);
          g.drawString(version, getWidth()/2 - g.getFontMetrics().stringWidth(version)/2, 190 + g.getFontMetrics().getAscent());
          g.setFont(PX14);
          g.setColor(glow);
          g.drawString(h1, getWidth()/2 - g.getFontMetrics().stringWidth(h1)/2, 220 + g.getFontMetrics().getAscent());
          g.drawString(h2, getWidth()/2 - g.getFontMetrics().stringWidth(h2)/2, 240 + g.getFontMetrics().getAscent());
          g.setColor(c3);
          g.drawString(p1, getWidth()/2 - g.getFontMetrics().stringWidth(p1)/2, 260 + g.getFontMetrics().getAscent());
     }
}

