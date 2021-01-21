package ide;
import ide.utils.UIManager;
import java.awt.Desktop;
public class Manuals {
     public static final void showBasicManual(){
     	try{
               Desktop.getDesktop().open(UIManager.loadDefaultFile("Basic Manual.pdf"));
     	}catch(Exception e){ System.err.println(e); }
     }
}
