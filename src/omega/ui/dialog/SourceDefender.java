/*
 * Backup Manager
 * Copyright (C) 2022 Omega UI

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package omega.ui.dialog;
import omega.ui.component.Editor;

import omega.ui.panel.RunPanel;

import omega.io.DataManager;
import omega.io.IconManager;

import omegaui.component.TextComp;
import omegaui.component.SwitchComp;

import java.awt.geom.RoundRectangle2D;

import omega.Screen;

import java.awt.Desktop;
import java.awt.Dimension;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.LinkedList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JWindow;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class SourceDefender extends JDialog {
	private TextComp titleComp;
	private SwitchComp sourceDefenceComp;
	private SwitchComp destroyDefenceAfterExitComp;
	private LinkedList<TextComp> backupSet = new LinkedList<>();

	public static final String BACKUP_DIR = ".omega-ide" + File.separator + "backups";

	public static volatile boolean backupCompleted = true;

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
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
		JPanel panel = new JPanel(null);
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

		titleComp = new TextComp("Source Defender", "All Backups are located at " + (new File(BACKUP_DIR).getAbsolutePath()), c2, c2, glow, null);
		titleComp.setBounds(0, 0, getWidth() - 100, 30);
		titleComp.setClickable(false);
		titleComp.setFont(PX14);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		add(titleComp);

		TextComp openBackups = new TextComp("Browse", "Click to open backup folder in your system's shell", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1,
		()->{
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

		TextComp createBackupComp = new TextComp("Create Backup", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->{
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
		scrollPane.setBorder(null);
		panel.setBackground(c2);
		add(scrollPane);
	}

	public void backupData(){
		String backupTime = "backup " + (new Date().toString());
		if(Screen.onWindows()){
			backupTime = backupTime.replaceAll(":", ",");
		}
		final String backupTitle = new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getProjectFile().getProjectName() + File.separator + backupTime;
		LinkedList<Editor> editors = Screen.getScreen().getAllEditors();
		editors.forEach(editor->{
			addToBackup(backupTitle, editor.currentFile);
		});

		TextComp comp = new TextComp(backupTime, "Click to Restore From Backup", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			File backupChannel = new File(BACKUP_DIR, backupTitle);
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

			backupCompleted = false;

			RunPanel printArea = new RunPanel();
			printArea.setLogMode(true);
			JScrollPane scrollPane = new JScrollPane(printArea);
			scrollPane.setBackground(c2);
			Screen.getScreen().getOperationPanel().addTab("Backup Operation", IconManager.fluentsaveImage, scrollPane, ()->{
				if(!backupCompleted)
					Screen.setStatus("Backup is still running in background!", 0, IconManager.fluentinfoImage);
			});
			printArea.clearTerminal();
			printArea.print("Restoring from backup ... \"" + backupTitle + "\"");
			LinkedList<File> files = new LinkedList<>();
			File backupChannel = new File(BACKUP_DIR, new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getProjectFile().getProjectName() + File.separator + backupTitle);
			loadAllFiles(files, backupChannel);
			printArea.print(files.size() + " files(s) will be restored!");
			for(File file : files){
				String path = file.getAbsolutePath();
				String backupPath = backupChannel.getAbsolutePath();
				path = path.substring(path.indexOf(backupPath) + backupPath.length());
				printArea.print("Restoring \"" + path + "\"");
				path = Screen.getProjectFile().getProjectPath() + path;
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
			printArea.print("If the Files are already opened in the editors then, reload them.");
			printArea.print("-----------------------------------------");
			printArea.print("All Backups are located at " + (new File(BACKUP_DIR).getAbsolutePath()));
			printArea.print("You can create full project backups and also you can delete or modify backups from there.");
			backupCompleted = true;
			Screen.setStatus("Backup Restoration Completed Successfully!", 0, IconManager.fluentinfoImage);
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
		if(!path.startsWith(Screen.getProjectFile().getProjectPath())){
			System.err.println("Cannot backup files that are located outside the project");
			return;
		}
		String backupPath = path.substring(path.indexOf(Screen.getProjectFile().getProjectPath()) + Screen.getProjectFile().getProjectPath().length());
		backupPath = backupTitle + File.separator + backupPath;
		File backupFile = new File(BACKUP_DIR, backupPath);
		try{
			backupFile.getParentFile().mkdirs();
			backupFile.createNewFile();
			InputStream i = new FileInputStream(file);
			OutputStream o = new FileOutputStream(backupFile);
			while(i.available() > 0)
				o.write(i.read());
			i.close();
			o.close();
		}
		catch(Exception e){
			System.err.println("An Error Occured When Writing.\nBackup File: \"" + file.getAbsolutePath() + "\" \nTarget Location: " + backupFile.getAbsolutePath());
			e.printStackTrace();
		}
	}


	public void readBackups(){
		backupSet.forEach(panel::remove);
		backupSet.clear();

		block = 0;

		File[] backups = new File(BACKUP_DIR + File.separator + new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getProjectFile().getProjectName()).listFiles();
		if(backups == null || backups.length == 0)
			return;

		for(File backup : backups){
			TextComp comp = new TextComp(backup.getName(), "Click to Restore From Backup", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
				File backupChannel = new File(BACKUP_DIR, new File(DataManager.getWorkspace()).getName() + File.separator + Screen.getProjectFile().getProjectName() + File.separator + backup.getName());
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
	public void setSize(int width, int height){
		super.setSize(width, height);
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
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
			titleComp = new TextComp("", c2, c2, glow, null);
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

			TextComp closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, this::dispose);
			closeComp.setBounds(0, getHeight() - 27, getWidth()/2, 25);
			closeComp.setFont(PX14);
			add(closeComp);

			TextComp applyComp = new TextComp("Restore", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
				SourceDefender.this.setVisible(false);
				new Thread(()->restoreBackup(BackupView.this.backupTitle)).start();
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
			final String name = BACKUP_DIR + File.separator + workspace + File.separator + Screen.getProjectFile().getProjectName() + File.separator + backupTitle;
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
