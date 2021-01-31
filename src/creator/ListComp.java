package creator;
import java.io.File;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import settings.comp.TextComp;
import javax.swing.JTextField;
import javax.swing.JComponent;
import static settings.Screen.*;
import static ide.utils.UIManager.*;
public class ListComp extends JComponent{
     private JTextField extField;
     private JTextField containerField;
     private TextComp rootComp;
     private TextComp chComp;
     private volatile boolean validComp = true;
     public Runnable r;
     
     public ListComp(Runnable r){
          this.r = r;
          setBackground(c2);
          setLayout(null);
          setPreferredSize(new Dimension(500, 40));
     	init();
     }

     public ListComp(ListUnit unit, Runnable r){
     	this(r);
          extField.setText(unit.ext);
          containerField.setText(unit.container);
          rootComp.setToolTipText(unit.sourceDir);
     }

     public void init(){
     	extField = new JTextField();
          extField.setBounds(0, 0, 210, 40);
          extField.setBackground(c2);
          extField.setFont(PX14);
          extField.addActionListener((e)->r.run());
          add(extField);

          containerField = new JTextField();
          containerField.setBounds(210, 0, 210, 40);
          containerField.setBackground(c2);
          containerField.setFont(PX14);
          containerField.addActionListener((e)->r.run());
          add(containerField);

          JFileChooser fc = new JFileChooser();
          fc.setMultiSelectionEnabled(false);
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setDialogTitle("Choose a directory which will be searched for (.) files");
          fc.setApproveButtonText("Select");

          rootComp = new TextComp(":", c1, c3, c2, ()->{
               int res = fc.showOpenDialog(ListComp.this);
               if(res == JFileChooser.APPROVE_OPTION){
                    rootComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
               }
          });
          rootComp.setBounds(420, 0, 40, 40);
          rootComp.setFont(PX14);
          rootComp.setArc(0, 0);
          rootComp.setToolTipText("Select Source Directory");
          add(rootComp);
          
          chComp = new TextComp("-", c1, c3, c2, ()->{});
          chComp.setRunnable(()->{
               validComp = !validComp;
               chComp.setText(validComp ? "-" : "+");
               chComp.setToolTipText(validComp ? "Click to Remove This Field" : "Click to Restore This Field");
          });
          chComp.setBounds(460, 0, 40, 40);
          chComp.setFont(PX14);
          chComp.setArc(0, 0);
          add(chComp);
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

     public ListUnit genUnit(){
     	return new ListUnit(extField.getText(), containerField.getText(), rootComp.getToolTipText());
     }
}
