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
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import omega.comp.TextComp;
import javax.swing.JPanel;
import omega.Screen;
import java.awt.Dimension;

import static omega.utils.UIManager.*;
import static omega.utils.IconManager.*;
import static omega.settings.Screen.*;
public class SideMenu extends JPanel {
     private Screen screen;

     private TextComp sep;
     public TextComp projectTabComp;
     public TextComp shellComp;
     public TextComp structureComp;
     public TextComp searchComp;
     
     public SideMenu(Screen screen){
          super(null);
     	this.screen = screen;

          setBackground(c2);
          setPreferredSize(new Dimension(42, 100));
          init();
     }

     public void init(){
          sep = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
          add(sep);
          
     	projectTabComp = new TextComp(fluentprojectstructureImage, 30, 30, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->Screen.getScreen().getToolMenu().structureComp.runnable.run());
          projectTabComp.setBounds(0, 0, 40, 40);
          projectTabComp.setArc(2, 2);
          add(projectTabComp);

          shellComp = new TextComp(fluentconsoleImage, 30, 30, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, Screen.getTerminalComp()::showTerminal);
          shellComp.setBounds(0, 40, 40, 40);
          shellComp.setFont(PX18);
          shellComp.setArc(2, 2);
          add(shellComp);

          structureComp = new TextComp(fluentstructureImage, 30, 30, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->Screen.getScreen().getToolMenu().structureView.setVisible(true));
          structureComp.setBounds(0, 80, 40, 40);
          structureComp.setFont(PX18);
          structureComp.setArc(2, 2);
          add(structureComp);

          searchComp = new TextComp(fluentsearchImage, 30, 30, TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->Screen.getFileView().getSearchWindow().setVisible(true));
          searchComp.setBounds(0, 120, 40, 40);
          searchComp.setArc(2, 2);
          add(searchComp);
     }

     public void changeLocations(boolean non_java){
          if(non_java)
               searchComp.setBounds(0, 80, 40, 40);
          else{
               structureComp.setBounds(0, 80, 40, 40);
               searchComp.setBounds(0, 120, 40, 40);
          }
          repaint();
     }

     @Override
     public void paint(Graphics g){
          sep.setBounds(40, 0, 2, getHeight());
     	super.paint(g);
     }
}

