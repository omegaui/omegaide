package plugin;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
public class PluginStore extends JFrame{
	private static LinkedList<Bucket> buckets = new LinkedList<>();
	private static LinkedList<Bucket> currentBuckets = new LinkedList<>();
	private static LinkedList<PlugInfo> infos;
	public static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 16);
	public static final Font FONT14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	private JScrollPane scrollPane;
	private JPanel panel;
	private int block;
	private volatile boolean ready;

	private Downloader downloader;

	public PluginStore(){
		super("Plugin Store");
		setSize(600, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(ide.Screen.getScreen().getIconImage());
		add(scrollPane = new JScrollPane(panel = new JPanel(null)), BorderLayout.CENTER);
		init();
	}

	public void init(){
		downloader = new Downloader(this);
		JTextField textField = new JTextField("Search");
		textField.addActionListener(e->{
			String text = textField.getText();
			search(text);
		});
		textField.setBounds(0, 0, getWidth(), 40);
		textField.setFont(FONT);
		panel.add(textField);
	}

	public void search(String text){
		setTitle("Plugin Store");
		currentBuckets.forEach(panel::remove);
		currentBuckets.clear();
		block = 40;
		for(Bucket b : buckets){
			if(!b.name.contains(text) && !b.desc.contains(text)) continue;
			b.setBounds(0, block, getWidth(), 140);
			currentBuckets.add(b);
			panel.add(b);
			block += 140;
		}
		panel.setPreferredSize(new Dimension(getWidth(), block));
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
		block = 40;
		infos = PlugInfoWriter.read(list);
		list.delete();
		for(PlugInfo i : infos){
			Bucket bucket = new Bucket(i.name, i.size, i.desc, (e)->{
				int res = JOptionPane.showConfirmDialog(PluginStore.this, "Do you want to Download " + i.name + " plugin of size " + i.size + "?\n If the plugin is already downloaded it will be deleted!", "Plugin Store",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(res == JOptionPane.OK_OPTION){
					File file = new File("omega-ide-plugins/" + i.fileName);
					if(file.exists()) {
						res = JOptionPane.showConfirmDialog(PluginStore.this, "This Plugin already exists. If you continue it we be deleted and reinstalled", "Plugin Store",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);;
						if(res == JOptionPane.OK_OPTION){
							file.delete();
						}
						else return;
					}
					new Thread(()->download(i.fileName)).start();
					ide.Screen.getPluginManager().refresh();
				}
			});
			bucket.setBounds(0, block, getWidth(), 140);
			panel.add(bucket);
			buckets.add(bucket);
			currentBuckets.add(bucket);
			block += 140;
		}
		panel.setPreferredSize(new Dimension(getWidth(), block));
		repaint();
		search("");
	}

	public File download(String name){
		File file = new File("omega-ide-plugins/"+name); 
		try{
			ready = true;
			file.delete();
			String url = "https://raw.githubusercontent.com/omegaui/omegaide-plugins/main/"+name;
			Process pull = new ProcessBuilder("wget", url, "--output-document=omega-ide-plugins/"+name).start();
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
		}catch(Exception e){
			return null; 
		}
		if(downloader.isErrorOccured()) ready = false;
		if(!ready) return null;
		if(name.contains("/")) name = name.substring(name.lastIndexOf('/') + 1).trim();
		return new File("omega-ide-plugins/"+name);
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		setTitle("Downloading List of Available Plugins from Github");
		if(value) {
			if(!ready) refresh();
			else search("");
		}
	}
}
