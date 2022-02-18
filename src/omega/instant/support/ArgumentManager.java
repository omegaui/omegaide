/*
 * The Universal Project Manager
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

package omega.instant.support;
import omega.instant.support.universal.DynamicListPanel;

import omegaui.dynamic.database.DataBase;
import omegaui.dynamic.database.DataEntry;

import omega.Screen;

import java.io.File;
import java.io.PrintWriter;

import java.util.LinkedList;
public class ArgumentManager extends DataBase{

	public LinkedList<String> run_time_args = new LinkedList<>();
	public LinkedList<String> compile_time_args = new LinkedList<>();
	public LinkedList<DynamicListPanel> units = new LinkedList<>();
	public String runDir;
	public String compileDir;

	public ArgumentManager(){
		super(Screen.getProjectFile().getProjectPath() + File.separator + ".args");
		compile_time_args.clear();
		run_time_args.clear();
		load();
	}

	public void load(){
		if(getEntries("Compile Time Argument") != null)
			getEntries("Compile Time Argument").forEach(entry->compile_time_args.add(entry.getValue()));
		if(getEntries("Run Time Argument") != null)
			getEntries("Run Time Argument").forEach(entry->run_time_args.add(entry.getValue()));
		compileDir = getEntryAt("Compile Time Working Directory", 0) != null ? getEntryAt("Compile Time Working Directory", 0).getValue() : "";
		runDir = getEntryAt("Run Time Working Directory", 0) != null ? getEntryAt("Run Time Working Directory", 0).getValue() : "";

		if(!new File(compileDir).exists())
			compileDir = "";
		if(!new File(runDir).exists())
			runDir = "";

		LinkedList<DataEntry> extensions = getEntries("Extensions");
		LinkedList<DataEntry> containers = getEntries("Containers");
		LinkedList<DataEntry> sources = getEntries("Sources");
		LinkedList<DataEntry> bounds = getEntries("Bounds Surrounded");
		LinkedList<DataEntry> dynamicModes = getEntries("Dynamic Modes");
		if(extensions == null) 
			return;
		for(int i = 0; i < extensions.size(); i++){
			units.add(new DynamicListPanel(
				extensions.get(i).getValue(),
				containers.get(i).getValue(),
				sources.get(i).getValue(),
				bounds.get(i).getValueAsBoolean(),
				dynamicModes.get(i).getValueAsBoolean()
			));
		}
	}

	public void genLists(){
		units.forEach(unit->{
			LinkedList<File> files = new LinkedList<>();
			if(unit.isDynamic()){
				Screen.getScreen().getAllEditors().forEach(editor->{
					if(editor.currentFile.getName().endsWith(unit.getFileExtension()))
						files.add(editor.currentFile);
				});
			}
			else{
				loadFiles(unit.getFileExtension(), files, new File(unit.getWorkingDirectory()));
			}
			if(!files.isEmpty())
				writeList(unit.getContainerName(), files, unit.isQuoted());
		});
	}

	public void writeList(String name, LinkedList<File> files, boolean sur){
		try{
			PrintWriter writer = new PrintWriter(new File(Screen.getProjectFile().getProjectPath() + File.separator + name));
			files.forEach(file->{
				if(sur)
					writer.println("\"" + file.getAbsolutePath() + "\"");
				else
					writer.println(file.getAbsolutePath());
			});
			writer.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}

	public void loadFiles(String ext, LinkedList<File> files, File dir){
		File[] F = dir.listFiles();
		if(F == null || F.length == 0) return;
		for(File fx : F){
			if(!fx.isDirectory() && fx.getName().endsWith(ext))
				files.add(fx);
			else if(fx.isDirectory())
				loadFiles(ext, files, fx);
		}
	}

	public String getCompileCommand(){
		String command = "";
		for(String cx : compile_time_args){
			command += cx + " ";
		}
		return command.trim();
	}

	public String getRunCommand(){
		String command = "";
		for(String cx : run_time_args){
			command += cx + " ";
		}
		return command.trim();
	}

	@Override
	public void save(){
		clear();
		compile_time_args.forEach(entry->addEntry("Compile Time Argument", entry));
		run_time_args.forEach(entry->addEntry("Run Time Argument", entry));
		updateEntry("Compile Time Working Directory", compileDir, 0);
		updateEntry("Run Time Working Directory", runDir, 0);
		for(int i = 0; i < units.size(); i++){
			DynamicListPanel u = units.get(i);
			updateEntry("Extensions", u.getFileExtension(), i);
			updateEntry("Containers", u.getContainerName(), i);
			updateEntry("Sources", u.getWorkingDirectory(), i);
			updateEntry("Bounds Surrounded", String.valueOf(u.isQuoted()), i);
			updateEntry("Dynamic Modes", String.valueOf(u.isDynamic()), i);
		}
		super.save();
	}
}

