package omega.utils;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import omega.comp.TextComp;
import omega.Screen;
import javax.swing.JDialog;
import static omega.utils.UIManager.*;
public class WorkspaceSelector extends JDialog{
     private int mouseX;
     private int mouseY;
     public WorkspaceSelector(Screen screen){
     	super(screen);
          setUndecorated(true);
          JPanel panel = new JPanel(null);
          panel.setBackground(c2);
          setContentPane(panel);
          setLayout(null);
          setBackground(c2);
          setTitle("Select Workspace Directory");
          setModal(true);
          setSize(500, 150);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(DISPOSE_ON_CLOSE);
          init();
     }

     public void init(){
          TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->dispose());
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(omega.settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp titleComp = new TextComp("Select Workspace Directory", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
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
          titleComp.setFont(omega.settings.Screen.PX16);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          add(titleComp);
          
          JTextField textField = new JTextField(DataManager.getWorkspace().equals("") ? "e.g : user.home/Documents/Omega Projects" : DataManager.getWorkspace());
          textField.setBounds(20, 50, getWidth() - 40, 40);
          textField.setFont(omega.settings.Screen.PX18);
          textField.setBackground(c2);
          textField.setForeground(glow);
          textField.setEditable(false);
          add(textField);

          JFileChooser chooser = new JFileChooser();
          chooser.setMultiSelectionEnabled(false);
          chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          chooser.setApproveButtonText("Select");
          chooser.setDialogTitle("Choose only one directory");

          TextComp chooseComp = new TextComp("select", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
               int res = chooser.showOpenDialog(WorkspaceSelector.this);
               if(res == JFileChooser.APPROVE_OPTION){
                    DataManager.setWorkspace(chooser.getSelectedFile().getAbsolutePath());
                    textField.setText(DataManager.getWorkspace());
                    setTitle("Lets Proceed Forward");
                    titleComp.setText(getTitle());
                    titleComp.setClickable(true);
               }
          });
          chooseComp.setBounds(getWidth()/2 - 30, 91, 60, 30);
          chooseComp.setFont(omega.settings.Screen.PX16);
          add(chooseComp);
     }
}
