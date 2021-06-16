package omega.utils;
import java.awt.*;
import omega.*;
import java.io.*;
import java.util.*;
import omega.comp.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SourceDefender extends JDialog {
	private TextComp titleComp;
	private SwitchComp sourceDefenceComp;
	private SwitchComp destroyDefenceAfterExitComp;
	private LinkedList<TextComp> backupSet = new LinkedList<>();

	public static final String BACKUP_DIR = ".omega-ide" + File.separator + "backups";

	private JScrollPane scrollPane;
	private JPanel panel;
	private int block;

	private BackupView backupView;
	
	public SourceDefender(Screen screen){
		super(screen, false);
		setTitle("Source Defender");
		setUndecorated(true);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);
		var panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		init();
	}

	public void init(){
		block = 0;
		
		File backupDir = new File(BACKUP_DIR);
		if(!backupDir.exists())
			backupDir.mkdirs();

		backupView = new BackupView();
		
		titleComp = new TextComp("Source Defender", "All Backups are located at " + (new File(BACKUP_DIR).getAbsolutePath()), TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth() - 100, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		add(titleComp);

		TextComp openBackups = new TextComp("Browse", "Click to open backup folder in your system's shell", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			try{
				Desktop.getDesktop().open(new File(BACKUP_DIR));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
		openBackups.setBounds(getWidth() - 100, 0, 70, 30);
		openBackups.setFont(PX14);
		openBackups.setArc(0, 0);
		add(openBackups);

		TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(0, 0);
		add(closeComp);

		TextComp label0 = new TextComp("", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null);
		label0.setText("Automatic Backup - " + (DataManager.isSourceDefenderEnabled() ? "On" : "Off"));
		label0.setBounds(10, 40, 300, 30);
		label0.setClickable(false);
		label0.setFont(PX14);
		add(label0);

		sourceDefenceComp = new SwitchComp(DataManager.isSourceDefenderEnabled(), TOOLMENU_COLOR1, TOOLMENU_COLOR3, TOOLMENU_COLOR2_SHADE, (value)->{
			label0.setText("Automatic Backup - " + (value ? "On" : "Off"));
			DataManager.setSourceDefenderEnabled(value);
		});
		sourceDefenceComp.setBounds(320, 40, 70, 30);
		sourceDefenceComp.setInBallColor(glow);
		add(sourceDefenceComp);

		TextComp createBackupComp = new TextComp("Create Backup", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			new Thread(()->{
				backupData();
				setVisible(true);
			}).start();
		});
		createBackupComp.setBounds(getWidth()/2 - 100, 80, 200, 30);
		createBackupComp.setFont(PX14);
		add(createBackupComp);

		scrollPane = new JScrollPane(panel = new JPanel(null));
		scrollPane.setBounds(10, 120, getWidth() - 20, getHeight() - 140);
		scrollPane.setBackground(c2);
		panel.setBackground(c2);
		add(scrollPane);
	}

	public void backupData(){
		final String backupTime = "backup " + (new Date().toString());
		final String backupTitle = new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getFileView().getProjectName() + File.separator + backupTime;
		LinkedList<Editor> editors = Screen.getScreen().getAllEditors();
		editors.forEach(editor->{
			addToBackup(backupTitle, editor.currentFile);
		});
		
		TextComp comp = new TextComp(backupTime, "Click to Restore From Backup", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
			File backupChannel = new File(BACKUP_DIR, new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getFileView().getProjectName() + File.separator + backupTitle);
			LinkedList<File> files = new LinkedList<>();
			loadAllFiles(files, backupChannel);
			backupView.showView(backupTitle, files);
		});
		comp.setBounds(0, block, scrollPane.getWidth(), 25);
		comp.setFont(PX14);
		comp.setArc(0, 0);
		panel.add(comp);
		backupSet.add(comp);

		block += 25;
		
		panel.setPreferredSize(new Dimension(scrollPane.getWidth(), block));
		repaint();
	}

	public void restoreBackup(String backupTitle){
		int res = ChoiceDialog.makeChoice("Do You Want to Restore this backup? Once started this process cannot be stopped!", "Yes", "No");
		if(res == ChoiceDialog.CHOICE1){
			setVisible(false);
			PrintArea printArea = new PrintArea();
			JScrollPane scrollPane = new JScrollPane(printArea);
			scrollPane.setBackground(c2);
			Screen.getScreen().getOperationPanel().addTab("Backup Operation", scrollPane, ()->{
				Screen.setStatus("Backup is still running in background!", 10);
			});
			printArea.clear();
			printArea.print("Restoring from backup ... \"" + backupTitle + "\"");
			LinkedList<File> files = new LinkedList<>();
			File backupChannel = new File(BACKUP_DIR, new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getFileView().getProjectName() + File.separator + backupTitle);
			loadAllFiles(files, backupChannel);
			printArea.print(files.size() + " files(s) will be restored!");
			for(File file : files){
				String path = file.getAbsolutePath();
				String backupPath = backupChannel.getAbsolutePath();
				path = path.substring(path.indexOf(backupPath) + backupPath.length());
				printArea.print("Restoring \"" + path + "\"");
				path = Screen.getFileView().getProjectPath() + path;
				try{
					File targetFile = new File(path);
					targetFile.getParentFile().mkdirs();
					InputStream i = new FileInputStream(file);
					OutputStream o = new FileOutputStream(targetFile);
					while(i.available() > 0)
						o.write(i.read());
					o.close();
					i.close();
				}
				catch(Exception e){
					printArea.print("An Error Occured While Restoring \"" + path + "\"");
				}
			}
			printArea.print("Backup Restoration Finished!");
			printArea.print("If The Files are already opened in the editors then,  reload them.");
			printArea.print("-----------------------------------------");
			printArea.print("All Backups are located at " + (new File(BACKUP_DIR).getAbsolutePath()));
			printArea.print("You can create full project backups, delete and modify backups from there.");
		}
	}

	public void loadAllFiles(LinkedList<File> files, File dir){
		File[] F = dir.listFiles();
		if(F == null || F.length == 0)
			return;
		for(File f : F){
			if(f.isDirectory())
				loadAllFiles(files, f);
			else
				files.add(f);
		}
	}

	public void addToBackup(String backupTitle, File file){
		String path = file.getAbsolutePath();
		if(!path.startsWith(Screen.getFileView().getProjectPath())){
			System.err.println("Cannot backup files that are located outside the project");
			return;
		}
		String backupPath = path.substring(path.indexOf(Screen.getFileView().getProjectPath()) + Screen.getFileView().getProjectPath().length());
		backupPath = backupTitle + File.separator + backupPath;
		File backupFile = new File(BACKUP_DIR, backupPath);
		try{
			backupFile.getParentFile().mkdirs();
			InputStream i = new FileInputStream(file);
			OutputStream o = new FileOutputStream(backupFile);
			while(i.available() > 0)
				o.write(i.read());
			i.close();
			o.close();
		}
		catch(Exception e){
			System.err.println("An Error Occured When Writing Backup File \"" + file.getAbsolutePath() + "\"");
		}
	}


	public void readBackups(){
		backupSet.forEach(panel::remove);
		backupSet.clear();

		block = 0;

		File[] backups = new File(BACKUP_DIR + File.separator + new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getFileView().getProjectName()).listFiles();
		if(backups == null || backups.length == 0)
			return;
		
		for(File backup : backups){
			TextComp comp = new TextComp(backup.getName(), "Click to Restore From Backup", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
				File backupChannel = new File(BACKUP_DIR, new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getFileView().getProjectName() + File.separator + backup.getName());
				LinkedList<File> files = new LinkedList<>();
				loadAllFiles(files, backupChannel);
				backupView.showView(backup.getName(), files);
			});
			comp.setBounds(0, block, scrollPane.getWidth(), 25);
			comp.setFont(PX14);
			comp.setArc(0, 0);
			panel.add(comp);
			backupSet.add(comp);
	
			block += 25;
		}
		
		panel.setPreferredSize(new Dimension(scrollPane.getWidth(), block));
		repaint();
	}
	
	@Override
	public void setVisible(boolean value){
		if(value){
			readBackups();
			setSize(400, backupSet.isEmpty() ? 110 : 400);
			setLocationRelativeTo(null);
		}
		super.setVisible(value);
	}

	private class BackupView extends JWindow {
		private TextComp titleComp;
		private JScrollPane scrollPane;
		private JPanel panel;
		private String backupTitle;

		private int block = 0;
		private LinkedList<TextComp> fileComps = new LinkedList<>();
		
		public BackupView(){
			super(SourceDefender.this);
			setSize(500, 300);
			setLocationRelativeTo(null);
			JPanel panel = new JPanel(null);
			panel.setBackground(c2);
			setBackground(c2);
			setContentPane(panel);
			setLayout(null);
			init();
		}

		public void init(){
			titleComp = new TextComp("", TOOLMENU_COLOR3, c2, c2, null);
			titleComp.setBounds(0, 0, getWidth(), 30);
			titleComp.setFont(PX14);
			titleComp.setClickable(false);
			titleComp.setArc(0, 0);
			titleComp.attachDragger(this);
			add(titleComp);

			TextComp label0 = new TextComp("Files to be re-written", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, null);
			label0.setBounds(5, 40, getWidth() - 10, 25);
			label0.setFont(PX14);
			label0.setClickable(false);
			add(label0);

			scrollPane = new JScrollPane(panel = new JPanel(null));
			scrollPane.setBounds(5, 80, getWidth() - 10, getHeight() - 120);
			scrollPane.setBackground(c2);
			panel.setBackground(c2);
			add(scrollPane);

			TextComp closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
			closeComp.setBounds(0, getHeight() - 27, getWidth()/2, 25);
			closeComp.setFont(PX14);
			add(closeComp);

			TextComp applyComp = new TextComp("Restore", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
				SourceDefender.this.setVisible(false);
				restoreBackup(BackupView.this.backupTitle);
			});
			applyComp.setBounds(getWidth()/2, getHeight() - 27, getWidth()/2, 25);
			applyComp.setFont(PX14);
			add(applyComp);
		}

		public void showView(String backupTitle, LinkedList<File> files){
			this.backupTitle = backupTitle;
			titleComp.setText(backupTitle);
			
			fileComps.forEach(panel::remove);
			fileComps.clear();
			block = 0;
			final String workspace = new File(DataManager.getWorkspace()).getName();
			final String name = BACKUP_DIR + File.separator + workspace + File.separator + Screen.getFileView().getProjectName() + File.separator + backupTitle;
			files.forEach(file->{
				String path = file.getAbsolutePath();
				path = path.substring(path.lastIndexOf(name) + name.length());
				
				TextComp comp = new TextComp(path, TOOLMENU_COLOR4, c2, c2, null);
				comp.setBounds(0, block, scrollPane.getWidth(), 25);
				comp.setFont(PX14);
				comp.setClickable(false);
				comp.setArc(0, 0);
				panel.add(comp);
				fileComps.add(comp);

				block += 25;
			});
			panel.setPreferredSize(new Dimension(scrollPane.getWidth(), block));
			repaint();
			setVisible(true);
		}
	}
}
