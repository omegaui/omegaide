package omega.plugin;
import omega.utils.UIManager;
import java.awt.Color;
import java.util.Scanner;
import java.awt.event.MouseEvent;
import omega.Screen;
import javax.swing.JOptionPane;
import java.io.File;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import omega.comp.NoCaretField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.LinkedList;
import javax.swing.JFrame;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class PluginStore extends JFrame{
	private static LinkedList<Bucket> buckets = new LinkedList<>();
	private static LinkedList<Bucket> currentBuckets = new LinkedList<>();
	private static LinkedList<PlugInfo> infos;
	private JScrollPane scrollPane;
	private JPanel panel;
	private NoCaretField searchField;
	private TextComp messageComp;
	
	private int block;
	private int pressX;
	private int pressY;
	private volatile boolean ready;
	private Downloader downloader;
	public PluginStore(){
		super("Plugin Store");
		setUndecorated(true);
		setSize(600, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(Screen.getScreen().getIconImage());
		setLayout(null);
		init();
	}
	public void init(){
		downloader = new Downloader(this);
		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBounds(0, 60, getWidth(), getHeight() - 85);
		panel.setBackground(c2);
		add(scrollPane);
		TextComp titleComp = new TextComp("Plugin Store -- Omega IDE", "", c1, c3, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 60, 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
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
		TextComp reloadComp = new TextComp("#", "Click to reload plugins information", c1, c2, c3, this::refresh);
		reloadComp.setBounds(getWidth() - 60, 0, 30, 30);
		reloadComp.setFont(PX14);
		reloadComp.setArc(0, 0);
		add(reloadComp);
		TextComp closeComp = new TextComp("x", c1, c2, c3, ()->setVisible(false));
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);
		searchField = new NoCaretField("", "type plugin name", UIManager.isDarkMode() ? c1 : Color.BLACK, c2, c3);
		searchField.setBounds(0, 30, getWidth(), 30);
		searchField.setFont(PX16);
		searchField.setOnAction(()->{
			search(searchField.getText());
		});
		add(searchField);
		addKeyListener(searchField);
		messageComp = new TextComp("Click Any Plugin to Download it", c1, c3, c2, null);
		messageComp.setBounds(0, getHeight() - 25, getWidth(), 25);
		messageComp.setFont(PX14);
		messageComp.setClickable(false);
		messageComp.setArc(0, 0);
		add(messageComp);
	}
	public void search(String text){
		setTitle("Plugin Store");
		currentBuckets.forEach(panel::remove);
		currentBuckets.clear();
		block = 0;
		for(Bucket b : buckets){
			if(!b.name.contains(text) && !b.desc.contains(text))
				continue;
			b.setBounds(0, block, getWidth(), 140);
			currentBuckets.add(b);
			panel.add(b);
			block += 140;
		}
		panel.setPreferredSize(new Dimension(getWidth() - 5, block));
		repaint();
	}
	public void refresh(){
		File list = download(".plugset");
		if(list == null) {
			setTitle("Error Downloading Plugin List");
			return;
		}
		setTitle("Plugin Store");
		ready = true;
		buckets.forEach(panel::remove);
		buckets.clear();
		currentBuckets.clear();
		block = 0;
		infos = PlugInfoWriter.read(list);
		list.delete();
		for(PlugInfo i : infos){
			Bucket bucket = new Bucket(i.name, i.size, i.desc, (e)->{
				int res = JOptionPane.showConfirmDialog(PluginStore.this, "Do you want to Download " + i.name + " plugin of size " + i.size + "?\n If the plugin is already downloaded it will be deleted!", "Plugin Store",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(res == JOptionPane.OK_OPTION){
					File file = new File("omega-ide-plugins" + File.separator + i.fileName);
					if(file.exists()) {
						res = JOptionPane.showConfirmDialog(PluginStore.this, "This Plugin already exists. If you continue it we be deleted and reinstalled", "Plugin Store",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);;
						if(res == JOptionPane.OK_OPTION){
							file.delete();
						}
						else return;
						}
					new Thread(()->download(i.fileName)).start();
					omega.Screen.getPluginManager().refresh();
				}
			});
			bucket.setBounds(0, block, getWidth(), 140);
			panel.add(bucket);
			buckets.add(bucket);
			currentBuckets.add(bucket);
			block += 140;
		}
		panel.setPreferredSize(new Dimension(getWidth() - 5, block));
		repaint();
		search("");
	}
	public File download(String name){
		File file = new File("omega-ide-plugins" + File.separator + name);
		try{
			ready = true;
			file.delete();
			String url = "https://raw.githubusercontent.com/omegaui/omegaide-plugins/main/"+name;
			Process pull = new ProcessBuilder("wget", url, "--output-document=omega-ide-plugins" + File.separator + name).start();
			Scanner out = new Scanner(pull.getInputStream());
			Scanner err = new Scanner(pull.getErrorStream());
			downloader.setVisible(true);
			new Thread(()->{
				while(pull.isAlive()){
					if(err.hasNextLine()) {
						downloader.print(err.nextLine());
					}
				}
				err.close();
			}).start();
			while(pull.isAlive()){
				if(out.hasNextLine()) downloader.print(out.nextLine());
				}
			out.close();
		}
		catch(Exception e){
			return null;
		}
		if(downloader.isErrorOccured())
			ready = false;
		if(!ready)
			return null;
		return file;
	}
	public void setMessage(String text){
		messageComp.setText(text);
	}
	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		setMessage("Downloading List of Available Plugins from Github");
		if(value) {
			if(!ready)
				refresh();
			else
				search("");
		}
		setMessage("Click Any Plugin to Download it");
	}
}
