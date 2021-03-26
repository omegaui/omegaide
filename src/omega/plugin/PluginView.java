package omega.plugin;
import java.awt.Graphics2D;
import java.awt.event.*;
import omega.utils.UIManager;
import omega.tabPane.IconManager;
import omega.Screen;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import omega.comp.TextComp;
import java.awt.image.BufferedImage;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.util.LinkedList;
import java.awt.Font;
import javax.swing.JDialog;

import static omega.utils.UIManager.*;
public class PluginView extends JDialog{
	private static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);
	private static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	private static LinkedList<Door> doors = new LinkedList<>();
	private static int block;
	private static JPanel leftPanel = new JPanel(null);
	private static JScrollPane leftPane = new JScrollPane(leftPanel);
	public static final int LEFT_OFFSET = 300;
     public static Plugin plugin;
	private BufferedImage image;
     private BufferedImage imageX;
	private static TextComp closeBtn;

	//Information View
	private static TextComp iconBtn;
	private static JTextField nameField;
	private static JTextField versionField;
	private static JTextArea descriptionArea;
	private static JScrollPane scrollPane = null;
	private static JTextField authorField;
	private static JTextField copyrightField;
	private static TextComp mRBtn;
	private static TextComp mLBtn;
	private static int index = 0;
     private int mouseX;
     private int mouseY;

	public PluginView(omega.Screen screen){
		super(screen);
          JPanel panel = new JPanel();
          panel.setBackground(c2);
          setContentPane(panel);
		setTitle("Plugin Manager");
		setIconImage(screen.getIconImage());
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
		image = IconManager.ideImage;
		init();
	}

	public void init(){
		closeBtn = new TextComp("X", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{
               setVisible(false);
               dispose();
	     });
		closeBtn.setFont(PX16);
		closeBtn.setBounds(0, 0, 40, 40);
		closeBtn.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				setLocation(e.getXOnScreen() - 50, e.getYOnScreen() - 20);
			}
		});
          closeBtn.setArc(0, 0);
		add(closeBtn);

		TextComp title = new TextComp("Plugin Manager", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{});
		title.setBounds(40, 0, LEFT_OFFSET - 40, 40);
		title.setFont(PX16);
          title.setArc(0, 0);
          title.setClickable(false);
          title.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - mouseX - 40, e.getYOnScreen() - mouseY);
               }
          });
          title.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
               }
          });
		add(title);

		leftPane.setBounds(0, 40, LEFT_OFFSET, getHeight());
		add(leftPane);

		if(omega.utils.UIManager.isDarkMode()) {
			omega.utils.UIManager.setData(leftPane);
		}
		else
			leftPane.setBackground(Color.WHITE);
		leftPanel.setBackground(leftPane.getBackground());
          
		//Initializing the View
		iconBtn = new TextComp("", c1, c2, c3, null){
               @Override
               public void draw(Graphics2D g){
               	g.drawImage(imageX, 0, 0, 40, 40, this);
               }
	     };
		iconBtn.setBounds(LEFT_OFFSET, 0, 40, 40);
          iconBtn.setClickable(false);
          iconBtn.setArc(0, 0);
		add(iconBtn);

		nameField = new JTextField();
		nameField.setFont(PX16);
		nameField.setEditable(false);
		nameField.setBounds(iconBtn.getX() + 40, 0, getWidth() - iconBtn.getX() - 40 - 140, 40);
          nameField.setBackground(omega.utils.UIManager.c2);
          nameField.setForeground(omega.utils.UIManager.c3);
		add(nameField);

		versionField = new JTextField();
		versionField.setFont(PX16);
		versionField.setEditable(false);
		versionField.setBounds(nameField.getX() + nameField.getWidth(), 0, 100, 40);
          versionField.setBackground(omega.utils.UIManager.c2);
          versionField.setForeground(omega.utils.UIManager.c3);
		add(versionField);
		
		TextComp removeBtn = new TextComp("-", omega.utils.UIManager.c1, omega.utils.UIManager.c3, omega.utils.UIManager.c2, ()->{
               if(plugin == null) return;
               String fileName = omega.Screen.getPluginManager().getPlug(plugin.getName()).fileName;
               int res = JOptionPane.showConfirmDialog(PluginView.this, "Do you want to uninstall " + plugin.getName() + "?\nThis Operation requires IDE restart!", "Plugin Manager", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
               if(res == JOptionPane.OK_OPTION) {
                    new java.io.File("omega-ide-plugins" + java.io.File.separator + fileName).delete();
               }
	     });
		removeBtn.setToolTipText("Click to Uninstall this Plugin");
		removeBtn.setFont(PX16);
		removeBtn.setBounds(getWidth() - 40, 0, 40, 40);
          removeBtn.setArc(0, 0);
		add(removeBtn);

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
          descriptionArea.setBackground(omega.utils.UIManager.c3);
          descriptionArea.setForeground(omega.utils.UIManager.c2);
		scrollPane.setBounds(LEFT_OFFSET, 40, getWidth() - LEFT_OFFSET, 100);
		add(scrollPane);

		authorField = new JTextField();
		authorField.setFont(PX14);
		authorField.setEditable(false);
		authorField.setBounds(LEFT_OFFSET, 140, 100, 40);
          authorField.setBackground(omega.utils.UIManager.c2);
          authorField.setForeground(omega.utils.UIManager.c3);
		add(authorField);

		copyrightField = new JTextField();
		copyrightField.setFont(PX14);
		copyrightField.setEditable(false);
		copyrightField.setBounds(authorField.getX() + authorField.getWidth(), 140, getWidth() - 80 - LEFT_OFFSET - authorField.getWidth(), 40);
          copyrightField.setBackground(omega.utils.UIManager.c2);
          copyrightField.setForeground(omega.utils.UIManager.c3);
		add(copyrightField);

		mRBtn = new TextComp(">", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{
               if(plugin == null || plugin.getImages() == null) return;
               if(!plugin.getImages().isEmpty()){
                    if(index < plugin.getImages().size() - 1) index++;
                    else if(index == plugin.getImages().size() - 1) index = 0;
               }
               repaint();
	     });
		mRBtn.setFont(PX14);
		mRBtn.setBounds(getWidth() - 40, 140, 40, 40);
          mRBtn.setArc(0, 0);
		add(mRBtn);

		mLBtn = new TextComp("<", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{
               if(plugin == null || plugin.getImages() == null) return;
               if(!plugin.getImages().isEmpty()){
                    if(index > 0) index--;
                    else if(index == 0) index = plugin.getImages().size() - 1;
               }
               repaint();
	     });
		mLBtn.setFont(PX14);
		mLBtn.setBounds(getWidth() - 80, 140, 40, 40);
          mLBtn.setArc(0, 0);
		add(mLBtn);
	}

	public void load(){
		doors.forEach(leftPanel::remove);
		doors.clear();
		block = 0;
		PluginManager.plugins.forEach((p)->{
			Door door = new Door(omega.Screen.getPluginManager().getPlug(p.getName()), p.getAuthor() + " - " + p.getVersion() + "/" + p.getName(), p.getImage() == null ? image : p.getImage(), ()->{
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
		imageX = plugin.getImage() == null ? image : plugin.getImage();
          iconBtn.repaint();
		nameField.setText(plugin.getName());
		versionField.setText(plugin.getVersion());
		descriptionArea.setText(plugin.getDescription());
		descriptionArea.setCaretPosition(0);
		authorField.setText(plugin.getAuthor());
		copyrightField.setText(plugin.getCopyright());
		index = 0;
		repaint();
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
		else omega.Screen.getPluginManager().save();
		super.setVisible(value);
	}
}
