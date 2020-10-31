package ide.utils;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
public class OperationMenu extends JComponent{
     private class ControlMenu extends JComponent{
          private String text;
          private volatile boolean enter;
          private volatile boolean press;
          private ControlMenu(String text, Runnable r){
               this.text = text;
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
                    public void mousePressed(MouseEvent e){
                         press = true;
                         repaint();
                         r.run();
                    }
                    @Override
                    public void mouseReleased(MouseEvent e){
                         press = false;
                         repaint();
                    }
               });
          }

          private void setText(String text){
               this.text = text;
          }

          @Override
          public void paint(Graphics g1){
               Graphics2D g = (Graphics2D)g1;
               g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               g.setColor(getBackground());
               if(press) g.fill3DRect(0, 0, getWidth(), getHeight(), !enter);
               else g.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, enter);
               g.setColor(getForeground());
               g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2, getHeight()/2);
          }
     }
     public OperationMenu(){
          setLayout(null);
          setPreferredSize(new Dimension(500, 30));
          init();
     }

     private void init(){
          ControlMenu toggleMenu = new ControlMenu("-", System.out::println);
          toggleMenu.setBounds(0, 0, 30, 30);
          add(toggleMenu);
     }

     public static void main(String[] args){
          try{javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");}catch(Exception e){e.printStackTrace();}
          JFrame f = new JFrame();
          f.setSize(1000, 660);
          f.add(new OperationMenu(), BorderLayout.NORTH);
          f.setVisible(true);
     }
}



