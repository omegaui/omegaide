package omega.plugin;
import omega.comp.TextComp;
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
          nameField = new TextComp(name + " -" + size, omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{
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
          description.setBackground(omega.utils.UIManager.c3);
          description.setForeground(omega.utils.UIManager.c2);
          add(new JScrollPane(description), BorderLayout.CENTER);
     }

     @Override
     public void paint(Graphics graphics){
     	super.paint(graphics);
          nameField.repaint();
     }
}
