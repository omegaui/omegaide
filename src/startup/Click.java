package startup;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Click extends JComponent{
     private String text;
     private volatile boolean enter = false;
     public Click(String text, Runnable r){
          this.text = text;
          setFont(Startup.BASE_FONT);
          addMouseListener(new MouseAdapter(){
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
               @Override
               public void mouseClicked(MouseEvent e){
                    r.run();
               }
          });
     }

     public void setText(String text){
          this.text = text;
          repaint();
     }

     public String getText(){
          return text;
     }
     
     @Override
     public void paint(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g.setFont(getFont());
          g.setColor(Startup.BASE_COLOR);
          g.fillRect(0, 0, getWidth(), getHeight());
          int x = g.getFontMetrics().stringWidth(text);
          g.setColor(Color.WHITE);
          g.drawString(text, getWidth()/2 - x/2, getFont().getSize());
          if(enter){
               g.setFont(getFont());
               g.setColor(Color.WHITE);
               g.fillRect(0, 0, getWidth(), getHeight());
               g.setColor(Startup.BASE_COLOR);
               g.drawString(text, getWidth()/2 - x/2, getFont().getSize());
          }
     }
}
