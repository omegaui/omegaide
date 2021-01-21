package popup;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class OPopupWindow extends JWindow{
     private int y;
     private String name;
     private Window owner;
     private JPanel panel;
     private JScrollPane scrollPane;
     private LinkedList<OPopupItem> items = new LinkedList<>();
     private OPopupItem closeItem;
     private int animaTime;
     
     public static int HEIGHT = 32;
     private boolean scrollable;
     private Runnable closeOperations;
     public OPopupWindow(String name, Window f, int animaTime, boolean scrollable){
     	super(f);
          this.name = name;
          this.owner = f;
          this.animaTime = animaTime;
          this.scrollable = scrollable;
          setLayout(scrollable ? new BorderLayout() : null);
          setBackground(ide.utils.UIManager.c2);
          setForeground(ide.utils.UIManager.c3);
          setType(JWindow.Type.POPUP);
          if(scrollable) {
               panel = new JPanel(null);
               panel.setBackground(ide.utils.UIManager.c2);
               panel.setForeground(ide.utils.UIManager.c3);
               super.add(scrollPane = new JScrollPane(panel), BorderLayout.CENTER);
          }
          closeItem = new OPopupItem(this, "Close " + name, null, ()->setVisible(false));
     }

     public void trash(){
     	items.forEach(this::remove);
          items.clear();
     }

     public OPopupWindow createItem(String name, BufferedImage image, Runnable run){
          OPopupItem item = new OPopupItem(this, name, image, run);
     	items.add(item);
          add(item);
          return this;
     }

     public OPopupWindow removeItem(String name){
          for(OPopupItem i : items){
               if(i.getName().equals(name)){
                    remove(i);
                    items.remove(i);
                    break;
               }
          }
          return this;
     }
     
     public OPopupWindow removeItem(int i){
          remove(items.get(i));
          items.remove(i);
          return this;
     }

     public OPopupWindow addItem(OPopupItem item){
          items.add(item);
          add(item);
     	return this;
     }

     public OPopupWindow width(int width){
          setSize(width, getHeight());
          return this;
     }

     public OPopupWindow height(int height){
          setSize(getWidth(), height);
          return this;
     }

     @Override
     public void setVisible(boolean value){
          if(value){
               if(items.isEmpty()) return;
               removeItem("Close " + name);
               items.add(closeItem);
               add(closeItem);
               
               if(scrollable)
                    panel.setPreferredSize(new Dimension(getWidth(), items.size() * 32));
               else
                    setSize(getWidth(), (items.size() * HEIGHT));
                    
               y = 0;
               items.forEach(item->{
                    item.setBounds(0, y, getWidth(), HEIGHT);
                    y += HEIGHT;
               });
               if(scrollable)
                    scrollPane.repaint();
          }
     	super.setVisible(value);
          if(scrollable){
               scrollPane.getVerticalScrollBar().setVisible(true);
               scrollPane.getVerticalScrollBar().setValue(0);
               scrollPane.getVerticalScrollBar().repaint();
          }
          if(!value && closeOperations != null) closeOperations.run();
          else if(value){
               new Thread(()->{
                    if(animaTime <= 0) return;
                    items.forEach(i->{
                         i.setEnter(true);
                         try{Thread.sleep(animaTime);}catch(Exception e){}
                         i.setEnter(false);
                    });
               }).start();
          }
     }

     @Override
     public Component add(Component c){
          if(scrollable)
               return panel.add(c);
          else
               return super.add(c);
     }

     @Override
     public void remove(Component c){
          if(scrollable)
               panel.remove(c);
          else
               super.add(c);
     }

     public void setOnClose(Runnable r){
     	this.closeOperations = r;
     }

     public static OPopupWindow gen(String name, Window owner, int animaTime, boolean scrollable){
     	return new OPopupWindow(name, owner, animaTime, scrollable);
     }
}
