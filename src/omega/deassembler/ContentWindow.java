/**
  * ContentAssist
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

package omega.deassembler;
import omega.framework.CodeFramework;

import omega.utils.DataManager;
import omega.utils.Editor;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;

import omega.comp.TextComp;

import java.util.LinkedList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


import static omega.utils.UIManager.*;
public class ContentWindow extends JPanel implements KeyListener{
     public LinkedList<TextComp> hints = new LinkedList<>();
     private final Font PX12 = new Font("Ubuntu", Font.BOLD, 12);
     private Editor editor;
     private JPanel panel;
     private JScrollPane scrollPane;
     private int block;
     private int width;
     private int height;
     public int index;
     public static final int MINIMUM_HINT_HEIGHT = 20;
     public int optimalHintHeight = MINIMUM_HINT_HEIGHT;
     
     public static Color highlightColor = glow;

     public ContentWindow(Editor editor){
          this.editor = editor;
          setVisible(false);
          setBackground(c2);
          setLayout(null);
          add(scrollPane = new JScrollPane(panel = new JPanel(null)));
          panel.setBackground(c2);
     }

     public void genView(LinkedList<DataMember> dataMembers, Graphics g){
          if(dataMembers.isEmpty()){
               setVisible(false);
               return;
          }
          hints.forEach(panel::remove);
          hints.clear();

          sort(dataMembers);
          
          block = 0;
          width = 0;

		Font hintFont = DataManager.getHintFont();

		optimalHintHeight = hintFont.getSize() + 6;
		optimalHintHeight = optimalHintHeight <  20 ? MINIMUM_HINT_HEIGHT : optimalHintHeight;
		
          g.setFont(hintFont);
          
          dataMembers.forEach(data->{
               String text = data.getRepresentableValue();
               if(text != null){
                    int w = g.getFontMetrics().stringWidth(text);
                    if(w > width)
					width = w;
               }
          });
          
          width += optimalHintHeight/2;

          final Editor e = editor;
          
     	dataMembers.forEach(d->{
               if(d.getRepresentableValue() != null){
                    TextComp textComp = new TextComp(d.getRepresentableValue(), d.isMethod() ? TOOLMENU_COLOR3_SHADE : TOOLMENU_COLOR2_SHADE, c2, d.isMethod() ? TOOLMENU_COLOR3 : TOOLMENU_COLOR2, ()->{
                         ContentWindow.this.setVisible(false);
                         String lCode = CodeFramework.getCodeIgnoreDot(editor.getText(), editor.getCaretPosition());
                         if(lCode == null) {
                              e.insert(d.name, e.getCaretPosition());
                         }
                         else {
                              String part = d.name;
                              try {
                                   part = part.substring(lCode.length());
                                   e.insert(part, e.getCaretPosition());
                                   if(d.parameterCount > 0) e.setCaretPosition(e.getCaretPosition() - 1);
                              }
                              catch(Exception es) { 
                                   es.printStackTrace();
                              }
                         }
                    });
                    textComp.setName(d.isMethod() + "");
                    textComp.setBounds(0, block, width, optimalHintHeight);
                    textComp.setArc(0, 0);
                    textComp.alignX = 5;
                    textComp.setFont(hintFont);
                    panel.add(textComp);
                    hints.add(textComp);
                    block += optimalHintHeight;
               }
	     });

          if(block == 0) return;

          hints.getFirst().setEnter(true);
          hints.getFirst().color3 = highlightColor;
          scrollPane.getVerticalScrollBar().setValue(0);
          
          index = 0;
          
          panel.setPreferredSize(new Dimension(width, block));
          scrollPane.setPreferredSize(panel.getPreferredSize());
          doLayout();
          
          width += optimalHintHeight;
          height = (block > 200 ? 200 : block) + 4;

          setVisible(false);
          setSize((width > 700 ? 700 : width), height);
          setMinimumSize(getSize());
          setPreferredSize(getSize());
          scrollPane.setBounds(0, 0, getWidth(), height);
          decideLocation();
          setVisible(true);
          repaint();
     }

     public void decideLocation(){
     	final Editor e = editor;
          Rectangle vRect = e.getAttachment().getVisibleRect();
          if(e.getCaret().getMagicCaretPosition() == null)
               return;
          int x = e.getCaret().getMagicCaretPosition().x;
          int y = e.getCaret().getMagicCaretPosition().y + e.getFont().getSize();
          int xSep = (x + getWidth()) - (int)(vRect.x + vRect.getWidth());
          int ySep = (y + getHeight()) - (int)(vRect.y + vRect.getHeight());
          if(xSep > 0){
               x -= xSep;
               if(x < vRect.x)
                    x = vRect.x;
          }
          if(ySep > 0){
               y = e.getCaret().getMagicCaretPosition().y - getHeight();
          }
          setLocation(x, y);
     }
     
     @Override
     public void keyTyped(KeyEvent keyEvent) {
          
     }
     
     @Override
     public void keyPressed(KeyEvent e) {
          if(!isVisible()) return;
          if(e.getKeyCode() == KeyEvent.VK_DOWN){
               if(index < hints.size() - 1){
                    hints.get(index).setEnter(false);
                    hints.get(index).color3 = Boolean.valueOf(hints.get(index).getName()) ? TOOLMENU_COLOR3 : TOOLMENU_COLOR2;
                    hints.get(++index).setEnter(true);
                    hints.get(index).color3 = highlightColor;
                    scrollPane.getVerticalScrollBar().setValue(index * optimalHintHeight);
               }
          }
          else if(e.getKeyCode() == KeyEvent.VK_UP){
               if(index > 0){
                    hints.get(index).setEnter(false);
                    hints.get(index).color3 = Boolean.valueOf(hints.get(index).getName()) ? TOOLMENU_COLOR3 : TOOLMENU_COLOR2;
                    hints.get(--index).setEnter(true);
                    hints.get(index).color3 = highlightColor;
                    scrollPane.getVerticalScrollBar().setValue(index * optimalHintHeight);
               }
          }
          else if(e.getKeyCode() == KeyEvent.VK_ENTER){
               hints.get(index).runnable.run();
          }
     }
     
     @Override
     public void keyReleased(KeyEvent keyEvent) {
          
     }

     public synchronized static void sort(LinkedList<DataMember> dataMembers) {
          Object[] members = dataMembers.toArray();
          LinkedList<DataMember> vars = new LinkedList<>();
          LinkedList<DataMember> meths = new LinkedList<>();
          for(Object obj : members) {
               DataMember m = (DataMember)obj;
               if(m.parameters == null) 
                    vars.add(m);
               else 
                    meths.add(m);
          }
          dataMembers.clear();
          Object[] var_ =vars.toArray();
          Object[] meths_ = meths.toArray(); 
          for(int i = 0; i < var_.length; i++) {
               for(int j = 0; j < var_.length - 1 - i; i++) {
                    DataMember m = (DataMember)var_[j];
                    DataMember n = (DataMember)var_[j + 1];
                    if(m.name.compareTo(n.name) > 0) {
                         Object o = var_[j];
                         var_[j] = var_[j + 1];
                         var_[j + 1] = o;
                    }
               }
          }
          
          for(int i = 0; i < meths_.length; i++) {
               for(int j = 0; j < meths_.length - 1 - i; i++) {
                    DataMember m = (DataMember)meths_[j];
                    DataMember n = (DataMember)meths_[j + 1];
                    if(m.name.compareTo(n.name) > 0) {
                         Object o = meths_[j];
                         meths_[j] = meths_[j + 1];
                         meths_[j + 1] = o;
                    }
               }
          }
          for(Object v : var_) {
               dataMembers.add((DataMember)v);
          }
          for(Object v : meths_) {
               dataMembers.add((DataMember)v);
          }
          var_ = null;
          meths_ = null;
          members = null;
     }
}

