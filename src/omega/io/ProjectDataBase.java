/*
 * Stores Project Settings
 * Uses Portable Project Info Format

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

package omega.io;
import omega.ui.component.Editor;

import javax.swing.text.Caret;

import omega.Screen;

import omega.instant.support.LanguageTagView;

import omegaui.dynamic.database.DataBase;
import omegaui.dynamic.database.DataEntry;

import java.util.LinkedList;

import java.io.File;
import java.io.PrintWriter;

public class ProjectDataBase extends DataBase{

	public String jdkPath;
	public String mainClass;

	public static final String PROJECT_ROOT = "project-root$";

	public File jdk;

	public int languageTag = -1;

	public LinkedList<String> jars = new LinkedList<>();
	public LinkedList<String> natives = new LinkedList<>();
	public LinkedList<String> resourceRoots = new LinkedList<>();
	public LinkedList<String> modules = new LinkedList<>();
	public LinkedList<String> compileTimeFlags = new LinkedList<>();
	public LinkedList<String> runTimeFlags = new LinkedList<>();

	public ProjectDataBase() {
		super(Screen.getProjectFile().getProjectPath() + File.separator + ".projectInfo");
		load();
	}

	public void load() {
		jdkPath = getEntryAt("JDK Path", 0) != null ? getEntryAt("JDK Path", 0).getValue() : null;
		mainClass = getEntryAt("Main Class", 0) != null ? getEntryAt("Main Class", 0).getValue() : "";

		if(getEntryAt("Language Tag", 0) != null)
			setLanguageTag(getEntryAt("Language Tag", 0).getValueAsInt());

		if(!isLanguageTagNonJava()) {
			jdk = new File(jdkPath != null ? jdkPath : "");
			try {
				Screen.getProjectRunner().mainClass = mainClass;
			}
			catch(Exception e) {

			}
		}

		BookmarksManager.readBookmarks(this);

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
					Screen.getProjectFile().getScreen().loadFile(f);
			}
		}
		
		if(rightEditors != null){
			for(DataEntry e : rightEditors) {
				String value = e.getValue();
				value = modifyProjectPath(value);
				File f = new File(value);
				if(f.exists())
					Screen.getProjectFile().getScreen().loadFileOnRightTabPanel(f);
			}
		}
		
		if(bottomEditors != null){
			for(DataEntry e : bottomEditors) {
				String value = e.getValue();
				value = modifyProjectPath(value);
				File f = new File(value);
				if(f.exists())
					Screen.getProjectFile().getScreen().loadFileOnBottomTabPanel(f);
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

		LinkedList<DataEntry> caretPositionList = getEntries("Caret Positions");
		if(caretPositionList != null){
			for(DataEntry entry : caretPositionList){
				File file = new File(modifyProjectPath(entry.lines().get(0)));
				if(file.exists()){
					Editor editor = Screen.getScreen().getEditor(file);
					editor.setCaretPosition(Integer.parseInt(entry.lines().get(2)));
					editor.getAttachment().getVerticalScrollBar().setValue(Integer.parseInt(entry.lines().get(1)));
				}
			}
		}
	}

	@Override
	public void save() {
		clear();
		addEntry("JDK Path", jdkPath);
		addEntry("Main Class", Screen.getProjectRunner().mainClass != null ? Screen.getProjectRunner().mainClass : "");
		addEntry("Language Tag", getLanguageTag() + "");
		Screen.getProjectFile().getScreen().getTabPanel().getEditors().forEach(editor->{
			if(editor.currentFile != null) {
				addEntry("Opened Editors on Main Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
			}
		});
		Screen.getProjectFile().getScreen().getRightTabPanel().getEditors().forEach(editor->{
			if(editor.currentFile != null) {
				addEntry("Opened Editors on Right Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
			}
		});
		Screen.getProjectFile().getScreen().getBottomTabPanel().getEditors().forEach(editor->{
			if(editor.currentFile != null) {
				addEntry("Opened Editors on Bottom Tab Panel", genProjectRootPath(editor.currentFile.getAbsolutePath()));
			}
		});
		Screen.getScreen().getAllEditors().forEach((editor)->{
			if(editor.currentFile != null){
				addEntry(
					"Caret Positions", 
					genProjectRootPath(editor.currentFile.getAbsolutePath()) + 
					"\n" + editor.getAttachment().getVerticalScrollBar().getValue() + 
					"\n" + editor.getCaretPosition()
				);
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
		Screen.getProjectFile().getExtendedDependencyView().getCompileTimeFlags().forEach(flag->{
			addEntry("Flags : Compile Time", flag);
		});
		Screen.getProjectFile().getExtendedDependencyView().getRunTimeFlags().forEach(flag->{
			addEntry("Flags : Run Time", flag);
		});
		BookmarksManager.saveBookmarks(this);
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
			File file = new File(projectPath, ".projectInfo");
			if(file.exists())
				return;
			PrintWriter writer = new PrintWriter(file);
			writer.println(">Language Tag");
			writer.println("-" + (non_java ? LanguageTagView.LANGUAGE_TAG_ANY : LanguageTagView.LANGUAGE_TAG_JAVA));
			writer.close();
			if(non_java){
				file = new File(projectPath, ".args");
				writer = new PrintWriter(file);
				writer.println(">Compile Time Argument");
				writer.println("-");
				writer.println(">Run Time Argument");
				writer.println("-");
				writer.println(">Compile Time Working Directory");
				writer.println("-");
				writer.println(">Run Time Working Directory");
				writer.println("-");
				writer.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setJDKPath(String path){
		this.jdkPath = path;
		Screen.getProjectFile().readJDK();
	}

	public boolean isLanguageTagNonJava(){
		return getLanguageTag() != LanguageTagView.LANGUAGE_TAG_JAVA;
	}

	public int getLanguageTag() {
		return languageTag;
	}

	public void setLanguageTag(int languageTag) {
		this.languageTag = languageTag;
	}

}

