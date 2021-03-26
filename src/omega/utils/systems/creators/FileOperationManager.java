package omega.utils.systems.creators;
import omega.utils.UIManager;
import java.awt.Color;
import omega.comp.NoCaretField;

import omega.Screen;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
import omega.utils.Editor;
import java.io.File;
import java.util.LinkedList;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JDialog;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import static omega.utils.UIManager.*;

public class FileOperationManager extends JDialog {
     private static TextComp titleComp;
     private static TextComp closeComp;
     private static NoCaretField nameField;
     private static TextComp actionComp;
     private static JScrollPane scrollPane;
     private static RSyntaxTextArea logArea;
     private static Thread operationThread;
     private static LinkedList<File> files;
     private static volatile boolean duplicateStructures = false;
     private int mouseX;
     private int mouseY;
     
     static {
          logArea  = new RSyntaxTextArea(){
               @Override
               public void append(String x){
               	super.append(x);
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
               }
          };
          scrollPane = new JScrollPane(logArea);
          logArea.setSyntaxEditingStyle(logArea.SYNTAX_STYLE_JAVA);
          Editor.getTheme().apply(logArea);
     }

	public FileOperationManager(Screen window) {
		super(window, true);
          setTitle("File Operation Manager");
          setUndecorated(true);
		setLayout(null);
		setAlwaysOnTop(true);
          setSize(400, 120);
          setLocationRelativeTo(null);
		init();
	}

     public void init(){
     	titleComp = new TextComp("File Operation Manager", c1, c2, c3, ()->{});
          titleComp.setBounds(40, 0, getWidth() - 40, 40);
          titleComp.setFont(omega.settings.Screen.PX16);
          titleComp.setArc(0, 0);
          titleComp.setClickable(false);
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
          add(titleComp);

          closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
          closeComp.setBounds(0, 0, 40, 40);
          closeComp.setFont(omega.settings.Screen.PX16);
          closeComp.setArc(0, 0);
          add(closeComp);

          nameField = new NoCaretField("", "type file name", UIManager.isDarkMode() ? c1 : Color.BLACK, c2, c3);
          nameField.setBounds(0, 40, getWidth(), 40);
          nameField.setFont(omega.settings.Screen.PX14);
          add(nameField);
          addKeyListener(nameField);

          actionComp = new TextComp("", c1, c2, c3, ()->{});
          actionComp.setBounds(0, 80, getWidth(), 40);
          actionComp.setFont(omega.settings.Screen.PX16);
          actionComp.setArc(0, 0);
          add(actionComp);
     }

     public static void rename(String title, String actionText, File file0){
     	final FileOperationManager fom = Screen.getProjectView().getFileOperationManager();
          fom.nameField.setText(file0.getName());
          fom.titleComp.setText(title);
          fom.actionComp.setText(actionText);
          fom.actionComp.setRunnable(()->{
               File file = new File(file0.getParentFile().getAbsolutePath() + File.separator + fom.nameField.getText());
               if(!file0.isDirectory()){
                    move(file0, file);
               }
               else{
                   file.mkdir();
                   File[] files = file0.listFiles();
                   for(File fx : files){
                         move(fx, file);
                   }
                   file0.delete();
               }
               fom.setVisible(false);
          });
          fom.setVisible(true);
     }

     public static void move(File target, File destination){
          if(operationThread != null && operationThread.isAlive())
               return;
     	copy(target, destination);
          if(duplicateStructures) return;
          while(operationThread.isAlive());
          if(!target.isDirectory()){
               target.delete();
               Screen.getProjectView().reload();
               return;
          }
          try{
               Editor.deleteDir(target);
          }catch(Exception e){ System.err.println(e); }
          Screen.getProjectView().reload();
     }

     public static void copy(File target, File destination){
          if(operationThread != null && operationThread.isAlive())
               return;
          duplicateStructures = false;
          operationThread = new Thread(()->{
               try{
                    if(!target.isDirectory()){
                         if(destination.isDirectory())
                              copyFileToDir(target, destination);
                         else 
                              copyFile(target, destination);
                         Screen.getProjectView().reload();
                         return;
                    }
                    logArea.setText("");
                    Screen.getScreen().getOperationPanel().addTab("File Operation", scrollPane, ()->operationThread.stop());
                    String xpath = destination.getAbsolutePath() + File.separator + target.getName();
                    new File(xpath).mkdir();
                    files = new LinkedList<>();
                    loadStructure(files, target);
                    files.forEach(file -> {
                         if(file.isDirectory()){
                              final String PATH = file.getAbsolutePath();
                              String path = PATH;
                              path = destination.getAbsolutePath() + File.separator;
                              path += target.getName() + File.separator;
                              path += PATH.substring(PATH.lastIndexOf(target.getName()) + target.getName().length() + 1);
                              createParentDir(path, destination.getAbsolutePath());
                              File x = new File(path);
                              if(!x.exists()){
                                   x.mkdir();
                                   logArea.append("\nCreating Dir : \"" + path + "\"");
                              }
                         }
                    });
                    files.forEach(file -> {
                         if(!file.isDirectory()){
                              final String PATH = file.getAbsolutePath();
                              String path = PATH;
                              path = destination.getAbsolutePath() + File.separator;
                              path += target.getName() + File.separator;
                              path += PATH.substring(PATH.lastIndexOf(target.getName()) + target.getName().length() + 1);
                              logArea.append("\nCreating File : \"" + path + "\"");
                              copyFile(file, new File(path));
                         }
                    });
                    Screen.getProjectView().reload();
               }catch(Exception e){ duplicateStructures = true; logArea.append("\n\nDuplicate Directory Names Found! Process Canceled"); }
          });
          operationThread.start();
     }

     public static void createParentDir(String path, String parent){
          String rpath = path.substring(parent.length() + 1);
          StringTokenizer tok = new StringTokenizer(rpath, File.separator);
          rpath = parent;
          while(tok.hasMoreTokens()){
               rpath += File.separator + tok.nextToken();
               File f = new File(rpath);
               if(!f.exists()){
                    f.mkdir();
                    logArea.append("\nCreating Dir : \"" + rpath + "\"");
               }
          }
     }

     public static void loadStructure(LinkedList<File> files, File dir){
     	File[] F = dir.listFiles();
          if(F == null || F.length == 0) return;
          for(File f : F){
               if(f.isDirectory()){
                    files.add(f);
                    loadStructure(files, f);
               }
               else
                    files.add(f);
          }
     }

     public static void copyFileToDir(File file, File targetDir) {
          try {
               InputStream in = new FileInputStream(file);
               OutputStream out = new FileOutputStream(targetDir.getAbsolutePath() + File.separator + file.getName());
               while(in.available() > 0)
                    out.write(in.read());
               in.close();
               out.close();
          }catch(Exception e) { logArea.append("\n" + e); }
     }
     
     public static void copyFile(File file, File target) {
          try {
               InputStream in = new FileInputStream(file);
               OutputStream out = new FileOutputStream(target);
               while(in.available() > 0)
                    out.write(in.read());
               in.close();
               out.close();
          }catch(Exception e) { e.printStackTrace(); }
     }
}
