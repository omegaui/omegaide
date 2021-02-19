package omega.search;
import omega.launcher.Door;
import omega.tabPane.IconManager;
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
public class SearchWindow extends JDialog{
	private JPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<File> files;
	private int blocks = -40;
	private JTextField field;
	private LinkedList<Door> doors;
	private int pointer;
	private BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	private Screen screen;
	public SearchWindow(Screen f){
		super(f, false);
		this.screen = f;
		setTitle("Search Files across the Project");
		setIconImage(f.getIconImage());
		setSize(500, 300);
		setLocationRelativeTo(null);
          setResizable(false);
		add(scrollPane = new JScrollPane(panel = new JPanel(null)), BorderLayout.CENTER);
		files = new LinkedList<>();
		doors = new LinkedList<>();
		add(field = new JTextField(), BorderLayout.NORTH);
		field.addKeyListener(new KeyAdapter() {
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
		field.setFont(omega.settings.Screen.PX16);
          field.setBackground(omega.utils.UIManager.c2);
          field.setForeground(omega.utils.UIManager.c3);
		omega.utils.UIManager.setData(panel);
          
          //Creating File Image of size 32, 32 here
          Graphics graphics = image.getGraphics();
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(IconManager.getBackground());
          g.fillRoundRect(0, 0, 32, 32, 5, 5);
          g.setColor(IconManager.getForeground());
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
				Door door = new Door(file.getAbsolutePath(), image, ()->{
					setVisible(false);
					screen.loadFile(file);
				});
				door.setBounds(0, blocks += 40, getWidth(), 40);
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
