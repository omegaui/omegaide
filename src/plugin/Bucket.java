package plugin;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import settings.comp.TextComp;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
public class Bucket extends JComponent{
     public String name;
     public String size;
     public TextComp nameField;
     public JTextArea description;
     public String desc;
     public Bucket(String name, String size, String sum, ActionListener a){
     	this.name = name;
          this.size = size;
          this.desc = sum;
          setLayout(new BorderLayout());
          nameField = new TextComp(name + " -" + size, ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               a.actionPerformed(null);
          });
          nameField.addMouseListener(new MouseAdapter(){
               @Override
               public void mouseEntered(MouseEvent e){
                   nameField.setText("Download " + name);
                   nameField.repaint();
               }
               @Override
               public void mouseExited(MouseEvent e){
                   nameField.setText(name + " -" + size);
                   nameField.repaint();
               }
          });
          nameField.setFont(PluginStore.FONT);
          nameField.setPreferredSize(new Dimension(600, 40));
          add(nameField, BorderLayout.NORTH);

          description = new JTextArea(sum);
          description.setEditable(false);
          description.setFont(PluginStore.FONT14);
          description.setBackground(ide.utils.UIManager.c3);
          description.setForeground(ide.utils.UIManager.c2);
          add(new JScrollPane(description), BorderLayout.CENTER);
     }

     @Override
     public void paint(Graphics graphics){
     	super.paint(graphics);
          nameField.repaint();
     }
}
