package omega.launcher;
import omega.utils.IconManager;
import omega.Screen;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import omega.utils.RecentsManager;
import java.net.URL;
import java.awt.Desktop;
import omega.utils.ToolMenu;
import java.io.File;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import static omega.utils.UIManager.*;
public class Launcher extends JFrame{
     private static final BufferedImage icon = getImage("/omega_ide_icon32.png");
     private static final BufferedImage drawIcon = getImage("/omega_ide_icon128.png");
	private final JPanel panel = new JPanel(null);
	private final JScrollPane scrollPane = new JScrollPane(panel);
     private TextComp closeComp;
     private TextComp imageComp;
     private TextComp textComp;
     private int mouseX;
     private int mouseY;
	public Launcher(){
          JPanel p = new JPanel(null);
          p.setBackground(c2);
          setContentPane(p);
		setUndecorated(true);
          setBackground(c2);
		setLayout(null);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage(icon);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                  setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
               }
          });
          addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
		init();
		setVisible(true);
	}

	private void init(){
		panel.setBackground(getBackground());
		scrollPane.setBounds(0, 220, getWidth(), getHeight() - 220);
		add(scrollPane);

		closeComp = new TextComp("X", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->System.exit(0));
		closeComp.setBounds(getWidth() - 40, 0, 40, 40);
          closeComp.setFont(omega.settings.Screen.PX18);
          closeComp.setArc(0, 0);
		add(closeComp);

          imageComp = new TextComp("", TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, c2, ()->{}){
               @Override
               public void paint(Graphics graphics){
               	Graphics2D g = (Graphics2D)graphics;
               	g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               	g.drawImage(drawIcon, 0, 0, null);
               }
          };
          imageComp.setBounds(getWidth()/2 - 64, 10, 128, 128);
          imageComp.setClickable(false);
          add(imageComp);

          textComp = new TextComp("Omega IDE " + Screen.VERSION, c2, c2, TOOLMENU_COLOR1, ()->{});
          textComp.setBounds(getWidth()/2 - 200, 140, 400, 50);
          textComp.setClickable(false);
          textComp.setFont(new Font("Ubuntu Mono", Font.BOLD, 40));
          textComp.setArc(0, 0);
          add(textComp);
		
		Door openDoor = new Door(File.separator + "ide" + File.separator + "Project" + File.separator + "Open", icon, ()->{
			if(Screen.getFileView().open("Project"))
				setVisible(false);
		});
		openDoor.setBounds(0, 0, getWidth(), 40);
          openDoor.setForeground(TOOLMENU_COLOR3);
		panel.add(openDoor);
		
          Door newDoor = new Door(File.separator + "ide" + File.separator + "Project" + File.separator + "New", icon, ()->{
               ToolMenu.projectWizard.setVisible(true);
               Screen.hideNotif();
          });
          newDoor.setBounds(0, 40, getWidth(), 40);
          newDoor.setForeground(TOOLMENU_COLOR3);
          panel.add(newDoor);
          
          Door bmanDoor = new Door(File.separator + "ide" + File.separator + "Non-Java Project"  + File.separator + "New", icon, ()->{
               ToolMenu.universalProjectWizard.setVisible(true);
               Screen.hideNotif();
          });
          bmanDoor.setBounds(0, 80, getWidth(), 40);
          bmanDoor.setForeground(TOOLMENU_COLOR3);
          panel.add(bmanDoor);
          
          Door stuckDoor = new Door(File.separator + "ide" + File.separator + "See Tutorial Videos"  + File.separator + "Stucked or need Help?", icon, ()->{
               try{
                    java.awt.Desktop.getDesktop().browse(new java.net.URL("https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg").toURI());
               }catch(Exception e){ System.err.println(e); }
               Screen.hideNotif();
          });
          stuckDoor.setBounds(0, 120, getWidth(), 40);
          stuckDoor.setForeground(TOOLMENU_COLOR3);
          panel.add(stuckDoor);

		//Creating Doors
		int y = 160;
		for(int i = RecentsManager.RECENTS.size() - 1; i >= 0; i--) {
			String path = RecentsManager.RECENTS.get(i);
			File file = new File(path);
			if(file.exists() && file.isDirectory()) {
				Door door = new Door(path, icon, ()->{
					setVisible(false);
					omega.Screen.getScreen().loadProject(file);
					omega.Screen.getScreen().setVisible(true);
				});
				door.setBounds(0, y, getWidth(), 40);
                    door.setForeground(TOOLMENU_COLOR1);
				panel.add(door);
				y += 40;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), y));
	}

     @Override
     public void setVisible(boolean value){
     	super.setVisible(value);
          repaint();
     }

	public static BufferedImage getImage(String path){
		return (BufferedImage)IconManager.getImageIcon(path).getImage();
	}
}
