package plugin;
import java.awt.*;
import org.fife.ui.rtextarea.*;
import javax.swing.*;
public class Downloader extends JDialog{
     private RTextArea textArea;
     public Downloader(PluginStore f){
     	super(f, "Plugin Download");
          setSize(600, 500);
          setLocationRelativeTo(f);
          setIconImage(f.getIconImage());
          setLayout(new BorderLayout());
          setModal(true);
          textArea = new RTextArea();
          add(new JScrollPane(textArea), BorderLayout.CENTER);
     }

     public void print(String text){
     	textArea.append(text + "\n");
     }

     @Override
     public void setVisible(boolean value){
     	super.setVisible(value);
          if(value)
               textArea.setText("Downloading\n");
     }
}
