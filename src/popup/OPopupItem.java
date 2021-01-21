package popup;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
public class OPopupItem extends JComponent{
     private String name;
     private Runnable run;
     private Runnable enterRun;
     private Runnable exitRun;
     private BufferedImage image;
     private OPopupWindow popup;
     private volatile boolean enter;
     private volatile boolean clickable = true;
     private static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);

     public OPopupItem(OPopupWindow popup, String name, BufferedImage image, Runnable run){
          this.popup = popup;
     	this.name = name;
          this.image = image;
          this.run = run;
          setFont(PX16);
          setBackground(popup.getBackground());
          setForeground(popup.getForeground());
          addMouseListener(new MouseAdapter(){
               @Override
               public void mouseEntered(MouseEvent e){
                   enter = true;
                   repaint();
                   if(enterRun != null)
                         enterRun.run();
               }
               
               @Override
               public void mouseExited(MouseEvent e){
                   enter = false;
                   repaint();
                   if(exitRun != null)
                         exitRun.run();
               }
               
               @Override
               public void mousePressed(MouseEvent e){
                   popup.setVisible(false);
                   if(OPopupItem.this.run != null)
                       OPopupItem.this.run.run();
               }
          });
     }

     public void setName(String name){
     	this.name = name;
          repaint();
     }

     public String getName(){
     	return name;
     }

     public void setImage(BufferedImage image){
     	this.image = image;
          repaint();
     }

     public BufferedImage getImage(){
     	return image;
     }

     public void setAction(Runnable run){
     	this.run = run;
     }

     public Runnable getAction(){
     	return run;
     }

     public void setOnEnter(Runnable r){
          this.enterRun = r;
     }
     
     public void setOnExit(Runnable r){
         this.exitRun = r;
     }

     public void setEnter(boolean enter){
     	this.enter = enter;
          repaint();
     }

     @Override
     public void paint(Graphics graphics){
     	Graphics2D g = (Graphics2D)graphics;
     	g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
     	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(enter ? getForeground() : getBackground());
          g.fillRect((enter && image != null) ? 32 : 0, 0, getWidth(), getHeight());
     	if(image != null)
               g.drawImage(image, 8, 8, 16, 16, null);
          g.setFont(getFont());
          g.setColor(enter ? getBackground() : getForeground());
          int y = (getHeight() - g.getFontMetrics().getHeight())/2 + g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
          g.drawString(name, 42, y);
     }
}
