package omega.utils;
import omega.*;
import java.awt.*;
import omega.comp.*;
import javax.swing.*;

import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class ChoiceDialog extends JDialog{
     private TextComp headerComp;
     private TextComp choice1Comp;
     private TextComp choice2Comp;
     private TextComp cancelComp;
     public static int CHOICE1 = 0;
     public static int CHOICE2 = 1;
     public static int CANCEL = 2;
     public int choice = CANCEL;
     private static ChoiceDialog choiceDialog;
     public ChoiceDialog(JFrame frame){
          super(frame, true);
          setUndecorated(true);
          setTitle("Choice Dialog");
          setBackground(c2);
          setLayout(null);
          init();
     }

     public void init(){
          headerComp = new TextComp("", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
          headerComp.setFont(PX14);
          headerComp.setClickable(false);
          headerComp.setArc(0, 0);
          headerComp.setLayout(null);
          add(headerComp);

          choice1Comp = new TextComp("", TOOLMENU_COLOR3, TOOLMENU_COLOR1, c2, ()->{
               choice = CHOICE1;
               setVisible(false);
          });
          choice1Comp.setFont(PX14);
          choice1Comp.setArc(0, 0);
          add(choice1Comp);
          
          choice2Comp = new TextComp("", TOOLMENU_COLOR3, TOOLMENU_COLOR1, c2, ()->{
               choice = CHOICE2;
               setVisible(false);
          });
          choice2Comp.setFont(PX14);
          choice2Comp.setArc(0, 0);
          add(choice2Comp);
          
          cancelComp = new TextComp("Cancel", TOOLMENU_COLOR3, TOOLMENU_COLOR2, c2, ()->setVisible(false));
          cancelComp.setFont(PX14);
          cancelComp.setArc(0, 0);
          headerComp.add(cancelComp);
     }

     public void plotComps(){
          headerComp.setBounds(0, 0, getWidth(), getHeight() - 30);
          choice1Comp.setBounds(0, getHeight() - 30, getWidth()/2, 30);
          choice2Comp.setBounds(getWidth()/2, getHeight() - 30, getWidth()/2, 30);
          cancelComp.setBounds(getWidth() - 100, 0, 100, 30);
     }

     public static int makeChoice(String text, String choice1, String choice2){
          if(choiceDialog == null)
               choiceDialog = new ChoiceDialog(Screen.getScreen());
          choiceDialog.headerComp.setText(text);
          choiceDialog.choice1Comp.setText(choice1);
          choiceDialog.choice2Comp.setText(choice2);

          Graphics g = Screen.getScreen().getGraphics();
          g.setFont(PX14);
          if(text.length() > 100)
               text = text.substring(0, 95) + "..." + text.charAt(text.length() - 1);
          int w = g.getFontMetrics().stringWidth(text);
          if(w < 400)
               w = 400;
          choiceDialog.setSize(w + 10, 150);
          choiceDialog.setLocationRelativeTo(null);
          choiceDialog.plotComps();
          choiceDialog.setVisible(true);
          return choiceDialog.choice;
     }
}
