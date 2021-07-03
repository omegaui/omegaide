/**
  * Auto-Import Dialog
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
import omega.jdk.*;
import omega.*;
import java.awt.*;
import java.awt.event.*;
import omega.comp.*;
import java.util.*;
import javax.swing.*;
import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class ImportResolver extends JDialog {
	private JScrollPane scrollPane;
	private LinkedList<TextComp> comps = new LinkedList<>();
	private JPanel panel = new JPanel(null);
     private int pressX;
     private int pressY;
     private int block;
     
	public ImportResolver() {
		super(Screen.getFileView().getScreen(), true);
          setTitle("Import Resolver");
          setUndecorated(true);
          setResizable(false);
          setSize(600, 400);
          setLocationRelativeTo(null);
          setLayout(null);
          setBackground(c2);
          init();
	}

     public void init(){
          scrollPane = new JScrollPane(panel);
          panel.setBackground(c2);
          scrollPane.setBounds(0, 40, getWidth(), getHeight() - 40);
          add(scrollPane);
          
     	TextComp titleComp = new TextComp("Select The Imports And Click \'x\'", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(PX16);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    pressX = e.getX();
                    pressY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
                    setLocation(e.getXOnScreen() - pressX - 40, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);

          TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(PX16);
          closeComp.setArc(0, 0);
          add(closeComp);
     }

	public LinkedList<Import> resolveImports(LinkedList<Import> imports) {
		if(imports.size() <= 1) {
			imports.clear();
			return imports;
		}
          comps.forEach(panel::remove);
          comps.clear();
		LinkedList<Import> selections = new LinkedList<>();
          int maxW = getWidth();
          block = 0;
          Graphics g = Screen.getScreen().getGraphics();
          g.setFont(PX14);
          for(Import im : imports){
               int w = g.getFontMetrics().stringWidth(im.getImport());
               if(w > maxW)
                    maxW = w;
          }
          for(Import im : imports){
               TextComp comp = new TextComp(im.getImport(), TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
               comp.setRunnable(()->{
                    comp.setColors(comp.color1, comp.color3, comp.color2);
               });
               comp.setBounds(0, block, maxW, 30);
               comp.setFont(PX14);
               comp.alignX = 5;
               comp.setArc(0, 0);
               panel.add(comp);
               comps.add(comp);
               block += 30;
          }
          panel.setPreferredSize(new Dimension(maxW, block));
          setVisible(true);
          comps.forEach(comp->{
               if(comp.color2 == TOOLMENU_COLOR3)
                    selections.add(new Import(comp.getText(), "", false));
          });
		return selections;
	}
}

