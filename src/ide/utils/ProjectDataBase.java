package ide.utils;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.PrintWriter;
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
     public File jdk;
     public volatile boolean non_java;

     public ProjectDataBase() {
          super(Screen.getFileView().getProjectPath() + File.separator + ".projectInfo");
          jdkPath = null;
          compile_time_args = "";
          run_time_args = "";
          non_java = false;
          load();
     }

	public void load() {
		jdkPath = getEntryAt("JDK Path", 0) != null ? getEntryAt("JDK Path", 0).getValue() : null;
		compile_time_args = getEntryAt("Compile_Time", 0) != null ? getEntryAt("Compile_Time", 0).getValue() : "";
		run_time_args = getEntryAt("Run_Time", 0) != null ? getEntryAt("Run_Time", 0).getValue() : "";
		mainClass = getEntryAt("Main Class", 0) != null ? getEntryAt("Main Class", 0).getValue() : "";
          non_java = getEntryAt("Non-Java Project", 0) != null ? getEntryAt("Non-Java Project", 0).getValueAsBoolean() : true;
          if(!non_java) {
               jdk = new File(jdkPath != null ? jdkPath : "");
     		try {
     			Screen.getRunView().mainClass = mainClass;
     		}catch(Exception e) {}
          }
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
          addEntry("Non-Java Project", String.valueOf(non_java));
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

     public static void genInfo(String projectPath, boolean non_java){
     	try{
               File file = new File(projectPath + File.separator + ".projectInfo");
               if(file.exists()) return;
     		PrintWriter writer = new PrintWriter(file);
               writer.println(">Non-Java Project");
               writer.println("-" + non_java);
               writer.close();
     	}catch(Exception e){ System.err.println(e); }
     }

     public boolean jdkExists(){
     	return jdk.exists();
     }

	public void readJDK(boolean internal) {
		try {
			JDKReader.read(jdkPath);
			Screen.getScreen().tools.initTools();
			Assembly.deassemble();
		}catch(Exception e) {System.out.println(e.getMessage());}
	}
}
