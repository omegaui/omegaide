package search;
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

import ide.Screen;
import launcher.Door;
public class SearchWindow extends JDialog{
	private JPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<File> files;
	private int blocks = -40;
	private JTextField field;
	private LinkedList<Door> doors;
	private int pointer;
	private BufferedImage image;
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
		ide.utils.UIManager.setData(field);
		ide.utils.UIManager.setData(panel);
		image = launcher.Launcher.getImage("/file.png");
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
		tree.FileTree.sort(this.files);
	}

	public void load(File f){
		File[] files = f.listFiles();
		if(files == null || files.length == 0) return;
		for(File file : files){
			if(file.isDirectory()) load(file);
			else this.files.add(file);
		}
	}
}
