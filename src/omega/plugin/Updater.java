package omega.plugin;
import java.awt.event.*;
import java.util.*;
import omega.comp.*;
import javax.swing.*;

import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class Updater extends JDialog{

     private int pressX;
     private int pressY;
     private TextComp titleComp;
     private TextComp changesComp;
     private String version;
     private LinkedList<String> changes = new LinkedList<>();
     private int block;
     private LinkedList<TextComp> items = new LinkedList<>();
     private TextComp closeComp;
     private TextComp installComp;
     
     public Updater(PluginCenter pluginCenter){
          super(pluginCenter, false);
          setUndecorated(true);
          setTitle("Omega IDE -- Release Updater");
     	setSize(600, 500);
          setLocationRelativeTo(null);
          JPanel panel = new JPanel(null);
          panel.setBackground(c2);
          setContentPane(panel);
          init();
     }

     public void init(){
          changes.add("New Plugin Center");
          changes.add("Better Visuals");
          changes.add("Super Light Mode");
          changes.add("Intense Dark Mode");
          
     	titleComp = new TextComp("Omega IDE v1.8", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null);
          titleComp.setBounds(0, 0, getWidth(), 30);
          titleComp.setFont(PX14);
          titleComp.setClickable(false);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    pressX = e.getX();
                    pressY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
               }
          });
          titleComp.setArc(0, 0);
          add(titleComp);

          changesComp = new TextComp("What's new in this release", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
          changesComp.setBounds(getWidth()/2 - 200, 40, 400, 35);
          changesComp.setFont(PX16);
          changesComp.setClickable(false);
          add(changesComp);

          closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(0, getHeight() - 30, getWidth()/2, 30);
          closeComp.setFont(PX14);
          add(closeComp);

          installComp = new TextComp("Install", "Open Github Release Section", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR4, null);
          installComp.setBounds(getWidth()/2, getHeight() - 30, getWidth()/2, 30);
          installComp.setFont(PX14);
          add(installComp);
     }

     public void genView(){
          items.forEach(this::remove);
          items.clear();
          
          block = 90;
          changes.forEach(change->{
               TextComp item = new TextComp(change, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
               item.setBounds(5, block, getWidth() - 10, 30);
               item.setFont(PX14);
               item.setClickable(false);
               add(item);
               items.add(item);
               block += 40;
          });
     }

     @Override
     public void setVisible(boolean value){
     	super.setVisible(value);
          if(value){
               genView();
          }
     }
}
