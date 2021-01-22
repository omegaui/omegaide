package ide.utils.systems.creators;
import settings.comp.*;
import javax.swing.*;
import static ide.utils.UIManager.*;
public class ChoiceDialog extends JDialog{
     private TextComp ch1;
     private TextComp ch2;
     private TextComp cancel;
     public static final byte CHOICE_1 = 0;
     public static final byte CHOICE_2 = 1;
     public static final byte CANCEL = 3;
     public static byte choice = CANCEL;
     public ChoiceDialog(ide.Screen screen){
     	super(screen);
          setUndecorated(true);
          setSize(600, 40);
          setLocationRelativeTo(null);
          setModal(true);
          setLayout(null);
          init();
     }

     public void init(){
           ch1 = new TextComp("", c1, c2, c3, ()->{
               choice = CHOICE_1;
               setVisible(false);
           });
           ch2 = new TextComp("", c1, c2, c3, ()->{
               choice = CHOICE_2;
               setVisible(false);
           });
           cancel = new TextComp("Cancel", c1, c2, c3, ()->{
               choice = CANCEL;
               setVisible(false);
           });
           
           ch1.setBounds(0, 0, 200, 40);
           ch1.setArc(0, 0);
           ch2.setBounds(200, 0, 200, 40);
           ch2.setArc(0, 0);
           cancel.setBounds(400, 0, 200, 40);
           cancel.setArc(0, 0);

           ch1.setFont(settings.Screen.PX16);
           ch2.setFont(settings.Screen.PX16);
           cancel.setFont(settings.Screen.PX16);

           add(ch1);
           add(ch2);
           add(cancel);
     }

     public int show(String choice1, String choice2){
            ch1.setText(choice1);
            ch2.setText(choice2);
            setVisible(true);
            return choice;
     }
}
