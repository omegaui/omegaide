package omega.ui;
import java.awt.*;
import java.awt.event.*;
import omega.comp.*;
import omega.Screen;
import omega.utils.UIManager;
import omega.utils.DataManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SDKSelector extends JDialog {
	private JScrollPane scrollPane;
	private LinkedList<TextComp> boxs = new LinkedList<>();
	private JPanel panel = new JPanel(null);
	private String selection = null;
	private static Dimension dimension;
	private int block;
	private int pressX;
	private int pressY;
	public SDKSelector(JFrame f) {
		super(f, true);
		setUndecorated(true);
		setSize(500, 400);
		setLocationRelativeTo(f);
		setLayout(null);
          
		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
		closeComp.setBounds(0, 0, 40, 40);
		closeComp.setFont(PX16);
		closeComp.setArc(0, 0);
		add(closeComp);
          
		TextComp titleComp = new TextComp("Select Your JDK Environment", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, null);
		titleComp.setBounds(40, 0, getWidth() - 40, 40);
		titleComp.setFont(PX16);
		titleComp.setClickable(false);
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
				setLocation(e.getXOnScreen() - pressX - 40, e.getYOnScreen() - pressY);
			}
		});
		add(titleComp);
		
		scrollPane = new JScrollPane(panel){
			@Override
			public void paint(Graphics graphics){
				if(boxs.isEmpty()){
					Graphics2D g = (Graphics2D)graphics;
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setColor(c2);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(TOOLMENU_COLOR3);
					g.setFont(PX14);
					g.drawString("No JDKs found at the specified path!", getWidth()/2 - g.getFontMetrics().stringWidth("No JDKs found at the specified path!")/2,
     					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() - 15);
				}
				else{
					super.paint(graphics);
				}
			}
		};
		scrollPane.setBounds(0, 40, getWidth(), getHeight());
          panel.setBackground(c2);
		add(scrollPane);
	}
	private void resolvePath() {
		boxs.forEach(box->panel.remove(box));
		boxs.clear();
		block = 0;
		selection = null;
		String pathJava = DataManager.getPathToJava();
		if(pathJava == null)
			return;
		File[] files = new File(pathJava).listFiles();
		if(files == null) {
			omega.Screen.setStatus("No JDKs found in \"" + pathJava + "\"", 10);
			return;
		}
		for(File file : files) {
			if(file.isFile())
				continue;
			String release = getRelease(file.getAbsolutePath());
			if(release != null) {
				TextComp box = new TextComp(file.getName() + "(" + release + ")", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
					selection = file.getAbsolutePath();
					SDKSelector.this.setVisible(false);
				});
				box.setBounds(0, block, getWidth(), 30);
				box.alignX = 5;
				box.setArc(0, 0);
				box.setFont(PX14);
				panel.add(box);
				boxs.add(box);
				block += 30;
			}
		}
		panel.setPreferredSize(new Dimension(getWidth(), block));
	}
	
	private String getRelease(String path) {
		File releaseFile = new File(path + File.separator + "release");
		if(!releaseFile.exists()) return null;
			try{
			Scanner reader = new Scanner(releaseFile);
			while(reader.hasNextLine()){
				String s = reader.nextLine();
				String cmd = "JAVA_VERSION=";
				if(s.startsWith(cmd)){
					s = s.substring(s.indexOf(cmd) + cmd.length());
					s = s.substring(s.indexOf('\"') + 1, s.lastIndexOf('\"'));
					reader.close();
					return s;
				}
			}
			reader.close();
		}
		catch(Exception e){
			return null;
		}
		return null;
	}
	
	public String getSelection() {
		return selection;
	}

     @Override
     public void paint(Graphics g){
     	super.paint(g);
          scrollPane.repaint();
     }
     
	@Override
	public void setVisible(boolean value) {
		if(value) {
			resolvePath();
		}
		super.setVisible(value);
	}
}
