package omega.plugin;
import omega.*;
import omega.utils.*;
import omega.comp.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class Installer extends JDialog {
     private TextComp msgComp;
     private TextComp headerComp;
     private TextComp imageComp;
     private BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
     public Installer(String msg, String header){
          super(Screen.getScreen(), true);
          setTitle("Omega IDE -- Installer");
          setUndecorated(true);
          setLayout(null);
          setBackground(c2);
          setSize(400, 460);
          setLocationRelativeTo(null);
          paintImage();
          init(msg, header);
          setVisible(true);
     }
     public void init(String msg, String header){
          headerComp = new TextComp(header, TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
          headerComp.setBounds(0, 0, getWidth(), 30);
          headerComp.setFont(PX14);
          headerComp.setArc(0, 0);
          headerComp.setClickable(false);
          add(headerComp);
          
          msgComp = new TextComp(msg, TOOLMENU_COLOR4_SHADE, c2, TOOLMENU_COLOR2, ()->{
               setVisible(false);
          });
          msgComp.setBounds(0, 30, getWidth(), 30);
          msgComp.setFont(PX14);
          msgComp.setArc(0, 0);
          msgComp.setClickable(false);
          add(msgComp);

          imageComp = new TextComp("", c2, c2, c2, null){
               @Override
               public void draw(Graphics2D g){
                    g.drawImage(image, 0, 0, this);
               }
          };
          imageComp.setBounds(0, 60, getWidth(), 400);
          imageComp.setArc(0, 0);
          imageComp.setClickable(false);
          add(imageComp);
     }
     public void paintImage(){
          Graphics2D g = (Graphics2D)image.getGraphics();
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          //Drawing Levels
          int x = getWidth()/2 - 40;
          int x2 = x + 80;
          int y = 100;
          for(int i = 0; i < 8; i++){
               drawBox(g, x, y, 80, 30);
               y += 30;
          }
          x = getWidth()/2 - 80;
          drawBox(g, x, y, 160, 30);
          for(int i = 0; i < 3; i++){
               x -= 30;
               y -= 30;
               drawBox(g, x, y, 70, 30);
               drawBox(g, x2, y, 70, 30);
               x2 += 30;
          }
          g.dispose();
     }
     public void notify(String msg){
          msgComp.setText(msg);
     }
     public void setHeader(String header){
          headerComp.setText(header);
     }
     public void enableClose(){
          msgComp.setClickable(true);
     }
     public void drawBox(Graphics g, int x, int y, int w, int h){
          g.setColor(TOOLMENU_COLOR3);
          g.fillRect(x, y, w, h);
          g.setColor(TOOLMENU_COLOR4);
          g.drawRect(x, y, w - 1, h - 1);
     }
}
