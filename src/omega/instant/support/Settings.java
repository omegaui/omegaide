package omega.instant.support;
import omega.Screen;
import omega.comp.Comp;
import omega.comp.TextComp;
import omega.instant.support.Settings;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JDialog;

import static omega.settings.Screen.*;
import static omega.utils.UIManager.*;
public class Settings extends JDialog{
     public TextComp titleComp;
     public JTextField compileTimeField;
     public JTextField runTimeField;
     public TextComp ctDirComp;
     public TextComp rtDirComp;
     public TextComp closeComp;
     public Comp applyComp;

     private JPanel panel;
     private JScrollPane scrollPane;
     private LinkedList<ListComp> comps = new LinkedList<>();
     private int block;
     
     public Settings(Screen screen){
     	super(screen);
          setUndecorated(true);
          setLayout(null);
          setSize(540, 360);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(HIDE_ON_CLOSE);
          init();
     }

     public void init(){
     	closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(PX18);
          closeComp.setArc(0, 0);
          add(closeComp);

          titleComp = new TextComp("Preferences", TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, c2, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(PX16);
          titleComp.setArc(0, 0);
          titleComp.setClickable(false);
          add(titleComp);

          TextComp cLabel = new TextComp("Compile Time", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
          cLabel.setBounds(0, 40, 100, 40);
          cLabel.setFont(PX14);
          cLabel.setArc(0, 0);
          cLabel.setClickable(false);
          add(cLabel);
          
          TextComp rLabel = new TextComp("Run Time", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{});
          rLabel.setBounds(0, 80, 100, 40);
          rLabel.setFont(PX14);
          rLabel.setArc(0, 0);
          rLabel.setClickable(false);
          add(rLabel);

          compileTimeField = new JTextField();
          compileTimeField.setBounds(100, 40, getWidth() - 100 - 40, 40);
          compileTimeField.setBackground(c2);
          compileTimeField.setForeground(glow);
          compileTimeField.setFont(PX14);
          add(compileTimeField);
          
          runTimeField = new JTextField();
          runTimeField.setBounds(100, 80, getWidth() - 100 - 40, 40);
          runTimeField.setBackground(c2);
          runTimeField.setForeground(glow);
          runTimeField.setFont(PX14);
          add(runTimeField);

          JFileChooser fc = new JFileChooser();
          fc.setDialogTitle("Select Working Directory");

          ctDirComp = new TextComp(":", "Working Directory When Building", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
               fc.setCurrentDirectory(new File(omega.Screen.getFileView().getProjectPath()));
               int res = fc.showOpenDialog(Settings.this);
               if(res == JFileChooser.APPROVE_OPTION)
                    ctDirComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
          });
          ctDirComp.setBounds(getWidth() - 40, 40, 40, 40);
          ctDirComp.setFont(PX16);
          ctDirComp.setArc(0, 0);
          add(ctDirComp);

          rtDirComp = new TextComp(":", "Working Directory When Running", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
               fc.setCurrentDirectory(new File(omega.Screen.getFileView().getProjectPath()));
               int res = fc.showOpenDialog(Settings.this);
               if(res == JFileChooser.APPROVE_OPTION)
                    rtDirComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
          });
          rtDirComp.setBounds(getWidth() - 40, 80, 40, 40);
          rtDirComp.setFont(PX16);
          rtDirComp.setArc(0, 0);
          add(rtDirComp);

          applyComp = new Comp("Apply", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, this::apply);
          applyComp.setBounds(0, getHeight() - 40, getWidth(), 40);
          applyComp.setFont(PX18);
          add(applyComp);

          scrollPane = new JScrollPane(panel = new JPanel(null));
          scrollPane.setBounds(0, 120, getWidth(), getHeight() - 160);
          panel.setBackground(c2);
          add(scrollPane);
     }

     public void apply(){
          Screen.getFileView().getArgumentManager().compile_time_args = compileTimeField.getText();
          Screen.getFileView().getArgumentManager().run_time_args = runTimeField.getText();
          Screen.getFileView().getArgumentManager().compileDir = ctDirComp.getToolTipText();
          Screen.getFileView().getArgumentManager().runDir = rtDirComp.getToolTipText();
          Screen.getFileView().getArgumentManager().units.clear();
          comps.forEach(c->{
               if(c.isValidComp()){
                    Screen.getFileView().getArgumentManager().units.add(c.genUnit());
               }
          });
          Screen.getFileView().getArgumentManager().save();
     }

     public void genFields(){
          comps.forEach(panel::remove);
          comps.clear();
     	LinkedList<ListUnit> units = Screen.getFileView().getArgumentManager().units;
          block = 0;
          if(units.isEmpty()){
               ListComp l = new ListComp(this::genNext);
               l.setBounds(0, block, getWidth(), 40);
               comps.add(l);
               panel.add(l);
               block += 40;
          }
          else{
               units.forEach(u->{
                    ListComp l = new ListComp(u, this::genNext);
                    l.setBounds(0, block, getWidth(), 40);
                    comps.add(l);
                    panel.add(l);
                    block += 40;
               });
          }
          panel.setPreferredSize(new Dimension(540, block));
          scrollPane.getVerticalScrollBar().setValue(0);
          scrollPane.getVerticalScrollBar().setVisible(true);
          panel.repaint();
     }

     public void genNext(){
     	ListComp l = new ListComp(this::genNext);
          l.setBounds(0, block, getWidth(), 40);
          comps.add(l);
          panel.add(l);
          block += 40;
          
          panel.setPreferredSize(new Dimension(500, block));
          scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
          scrollPane.getVerticalScrollBar().setVisible(true);
          panel.repaint();
     }

     @Override
     public void setVisible(boolean value){
     	if(value){
               compileTimeField.setText(Screen.getFileView().getArgumentManager().compile_time_args);
               runTimeField.setText(Screen.getFileView().getArgumentManager().run_time_args);
               ctDirComp.setToolTipText(Screen.getFileView().getArgumentManager().compileDir.equals("") ? "Working Directory When Building" : Screen.getFileView().getArgumentManager().compileDir);
               rtDirComp.setToolTipText(Screen.getFileView().getArgumentManager().runDir.equals("") ? "Working Directory When Running" : Screen.getFileView().getArgumentManager().runDir);
     	     genFields();
     	}
          super.setVisible(value);
     }
}
