package omega.instant.support;
import omega.Screen;
import omega.utils.DataManager;
import omega.utils.ProjectDataBase;
import omega.comp.Comp;
import omega.comp.TextComp;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import org.fife.ui.rtextarea.RTextArea;
import java.io.File;
import javax.swing.JTextField;
import javax.swing.JDialog;

import static omega.utils.UIManager.*;
public class UniversalProjectWizard extends JDialog{
     private TextComp titleComp;
     private JTextField nameField;
     private TextComp rootComp;
     private RTextArea dirArea;
     private int mouseX;
     private int mouseY;
     public UniversalProjectWizard(Screen screen) {
     	super(screen);
          setModal(true);
          setLayout(null);
          setSize(500, 400);
          setLocationRelativeTo(null);
          setUndecorated(true);
          init();
     }

     public void init(){
     	TextComp closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(omega.settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);
          
          titleComp = new TextComp("Project Wizard", c1, c2, c3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(omega.settings.Screen.PX16);
          titleComp.setArc(0, 0);
          titleComp.setClickable(false);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
                    setLocation(e.getXOnScreen() - mouseX - 40, e.getYOnScreen() - mouseY);
               }
          });
          add(titleComp);

          nameField = new JTextField();
          nameField.setBounds(0, 40, getWidth() - 40, 40);
          nameField.setFont(omega.settings.Screen.PX18);
          nameField.setBackground(c2);
          nameField.setForeground(c3);
          ProjectWizard.addHoverEffect(nameField, "Enter Project Name (do not include \'" + File.separator + "\')");
          add(nameField);

          JFileChooser fc = new JFileChooser();
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setApproveButtonText("Select");
          fc.setDialogTitle("Choose Project Workspace Directory");

          rootComp = new TextComp(":", c1, c3, c2, ()->{
               int res = fc.showOpenDialog(UniversalProjectWizard.this);
               if(res == JFileChooser.APPROVE_OPTION)
                    rootComp.setToolTipText(fc.getSelectedFile().getAbsolutePath());
          });
          rootComp.setBounds(getWidth() - 40, 40, 40, 40);
          rootComp.setFont(omega.settings.Screen.PX18);
          rootComp.setArc(0, 0);
          add(rootComp);

          dirArea = new RTextArea();
          JScrollPane scrollPane = new JScrollPane(dirArea);
          scrollPane.setBounds(0, 80, getWidth(), getHeight() - 50 - 80);
          dirArea.setBackground(c2);
          dirArea.setForeground(c3);
          dirArea.setFont(omega.settings.Screen.PX14);
          ProjectWizard.addHoverEffect(dirArea, "Enter the names of the directories that you want to create.\nFor Example:\nbin\nsrc\nlib\nThey will be created when you create the Project!");
          add(scrollPane);

          Comp createBtn = new Comp("Create", c1, c2, c3, ()->{
               String proName = rootComp.getToolTipText() + File.separator + nameField.getText();
               LinkedList<String> dirs = new LinkedList<>();
               if(!dirArea.getText().equals(dirArea.getToolTipText())){
                    StringTokenizer tok = new StringTokenizer(dirArea.getText(), "\n");
                    while(tok.hasMoreTokens())
                         dirs.add(tok.nextToken());
               }
               if(create(dirs, proName)){
                    setVisible(false);
                    ProjectDataBase.genInfo(proName, true);
                    Screen.getScreen().loadProject(new File(proName));
               }
          });
          createBtn.setBounds(0, getHeight() - 50, getWidth(), 50);
          createBtn.setFont(omega.settings.Screen.PX18);
          createBtn.setArc(0, 0);
          add(createBtn);
     }

     public boolean create(LinkedList<String> dirs, String proName){
     	File pro = new File(proName);
          if(pro.exists()) {
               titleComp.setText("Project with this name already exists!");
               return false;
          }
          pro.mkdir();
          dirs.forEach(dir->{
               File x = new File(pro.getAbsolutePath() + File.separator + dir);
               x.mkdir();
          });
          return true;
     }

     @Override
     public void setVisible(boolean value) {
          if(value){
               rootComp.setToolTipText(DataManager.getProjectsHome());
          }
          super.setVisible(value);
     }     
}
