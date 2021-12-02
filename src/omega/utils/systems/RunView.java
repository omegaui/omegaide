/**
* RunView
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

import omega.token.factory.ShellTokenMaker;

import org.fife.ui.rtextarea.RTextScrollPane;

import omega.Screen;

import omega.comp.TextComp;
import omega.comp.FlexPanel;

import java.awt.event.MouseListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import omega.instant.support.SyntaxParsers;
import omega.instant.support.ErrorHighlighters;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;

import omega.jdk.JDKManager;

import omega.instant.support.java.JavaSyntaxParser;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComponent;

import omega.popup.NotificationPopup;

import java.io.File;
import java.io.PrintWriter;

import omega.utils.BuildLog;
import omega.utils.ToolMenu;
import omega.utils.DataManager;
import omega.utils.IconManager;
import omega.utils.Editor;
import omega.utils.UIManager;
import omega.utils.JetRunPanel;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Scanner;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class RunView extends View {
	
	public String mainClassPath = null;
	public String mainClass = "";
	
	private String statusX = "No Errors";
	
	public static String NATIVE_PATH = "";
	
	private static String errorlog = "";
	
	public LinkedList<Process> runningApps = new LinkedList<>();
	
	private BuildLog buildLog;

	private Process compileProcess;
	
	private volatile boolean wasKilled = false;
	
	public RunView(String title, Screen window, boolean canRun) {
		super(title, window);
		buildLog = new BuildLog();
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	public void setMainClassPath(String mainClassPath) {
		if(!mainClassPath.endsWith(".java"))
			return;
		mainClass = "";
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(mainClassPath, File.separator);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(canRecord)
				mainClass += token +".";
			else if(token.equals("src"))
				canRecord = true;
		}
		mainClass = mainClass.substring(0, mainClass.length() - 6);
	}
	
	public void setMainClass() {
		if(getScreen().getCurrentEditor().currentFile == null)
			return;
		mainClassPath = getScreen().getCurrentEditor().currentFile.getAbsolutePath();
		if(!mainClassPath.endsWith(".java"))
			return;
		mainClass = "";
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(mainClassPath, File.separator);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(canRecord)
				mainClass += token + ".";
			else if(token.equals("src"))
				canRecord = true;
		}
		mainClass = mainClass.substring(0, mainClass.length() - 6);
	}
	
	public void runNJ(){
		new Thread(()->{
			getScreen().getToolMenu().buildComp.setClickable(false);
			getScreen().getToolMenu().runComp.setClickable(false);
			getScreen().getOperationPanel().removeTab("Build");
			omega.Screen.getFileView().getArgumentManager().genLists();
			LinkedList<String> args = Screen.getFileView().getArgumentManager().compile_time_args;
			if(Screen.isNotNull(Screen.getFileView().getArgumentManager().getCompileCommand())){
				Screen.setStatus("Building Project", 45, IconManager.fluentbuildImage);
				String compileDir = Screen.getFileView().getArgumentManager().compileDir;
				
				String[] commandsAsArray = new String[args.size()];
				for(int i = 0; i < args.size(); i++){
					commandsAsArray[i] = args.get(i);
				}
				
				JetRunPanel printArea = new JetRunPanel(false, commandsAsArray, compileDir);
				printArea.setLogMode(true);
				try {
					printArea.printText("Building Project ...");
					printArea.printText("Executing ... " + Screen.getFileView().getArgumentManager().getCompileCommand());
					
					printArea.start();
					
					runningApps.add(printArea.terminalPanel.process);

					Screen.setStatus("Building Project -- Double Click to kill this process", 70, IconManager.fluentbuildImage);
					Screen.getScreen().getBottomPane().setDoubleClickAction(()->{
						Screen.setStatus("Killing Build Process", 10, IconManager.closeImage);
						if(printArea.terminalPanel.process.isAlive())
							printArea.terminalPanel.process.destroyForcibly();
						Screen.setStatus("", 100, null);
					});

					errorlog = "";
					
					while(printArea.terminalPanel.process.isAlive());

					errorlog = printArea.getText();
					
					Screen.setStatus("Building Project", 100, null);
					
					ErrorHighlighters.resetAllErrors();
					
					printArea.printText("Compilation Finished with Exit Code " + printArea.terminalPanel.process.exitValue());
					
					if(printArea.terminalPanel.process.exitValue() != 0){
						if(ErrorHighlighters.isLoggerPresentForCurrentLang()){
							ErrorHighlighters.showErrors(errorlog);
							getScreen().getBottomPane().setShowLogAction(()->{
								getScreen().getOperationPanel().addTab("Build", IconManager.fluenttesttubeImage, printArea, ()->printArea.killProcess());
								getScreen().getBottomPane().setShowLogAction(null);
							});
						}
						else{
							getScreen().getBottomPane().setShowLogAction(null);
							getScreen().getOperationPanel().addTab("Build", IconManager.fluenttesttubeImage, printArea, ()->printArea.killProcess());
						}
						getScreen().getToolMenu().buildComp.setClickable(true);
						getScreen().getToolMenu().runComp.setClickable(true);
						return;
					}
					getScreen().getBottomPane().setShowLogAction(null);
				}
				catch(Exception e){
					printArea.printText("Compilation Failed!");
					printArea.printText("System was unable to find the specified command.");
					printArea.printText(e.toString());
					e.printStackTrace();
				}
				finally{
					getScreen().getToolMenu().buildComp.setClickable(true);
					getScreen().getToolMenu().runComp.setClickable(true);
					Screen.setStatus("", 100, null);
				}
			}
			
			getScreen().getToolMenu().buildComp.setClickable(true);
			
			justRunNJ();
		}).start();
	}
	
	public void justRunNJ(){
		new Thread(()->{
			getScreen().getToolMenu().runComp.setClickable(false);
			getScreen().getOperationPanel().removeTab("Build");
			omega.Screen.getFileView().getArgumentManager().genLists();
			LinkedList<String> args = Screen.getFileView().getArgumentManager().run_time_args;
			
			Screen.setStatus("Running Project", 56, IconManager.fluentrunImage);
			String status = "Successfully";
			
			String name = "Run";
			int count = OperationPane.count(name);
			if(count > -1)
				name = name + " " + count;

			if(!Screen.isNotNull(Screen.getFileView().getArgumentManager().getRunCommand())){
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

			String[] commandsAsArray = new String[args.size()];
			for(int i = 0; i < args.size(); i++){
				commandsAsArray[i] = args.get(i);
			}
			
			String runDir = Screen.getFileView().getArgumentManager().runDir;
			JetRunPanel terminal = new JetRunPanel(false, commandsAsArray, runDir);
			try{
				terminal.printText("Running Project...\n" + Screen.getFileView().getArgumentManager().getRunCommand());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");

				terminal.start();
				
				getScreen().getOperationPanel().addTab(name, IconManager.fluentquickmodeonImage, terminal, terminal::killProcess);
				
				runningApps.add(terminal.terminalPanel.process);
				
				Screen.setStatus("Running Project", 100, null);
				getScreen().getToolMenu().runComp.setClickable(true);
				
				new Thread(()->{
					while(terminal.terminalPanel.process != null && terminal.terminalPanel.process.isAlive());
					terminal.printText("---<>--------------------------------------<>---");
					if(terminal.terminalPanel.process != null)
						terminal.printText("Program Execution finished with Exit Code " + terminal.terminalPanel.process.exitValue());
					else
						terminal.printText("Unable to Run the Specified Command!");
					
					runningApps.remove(terminal.terminalPanel.process);
					Screen.getProjectView().reload();
				}).start();
				
			}
			catch(Exception e){
				terminal.printText("Enter commands correctly!");
				terminal.printText(e.toString());
				terminal.printText("---<>--------------------------------------<>---");
				e.printStackTrace();
			}
			finally{
				getScreen().getToolMenu().runComp.setClickable(true);
				Screen.setStatus("", 100, null);
			}
		}).start();
	}
	
	public void justRun(){
		getScreen().saveAllEditors();
		if(omega.Screen.getFileView().getProjectManager().non_java){
			justRunNJ();
			return;
		}
		new Thread(()->{
			String mainClass = this.mainClass;
			String mainClassPath = this.mainClassPath;
			
			if(!Screen.isNotNull(mainClass)){
				NotificationPopup.create(getScreen())
				.size(500, 120)
				.title("Project Management")
				.message("No Main-Class Specified, Cancelling Run Operation", TOOLMENU_COLOR2)
				.shortMessage("Click Editor's Tab Icon to Mark a class as Main-Class", TOOLMENU_COLOR1)
				.dialogIcon(IconManager.fluentfolderImage)
				.build()
				.locateOnBottomLeft()
				.showIt();
				return;
			}
			try {
				Screen.setStatus("Running Project", 23, IconManager.fluentrunImage);
				
				NATIVE_PATH = "";
				for(String d : Screen.getFileView().getProjectManager().natives) {
					NATIVE_PATH += d + omega.Screen.PATH_SEPARATOR;
				}
				
				if(!NATIVE_PATH.equals("")) {
					NATIVE_PATH = NATIVE_PATH.substring(0, NATIVE_PATH.length() - 1);
					NATIVE_PATH = NATIVE_PATH + omega.Screen.PATH_SEPARATOR + "$PATH";
				}
				else
					NATIVE_PATH = "$PATH";
				
				String depenPath = "";
				for(String d : Screen.getFileView().getProjectManager().jars)
					depenPath += d + omega.Screen.PATH_SEPARATOR;
				for(String d : Screen.getFileView().getProjectManager().resourceRoots)
					depenPath += d + omega.Screen.PATH_SEPARATOR;
				if(!depenPath.equals("")) {
					depenPath = depenPath.substring(0, depenPath.length() - 1);
				}
				
				
				Screen.setStatus("Running Project", 56, IconManager.fluentrunImage);
				if(Screen.getFileView().getProjectManager().jdkPath == null){
					getScreen().getToolMenu().runComp.setClickable(true);
					Screen.setStatus("Please Setup the Project JDK First!", 99, IconManager.fluentbrokenbotImage);
					return;
				}
				
				String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
				String cmd = null;
				if(jdkPath != null && new File(jdkPath).exists())
					cmd = jdkPath + File.separator + "bin" + File.separator + "java";
				else
					cmd = "java" + cmd;
				
				File workingDir = new File(Screen.getFileView().getProjectPath() + File.separator + "bin");
				LinkedList<String> commands = new LinkedList<>();
				commands.add(cmd);
				
				if(Screen.isNotNull(depenPath)){
					commands.add("-classpath");
					commands.add(depenPath + File.pathSeparator + ".");
				}
				
				String modulePath = Screen.getFileView().getDependencyView().getModulePath();
				String modules = Screen.getFileView().getDependencyView().getModules();
				if(Screen.isNotNull(modulePath)){
					commands.add("--module-path");
					commands.add(modulePath);
					commands.add("--add-modules");
					commands.add(modules);
				}
				
				if(Screen.isNotNull(NATIVE_PATH))
					commands.add("-Djava.library.path=" + NATIVE_PATH);
				
				Screen.getFileView().getProjectManager().runTimeFlags.forEach(commands::add);
				
				commands.add(mainClass);
				
				String[] commandsAsArray = new String[commands.size()];
				
				int k = -1;
				for(String command : commands)
					commandsAsArray[++k] = command;
				
				JetRunPanel terminal = new JetRunPanel(true, commandsAsArray, workingDir.getAbsolutePath());
				
				terminal
				.reRunAction(()->{
					terminal.killProcess();
					run();
				})
				.reRunDynamicallyAction(()->{
					terminal.killProcess();
					instantRun();
				});
				
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				
				runningApps.add(terminal.terminalPanel.process);
				
				String name = "Run("+mainClass;
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;
				name =  name + ")";
				
				getScreen().getOperationPanel().addTab(name, IconManager.fluentquickmodeonImage, terminal, terminal::killProcess);
				terminal.start();
				
				Screen.setStatus("Running Project", 100, null);
				
				new Thread(()->{
					while(terminal.terminalPanel.process != null && terminal.terminalPanel.process.isAlive());
					terminal.printText("---<>--------------------------------------<>---");
					
					if(terminal.terminalPanel.process != null)
						terminal.printText("Program Execution finished with Exit Code " + terminal.terminalPanel.process.exitValue());
					else
						terminal.printText("Unable to Run the Specified Command!");
					
					runningApps.remove(terminal.terminalPanel.process);
					Screen.getProjectView().reload();
				}).start();
				
				getScreen().getToolMenu().runComp.setClickable(true);
			}
			catch(Exception e) {
				
				e.printStackTrace();
			}
		}).start();
	}
	
	public boolean isRunCapable(File dir){
		boolean value = false;
		File[] F = dir.listFiles();
		if(F != null && F.length != 0){
			for(File f : F){
				if(f.isDirectory())
					value = isRunCapable(f);
				else if(f.getName().endsWith(".class")){
					value = true;
					break;
				}
			}
		}
		return value;
	}
	
	public void instantBuild(){
		getScreen().saveAllEditors();
		if(omega.Screen.getFileView().getProjectManager().non_java || JavaSyntaxParser.packingCodes){
			return;
		}
		
		new Thread(()->{
			try{
				if(!JDKManager.isJDKPathValid(Screen.getFileView().getProjectManager().jdkPath)){
					Screen.setStatus("Please first select a valid JDK for the project", 10, IconManager.fluentrocketbuildImage);
					return;
				}
				
				File fx = new File(".omega-ide", ".firstaccuracybuild");
				if(!fx.exists()){
					fx.createNewFile();
					NotificationPopup.create(getScreen())
					.size(530, 120)
					.title("Instant Dynamic Compilation")
					.dialogIcon(IconManager.fluentrocketbuildImage)
					.message("Extremely Fast but Accuracy Mode rapidly increases Memory Footprint")
					.shortMessage("Use it only when creating artifacts", TOOLMENU_COLOR1)
					.iconButton(IconManager.fluentinfoImage, ()->omega.utils.ToolMenu.instructionWindow.setVisible(true), "See Instructions For More Detail on Tools")
					.build()
					.locateOnBottomLeft()
					.showIt();
				}
				
				fx = null;
				
				Screen.setStatus("Building Project -- Instant Build", 0, IconManager.fluentrocketbuildImage);
				DiagnosticCollector<JavaFileObject> diagnostics = SyntaxParsers.javaSyntaxParser.compileFullProject();
				if(diagnostics == null){
					Screen.setStatus("", 100, null);
					System.gc();
					return;
				}
				
				boolean passed = true;
				if(diagnostics.getDiagnostics() != null){
					for(Diagnostic d : diagnostics.getDiagnostics()){
						if(d.getKind() == Diagnostic.Kind.ERROR){
							passed = false;
							break;
						}
					}
				}
				else
					passed = false;
				
				if(!passed){
					String errorLog = "";
					
					for(Diagnostic d : diagnostics.getDiagnostics()){
						errorLog += d.toString() + "\n";
					}
					Screen.getErrorHighlighter().removeAllHighlights();
					Screen.getErrorHighlighter().loadErrors(errorLog);
					buildLog.setHeading("Build Resulted in following Error(s)");
					buildLog.genView(errorLog);
					getScreen().getOperationPanel().addTab("Compilation", IconManager.fluentbrokenbotImage, buildLog, ()->{  });
					System.gc();
					return;
				}
				Screen.getErrorHighlighter().removeAllHighlights();
				
				Screen.setStatus("Building Project, Accomplished Successfully -- Instant Build", 0, IconManager.fluentrocketbuildImage);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				System.gc();
			}
		}).start();
	}
	
	public void instantRun(){
		getScreen().saveAllEditors();
		if(omega.Screen.getFileView().getProjectManager().non_java || JavaSyntaxParser.packingCodes){
			return;
		}
		
		new Thread(()->{
			try{
				if(!JDKManager.isJDKPathValid(Screen.getFileView().getProjectManager().jdkPath)){
					Screen.setStatus("Please first select a valid JDK for the project", 10, IconManager.fluentbrokenbotImage);
					return;
				}
				String text = DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED) ? "instant-mode-speed" : "instant-mode-accuracy";
				Screen.setStatus("Building Project -- Instant Run : " + text, 0, IconManager.fluentrocketImage);
				DiagnosticCollector<JavaFileObject> diagnostics = DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED) ? SyntaxParsers.javaSyntaxParser.compileAndSaveToProjectBin() : SyntaxParsers.javaSyntaxParser.compileFullProject();
				
				if(diagnostics == null){
					if(!isRunCapable(new File(Screen.getFileView().getProjectPath() + File.separator + "bin"))) {
						NotificationPopup.create(Screen.getScreen())
						.size(400, 120)
						.title("Instant Dynamic Compiler", TOOLMENU_COLOR4)
						.message("Instant Mode Speed Requires Pre-Compiled Byte Codes", TOOLMENU_COLOR2)
						.shortMessage("Click to Start a Headless Build", TOOLMENU_COLOR1)
						.dialogIcon(IconManager.fluenterrorImage)
						.iconButton(IconManager.fluentbuildImage, Screen.getBuildView()::compileProject, "Click to Clean & Build")
						.build()
						.locateOnBottomLeft()
						.showIt();
						
						Screen.setStatus("", 100, null);
						return;
					}
					else{
						justRun();
						NotificationPopup.create(Screen.getScreen())
						.size(350, 120)
						.title("Instant Dynamic Compiler", TOOLMENU_COLOR3)
						.message("No Editor Present in the current session", TOOLMENU_COLOR4)
						.shortMessage("Proceeding Without Build")
						.dialogIcon(IconManager.fluentwarningImage)
						.build()
						.locateOnBottomLeft()
						.showIt();
					}
					System.gc();
					Screen.setStatus("", 100, null);
					return;
				}
				
				boolean passed = true;
				if(diagnostics.getDiagnostics() != null){
					for(Diagnostic d : diagnostics.getDiagnostics()){
						if(d.getKind() == Diagnostic.Kind.ERROR){
							passed = false;
							break;
						}
					}
				}
				else
					passed = false;
				
				if(!passed){
					String errorLog = "";
					
					for(Diagnostic d : diagnostics.getDiagnostics()){
						errorLog += d.toString() + "\n";
					}
					Screen.getErrorHighlighter().loadErrors(errorLog);
					buildLog.setHeading("Build Resulted in following Error(s)");
					buildLog.genView(errorLog);
					getScreen().getOperationPanel().addTab("Compilation", IconManager.fluentbrokenbotImage, buildLog, ()->{});
					Screen.setStatus("Avoid closing editors after editing else instant run will not be able to run successfully.", 10, IconManager.fluentbrokenbotImage);
					System.gc();
					return;
				}
				Screen.getErrorHighlighter().removeAllHighlights();
				
				if(!isRunCapable(new File(Screen.getFileView().getProjectPath() + File.separator + "bin"))) {
					Screen.setStatus("None Compiled Codes Present, Aborting Instant Run. Rebuild the Project First -- Instant Run", 0, IconManager.fluentbrokenbotImage);
					System.gc();
					return;
				}
				
				Screen.setStatus("Running Project -- Instant Run", 50, IconManager.fluentrocketImage);
				justRun();
				Screen.setStatus("Running Project", 100, null);
				
				System.gc();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				System.gc();
			}
		}).start();
	}
	
	public void run() {
		getScreen().saveAllEditors();
		System.gc();
		if(omega.Screen.getFileView().getProjectManager().non_java){
			runNJ();
			return;
		}
		new Thread(()->{
			String mainClass = this.mainClass;
			String mainClassPath = this.mainClassPath;
			
			if(!Screen.isNotNull(mainClass)){
				NotificationPopup.create(getScreen())
				.size(500, 120)
				.title("Project Management")
				.message("No Main-Class Specified, Cancelling Run Operation", TOOLMENU_COLOR2)
				.shortMessage("Click Editor's Tab Icon to Mark a class as Main-Class", TOOLMENU_COLOR1)
				.dialogIcon(IconManager.fluentfolderImage)
				.build()
				.locateOnBottomLeft()
				.showIt();
				return;
			}
			
			try {
				try {
					if(!JDKManager.isJDKPathValid(Screen.getFileView().getProjectManager().jdkPath)){
						Screen.setStatus("Please first select a valid JDK for the project", 10, IconManager.fluentbrokenbotImage);
						return;
					}
					Screen.getBuildView().createClassList();
					if(Screen.getBuildView().classess.isEmpty())
						return;
					getScreen().getOperationPanel().removeTab("Compilation");
					Screen.getErrorHighlighter().removeAllHighlights();
					BuildView.optimizeProjectOutputs();
					getScreen().getToolMenu().buildComp.setClickable(false);
					getScreen().getToolMenu().runComp.setClickable(false);
					errorlog = "";
					
					int percent = 70;
					
					wasKilled = false;
					Screen.setStatus("Building Project -- Double Click to kill this process", percent, IconManager.fluentrunImage);
					Screen.getScreen().getBottomPane().setDoubleClickAction(()->{
						new Thread(()->{
							wasKilled = true;
							Screen.setStatus("Killing Build Process", 10, IconManager.fluentcloseImage);
							if(compileProcess != null && compileProcess.isAlive())
								compileProcess.destroyForcibly();
							Screen.setStatus("", 100, null);
							getScreen().getOperationPanel().removeTab("Compilation");
						}).start();
					});
					
					String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
					if(jdkPath != null && new File(jdkPath).exists())
						jdkPath = String.copyValueOf(jdkPath.toCharArray()) + File.separator + "bin" + File.separator;
					
					String cmd = "";
					String depenPath = "";
					if(!Screen.getFileView().getProjectManager().jars.isEmpty()) {
						for(String d : Screen.getFileView().getProjectManager().jars) {
							depenPath += d + omega.Screen.PATH_SEPARATOR;
						}
					}
                    if(!Screen.getFileView().getProjectManager().resourceRoots.isEmpty()) {
                         for(String d : Screen.getFileView().getProjectManager().resourceRoots)
                              depenPath += d + omega.Screen.PATH_SEPARATOR;
                    }
					if(!depenPath.equals("")) {
						depenPath = depenPath.substring(0, depenPath.length() - 1);
					}
					
					if(jdkPath != null && new File(jdkPath).exists())
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
					
					runningApps.add(compileProcess);
					
					Scanner errorReader = new Scanner(compileProcess.getErrorStream());
					while(compileProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							errorlog += errorReader.nextLine() + "\n";
						}
					}
					errorReader.close();
					
					Screen.setStatus("Building Project -- Double Click to kill this process", 90, IconManager.fluentrunImage);
					getScreen().getToolMenu().buildComp.setClickable(true);
					Screen.getProjectView().reload();
					if(compileProcess.exitValue() != 0) {
						Screen.setStatus("", 100, null);
						runningApps.remove(compileProcess);
						getScreen().getToolMenu().runComp.setClickable(true);
						if(!wasKilled) {
							Screen.getErrorHighlighter().loadErrors(errorlog);
							buildLog.setHeading("Build Resulted in following Error(s)");
							buildLog.genView(errorlog);
							getScreen().getOperationPanel().addTab("Compilation", IconManager.fluentbrokenbotImage, buildLog, ()->{  });
						}
						return;
					}
					Screen.getErrorHighlighter().removeAllHighlights();
				}
				catch(Exception e2) {
					e2.printStackTrace();
				}
				
				getScreen().getOperationPanel().removeTab("Compilation");
				
				Screen.setStatus("Building Project Completed", 100, null);
				
				System.gc();
				
				Screen.setStatus("Running Project", 23, IconManager.fluentrunImage);
				
				NATIVE_PATH = "";
				for(String d : Screen.getFileView().getProjectManager().natives) {
					NATIVE_PATH += d + omega.Screen.PATH_SEPARATOR;
				}
				if(!NATIVE_PATH.equals("")) {
					NATIVE_PATH = NATIVE_PATH.substring(0, NATIVE_PATH.length() - 1);
					NATIVE_PATH = NATIVE_PATH + omega.Screen.PATH_SEPARATOR + "$PATH";
				}
				else
					NATIVE_PATH = "$PATH";
				
				String depenPath = "";
				for(String d : Screen.getFileView().getProjectManager().jars)
					depenPath += d + omega.Screen.PATH_SEPARATOR;
				for(String d : Screen.getFileView().getProjectManager().resourceRoots)
					depenPath += d + omega.Screen.PATH_SEPARATOR;
				if(!depenPath.equals(""))
					depenPath = depenPath.substring(0, depenPath.length() - 1);
				
				
				Screen.setStatus("Running Project", 56, IconManager.fluentrunImage);
				if(Screen.getFileView().getProjectManager().jdkPath == null){
					getScreen().getToolMenu().runComp.setClickable(true);
					Screen.setStatus("Please Setup the Project JDK First!", 99, IconManager.fluentbrokenbotImage);
					return;
				}
				String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
				String cmd = null;
				if(jdkPath != null && new File(jdkPath).exists())
					cmd = jdkPath + File.separator + "bin" + File.separator + "java";
				else
					cmd = "java" + cmd;
				
				File workingDir = new File(Screen.getFileView().getProjectPath() + File.separator + "bin");
				LinkedList<String> commands = new LinkedList<>();
				commands.add(cmd);
				
				if(Screen.isNotNull(depenPath)){
					commands.add("-classpath");
					commands.add(depenPath + File.pathSeparator + ".");
				}
				
				String modulePath = Screen.getFileView().getDependencyView().getModulePath();
				String modules = Screen.getFileView().getDependencyView().getModules();
				if(Screen.isNotNull(modulePath)){
					commands.add("--module-path");
					commands.add(modulePath);
					commands.add("--add-modules");
					commands.add(modules);
				}
				
				if(Screen.isNotNull(NATIVE_PATH))
					commands.add("-Djava.library.path=" + NATIVE_PATH);
				
				Screen.getFileView().getProjectManager().runTimeFlags.forEach(commands::add);
				
				commands.add(mainClass);
				
				String[] commandsAsArray = new String[commands.size()];
				
				int k = -1;
				for(String command : commands)
					commandsAsArray[++k] = command;
				
				JetRunPanel terminal = new JetRunPanel(true, commandsAsArray, workingDir.getAbsolutePath());
				terminal
				.reRunAction(()->{
					terminal.killProcess();
					run();
				})
				.reRunDynamicallyAction(()->{
					terminal.killProcess();
					instantRun();
				});
				runningApps.add(terminal.terminalPanel.process);
				
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				
				String name = "Run("+mainClass;
				int count = OperationPane.count(name);
				if(count > -1) {
					name = name + " " + count;
				}
				name =  name + ")";
				
				getScreen().getOperationPanel().addTab(name, IconManager.fluentquickmodeonImage, terminal, terminal::killProcess);
				terminal.start();
				
				Screen.setStatus("Running Project", 100, null);
				
				new Thread(()->{
					while(terminal.terminalPanel.process.isAlive());
					terminal.printText("---<>--------------------------------------<>---");
					terminal.printText("Program Execution finished with Exit Code " + terminal.terminalPanel.process.exitValue());
					runningApps.remove(terminal.terminalPanel.process);
					Screen.getProjectView().reload();
				}).start();
								
				getScreen().getToolMenu().runComp.setClickable(true);
			}
			catch(Exception e) {e.printStackTrace();}
		}).start();
	}
}

