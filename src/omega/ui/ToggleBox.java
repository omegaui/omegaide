package omega.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
public class ToggleBox extends JComponent{
     private String text;
     private volatile boolean enter = false;
     protected volatile boolean selected = false;

     public ToggleBox(String text, Runnable r){
          this.text = text;
          setFont(omega.settings.Screen.PX14);
          omega.utils.UIManager.setData(this);
          addMouseListener(new MouseAdapter(){
               @Override
               public void mouseClicked(MouseEvent e){
                    selected = !selected;
                    repaint();
                    if(r != null)
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
     
     public interface Toggle {
    	 void toggle(boolean value);
     }
     
     public ToggleBox(String text, Toggle t){
         this.text = text;
         setFont(ImportResolver.font);
         addMouseListener(new MouseAdapter(){
             @Override
             public void mouseClicked(MouseEvent e){
                  selected = !selected;
                  repaint();
                  if(t != null) {
                	  t.toggle(selected);
                  }
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
          g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
          int x = g.getFontMetrics().stringWidth(text);
          int cx = x;
          x = getWidth()/2 - x/2;
          g.setColor(getForeground());
          g.drawString(text, x + 3, getHeight() / 2);
          int fx = getFont().getSize();
          g.drawRect(x - fx, getHeight() / 2 - fx + 2, fx, fx);
          if(selected){
               g.fillRect(x - fx + 2, (getHeight() / 2) - fx + 2 + 2, fx - 3, fx - 3);
          }
          if(enter){
               g.fillRect(x, getHeight() - getFont().getSize() + 2, cx, 2);
          }
     }

}
