package omega.comp;
import javax.swing.JComponent;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JPanel;
public class FlexPanel extends JComponent{
     
     private int arcX = 40;
     private int arcY = 40;

     private Color accentColor;
     private GradientPaint paint;
     private boolean paintGradientEnabled = false;
     
     public FlexPanel(LayoutManager layout, Color background, Color accentColor){
          setLayout(layout);
          setBackground(background);
          this.accentColor = accentColor;
     }

     public boolean isPaintGradientEnabled() {
          return paintGradientEnabled;
     }
     public void setPaintGradientEnabled(boolean paintGradientEnabled) {
          this.paintGradientEnabled = paintGradientEnabled;
          repaint();
     }
     
     public Color getAccentColor() {
          return accentColor;
     }
     
     public void setAccentColor(Color accentColor) {
          this.accentColor = accentColor;
     }

     private void setGradient(){
          paint = new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), accentColor == null ? getBackground() : accentColor);
     }
     
     public void setArc(int x, int y){
          arcX = x;
          arcY = y;
     }
     
     @Override
     public void paint(Graphics graphics){
          if(paintGradientEnabled)
               setGradient();
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          if(paintGradientEnabled)
               g.setPaint(paint);
          else
               g.setColor(getBackground());
          g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
          super.paint(g);
     }
}
