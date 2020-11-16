package update;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.fife.ui.rtextarea.RTextArea;

import ide.Screen;
public class Updater extends JDialog{
	private File releaseFile;
	private JLabel label;
	private RTextArea terminalArea;
	private JButton downBtn;
	private String version = null;
	private JScrollPane scrollPane;
	private static Font font = new Font("Ubuntu Mono", Font.BOLD, 16);
	public Updater(Screen screen){
		super(screen, "Release Updater");
		setModal(false);
		setIconImage(screen.getIconImage());
		setSize(660, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
	}

	public void init(){
		label = new JLabel("Checking for Update");
		label.setFont(font);
		add(label, BorderLayout.NORTH);

		terminalArea = new RTextArea("Downloading release file");
		terminalArea.setFont(font);
		terminalArea.setEditable(false);
		add(scrollPane = new JScrollPane(terminalArea), BorderLayout.CENTER);

		downBtn = new JButton("Install Update");
		downBtn.addActionListener((e)->{
			new Thread(()->{
				clean();
				terminalArea.setText("");
				downBtn.setVisible(false);
				label.setText("Downloading Update");
				File debFile = download("out/omega-ide_" + version + "_all.deb");
				if(debFile == null){
					label.setText("Problem Receiving installation File from server' end");
					return;
				}
				terminalArea.setText("Update Downloaded\n");
				print("Make user you have gdebi installed if you are using debian");
				label.setText("Installing....");
				print("Before installing close the IDE else your opened project may get corrupted!");
				print("Running ... \"" + "java.awt.Desktop.getDesktop().open(debFile)\"");
				try{
					System.out.println(debFile);
					java.awt.Desktop.getDesktop().open(debFile);
				}catch(Exception ex){ print(ex.toString()); }
			}).start();
		});
		downBtn.setFont(font);
		downBtn.setVisible(false);
		add(downBtn, BorderLayout.SOUTH);
	}

	public File download(String name){
		try{
			String url = "https://raw.githubusercontent.com/omegaui/omegaide/main/"+name;
			Process pull = new ProcessBuilder("wget", url).start();
			Scanner out = new Scanner(pull.getInputStream());
			Scanner err = new Scanner(pull.getErrorStream());
			new Thread(()->{
				while(pull.isAlive()){
					if(err.hasNextLine()) print(err.nextLine());
				}
				err.close();
			}).start();
			while(pull.isAlive()){
				if(out.hasNextLine()) print(out.nextLine());
			}
			out.close();
		}catch(Exception e){ System.err.println(e); }
		if(name.contains("/")) name = name.substring(name.lastIndexOf('/') + 1).trim();
		return new File(name);
	}

	public void check(){
		terminalArea.setText("Make Sure your internet is working!\n");
		releaseFile = download(".release");
		if(releaseFile == null) print("Unable to download Release File check whether wget is installed or not");
		print("Downloaded Release File");
		terminalArea.setText("------------------\nReading Release File\n");
		try{
			Scanner reader = new Scanner(releaseFile);
			version = reader.nextLine();
			label.setText("Terminal");
			if(("v" + version).equals(ide.Screen.VERSION)){
				clean();
				print("No updates avaliable, ");
				print("The latest version is already installed.");
				reader.close();
				return;
			}
			print("Update Available\n");
			print("Omega IDE v" + version);
			print("Download Size : " + reader.nextLine() + "\n");
			print("Whats New!");
			while(reader.hasNextLine())
				print("\t" + reader.nextLine());
			reader.close();
			downBtn.setVisible(true);
		}
		catch(Exception e){ print(e.toString()); }
	}

	public void clean(){
		if(releaseFile == null) return;
		releaseFile.delete();
	}

	public void print(String text){
		terminalArea.append(text + "\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(true);
          setTitle("Checking for Updates");
		if(value){
			check();
		}
		else{
			clean();
		}
          setTitle("Release Updater");
	}
}
