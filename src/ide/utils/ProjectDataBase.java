package ide.utils;

import java.io.File;
import java.util.LinkedList;

import deassembler.Assembly;
import ide.Screen;
import importIO.ImportManager;
import importIO.JDKReader;
import omega.database.DataBase;
import omega.database.DataEntry;

public class ProjectDataBase extends DataBase{
	public String compile_time_args;
	public String run_time_args;
	public String jdkPath;
	public String mainClass;

	public ProjectDataBase() {
		super(Screen.getFileView().getProjectPath()+"/.projectInfo");
		jdkPath = null;
		compile_time_args = "";
		run_time_args = "";
		load();
	}

	public void load() {
		jdkPath = getEntryAt("JDK Path", 0) != null ? getEntryAt("JDK Path", 0).getValue() : null;
		compile_time_args = getEntryAt("Compile_Time", 0) != null ? getEntryAt("Compile_Time", 0).getValue() : "";
		run_time_args = getEntryAt("Run_Time", 0) != null ? getEntryAt("Run_Time", 0).getValue() : "";
		mainClass = getEntryAt("Main Class", 0) != null ? getEntryAt("Main Class", 0).getValue() : "";
		try {
			Screen.getRunView().mainClass = mainClass;
		}catch(Exception e) {}
		LinkedList<DataEntry> entries = getEntries("Opened Editors");
		if(entries == null) return;
		for(DataEntry e : entries) {
			File f = new File(e.getValue());
			if(f.exists())
				Screen.getFileView().getScreen().loadFile(f);
		}
	}

	@Override
	public void save() {
		clear();
		addEntry("JDK Path", jdkPath);
		addEntry("Compile_Time", compile_time_args);
		addEntry("Run_Time", run_time_args);
		addEntry("Main Class", Screen.getRunView().mainClass != null ? Screen.getRunView().mainClass : "");
		Screen.getFileView().getScreen().getTabPanel().getEditors().forEach(editor->{
			if(editor.currentFile != null) {
				addEntry("Opened Editors", editor.currentFile.getAbsolutePath());
			}
		});
		super.save();
	}

	public void setJDKPath(String path) {
		if(path == null || !new File(path).exists()) return;
		jdkPath = path;
		Screen.hideNotif();
		new Thread(()->{
			while(ImportManager.reading);
			readJDK(true);
		}).start();
	}

	public void readJDK(boolean internal) {
		try {
			JDKReader.read(jdkPath);
			Screen.getFileView().getScreen().getToolPane().initEditorTools();
			Assembly.deassemble();
		}catch(Exception e) {System.out.println(e.getMessage());}
	}
}
