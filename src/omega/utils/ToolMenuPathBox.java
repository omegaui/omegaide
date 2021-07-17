/**
  * PathBox
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
import java.awt.*;
import omega.comp.*;
import java.util.*;
import omega.*;
import java.io.*;
import omega.deassembler.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
public class ToolMenuPathBox extends JComponent{
     public String path;
     public LinkedList<EdgeComp> edges = new LinkedList<>();
     public int block = 0;
     public ToolMenuPathBox(){
          setLayout(null);
     	setBackground(c2);
          prepareEdges();
     }
     public void prepareEdges(){
          edges.forEach(this::remove);
          edges.clear();
          block = 0;
          if(path == null){
               EdgeComp comp = new EdgeComp("Path Box", c2, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, null);
               comp.setBounds(block, 0, 100, 25);
               comp.setUseFlatLineAtBack(true);
               comp.setFont(PX14);
               add(comp);
               edges.add(comp);
               block += comp.getWidth();
               repaint();
               return;
          }
          
          if(path.startsWith(Screen.getFileView().getProjectPath()))
               path = path.substring(Screen.getFileView().getProjectPath().length());
          block = 0;
     	LinkedList<String> tokens = CodeTokenizer.tokenize(path, File.separatorChar);
          tokens.add(path.substring(path.lastIndexOf(File.separator) + 1));
          tokens.forEach(token->{
               if(!token.equals("")){
                    EdgeComp comp = new EdgeComp(token, c2, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, null);
                    comp.setBounds(block, 0, getPreferredWidth(token), 25);
                    comp.setFont(PX14);
                    comp.setEnabled(false);
                    add(comp);
                    edges.add(comp);
                    block += comp.getWidth() - comp.getHeight()/2;
               }
               else {
                    EdgeComp comp = new EdgeComp("Root", c2, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, null);
                    comp.setBounds(block, 0, getPreferredWidth("Root"), 25);
                    comp.setFont(PX14);
                    add(comp);
                    edges.add(comp);
                    block += comp.getWidth() - comp.getHeight()/2;
               }
          });
          EdgeComp comp = edges.get(0);
          comp.setColors(c2, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3);
          comp.setUseFlatLineAtBack(true);
          
          EdgeComp comp1 = edges.getLast();
          comp1.setColors(c2, TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1);
          
          repaint();
     }
     public int getPreferredWidth(String text){
     	Graphics g = Screen.getScreen().getGraphics();
          g.setFont(PX14);
          return g.getFontMetrics().stringWidth("lll" + text + "  ");
     }
     public java.lang.String getPath() {
          return path;
     }
     public void setPath(java.lang.String path) {
          this.path = path;
          prepareEdges();
     }
     
}
