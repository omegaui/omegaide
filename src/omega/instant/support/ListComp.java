package omega.instant.support;
import java.util.LinkedList;
import omega.utils.FileSelectionDialog;
import omega.Screen;
import omega.instant.support.ListUnit;
import omega.comp.TextComp;
import omega.instant.support.ListComp;
import java.io.File;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JComponent;
import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class ListComp extends JComponent{
     private JTextField extField;
     private JTextField containerField;
     private TextComp rootComp;
     private TextComp chComp;
     private TextComp surComp;
     private volatile boolean validComp = true;
     private volatile boolean sur = true;
     public Runnable r;
     
     public ListComp(Runnable r){
          this.r = r;
          setBackground(c2);
          setLayout(null);
          setPreferredSize(new Dimension(540, 40));
     	init();
     }

     public ListComp(ListUnit unit, Runnable r){
     	this(r);
          extField.setText(unit.ext);
          containerField.setText(unit.container);
          rootComp.setToolTipText(unit.sourceDir);
          surComp.setToolTipText(unit.sur ? "Click to do not surround file paths within \" double quotes" : "Click to surround file paths within \" double quotes");
          surComp.setText(unit.sur ? "-\"" : "+\"");
          sur = unit.sur;
     }

     public void init(){
     	extField = new JTextField();
          extField.setBounds(0, 0, 210, 40);
          extField.setBackground(c2);
          extField.setForeground(glow);
          extField.setFont(PX14);
          extField.addActionListener((e)->r.run());
          add(extField);

          containerField = new JTextField();
          containerField.setBounds(210, 0, 210, 40);
          containerField.setBackground(c2);
          containerField.setForeground(glow);
          containerField.setFont(PX14);
          containerField.addActionListener((e)->r.run());
          add(containerField);

          FileSelectionDialog fc = new FileSelectionDialog(Screen.getScreen());
          fc.setTitle("Choose a directory which will be searched for (.) files");

          rootComp = new TextComp(":", TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, c2, ()->{
               fc.setCurrentDirectory(new File(omega.Screen.getFileView().getProjectPath()));
               LinkedList<File> selections = fc.selectDirectories();
               if(!selections.isEmpty()){
                    rootComp.setToolTipText(selections.get(0).getAbsolutePath());
               }
          });
          rootComp.setBounds(420, 0, 40, 40);
          rootComp.setFont(PX14);
          rootComp.setArc(0, 0);
          rootComp.setToolTipText("Select Source Directory");
          add(rootComp);
          
          chComp = new TextComp("-", TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, c2, ()->{});
          chComp.setRunnable(()->{
               validComp = !validComp;
               chComp.setText(validComp ? "-" : "+");
               chComp.setToolTipText(validComp ? "Click to Remove This Field" : "Click to Restore This Field");
          });
          chComp.setBounds(460, 0, 40, 40);
          chComp.setFont(PX14);
          chComp.setArc(0, 0);
          add(chComp);

          surComp = new TextComp("\"", "Click to surround file paths within \" double quotes", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{});
          surComp.setRunnable(()->{
               sur = !sur;
               surComp.setToolTipText(sur ? "Click to do not surround file paths within \" double quotes" : "Click to surround file paths within \" double quotes");
               surComp.setText(sur ? "-\"" : "+\"");
          });
          surComp.setBounds(500, 0, 40, 40);
          surComp.setFont(PX16);
          surComp.setArc(0, 0);
          add(surComp);
     }

     public String getExt(){
          return extField.getText();
     }
     
     public String getContainer(){
         return containerField.getText();
     }

     public String getSourceDir(){
     	return rootComp.getToolTipText();
     }

     public boolean isValidComp() {
          return (validComp 
                    && !getExt().equals("") 
                    && !getContainer().equals("") 
                    && new File(getSourceDir()).exists());
     }

     public boolean surroundWithinDoubleQuotes(){
     	return sur;
     }

     public ListUnit genUnit(){
     	return new ListUnit(extField.getText(), containerField.getText(), rootComp.getToolTipText(), sur);
     }
}
