/**
* BuildView
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

package omega.utils.systems;
import omega.popup.NotificationPopup;

import omega.instant.support.ErrorHighlighters;

import omega.token.factory.ShellTokenMaker;

import omega.comp.FlexPanel;

import omega.jdk.JDKManager;

import omega.instant.support.java.JavaSyntaxParser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;

import omega.Screen;
import omega.utils.Editor;
import omega.utils.UIManager;
import omega.utils.BuildLog;
import omega.utils.IconManager;
import omega.utils.JetRunPanel;
import omega.utils.systems.creators.FileOperationManager;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class BuildView extends View {
	
	public volatile Process compileProcess;
	public LinkedList<String> classess = new LinkedList<>();
	public static final String SRC_LIST = ".sources";
	public BuildLog buildLog;
	public String errorlog = "";
	
	public BuildView(String title, Screen window) {
		super(title, window);
		buildLog = new BuildLog();
	}
	
	public static LinkedList<File> loadAllFiles(String dir, LinkedList<File> files){
		File[] fs = new File(dir).listFiles();
		for(File f : fs) {
			if(f.isDirectory())
				loadAllFiles(f.getAbsolutePath(), files);
			else
				files.add(f);
		}
		return files;
	}
	
	public void compile(){
		new Thread(()->{
			LinkedList<String> args = Screen.getFileView().getArgumentManager().compile_time_args;
			if(Screen.getFileView().getArgumentManager().getCompileCommand().trim().equals("")){
				NotificationPopup.create(getScreen())
				.size(500, 120)
				.title("Project Management")
				.message("No Run Time Command Specified!", TOOLMENU_COLOR2)
				.shortMessage("Click \"Settings\", then Click \"All Settings\"", TOOLMENU_COLOR1)
				.dialogIcon(IconManager.fluentfolderImage)
				.build()
				.locateOnBottomLeft()
				.showIt();
				return;
			}
			getScreen().getToolMenu().buildComp.setClickable(false);
			getScreen().getToolMenu().runComp.setClickable(false);
			omega.Screen.getFileView().getArgumentManager().genLists();
			String compileDir = Screen.getFileView().getArgumentManager().compileDir;
			String[] commandsAsArray = new String[args.size()];
				for(int i = 0; i < args.size(); i++){
					commandsAsArray[i] = args.get(i);
			}
			
			JetRunPanel printArea = new JetRunPanel(false, commandsAsArray, compileDir);
			printArea.setLogMode(true);
			
			try {
				getScreen().getOperationPanel().addTab("Build", IconManager.fluenttesttubeImage, printArea, printArea::killProcess);
				
				printArea.print("Building Project...\n\"" + args + "\"");
				printArea.print("Executing ... " + Screen.getFileView().getArgumentManager().getCompileCommand());
				printArea.start();
				
				errorlog = "";
				
				while(printArea.terminalPanel.process.isAlive());
				
				printArea.printText("Compilation Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
				
				Screen.setStatus("Building Project", 100, null);
				
				ErrorHighlighters.resetAllErrors();
				
				if(printArea.terminalPanel.process.exitValue() != 0){
					ErrorHighlighters.showErrors(errorlog);
					getScreen().getOperationPanel().addTab("Build",IconManager.fluenttesttubeImage,  printArea, ()->printArea.killProcess());
					getScreen().getToolMenu().buildComp.setClickable(true);
					getScreen().getToolMenu().runComp.setClickable(true);
					return;
				}
			}
			catch(Exception e){
				printArea.printText("Compilation Failed!");
				printArea.printText("System was unable to find the specified command.");
				printArea.printText(e.toString());
				e.printStackTrace();
			}
			finally {
				getScreen().getToolMenu().buildComp.setClickable(true);
				getScreen().getToolMenu().runComp.setClickable(true);
				getScreen().getProjectView().reload();
			}
		}).start();
	}
	
	public static String[] convertToArray(String args){
		String token = "";
		LinkedList<String> arguments = new LinkedList<>();
		boolean canRecord = false;
		boolean strRec = false;
		for(int i = 0; i < args.length(); i++){
			char ch = args.charAt(i);
			if(!canRecord && (Character.isLetterOrDigit(ch) ||
			"-@\"".
			contains(ch + ""))){
				token = "";
				canRecord = true;
			}
			if(ch == '\"' && !strRec)
			strRec = true;
			else if(ch == '\"' && strRec)
			strRec = false;
			if(ch == ' ' && !strRec){
				arguments.add(token);
				canRecord = false;
			}
			else if(canRecord){
				token += ch;
			}
		}
		arguments.add(token);
		String[] A = new String[arguments.size()];
		int k = 0;
		for(String x : arguments)
			A[k++] = x;
		return A;
	}
	
	public void compileProject() {
		if(compileProcess != null) {
			if(compileProcess.isAlive())
				return;
		}
		
		getScreen().saveAllEditors();
		
		if(omega.Screen.getFileView().getProjectManager().non_java){
			compile();
			return;
		}
		
		new Thread(()->{
			
			try  {
				createClassList();
				if(classess.isEmpty())
					return;
				
				if(!JDKManager.isJDKPathValid(Screen.getFileView().getProjectManager().jdkPath)) {
					Screen.setStatus("Please first select a valid JDK for the project", 10, IconManager.fluentbrokenbotImage);
					return;
				}
				
				buildLog.genView("");
				Screen.getErrorHighlighter().removeAllHighlights();
				
				optimizeProjectOutputs();
				
				getScreen().getToolMenu().buildComp.setClickable(false);
				getScreen().getToolMenu().runComp.setClickable(false);
				
				String status = " Successfully";
				String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
				
				if(jdkPath != null && new File(jdkPath).exists())
					jdkPath = String.copyValueOf(jdkPath.toCharArray()) + File.separator + "bin" + File.separator;
				
				String cmd = "";
				String depenPath = "";
				
				if(!Screen.getFileView().getProjectManager().jars.isEmpty()) {
					
					for(String d : Screen.getFileView().getProjectManager().jars)
						depenPath += d + omega.Screen.PATH_SEPARATOR;
				}
				
				if(!Screen.getFileView().getProjectManager().resourceRoots.isEmpty()) {
					for(String d : Screen.getFileView().getProjectManager().resourceRoots)
						depenPath += d + omega.Screen.PATH_SEPARATOR;
				}
				
				if(Screen.isNotNull(depenPath))
					depenPath = depenPath.substring(0, depenPath.length() - 1);
				
				if(Screen.isNotNull(jdkPath) && new File(jdkPath).exists())
					cmd = jdkPath + "javac";
				else
					cmd = "javac" + cmd;
				
				File workingDir = new File(Screen.getFileView().getProjectPath());
				LinkedList<String> commands = new LinkedList<>();
				commands.add(cmd);
				commands.add("-d");
				commands.add("bin");
				
				if(Screen.isNotNull(depenPath)){
					commands.add("-classpath");
					commands.add(depenPath);
				}
				
				String modulePath = Screen.getFileView().getDependencyView().getModulePath();
				String modules = Screen.getFileView().getDependencyView().getModules();
				if(Screen.isNotNull(modulePath)){
					commands.add("--module-path");
					commands.add(modulePath);
					commands.add("--add-modules");
					commands.add(modules);
				}
				
				Screen.getFileView().getProjectManager().compileTimeFlags.forEach(commands::add);
				
				commands.add("@" + BuildView.SRC_LIST);
				
				String[] commandsAsArray = new String[commands.size()];
				
				int k = -1;
				for(String command : commands)
					commandsAsArray[++k] = command;
				
				compileProcess = new ProcessBuilder(commandsAsArray).directory(workingDir).start();
				
				buildLog.setHeading("Building Project with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				
				getScreen().getOperationPanel().addTab("Compilation", IconManager.fluenttesttubeImage, buildLog, ()->{
					if(compileProcess != null && compileProcess.isAlive())
						compileProcess.destroyForcibly();
				});
				
				String errorlog = "";
				Scanner errorReader = new Scanner(compileProcess.getErrorStream());
				
				while(compileProcess.isAlive()) {
					while(errorReader.hasNextLine()) {
						String line = errorReader.nextLine();
						errorlog += line + "\n";
					}
				}
				
				errorReader.close();
				
				if(compileProcess.exitValue() != 0) {
					buildLog.setHeading("Build Resulted in following Error(s)");
					buildLog.genView(errorlog);
					Screen.getErrorHighlighter().loadErrors(errorlog);
					buildLog.repaint();
				}
				else{
					buildLog.setHeading("Build Completed Successfully");
				}
				
				compileProcess = null;
				getScreen().getToolMenu().buildComp.setClickable(true);
				getScreen().getToolMenu().runComp.setClickable(true);
				Screen.getProjectView().reload();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void optimizeProjectOutputs(){
		File outputDir = new File(Screen.getFileView().getProjectPath() + File.separator + "bin");
		LinkedList<File> files = new LinkedList<>();
		loadClassFiles(outputDir, files);
		if(files.isEmpty())
			return;
		for(File file : files) {
			file.delete();
		}
		FileView.checkDir(outputDir);
	}
	
	public static void loadClassFiles(File out, LinkedList<File> files) {
		File[] F = out.listFiles();
		if(F == null || F.length == 0) return;
		for(File f : F) {
			if(f.isDirectory()) loadClassFiles(f, files);
			else if(f.getName().endsWith(".class")) files.add(f);
			}
	}
	
	public void deleteDir(File file) {
		
		if (file.isDirectory()) {
			if (file.list().length == 0)
				deleteEmptyDir(file);
			else {
				
				File files[] = file.listFiles();
				for (File fileDelete : files) {
					deleteDir(fileDelete);
				}
				
				if (file.list().length == 0) {
					deleteEmptyDir(file);
				}
			}
			
		}
		else {
			deleteEmptyDir(file);
		}
	}
	
	private void deleteEmptyDir(File file) {
		file.delete();
	}
	
	public static void getAllFolders(File[] files, LinkedList<File> folders) {
		for(File f : files) {
			if(f.isDirectory()) {
				folders.add(f);
				getAllFolders(f.listFiles(), folders);
			}
		}
	}
	
	public static void getAllFiles(File[] files, LinkedList<File> fs) {
		for(File f : files) {
			if(!f.isDirectory()) {
				fs.add(f);
			}
			else {
				getAllFiles(f.listFiles(), fs);
			}
		}
	}
	
	public void createClassList() {
		if(omega.Screen.getFileView().getProjectManager().non_java) return;
		classess.clear();
		try {
			LinkedList<String> dirs = new LinkedList<>();
			loadData(dirs, new File(Screen.getFileView().getProjectPath() + File.separator + "src").listFiles());
			
			while(!dirs.isEmpty()) {
				try {
					for(String path : dirs) {
						File file = new File(path);
						if(!file.isDirectory()) {
							if(path.endsWith(".java")) {
								classess.add(path);
							}
						}
						else {
							LinkedList<String> subDirs = new LinkedList<>();
							File[] fileList = file.listFiles();
							if(fileList == null)
								continue;
							loadData(subDirs, fileList);
							loadData(subDirs, dirs);
						}
						dirs.remove(path);
					}
				}
				catch(Exception e) {}
			}
			
			LinkedList<String> paths = new LinkedList<>();
			classess.forEach((c)->{
				String res = "";
				StringTokenizer tokenizer = new StringTokenizer(c, File.separator);
				while(tokenizer.hasMoreTokens()) {
					res += tokenizer.nextToken() + "/";
				}
				res = res.substring(0, res.length() - 1);
				if(Screen.onWindows())
					res = "\"" + res + "\"";
				else
					res = "\"" + File.separator + res + "\"";
				paths.add(res);
			});
			//Creating SourcePath
			PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath() + File.separator + SRC_LIST));
			paths.forEach(path->writer.println(path));
			writer.close();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	private static void loadData(LinkedList<String> paths, File[] files) {
		try {
			for (File file : files) {
				paths.add(file.getPath());
			}
		}
		catch(Exception e){
		}
	}
	
	private static void loadData(LinkedList<String> paths0, LinkedList<String> paths1) {
		for(String path0 : paths0)
			paths1.add(path0);
	}
	
}

