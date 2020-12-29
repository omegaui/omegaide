package terminal;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JFrame;
public class Main extends JFrame{
     public Main(){
          try{
          	javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
          }catch(Exception e){ System.err.println(e); }
     	setSize(400, 400);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(EXIT_ON_CLOSE);
          Terminal terminal = new Terminal();
          setVisible(true);
          add(new JScrollPane(terminal), BorderLayout.CENTER);
          new Thread(()->terminal.launch()).start();
     }

     public static void main(String[] args){
     	new Main();
     }
}
