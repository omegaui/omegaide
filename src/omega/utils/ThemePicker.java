package omega.utils;
import java.awt.event.MouseEvent;
import omega.Screen;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import omega.comp.TextComp;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JDialog;
public class ThemePicker extends JDialog{
     private Color c1 = new Color(0, 0, 255, 20);
     private Color c2 = Color.WHITE;
     private Color c3 = new Color(0, 0, 255, 130);
     private Color b1 = Color.decode("#F93800");
     private Color b2 = Color.decode("#283350");
     private Color b3 = c2;
     private Font PX20 = new Font("Ubuntu Mono", Font.BOLD, 20);
     private int pressX;
     private int pressY;
     private BufferedImage image;
     //Components
     private JPanel panel;
     private TextComp applyComp;
     private TextComp titleComp;
     private TextComp lightComp;
     private TextComp darkComp;
     public boolean lightMode = true;
     
     public ThemePicker(Screen screen){
          super(screen);
          setTitle("Theme Picker");
          setModal(true);
          panel = new JPanel(null){
               @Override
               public void paint(Graphics graphics){
                    super.paint(graphics);
                    Graphics2D g = (Graphics2D)graphics;
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setColor(lightMode ? c3 : b3);
                    if(image == null){
                         g.setFont(PX20);
                         String msg = "Unable to Read the Preview Image";
                         g.drawString(msg, getWidth()/2 - g.getFontMetrics().stringWidth(msg)/2, 
                              getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
                    }
                    else{
                         g.drawImage(image, 5, 80, getWidth() - 10, getHeight() - 90, null);
                    }
               }
          };
          panel.setBackground(c2);
          setUndecorated(true);
          setLayout(null);
          setSize(720, 505);
          setLocationRelativeTo(null);
          setContentPane(panel);
          init();
     }
     public void init(){
          applyComp = new TextComp("Apply", c1, c2, c3, ()->{
               setVisible(false);
               DataManager.setTheme(lightMode ? "light" : "dark");
               Screen.getDataManager().save();
          });
          applyComp.setBounds(getWidth() - 80, 0, 80, 40);
          applyComp.setFont(PX20);
          applyComp.setArc(0, 0);
          add(applyComp);
     
          titleComp = new TextComp("Choose IDE Theme", c1, c2, c3, null);
          titleComp.setBounds(0, 0, getWidth() - 80, 40);
          titleComp.setFont(PX20);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    pressX = e.getX();
                    pressY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
                    setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);
          
          lightComp = new TextComp("<light>", c1, c2, c3, ()->{
               lightMode = true;
               manageTheme();
               loadImage("light.png");
          }){
               @Override
               public void paint(Graphics graphics){
                    super.paint(graphics);
                    if(lightMode){
                         graphics.fillRect(0, getHeight() - 3, getWidth(), 3);
                    }
               }
          };
          lightComp.setBounds(0, 40, getWidth()/2, 40);
          lightComp.setArc(0, 0);
          lightComp.setFont(PX20);
          add(lightComp);
          
          darkComp = new TextComp("<dark>", c1, c2, c3, ()->{
               lightMode = false;
               manageTheme();
               loadImage("dark.png");
          }){
               @Override
               public void paint(Graphics graphics){
                    super.paint(graphics);
                    if(!lightMode){
                         graphics.fillRect(0, getHeight() - 3, getWidth(), 3);
                    }
               }
          };
          darkComp.setBounds(getWidth()/2, 40, getWidth()/2, 40);
          darkComp.setArc(0, 0);
          darkComp.setFont(PX20);
          add(darkComp);
     }
     
     public void manageTheme(){
          Color c1 = lightMode ? this.c1 : b1;
          Color c2 = lightMode ? this.c2 : b2;
          Color c3 = lightMode ? this.c3 : b3;
          panel.setBackground(c2);
          applyComp.setColors(c1, c2, c3);
          titleComp.setColors(c1, c2, c3);
          lightComp.setColors(c1, c2, c3);
          darkComp.setColors(c1, c2, c3);
          repaint();
     }

     public void loadImage(String name){
          try{
               image = ImageIO.read(getClass().getResourceAsStream("/" + name));
          }
          catch(Exception e) { 
               System.err.println(e);
          }
     }
}
