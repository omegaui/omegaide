/**
  * Stores Project Settings
  * Uses Portable Project Info Format
  * Copyright (C) 2021 Omega UI

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

package omega.utils;
import omega.Screen;

import java.util.LinkedList;

import java.io.File;
import java.io.PrintWriter;

import omega.database.DataBase;
import omega.database.DataEntry;
public class ProjectDataBase extends DataBase{
	public String jdkPath;
	public String mainClass;
	public static final String PROJECT_ROOT = "project-root$";
	
     public File jdk;
     
     public volatile boolean non_java;
     
     public LinkedList<String> jars = new LinkedList<>();
     public LinkedList<String> natives = new LinkedList<>();
     public LinkedList<String> resourceRoots = new LinkedList<>();
     public LinkedList<String> modules = new LinkedList<>();
     public LinkedList<String> compileTimeFlags = new LinkedList<>();
     public LinkedList<String> runTimeFlags = new LinkedList<>();

     public ProjectDataBase() {
          super(Screen.getFileView().getProjectPath() + File.separator + ".projectInfo");
          load();
     }

	public void load() {
		jdkPath = getEntryAt("JDK Path", 0) != null ? getEntryAt("JDK Path", 0).getValue() : null;
		mainClass = getEntryAt("Main Class", 0) != null ? getEntryAt("Main Class", 0).getValue() : "";
          non_java = getEntryAt("Non-Java Project", 0) != null ? getEntryAt("Non-Java Project", 0).getValueAsBoolean() : false;
          if(!non_java) {
               jdk = new File(jdkPath != null ? jdkPath : "");
     		try {
     			Screen.getRunView().mainClass = mainClass;
     		}
     		catch(Exception e) {
                    
		     }
          }
          LinkedList<DataEntry> mainEditors = getEntries("Opened Editors on Main Tab Panel");
          LinkedList<DataEntry> rightEditors = getEntries("Opened Editors on Right Tab Panel");
          LinkedList<DataEntry> bottomEditors = getEntries("Opened Editors on Bottom Tab Panel");
          LinkedList<DataEntry> jars = getEntries("Project Classpath : Required Jars");
          LinkedList<DataEntry> natives = getEntries("Project Classpath : Required Native Libraries");
          LinkedList<DataEntry> resourceRoots = getEntries("Project Classpath : Required Resource Roots");
          LinkedList<DataEntry> modules = getEntries("Project Classpath : Required Modules");
          LinkedList<DataEntry> compileTimeFlags = getEntries("Flags : Compile Time");
          LinkedList<DataEntry> runTimeFlags = getEntries("Flags : Run Time");
          
          if(mainEditors != null){
             for(DataEntry e : mainEditors) {
               	String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFile(f);
               }
          }
          if(rightEditors != null){
              for(DataEntry e : rightEditors) {
               	String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFileOnRightTabPanel(f);
               }
          }
          if(bottomEditors != null){
              for(DataEntry e : bottomEditors) {
                    String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFileOnBottomTabPanel(f);
               }
          }
          if(jars != null){
               for(DataEntry e : jars){
               	String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         this.jars.add(value);
               }
          }
          if(natives != null){
               for(DataEntry e : natives){
                    String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         this.natives.add(value);
               }
          }
          if(resourceRoots != null){
               for(DataEntry e : resourceRoots){
                    String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         this.resourceRoots.add(value);
               }
          }
          if(modules != null){
               for(DataEntry e : modules){
                    String value = e.getValue();
               	value = modifyProjectPath(value);
                    File f = new File(value);
                    if(f.exists())
                         this.modules.add(value);
               }
          }
          if(compileTimeFlags != null){
          	compileTimeFlags.forEach(entry->{
          		this.compileTimeFlags.add(entry.getValue());
          	});
          }
          if(runTimeFlags != null){
          	runTimeFlags.forEach(entry->{
          		this.runTimeFlags.add(entry.getValue());
          	});
          }
	}

	@Override
	public void save() {
		clear();
		addEntry("JDK Path", jdkPath);
		addEntry("Main Class", Screen.getRunView().mainClass != null ? Screen.getRunView().mainClass : "");
          addEntry("Non-Java Project", String.valueOf(non_java));
          Screen.getFileView().getScreen().getTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Main Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
               }
          });
          Screen.getFileView().getScreen().getRightTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Right Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
               }
          });
          Screen.getFileView().getScreen().getBottomTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Bottom Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
               }
          });
          jars.forEach(path->{
               addEntry("Project Classpath : Required Jars", genProjectRootPath(path));
          });
          natives.forEach(path->{
               addEntry("Project Classpath : Required Native Libraries", genProjectRootPath(path));
          });
          resourceRoots.forEach(path->{
               addEntry("Project Classpath : Required Resource Roots", genProjectRootPath(path));
          });
          modules.forEach(path->{
               addEntry("Project Classpath : Required Modules", genProjectRootPath(path));
          });
          Screen.getFileView().getExtendedDependencyView().getCompileTimeFlags().forEach(flag->{
          	addEntry("Flags : Compile Time", flag);
     	});
          Screen.getFileView().getExtendedDependencyView().getRunTimeFlags().forEach(flag->{
          	addEntry("Flags : Run Time", flag);
     	});
		super.save();
	}

	public String modifyProjectPath(String value){
		if(!value.startsWith(PROJECT_ROOT))
			return value;
		return getProjectPath() + value.substring(value.indexOf(PROJECT_ROOT) + PROJECT_ROOT.length());
	}

	public String genProjectRootPath(String value){
		if(!value.startsWith(getProjectPath()))
			return value;
		return PROJECT_ROOT + value.substring(value.indexOf(getProjectPath()) + getProjectPath().length());
	}

	public String getProjectPath(){
		return getDataBaseFile().getParentFile().getAbsolutePath();
	}

     public static void genInfo(String projectPath, boolean non_java){
     	try{
               File file = new File(projectPath + File.separator + ".projectInfo");
               if(file.exists()) return;
     		PrintWriter writer = new PrintWriter(file);
               writer.println(">Non-Java Project");
               writer.println("-" + non_java);
               writer.close();
     	}
     	catch(Exception e){ 
     	     e.printStackTrace();
	     }
     }

     public void setJDKPath(String path){
     	this.jdkPath = path;
          Screen.getFileView().readJDK();
     }
}

