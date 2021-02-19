package omega.ui;
import java.util.LinkedList;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
public class Switch extends JComponent{
     private String text;
     private volatile boolean enter = false;
     protected volatile boolean selected = false;
     private static final LinkedList<Switch> switches = new LinkedList<>();
     
     public Switch(String text, Runnable r){
          switches.add(this);
          this.text = text;
          setFont(omega.settings.Screen.PX14);
          omega.utils.UIManager.setData(this);
          addMouseListener(new MouseAdapter(){
               @Override
               public void mouseClicked(MouseEvent e){
                    selected = true;
                    repaint();
                    r.run();
               }
               @Override
               public void mousePressed(MouseEvent e){
                    selected = true;
                    repaint();
                    r.run();
               }
               @Override
               public void mouseEntered(MouseEvent e){
                    enter = true;
                    repaint();
               }
               @Override
               public void mouseExited(MouseEvent e){
                    enter = false;
                    repaint();
               }
          });
     }

     @Override
     public void paint(Graphics graphics){
          super.paint(graphics);
          Graphics2D g = (Graphics2D)graphics;
          g.setFont(getFont());
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(getBackground());
          g.fillRect(0, 0, getWidth(), getHeight());
          int x = g.getFontMetrics().stringWidth(text);
          int cx = x;
          x = getWidth()/2 - x/2;
          g.setColor(getForeground());
          g.drawString(text, x, getHeight() / 2);
          if(selected){
               switches.forEach(s->{
                    if(s != this){
                         s.selected = false;
                         s.repaint();
                    }
               });
               g.setColor(getForeground());
               g.fillRect(0, 0, getWidth(), getHeight());
               x = g.getFontMetrics().stringWidth(text);
               cx = x;
               x = getWidth()/2 - x/2;
               g.setColor(getBackground());
               g.drawString(text, x, getHeight() / 2);
               g.fillRect(x, getHeight() - getFont().getSize() + 2, cx, 2);
          }
          if(enter){
               g.fillRect(x, getHeight() - getFont().getSize() + 2, cx, 2);
          }
     }
}
