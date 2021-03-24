package omega.comp;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
public class ToggleComp extends TextComp {
     
     private volatile boolean state = false;
     private ToggleListener toggleListener = (value)->{};
     
     public ToggleComp(String text, Color c1, Color c2, Color c3, boolean state){
     	super(text, c1, c2, c3, null);
          setArc(6, 6);
          this.state = state;
          addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
               	ToggleComp.this.state = !ToggleComp.this.state;
                    repaint();
                    if(ToggleComp.this.toggleListener != null)
                         ToggleComp.this.toggleListener.toggle(ToggleComp.this.state);
               }
          });
     }

     public ToggleComp(String text, Color c1, Color c2, Color c3, boolean state, ToggleListener toggleListener){
          this(text, c1, c2, c3, state);
          setOnToggle(toggleListener);
     }

     public void setOnToggle(ToggleListener toggleListener){
          this.toggleListener = toggleListener;
     }

     public boolean isOn(){
          return state == true;
     }

     public boolean isOff(){
          return state == false;
     }

     @Override
     public void paint(Graphics graphics){
          alignX = getHeight() + 4;
     	super.paint(graphics);
     }

     @Override
     public void draw(Graphics2D g){
          super.draw(g);
          g.setColor(color3);
          g.fillRoundRect(2, 2, getHeight() - 4, getHeight() - 4, arcX, arcY);
          if(state){
               g.fillRect(alignX, getHeight() - 2, g.getFontMetrics().stringWidth(getText()), 2);
               g.setColor(color2);
               g.fillRoundRect(8, 8, getHeight() - 16, getHeight() - 16, arcX, arcY);
          }
     }
}
