package ide.utils;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import settings.comp.TextComp;
import ide.Screen;
import javax.swing.JDialog;
import static ide.utils.UIManager.*;
public class ProjectsHomeSelector extends JDialog{
     private int mouseX;
     private int mouseY;
     public ProjectsHomeSelector(Screen screen){
     	super(screen);
          setUndecorated(true);
          JPanel panel = new JPanel(null);
          panel.setBackground(c2);
          setContentPane(panel);
          setLayout(null);
          setBackground(c2);
          setTitle("Select Projects Home");
          setModal(true);
          setSize(500, 150);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(DISPOSE_ON_CLOSE);
          init();
     }

     public void init(){
          TextComp closeComp = new TextComp("x", c1, c2, c3, ()->dispose());
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp titleComp = new TextComp("Select Projects Home Directory", c1, c2, c3, ()->{
               setVisible(false);
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - mouseX - 40, e.getYOnScreen() - mouseY);
               }
          });
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(settings.Screen.PX16);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          add(titleComp);
          
          JTextField textField = new JTextField(DataManager.getProjectsHome().equals("") ? "e.g : user.home/Documents/Omega Projects" : DataManager.getProjectsHome());
          textField.setBounds(20, 50, getWidth() - 40, 40);
          textField.setFont(settings.Screen.PX18);
          textField.setBackground(c2);
          textField.setForeground(c3);
          textField.setEditable(false);
          add(textField);

          JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);
          chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          chooser.setApproveButtonText("Select");
          chooser.setDialogTitle("Choose only one directory");

          TextComp chooseComp = new TextComp("select", c1, c2, c3, ()->{
               int res = chooser.showOpenDialog(ProjectsHomeSelector.this);
               if(res == JFileChooser.APPROVE_OPTION){
                    DataManager.setProjectsHome(chooser.getSelectedFile().getAbsolutePath());
                    textField.setText(DataManager.getProjectsHome());
                    setTitle("Lets Proceed Forward");
                    titleComp.setText(getTitle());
                    titleComp.setClickable(true);
               }
          });
          chooseComp.setBounds(getWidth()/2 - 30, 91, 60, 30);
          chooseComp.setFont(settings.Screen.PX16);
          add(chooseComp);
     }
}
