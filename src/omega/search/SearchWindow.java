package omega.search;
import omega.tree.*;
import java.awt.Color;
import omega.comp.NoCaretField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import omega.launcher.Door;
import omega.utils.IconManager;
import omega.utils.UIManager;
import omega.Screen;
import omega.tree.FileTree;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SearchWindow extends JDialog{
	private JPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<File> files;
	private int blocks = -40;
	private NoCaretField field;
	private LinkedList<Door> doors;
	private int pointer;
     private BufferedImage textImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
     private BufferedImage imageImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
     private BufferedImage allImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	private Screen screen;

     private int pressX;
     private int pressY;
     
	public SearchWindow(Screen f){
		super(f, false);
		this.screen = f;
          files = new LinkedList<>();
          doors = new LinkedList<>();
          setLayout(null);
          setUndecorated(true);
		setTitle("Search Files across the Project");
		setIconImage(f.getIconImage());
		setSize(500, 300);
		setLocationRelativeTo(null);
          setResizable(false);
          
		scrollPane = new JScrollPane(panel = new JPanel(null));
          scrollPane.setBackground(c2);
          scrollPane.setBounds(0, 60, getWidth(), getHeight() - 60);
          add(scrollPane);

          TextComp titleComp = new TextComp(getTitle(), TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2, null);
          titleComp.setBounds(0, 0, getWidth() - 60, 30);
          titleComp.setClickable(false);
          titleComp.setFont(PX14);
          titleComp.setArc(0, 0);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
               	pressX = e.getX();
                    pressY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
               	setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);

          TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(getWidth() - 30, 0, 30, 30);
          closeComp.setFont(PX14);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp reloadComp = new TextComp("#", "Click to Reload File Tree", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->cleanAndLoad(new File(Screen.getFileView().getProjectPath())));
          reloadComp.setBounds(getWidth() - 60, 0, 30, 30);
          reloadComp.setArc(0, 0);
          reloadComp.setFont(PX14);
          add(reloadComp);

          field = new NoCaretField("", "Type File Name", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
          field.setBounds(0, 30, getWidth(), 30);
          field.setFont(PX16);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!doors.isEmpty()) {
					if(e.getKeyCode() == KeyEvent.VK_UP && pointer > 0) {
						doors.get(pointer).set(false);
						doors.get(--pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - 40);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN && pointer < doors.size()) {
						doors.get(pointer).set(false);
						doors.get(++pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + 40);
					}
					else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						setVisible(false);
						screen.loadFile(new File(doors.get(pointer).getPath()));
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER)
					list(field.getText());
			}
		});
          add(field);
          addKeyListener(field);
          
		omega.utils.UIManager.setData(panel);
          
          //Creating File Image of size 32, 32 here
          writeImage(textImage, TOOLMENU_COLOR2, c2);
          writeImage(imageImage, TOOLMENU_COLOR3, c2);
          writeImage(allImage, IconManager.getBackground(), IconManager.getForeground());
	}

     public void writeImage(BufferedImage image, Color f, Color b){
          Graphics graphics = image.getGraphics();
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(f);
          g.fillRoundRect(0, 0, 32, 32, 5, 5);
          g.setColor(b);
          g.fillRect(4, 4, 4, 2);
          g.fillRect(8, 4, 4, 2);
          g.fillRect(6, 8, 4, 2);
          g.fillRect(10, 8, 4, 2);
          g.fillRect(4, 12, 4, 2);
          g.fillRect(8, 12, 4, 2);
          g.dispose();
     }

	public void list(String text){
		doors.forEach(panel::remove);
		doors.clear();
		blocks = -40;
		files.forEach(file->{
			if(file.getName().contains(text)){
                    String ext = file.getName();
                    if(ext.contains("."))
                         ext = ext.substring(ext.lastIndexOf('.'));
                    BufferedImage image = switch(ext){
                         case ".txt", ".groovy", ".java", ".xml", "properties", ".rs", ".py", ".js", ".html", ".sh", ".c", ".cpp" -> textImage;
                         case ".png", ".jpg", ".bmp", ".jpeg" -> imageImage;
                         default -> allImage;
                    };
				Door door = new Door(file.getAbsolutePath(), image, ()->{
					setVisible(false);
					screen.loadFile(file);
				});
				door.setBounds(0, blocks += 40, getWidth(), 40);
                    door.setToolTipText(file.getAbsolutePath());
                    door.setBackground(c2);
                    door.setForeground(switch(ext){
                         case ".txt", ".groovy", ".java", ".xml", "properties", ".rs", ".py", ".js", ".html", ".sh", ".c", ".cpp" -> TOOLMENU_COLOR2;
                         case ".png", ".jpg", ".bmp", ".jpeg" -> TOOLMENU_COLOR3;
                         default -> c3;
                    });
				panel.add(door);
				doors.add(door);
			}
		});
		panel.setPreferredSize(new Dimension(getWidth(), blocks));
		scrollPane.repaint();
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.getVerticalScrollBar().repaint();
		repaint();
		if(!doors.isEmpty()) {
			doors.get(pointer = 0).set(true);
		}
		doLayout();
	}

	public void cleanAndLoad(File f){
		this.files.clear();
		load(f);
		omega.tree.FileTree.sort(this.files);
	}

	public void load(File f){
		File[] files = f.listFiles();
		if(files == null || files.length == 0) return;
		for(File file : files){
			if(file.isDirectory()) load(file);
			else if(!file.getName().endsWith(".class")) this.files.add(file);
		}
	}
}
