/**
  * StructureView
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
import omega.deassembler.*;
import omega.framework.*;
import omega.jdk.*;
import omega.*;
import java.awt.*;
import java.util.*;
import omega.comp.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
public class StructureView extends JDialog {
     public TextComp titleComp;
     public TextComp closeComp;
     public FlexPanel classPanel;
     public NoCaretField classSearchField;
     public FlexPanel classContentPanel;
     public JScrollPane classContentScrollPane;
     public FlexPanel memberPanel;
     public NoCaretField memberSearchField;
     public FlexPanel memberContentPanel;
     public JScrollPane memberContentScrollPane;
     public int block = 0;
     public LinkedList<TextComp> classComps = new LinkedList<>();
     public LinkedList<TextComp> memberComps = new LinkedList<>();

     public StructureView(Screen screen){
          super(screen, false);
          setTitle("Omega IDE -- StructureView");
          setUndecorated(true);
          FlexPanel panel = new FlexPanel(null, c2, c2);
          panel.setArc(0, 0);
          setContentPane(panel);
          setBackground(c2);
          setLayout(null);
          setSize(900, 500);
          setLocationRelativeTo(null);
          init();
     }
     
     public void init(){
          titleComp = new TextComp("View Code Strutures", TOOLMENU_COLOR3, c2, c2, null);
          titleComp.setBounds(0, 0, getWidth() - 30, 30);
          titleComp.setFont(PX14);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          titleComp.attachDragger(this);
          add(titleComp);

          closeComp = new TextComp("x", "Close", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(getWidth() - 30, 0, 30, 30);
          closeComp.setArc(0, 0);
          closeComp.setFont(PX14);
          add(closeComp);
          
          classPanel = new FlexPanel(null, c2, null);
          classPanel.setBounds(10, 40, getWidth()/2 - 20, getHeight() - 60);
          classPanel.setArc(20, 20);
          classPanel.setPaintBorder(true);
          classPanel.setBorderColor(TOOLMENU_COLOR2);
          add(classPanel);

          classSearchField = new NoCaretField("", "Search Any Class", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
          classSearchField.setBounds(5, 5, classPanel.getWidth() - 10, 25);
          classSearchField.setFont(PX14);
          classSearchField.setOnAction(()->searchClass(classSearchField.getText()));
          classPanel.add(classSearchField);

          classContentScrollPane = new JScrollPane(classContentPanel = new FlexPanel(null, c2, null));
          classContentScrollPane.setBounds(5, 35, classPanel.getWidth() - 10, classPanel.getHeight() - 45);
          classContentScrollPane.setBackground(c2);
          classContentPanel.setArc(0, 0);
          classPanel.add(classContentScrollPane);

          memberPanel = new FlexPanel(null, c2, null);
          memberPanel.setBounds(getWidth()/2, 40, getWidth() - getWidth()/2 - 20, getHeight() - 60);
          memberPanel.setBorderColor(TOOLMENU_COLOR2);
          memberPanel.setPaintBorder(true);
          memberPanel.setArc(20, 20);
          add(memberPanel);

          memberSearchField = new NoCaretField("", "Search Class Members", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
          memberSearchField.setBounds(5, 5, memberPanel.getWidth() - 10, 25);
          memberSearchField.setFont(PX14);
          memberSearchField.setOnAction(()->searchMember(memberSearchField.getText()));
          memberPanel.add(memberSearchField);
          
          memberContentScrollPane = new JScrollPane(memberContentPanel = new FlexPanel(null, c2, null));
          memberContentScrollPane.setBounds(5, 35, memberPanel.getWidth() - 10, memberPanel.getHeight() - 45);
          memberContentScrollPane.setBackground(c2);
          memberContentPanel.setArc(0, 0);
          memberPanel.add(memberContentScrollPane);

          classContentPanel.setPreferredSize(new Dimension(classContentScrollPane.getWidth(), 25));
          memberContentPanel.setPreferredSize(new Dimension(memberContentScrollPane.getWidth(), 25));
     }

     public void searchClass(String text){
          LinkedList<Import> matches = new LinkedList<>();
          for(Import im : JDKManager.getAllImports()){
               if(im.getImport().contains(text))
                    matches.add(im);
          }
          if(matches.isEmpty()){
               titleComp.setColors(TOOLMENU_COLOR2, c2, c2);
               titleComp.setText("No Matches Found!");
               return;
          }
          titleComp.setColors(TOOLMENU_COLOR3, c2, c2);
          titleComp.setText("View Code Structures");
          
          classComps.forEach(classContentPanel::remove);
          classComps.clear();
          block = 0;
          matches.forEach(im->{
               TextComp comp = new TextComp(im.getImport(), null, null, null, ()->genView(im));
               comp.setBounds(0, block, classContentPanel.getWidth(), 25);
               comp.getExtras().add(im);
               setDesiredColor(comp);
               comp.setFont(PX14);
               comp.setArc(0, 0);
               comp.alignX = 5;
               classContentPanel.add(comp);
               classComps.add(comp);
               
               block += 25;
          });
          classContentPanel.setPreferredSize(new Dimension(classContentScrollPane.getWidth(), block));
          classContentScrollPane.getVerticalScrollBar().setValue(0);
          classContentScrollPane.getVerticalScrollBar().setVisible(true);
          repaint();
     }

     public void genView(Import im){
          memberComps.forEach(memberContentPanel::remove);
          memberComps.clear();
          block = 0;
     	if(CodeFramework.isSource(im.getImport())){
               SourceReader reader = new SourceReader(CodeFramework.getContent(im.getImport()));
               reader.dataMembers.forEach(dx->{
                    TextComp comp = new TextComp(dx.getRepresentableValue(), null, null, null, null);
                    comp.setBounds(0, block, memberContentPanel.getWidth(), 25);
                    comp.setToolTipText(dx.getData());
                    comp.getExtras().add(dx);
                    comp.setFont(PX14);
                    setDesiredColor(comp);
                    comp.setClickable(false);
                    comp.setArc(0, 0);
               	comp.alignX = 5;
                    memberContentPanel.add(comp);
                    memberComps.add(comp);
                    block += 25;
               });
     	}
          else{
               ByteReader reader = null;
               if(Assembly.has(im.getImport()))
                    reader = Assembly.getReader(im.getImport());
               else
                    reader = Screen.getFileView().getJDKManager().prepareReader(im.getImport());
               reader.dataMembers.forEach(dx->{
                    TextComp comp = new TextComp(dx.getRepresentableValue(), null, null, null, null);
                    comp.setBounds(0, block, memberContentPanel.getWidth(), 25);
                    comp.setToolTipText(dx.getData());
                    comp.getExtras().add(dx);
                    comp.setClickable(false);
                    comp.setFont(PX14);
                    setDesiredColor(comp);
                    comp.setArc(0, 0);
               	comp.alignX = 5;
                    memberContentPanel.add(comp);
                    memberComps.add(comp);
                    block += 25;
               });
          }
          memberContentPanel.setPreferredSize(new Dimension(memberContentScrollPane.getWidth(), block));
          memberContentScrollPane.getVerticalScrollBar().setValue(0);
          memberContentScrollPane.getVerticalScrollBar().setVisible(true);
          repaint();
     }

     public void searchMember(String text){
          memberComps.forEach(memberContentPanel::remove);
          block = 0;
          memberComps.forEach((comp)->{
               if(comp.getText().contains(text)){
                    comp.setBounds(0, block, memberContentPanel.getWidth(), 25);
                    memberContentPanel.add(comp);
                    block += 25;
               }
          });
          memberContentPanel.setPreferredSize(new Dimension(memberContentScrollPane.getWidth(), block));
          repaint();
     }

     public void setDesiredColor(TextComp comp){
          if(comp.getExtras().get(0) instanceof Import){
          	if(comp.getExtras().get(0).toString().startsWith("java"))
                    comp.setColors(TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1);
               else
                    comp.setColors(TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3);
          }
          else {
               if(((DataMember)comp.getExtras().get(0)).isMethod())
                    comp.setColors(TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3);
               else
                    comp.setColors(TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2);
          }
     }
}
