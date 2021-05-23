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
import javax.swing.JDialog;
import java.net.URL;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.BufferCapabilities;
import java.awt.Color;
import omega.comp.FlexPanel;
import javax.swing.JFrame;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class MadeWithScreen extends JDialog{
     private TextComp rImageComp;
     private TextComp rLinkComp;
     private TextComp lafImageComp;
     private TextComp lafLinkComp;
     private TextComp fluentImageComp;
     private TextComp fluentLinkComp;
     private TextComp omegauiImageComp;
     private TextComp omegauiLinkComp;
     
     public MadeWithScreen(JFrame frame){
          super(frame, true);
          setTitle("Made With");
          setUndecorated(true);
          pack();
          createBufferStrategy(3);
          setSize(400, 400);
          setLocationRelativeTo(null);
          setBackground(new Color(0, 0, 0, 0));
          FlexPanel panel = new FlexPanel(null, TOOLMENU_COLOR1, TOOLMENU_COLOR2);
          panel.setPaintGradientEnabled(true);
          panel.setArc(0, 0);
          setContentPane(panel);
          addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    dispose();
               }
          });
          init();
     }
     public void init(){
          TextComp mes = new TextComp("Made in combination with", getBackground(), getBackground(), c2, null);
          mes.setBounds(0, 0, getWidth(), 30);
          mes.setFont(PX20);
          add(mes);
          
          rImageComp = new TextComp(IconManager.fluentfileImage, 50, 50, c2, c2, c2, null);
          rImageComp.setBounds(50, 50, 60, 60);
          rImageComp.setClickable(false);
          add(rImageComp);
          
          rLinkComp = new TextComp("RSyntaxTextArea", "https://github.com/bobbylight/RSyntaxTextArea", c2, c2, TOOLMENU_COLOR2, ()->{
               openURl(rLinkComp.getToolTipText());
          });
          rLinkComp.setBounds(120, 50, getWidth() - 200, 60);
          rLinkComp.setFont(PX20);
          add(rLinkComp);
          
          TextComp rMessage = new TextComp("with theming tweaks", c2, c2, c3, null);
          rMessage.setBounds(0, rLinkComp.getHeight() - 20, rLinkComp.getWidth(), 20);
          rMessage.setArc(0, 0);
          rMessage.setFont(PX14);
          rLinkComp.add(rMessage);
          
          lafImageComp = new TextComp(IconManager.fluentsettingsImage, 50, 50, c2, c2, c2, null);
          lafImageComp.setBounds(50, 120, 60, 60);
          lafImageComp.setFont(PX22);
          lafImageComp.setClickable(false);
          add(lafImageComp);
          
          lafLinkComp = new TextComp("FlatLaf", "https://www.formdev.com/flatlaf", c2, c2, TOOLMENU_COLOR1, ()->{
               openURl(lafLinkComp.getToolTipText());
          });
          lafLinkComp.setBounds(120, 120, getWidth() - 200, 60);
          lafLinkComp.setFont(PX20);
          lafImageComp.setClickable(false);
          add(lafLinkComp);
          
          TextComp lafMessage = new TextComp("with minor tweaks", c2, c2, c3, null);
          lafMessage.setBounds(0, lafLinkComp.getHeight() - 25, lafLinkComp.getWidth(), 25);
          lafMessage.setArc(0, 0);
          lafMessage.setFont(PX14);
          lafLinkComp.add(lafMessage);
          
          fluentImageComp = new TextComp("", c2, c2, c2, null){
               @Override
               public void draw(Graphics2D g){
                    g.drawImage(IconManager.fluentlinuxImage, getWidth()/2 - 25/2, getHeight()/2 - 25/2, 25, 25, null);
                    g.drawImage(IconManager.fluentconsoleImage, 2, 2, 25, 25, null);
                    g.drawImage(IconManager.fluentshellImage, 32, 32, 25, 25, null);
                    g.drawImage(IconManager.fluentwindowsImage, 32, 2, 25, 25, null);
                    g.drawImage(IconManager.fluentmacImage, 2, 32, 25, 25, null);
               }
          };
          fluentImageComp.setBounds(50, 190, 60, 60);
          fluentImageComp.setFont(PX22);
          fluentImageComp.setClickable(false);
          add(fluentImageComp);
          
          fluentLinkComp = new TextComp("Fluent Icons", "icons8.com", c2, c2, TOOLMENU_COLOR2, ()->{
               openURl(fluentLinkComp.getToolTipText());
          });
          fluentLinkComp.setBounds(120, 190, getWidth() - 200, 60);
          fluentLinkComp.setFont(PX20);
          fluentLinkComp.setClickable(false);
          add(fluentLinkComp);
          
          TextComp fluentMessage = new TextComp("without any tweaks", c2, c2, c3, null);
          fluentMessage.setBounds(0, fluentLinkComp.getHeight() - 25, fluentLinkComp.getWidth(), 25);
          fluentMessage.setArc(0, 0);
          fluentMessage.setFont(PX14);
          fluentLinkComp.add(fluentMessage);

          omegauiImageComp = new TextComp(IconManager.ideImage64, 64, 64, c2, c2, c2, null);
          omegauiImageComp.setBounds(40, 260, 70, 70);
          omegauiImageComp.setClickable(false);
          add(omegauiImageComp);
          
          omegauiLinkComp = new TextComp("Omega UI", "github.com/omegaui", c2, c2, TOOLMENU_COLOR3, ()->{
               openURl(omegauiLinkComp.getToolTipText());
          });
          omegauiLinkComp.setBounds(120, 260, getWidth() - 190, 70);
          omegauiLinkComp.setFont(PX26);
          omegauiLinkComp.setClickable(false);
          add(omegauiLinkComp);
          
          TextComp omegaMessage = new TextComp("super flexible ui elements", c2, c2, c3, null);
          omegaMessage.setBounds(0, omegauiLinkComp.getHeight() - 25, omegauiLinkComp.getWidth(), 25);
          omegaMessage.setArc(0, 0);
          omegaMessage.setFont(PX14);
          omegauiLinkComp.add(omegaMessage);
     }
     @Override
     public void paint(Graphics g){
          if(getBufferStrategy() == null)
               return;
          BufferStrategy bs = getBufferStrategy();
          super.paint(bs.getDrawGraphics());
          bs.show();
     }
     public void openURl(String url){
          new Thread(()->{
               try{
                    Desktop.getDesktop().browse(new URL(url).toURI());
               }
               catch(Exception e){ 
                    e.printStackTrace();
               }
          }).start();
     }
}

