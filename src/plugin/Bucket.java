package plugin;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
public class Bucket extends JComponent{
     public String name;
     public String size;
     public JButton nameField;
     public JTextArea description;
     public String desc;
     public Bucket(String name, String size, String sum, ActionListener a){
     	this.name = name;
          this.size = size;
          this.desc = sum;
          setLayout(new BorderLayout());
          nameField = new JButton(name + " -" + size);
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
          nameField.addActionListener(a);
          add(nameField, BorderLayout.NORTH);

          description = new JTextArea(sum);
          description.setEditable(false);
          description.setFont(PluginStore.FONT14);
          add(new JScrollPane(description), BorderLayout.CENTER);
     }

     @Override
     public void paint(Graphics graphics){
     	super.paint(graphics);
          nameField.repaint();
     }
}
