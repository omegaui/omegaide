package omega.utils;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import omega.comp.TextComp;
import javax.swing.JPanel;
import omega.Screen;
import java.awt.Dimension;

import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SideMenu extends JPanel {
     private Screen screen;

     private TextComp sep;
     public TextComp projectTabComp;
     public TextComp shellComp;
     public TextComp structureComp;
     public TextComp searchComp;
     
     public SideMenu(Screen screen){
          super(null);
     	this.screen = screen;

          setBackground(c2);
          setPreferredSize(new Dimension(42, 100));
          init();
     }

     public void init(){
          sep = new TextComp("", c1, c3, c3, null);
          add(sep);
          
     	projectTabComp = new TextComp("", c1, c2, c3, ()->Screen.getScreen().getToolMenu().structureComp.runnable.run()){
               @Override
               public void draw(Graphics2D g) {
                    g.setColor(color3);
                    g.fillRoundRect(8, 16, 24, 20, 5, 5);
                    g.drawRect(8, 10, 14, 6);
                    g.fillRect(8, 11, 15, 3);
               }
	     };
          projectTabComp.setBounds(0, 0, 40, 40);
          projectTabComp.setArc(2, 2);
          add(projectTabComp);

          shellComp = new TextComp(">_", c1, c2, c3, ()->Screen.getTerminalComp().showTerminal(true));
          shellComp.setBounds(0, 40, 40, 40);
          shellComp.setFont(PX18);
          shellComp.setArc(2, 2);
          add(shellComp);

          structureComp = new TextComp("|>>", c1, c2, c3, ()->Screen.getScreen().getToolMenu().structureView.setVisible(true));
          structureComp.setBounds(0, 80, 40, 40);
          structureComp.setFont(PX18);
          structureComp.setArc(2, 2);
          add(structureComp);

          searchComp = new TextComp("", c1, c2, c3, ()->Screen.getFileView().getSearchWindow().setVisible(true)){
               @Override
               public void draw(Graphics2D g) {
                    g.setColor(color3);
                    g.fillOval(6, 6, 20, 20);
                    g.setColor(UIManager.isDarkMode() ? color1 : color2);
                    g.fillOval(10, 10, 12, 12);
                    g.setColor(color3);
                    g.drawLine(34, 34, 22, 22);
                    g.drawLine(33, 33, 22, 22);
               }
          };
          searchComp.setBounds(0, 120, 40, 40);
          searchComp.setArc(2, 2);
          add(searchComp);
     }

     @Override
     public void paint(Graphics g){
          sep.setBounds(40, 0, 2, getHeight());
     	super.paint(g);
     }
}
