/**
  * The Override/Implement Window
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

package omega.instant.support.java.generator.dialog;
import omega.instant.support.java.generator.Generator;

import omega.instant.support.java.framework.CodeFramework;

import omega.instant.support.java.assist.DataMember;
import omega.instant.support.java.assist.SourceReader;
import omega.instant.support.java.assist.ByteReader;
import omega.instant.support.java.assist.Assembly;

import omegaui.component.TextComp;
import omegaui.component.NoCaretField;

import java.awt.geom.RoundRectangle2D;

import omega.Screen;

import java.awt.Dimension;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import static omega.io.UIManager.*;
public class OverView extends JDialog{
     private LinkedList<TextComp> comps = new LinkedList<>();
     private LinkedList<DataMember> members = new LinkedList<>();
     private RSyntaxTextArea textArea;
     private JScrollPane scrollPane;
     private JPanel panel;
     public OverView(Screen screen){
          super(screen);
          setModal(false);
          setLayout(null);
          setUndecorated(true);
          setSize(600, 530);
          setLocationRelativeTo(screen);
          setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
          setResizable(false);
          init();
     }

     public void init(){
          scrollPane = new JScrollPane(panel = new JPanel(null));
          scrollPane.setBounds(0, 30, getWidth(), getHeight() - 90);
          scrollPane.setBorder(null);
          panel.setBackground(c2);
          add(scrollPane);
          
          TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
          closeComp.setBounds(0, 0, 30, 30);
          closeComp.setFont(PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp titleComp = new TextComp("Override/Implement Methods", c2, c2, glow, ()->{});
          titleComp.setBounds(30, 0, getWidth() - 30, 30);
          titleComp.setFont(PX16);
          titleComp.setClickable(false);
          titleComp.attachDragger(this);
          titleComp.setArc(0, 0);
          add(titleComp);

          TextComp genComp = new TextComp("Implement", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::generate);
          genComp.setBounds(0, getHeight() - 60, getWidth(), 30);
          genComp.setFont(PX16);
          genComp.setArc(0, 0);
          add(genComp);
          
          NoCaretField searchField = new NoCaretField("", "search any method here", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
          searchField.setBounds(0, getHeight() - 30, getWidth(), 30);
          searchField.setFont(PX14);
          searchField.setOnAction(()->search(searchField.getText()));
          add(searchField);
          addKeyListener(searchField);
     }

     public synchronized void search(String text){
          comps.forEach(panel::remove);
          int y = 0;
          for(TextComp comp : comps) {
               if(comp.getName().contains(text)){
                    comp.setBounds(0, y, getWidth(), 30);
                    panel.add(comp);
                    y += 30;
               }
          }
          panel.setPreferredSize(new Dimension(getWidth(), y));
          scrollPane.getVerticalScrollBar().setVisible(true);
          scrollPane.getVerticalScrollBar().setValue(0);
          scrollPane.repaint();
     }

     public void generate(){
          LinkedList<DataMember> selections = new LinkedList<>();
          comps.forEach(c->{
               if(c.color2 == TOOLMENU_COLOR3)
                    selections.add(members.get(comps.indexOf(c)));
          });
          selections.forEach(d->Generator.implement(d, textArea));
          selections.clear();
     }

     public void genView(RSyntaxTextArea textArea){
          if(omega.Screen.getProjectFile().getProjectManager().non_java || textArea == null)
          	return;
          new Thread(()->{
               this.textArea = textArea;
               comps.forEach(panel::remove);
               comps.clear();
               members.clear();
               SourceReader reader = new SourceReader(textArea.getText());
               LinkedList<ByteReader> brs = new LinkedList<>();
               LinkedList<SourceReader> srs = new LinkedList<>();
               if(reader.features != null){
                    for(String feature : reader.features){
                         String path = reader.getPackage(feature);
                         if(CodeFramework.isSource(path))
                              srs.add(new SourceReader(CodeFramework.getContent(path)));
                         else{
                              ByteReader bx = null;
                              if(Assembly.has(path))
                                   bx = Assembly.getReader(path);
                              else 
                                   bx = omega.Screen.getProjectFile().getJDKManager().prepareReader(path);
                              brs.add(bx);
                         }
                    }
               }
               if(!reader.parent.equals("Object")){
                    String path = reader.getPackage(reader.parent);
                    if(CodeFramework.isSource(path))
                         srs.add(new SourceReader(CodeFramework.getContent(path)));
                    else{
                         ByteReader bx = null;
                         if(Assembly.has(path))
                              bx = Assembly.getReader(path);
                         else 
                              bx = omega.Screen.getProjectFile().getJDKManager().prepareReader(path);
                         brs.add(bx);
                    }
               }
               int y = 0;
               for(ByteReader b : brs){
                    TextComp txComp = new TextComp(b.className, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
                    txComp.setBounds(0, y, getWidth(), 30);
                    txComp.setArc(0, 0);
                    txComp.setClickable(false);
                    txComp.setFont(PX14);
                    txComp.setName(b.className);
                    panel.add(txComp);
                    comps.add(txComp);
                    members.add(new DataMember("", "", "", "", ""));
                    y += 30;
                    for(DataMember d : b.dataMembers){
                         if(d.parameters != null && !Generator.isMemberOfObject(d) && d.modifier != null && !d.modifier.contains("final")){
                              String rep = d.getRepresentableValue();
                              if(rep == null) continue;
                              TextComp textComp = new TextComp(rep, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
                              textComp.setRunnable(()->{
                                   textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
                              });
                              textComp.setBounds(0, y, getWidth(), 30);
                              textComp.setArc(0, 0);
                              textComp.alignX = 5;
                              textComp.setFont(PX14);
                              textComp.setName(rep);
                              panel.add(textComp);
                              comps.add(textComp);
                              members.add(d);
                              y += 30;
                         }
                    }
               }
               for(SourceReader s : srs){
                    TextComp txComp = new TextComp(s.className, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
                    txComp.setBounds(0, y, getWidth(), 30);
                    txComp.setArc(0, 0);
                    txComp.setClickable(false);
                    txComp.setFont(PX14);
                    txComp.setName(s.className);
                    panel.add(txComp);
                    comps.add(txComp);
                    members.add(new DataMember("", "", "", "", ""));
                    y += 30;
                    for(DataMember d : s.dataMembers){
                         if(d.parameters != null && !Generator.isMemberOfObject(d) && d.modifier != null && !d.modifier.contains("final")){
                              String rep = d.getRepresentableValue();
                              if(rep == null) continue;
                              TextComp textComp = new TextComp(rep, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
                              textComp.setRunnable(()->{
                                   textComp.setColors(textComp.color1, textComp.color3, textComp.color2);
                              });
                              textComp.setBounds(0, y, getWidth(), 30);
                              textComp.setArc(0, 0);
                              textComp.alignX = 5;
                              textComp.setFont(PX14);
                              textComp.setName(rep);
                              panel.add(textComp);
                              comps.add(textComp);
                              members.add(d);
                              y += 30;
                         }
                    }
               }
               panel.setPreferredSize(new Dimension(getWidth(), y));
               setVisible(true);
               scrollPane.getVerticalScrollBar().setVisible(true);
               scrollPane.getVerticalScrollBar().setValue(0);
               scrollPane.repaint();
          }).start();
     }
}

