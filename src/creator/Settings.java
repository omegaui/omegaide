package creator;
import java.io.File;
import settings.comp.Comp;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import settings.comp.TextComp;
import ide.Screen;
import javax.swing.JDialog;

import static settings.Screen.*;
import static ide.utils.UIManager.*;
public class Settings extends JDialog{
     public TextComp titleComp;
     public JTextField compileTimeField;
     public JTextField runTimeField;
     public TextComp ctDirComp;
     public TextComp rtDirComp;
     public TextComp closeComp;
     public Comp applyComp;
     public Settings(Screen screen){
     	super(screen);
          setUndecorated(true);
          setLayout(null);
          setSize(500, 160);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(HIDE_ON_CLOSE);
          init();
     }

     public void init(){
     	closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(PX18);
          closeComp.setArc(0, 0);
          add(closeComp);

          titleComp = new TextComp("Preferences", c1, c3, c2, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(PX16);
          titleComp.setArc(0, 0);
          titleComp.setClickable(false);
          add(titleComp);

          TextComp cLabel = new TextComp("Compile Time", c1, c2, c3, ()->{});
          cLabel.setBounds(0, 40, 100, 40);
          cLabel.setFont(PX14);
          cLabel.setArc(0, 0);
          cLabel.setClickable(false);
          add(cLabel);
          
          TextComp rLabel = new TextComp("Run Time", c1, c2, c3, ()->{});
          rLabel.setBounds(0, 80, 100, 40);
          rLabel.setFont(PX14);
          rLabel.setArc(0, 0);
          rLabel.setClickable(false);
          add(rLabel);

          compileTimeField = new JTextField();
          compileTimeField.setBounds(100, 40, getWidth() - 100 - 40, 40);
          compileTimeField.setBackground(c2);
          compileTimeField.setFont(PX14);
          add(compileTimeField);
          
          runTimeField = new JTextField();
          runTimeField.setBounds(100, 80, getWidth() - 100 - 40, 40);
          runTimeField.setBackground(c2);
          runTimeField.setFont(PX14);
          add(runTimeField);

          JFileChooser fc = new JFileChooser();
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setDialogTitle("Select Working Directory");
          fc.setApproveButtonText("Select");

          ctDirComp = new TextComp(":", "Working Directory When Building", c1, c2, c3, ()->{
               fc.setCurrentDirectory(new File(ide.Screen.getFileView().getProjectPath()));
               int res = fc.showOpenDialog(Settings.this);
               if(res == JFileChooser.APPROVE_OPTION)
                    ctDirComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
          });
          ctDirComp.setBounds(getWidth() - 40, 40, 40, 40);
          ctDirComp.setFont(PX16);
          ctDirComp.setArc(0, 0);
          add(ctDirComp);

          rtDirComp = new TextComp(":", "Working Directory When Running", c1, c2, c3, ()->{
               fc.setCurrentDirectory(new File(ide.Screen.getFileView().getProjectPath()));
               int res = fc.showOpenDialog(Settings.this);
               if(res == JFileChooser.APPROVE_OPTION)
                    rtDirComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
          });
          rtDirComp.setBounds(getWidth() - 40, 80, 40, 40);
          rtDirComp.setFont(PX16);
          rtDirComp.setArc(0, 0);
          add(rtDirComp);

          applyComp = new Comp("Apply", c1, c2, c3, this::apply);
          applyComp.setBounds(0, getHeight() - 40, getWidth(), 40);
          applyComp.setFont(PX18);
          add(applyComp);
     }

     public void apply(){
          Screen.getFileView().getArgumentManager().compile_time_args = compileTimeField.getText();
          Screen.getFileView().getArgumentManager().run_time_args = runTimeField.getText();
          Screen.getFileView().getArgumentManager().compileDir = ctDirComp.getToolTipText();
          Screen.getFileView().getArgumentManager().runDir = rtDirComp.getToolTipText();
          Screen.getFileView().getArgumentManager().save();
     }

     @Override
     public void setVisible(boolean value){
     	if(value){
               compileTimeField.setText(Screen.getFileView().getArgumentManager().compile_time_args);
               runTimeField.setText(Screen.getFileView().getArgumentManager().run_time_args);
               ctDirComp.setToolTipText(Screen.getFileView().getArgumentManager().compileDir.equals("") ? "Working Directory When Building" : Screen.getFileView().getArgumentManager().compileDir);
               rtDirComp.setToolTipText(Screen.getFileView().getArgumentManager().runDir.equals("") ? "Working Directory When Running" : Screen.getFileView().getArgumentManager().runDir);
     	}
          super.setVisible(value);
     }
}
