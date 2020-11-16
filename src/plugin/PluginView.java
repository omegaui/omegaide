package plugin;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
public class PluginView extends JDialog{
	private static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);
	private static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	private static LinkedList<Door> doors = new LinkedList<>();
	private static int block;
	private static JPanel leftPanel = new JPanel(null);
	private static JScrollPane leftPane = new JScrollPane(leftPanel);
	public static final int LEFT_OFFSET = 300;
	private BufferedImage image;
	public static Plugin plugin;
	private static JButton closeBtn;

	//Information View
	private static JButton iconBtn;
	private static JTextField nameField;
	private static JTextField versionField;
	private static JTextArea descriptionArea;
	private static JScrollPane scrollPane = null;
	private static JTextField authorField;
	private static JTextField copyrightField;
	private static JButton mRBtn;
	private static JButton mLBtn;
	private static int index = 0;

	public PluginView(ide.Screen screen){
		super(screen);
		setTitle("Plugin Manager");
		setModal(false);
		setUndecorated(true);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				setVisible(false);
				dispose();
			}
		});
		try{
			image = (BufferedImage) ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon32.png"));
		}catch(Exception e){ System.err.println(e); }
		init();
	}

	public void init(){
		closeBtn = new JButton("X");
		closeBtn.setFont(PX16);
		closeBtn.setBounds(0, 0, 40, 40);
		closeBtn.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				setLocation(e.getXOnScreen() - 50, e.getYOnScreen() - 20);
			}
		});
		closeBtn.addActionListener((e)->{
			setVisible(false);
			dispose();
		});
		add(closeBtn);

		JTextField title = new JTextField("       Plugin Manager");
		title.setBounds(40, 0, LEFT_OFFSET - 40, 40);
		title.setEditable(false);
		title.setFont(PX16);
		add(title);

		leftPane.setBounds(0, 40, LEFT_OFFSET, getHeight());
		add(leftPane);

		if(((Color)javax.swing.UIManager.get("Button.background")).getRed() <= 53) {
			ide.utils.UIManager.setData(leftPane);
		}
		else {
			leftPane.setBackground(Color.WHITE);
		}
		leftPanel.setBackground(leftPane.getBackground());
		//Initializing the View
		iconBtn = new JButton();
		iconBtn.setBounds(LEFT_OFFSET, 0, 40, 40);
		add(iconBtn);

		nameField = new JTextField();
		nameField.setFont(PX16);
		nameField.setEditable(false);
		nameField.setBounds(iconBtn.getX() + 40, 0, getWidth() - iconBtn.getX() - 40 - 140, 40);
		add(nameField);

		versionField = new JTextField();
		versionField.setFont(PX16);
		versionField.setEditable(false);
		versionField.setBounds(nameField.getX() + nameField.getWidth(), 0, 140, 40);
		add(versionField);

		descriptionArea = new JTextArea() {
			@Override
			public void setText(String text) {
				super.setText(text);
				scrollPane.getHorizontalScrollBar().setValue(0);
			}
		};
		scrollPane = new JScrollPane(descriptionArea);
		descriptionArea.setFont(PX14);
		descriptionArea.setEditable(false);
		scrollPane.setBounds(LEFT_OFFSET, 40, getWidth() - LEFT_OFFSET, 100);
		add(scrollPane);

		authorField = new JTextField();
		authorField.setFont(PX14);
		authorField.setEditable(false);
		authorField.setBounds(LEFT_OFFSET, 140, 100, 40);
		add(authorField);

		copyrightField = new JTextField();
		copyrightField.setFont(PX14);
		copyrightField.setEditable(false);
		copyrightField.setBounds(authorField.getX() + authorField.getWidth(), 140, getWidth() - 80 - LEFT_OFFSET - authorField.getWidth(), 40);
		add(copyrightField);

		mRBtn = new JButton(">");
		mRBtn.setFont(PX14);
		mRBtn.setBounds(getWidth() - 40, 140, 40, 40);
		mRBtn.addActionListener((e)->{
			if(plugin == null || plugin.getImages() == null) return;
			if(!plugin.getImages().isEmpty()){
				if(index < plugin.getImages().size() - 1) index++;
				else if(index == plugin.getImages().size() - 1) index = 0;
			}
			repaint();
		});
		add(mRBtn);

		mLBtn = new JButton("<");
		mLBtn.setFont(PX14);
		mLBtn.setBounds(getWidth() - 80, 140, 40, 40);
		mLBtn.addActionListener((e)->{
			if(plugin == null || plugin.getImages() == null) return;
			if(!plugin.getImages().isEmpty()){
				if(index > 0) index--;
				else if(index == 0) index = plugin.getImages().size() - 1;
			}
			repaint();
		});
		add(mLBtn);
	}

	public void load(){
		doors.forEach(leftPanel::remove);
		doors.clear();
		block = 0;
		PluginManager.plugins.forEach((p)->{
			Door door = new Door(ide.Screen.getPluginManager().getPlug(p.getName()), p.getAuthor() + " - " + p.getVersion() + "/" + p.getName(), p.getImage() == null ? image : p.getImage(), ()->{
				plugin = p;
				showPlug();
			});
			door.setBounds(0, block, LEFT_OFFSET, 40);
			leftPanel.add(door);
			doors.add(door);
			block += 40;
		});
		leftPanel.setPreferredSize(new Dimension(LEFT_OFFSET, block));
		leftPane.getVerticalScrollBar().setVisible(true);
		leftPane.getVerticalScrollBar().setValue(leftPane.getVerticalScrollBar().getValue());
		repaint();
		if(doors.size() != 0){
			plugin = PluginManager.plugins.getFirst();
			showPlug();
		}
	}

	public void showPlug(){
		if(plugin == null) return;
		iconBtn.setIcon(new ImageIcon(plugin.getImage() == null ? image : plugin.getImage()));
		nameField.setText(plugin.getName());
		versionField.setText(plugin.getVersion());
		descriptionArea.setText(plugin.getDescription());
		descriptionArea.setCaretPosition(0);
		authorField.setText(plugin.getAuthor());
		copyrightField.setText(plugin.getCopyright());
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		leftPanel.repaint();
		closeBtn.repaint();
		mRBtn.repaint();
		mLBtn.repaint();
		if(plugin == null || plugin.getImages() == null) return;
		if(!plugin.getImages().isEmpty()){
			g.drawImage(plugin.getImages().get(index), LEFT_OFFSET, 180, getWidth() - LEFT_OFFSET, getHeight() - 180, this);
		}
	}

	@Override
	public void setVisible(boolean value){
		if(value) load();
		else ide.Screen.getPluginManager().save();
		super.setVisible(value);
	}
}
