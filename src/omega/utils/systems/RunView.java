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
import omega.utils.RunPanel;

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
	
	private Process compileProcess = null;
	private Process runProcess = null;
	
	private BuildLog buildLog;
	
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
				RunPanel printArea = new RunPanel(false);
				printArea.setLogMode(true);
				try {
					Process compileProcess = new ProcessBuilder(args).directory(new File(compileDir)).start();
					Scanner errorReader = new Scanner(compileProcess.getErrorStream());
					Scanner inputReader = new Scanner(compileProcess.getInputStream());
					printArea.setProcess(compileProcess);
					printArea.printText("Building Project ...");
					printArea.printText("Executing ... " + Screen.getFileView().getArgumentManager().getCompileCommand());
					
					runningApps.add(compileProcess);
					
					Screen.setStatus("Building Project -- Double Click to kill this process", 70, IconManager.fluentbuildImage);
					Screen.getScreen().getBottomPane().setDoubleClickAction(()->{
						Screen.setStatus("Killing Build Process", 10, IconManager.closeImage);
						if(compileProcess.isAlive())
							compileProcess.destroyForcibly();
						Screen.setStatus("", 100, null);
					});

					errorlog = "";
					
					new Thread(()->{
						while(compileProcess.isAlive()) {
							while(inputReader.hasNextLine()) {
								String data = inputReader.nextLine();
								errorlog += data + "\n";
								printArea.printText(data);
							}
						}
						try{
			                    if(compileProcess.getInputStream().available() > 0){
			                    	while(inputReader.hasNextLine()) {
									String data = inputReader.nextLine();
			                    		errorlog += data + "\n";
			                              printArea.print(data);
			                         }
			                    }
		                    }
		                    catch(Exception e){
		                    	
		                    }
						inputReader.close();
					}).start();
					while(compileProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							String line = errorReader.nextLine();
							errorlog += line + "\n";
							printArea.printText(line);
						}
					}
					
					try{
		                    if(compileProcess.getErrorStream().available() > 0){
		                    	while(errorReader.hasNextLine()) {
		                              String line = errorReader.nextLine();
								errorlog += line + "\n";
								printArea.printText(line);
		                         }
		                    }
	                    }
	                    catch(Exception e){
	                    	
	                    }
					errorReader.close();
					
					Screen.setStatus("Building Project", 100, null);
					
					ErrorHighlighters.resetAllErrors();
					
					if(compileProcess.exitValue() != 0){
						if(ErrorHighlighters.isLoggerPresentForCurrentLang()){
							ErrorHighlighters.showErrors(errorlog);
							getScreen().getBottomPane().setShowLogAction(()->{
								getScreen().getOperationPanel().addTab("Build", printArea, ()->printArea.killProcess());
								getScreen().getBottomPane().setShowLogAction(null);
							});
						}
						else{
							getScreen().getBottomPane().setShowLogAction(null);
							getScreen().getOperationPanel().addTab("Build", printArea, ()->printArea.killProcess());
						}
						getScreen().getToolMenu().buildComp.setClickable(true);
						getScreen().getToolMenu().runComp.setClickable(true);
						printArea.printText("Compilation Finished with Exit Code " + compileProcess.exitValue());
						return;
					}
					getScreen().getBottomPane().setShowLogAction(null);
				}
				catch(Exception e){ 
					printArea.printText("Compilation Failed!\nSystem was unable to find the specified command!");
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
			
			Screen.setStatus("Running Project", 56, IconManager.fluentrunImage);
			RunPanel terminal = new RunPanel(false);
			try{
				args = Screen.getFileView().getArgumentManager().run_time_args;
				terminal.printText("Running Project...\n" + Screen.getFileView().getArgumentManager().getRunCommand());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				String status = "Successfully";
				
				String name = "Run";
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;

				getScreen().getOperationPanel().addTab(name, terminal, terminal::killProcess);
				
				if(!Screen.isNotNull(Screen.getFileView().getArgumentManager().getRunCommand())){
					terminal.printText("\'No Run Time Command Specified!!!\'");
					terminal.printText("Click \"Settings\", then Click \"All Settings\"");
					terminal.printText("And Specify the Run Time Args or Command");
					return;
				}
				String runDir = Screen.getFileView().getArgumentManager().runDir;
				
				Process runProcess = new ProcessBuilder(args).directory(new File(runDir)).start();
				terminal.setProcess(runProcess);
				
				runningApps.add(runProcess);
				
				Screen.setStatus("Running Project", 100, null);
				getScreen().getToolMenu().runComp.setClickable(true);
				
				new Thread(()->{
					try(Scanner errorReader = new Scanner(runProcess.getErrorStream())){
						while(runProcess.isAlive()) {
							while(errorReader.hasNextLine()) {
								terminal.printText(errorReader.nextLine());
							}
						}
		                    if(runProcess.getErrorStream().available() > 0){
		                    	while(errorReader.hasNextLine()) {
		                              terminal.print(errorReader.nextLine());
		                         }
		                    }
	                    }
	                    catch(Exception e){
	                    	e.printStackTrace();
	                    }
				}).start();
				
				try(Scanner inputReader = new Scanner(runProcess.getInputStream())){
					while(runProcess.isAlive()) {
						while(inputReader.hasNextLine()) {
							terminal.printText(inputReader.nextLine());
						}
					}
	                    if(runProcess.getInputStream().available() > 0){
	                    	while(inputReader.hasNextLine()) {
	                              terminal.printText(inputReader.nextLine());
	                         }
	                    }
                    }
                    catch(Exception e){
                    	e.printStackTrace();
                    }
				runningApps.remove(runProcess);
				Screen.getProjectView().reload();
				terminal.printText("---<>--------------------------------------<>---");
				terminal.printText("Program Execution finished with Exit Code " + runProcess.exitValue());
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
	
	public void justRunNJ(){
		new Thread(()->{
			getScreen().getToolMenu().runComp.setClickable(false);
			getScreen().getOperationPanel().removeTab("Build");
			omega.Screen.getFileView().getArgumentManager().genLists();
			LinkedList<String> args = Screen.getFileView().getArgumentManager().run_time_args;
			
			Screen.setStatus("Running Project", 56, IconManager.fluentrunImage);
			RunPanel terminal = new RunPanel(false);
			try{
				terminal.printText("Running Project...\n" + Screen.getFileView().getArgumentManager().getRunCommand());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				String status = "Successfully";
				
				String name = "Run";
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;
				
				getScreen().getOperationPanel().addTab(name, terminal, terminal::killProcess);
				
				if(!Screen.isNotNull(Screen.getFileView().getArgumentManager().getRunCommand())){
					terminal.printText("\'No Run Time Command Specified!!!\'");
					terminal.printText("Click \"Settings\", then Click \"All Settings\"");
					terminal.printText("And Specify the Run Time Args or Command");
					return;
				}
				String runDir = Screen.getFileView().getArgumentManager().runDir;
				
				Process runProcess = new ProcessBuilder(args).directory(new File(runDir)).start();
				terminal.setProcess(runProcess);
				
				runningApps.add(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				
				Screen.setStatus("Running Project", 100, null);
				getScreen().getToolMenu().runComp.setClickable(true);
				
				new Thread(()->{
					String statusX = "No Errors";
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							if(!statusX.equals("Errors")) 
								statusX = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					try{
		                    if(runProcess.getErrorStream().available() > 0){
		                    	while(errorReader.hasNextLine()) {
		                              terminal.print(errorReader.nextLine());
		                         }
		                    }
	                    }
	                    catch(Exception e){
	                    	e.printStackTrace();
	                    }
					errorReader.close();
				}).start();
				
				while(runProcess.isAlive()) {
					while(inputReader.hasNextLine()) {
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				try{
	                    if(runProcess.getInputStream().available() > 0){
	                    	while(inputReader.hasNextLine()) {
	                              terminal.print(inputReader.nextLine());
	                         }
	                    }
                    }
                    catch(Exception e){
                    	e.printStackTrace();
                    }
				inputReader.close();
				runningApps.remove(runProcess);
				Screen.getProjectView().reload();
				terminal.printText("---<>--------------------------------------<>---");
				terminal.printText("Program Execution finished with " + statusX);
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
				
				RunPanel terminal = new RunPanel(true);
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				
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
				
				runProcess = new ProcessBuilder(commandsAsArray).directory(workingDir).start();
				
				runningApps.add(runProcess);
				
				String name = "Run("+mainClass;
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;
				name =  name + ")";
				
				getScreen().getOperationPanel().addTab(name, terminal, terminal::killProcess);
				
				terminal.setProcess(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				
				terminal.setVisible(true);
				
				Screen.setStatus("Running Project", 100, null);
				
				new Thread(()->{
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							terminal.printText(errorReader.nextLine());
						}
					}
					errorReader.close();
				}).start();
				
				getScreen().getToolMenu().runComp.setClickable(true);
				while(runProcess.isAlive()) {
					while(inputReader.hasNextLine()) {
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				inputReader.close();
				runningApps.remove(runProcess);
				Screen.getProjectView().reload();
				terminal.printText("---<>--------------------------------------<>---");
				terminal.printText("Program Execution finished with Exit Code " + runProcess.exitValue());
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
					getScreen().getOperationPanel().addTab("Compilation", buildLog, ()->{  });
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
					getScreen().getOperationPanel().addTab("Compilation", buildLog, ()->{});
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
							getScreen().getOperationPanel().addTab("Compilation", buildLog, ()->{  });
						}
						return;
					}
					Screen.getErrorHighlighter().removeAllHighlights();
				}
				catch(Exception e2) {
					e2.printStackTrace();
				}
				
				getScreen().getOperationPanel().removeTab("Compilation");
				
				if(mainClass == null) {
					RunPanel p = new RunPanel(true);
					getScreen().getOperationPanel().addTab("No Main Config", p, p::killProcess);
					
					if(mainClassPath.equals(Screen.getFileView().getProjectPath() + File.separator + "src.java"))
						p.printText("\"No Main Class Defined for the Project!\" \n\t or \n \"Defined Main Class does not exits!\"");
					
					Screen.setStatus("Running Project Failed", 100, null);
					getScreen().getToolMenu().runComp.setClickable(true);
					return;
				}
				
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
				
				RunPanel terminal = new RunPanel(true);
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				
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
				
				runProcess = new ProcessBuilder(commandsAsArray).directory(workingDir).start();
				
				runningApps.add(runProcess);
				String name = "Run("+mainClass;
				int count = OperationPane.count(name);
				if(count > -1) {
					name = name + " " + count;
				}
				name =  name + ")";
				
				getScreen().getOperationPanel().addTab(name, terminal, terminal::killProcess);
				terminal.setProcess(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				Screen.setStatus("Running Project", 100, null);
				
				new Thread(()->{
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							terminal.printText(errorReader.nextLine());
						}
					}
					errorReader.close();
				}).start();
				
				getScreen().getToolMenu().runComp.setClickable(true);
				while(runProcess.isAlive()) 	{
					while(inputReader.hasNextLine()) {
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				inputReader.close();
				runningApps.remove(runProcess);
				Screen.getProjectView().reload();
				terminal.printText("---<>--------------------------------------<>---");
				terminal.printText("Program Execution finished with Exit Code " + runProcess.exitValue());
			}
			catch(Exception e) {e.printStackTrace();}
		}).start();
	}
	
	public class RunPanel extends JPanel {
		private FlexPanel actionPanel;
		private TextComp runComp;
		private TextComp instantRunComp;
		private TextComp clearComp;
		private TextComp killComp;

		private FlexPanel runTextAreaPanel;
		private JScrollPane scrollPane;
		private RunTextArea runTextArea;

		private boolean processTerminal;
		private boolean logMode;

		private Process process;
		private PrintWriter writer;
		
		public RunPanel(boolean processTerminal){
			super(null);
			this.processTerminal = processTerminal;
			setBackground(c2);
			init();
		}
		
		public void init(){
			actionPanel = new FlexPanel(null, back1, null);
			actionPanel.setArc(10, 10);
			add(actionPanel);
			
			runComp = new TextComp(processTerminal ? IconManager.fluentrunImage : IconManager.fluentlaunchImage, 20, 20, "Re-Run",TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::reRun);
			actionPanel.add(runComp);
			
			instantRunComp = new TextComp(IconManager.fluentrocketImage, 20, 20, "Re-Run(Dynamic)", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::reRunDynamically);
			instantRunComp.setVisible(processTerminal);
			actionPanel.add(instantRunComp);
			
			clearComp = new TextComp(IconManager.fluentclearImage, 20, 20, "Clear Terminal", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::clearTerminal);
			actionPanel.add(clearComp);
			
			killComp = new TextComp(IconManager.fluentcloseImage, 15, 15, "Kill Process", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::killProcess);
			actionPanel.add(killComp);

			runTextAreaPanel = new FlexPanel(null, back1, null);
			runTextAreaPanel.setArc(10, 10);
			scrollPane = new JScrollPane(runTextArea = new RunTextArea());
			runTextAreaPanel.add(scrollPane);
			add(runTextAreaPanel);
		}

		public void reRun(){
			killProcess();
			run();
		}

		public void reRunDynamically(){
			killProcess();
			instantRun();
		}

		public void clearTerminal(){
			runTextArea.setText("");
		}

		public void killProcess(){
			if(process != null && process.isAlive()){
				try{
					process.destroyForcibly();
					writer.close();
				}
				catch(Exception e){
					
				}
			}
		}

		public void setProcess(Process process){
			this.process = process;
			if(!logMode)
				writer = new PrintWriter(process.getOutputStream());
		}

		public void setLogMode(boolean logMode){
			this.logMode = logMode;
			runTextArea.removeKeyListener(runTextArea.getKeyListeners()[0]);
			actionPanel.setVisible(true);
		}

		public void print(String text){
			runTextArea.append(text + "\n");
			layout();
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
		
		public void printText(String text){
			print(text);
		}
		
		public void relocate(){
			if(!logMode){
				actionPanel.setBounds(5, 5, 30, getHeight() - 10);
				runComp.setBounds(3, 5, 25, 25);
				instantRunComp.setBounds(3, 32, 25, 25);
				clearComp.setBounds(3, processTerminal ? 60 : 32, 25, 25);
				killComp.setBounds(3, processTerminal ? 87 : 60, 25, 25);
			}
			if(logMode)
				runTextAreaPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
			else	
				runTextAreaPanel.setBounds(40, 5, getWidth() - 50, getHeight() - 10);
			scrollPane.setBounds(5, 5, runTextAreaPanel.getWidth() - 10, runTextAreaPanel.getHeight() - 10);
		}
		
		@Override
		public void layout(){
			relocate();
			super.layout();
		}

		public class RunTextArea extends RSyntaxTextArea {
			private static volatile boolean ctrl;
			private static volatile boolean l;
			public RunTextArea(){
				Editor.getTheme().apply(this);
				ShellTokenMaker.apply(this);
				addKeyListener(new KeyAdapter(){
					@Override
					public void keyPressed(KeyEvent e){
						int code = e.getKeyCode();
						if(code == KeyEvent.VK_CONTROL)
							ctrl = true;
						else if(code == KeyEvent.VK_L)
							l = true;

						performShortcuts(e);
					}
					@Override
					public void keyReleased(KeyEvent e){
						int code = e.getKeyCode();
						if(code == KeyEvent.VK_CONTROL)
							ctrl = false;
						else if(code == KeyEvent.VK_L)
							l = false;
					}
				});
			}

			public void performShortcuts(KeyEvent e){
				if(process == null)
					return;
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(writer == null || !process.isAlive())
						e.consume();
					
					String text = getText();
					text = text.substring(0, getCaretPosition());
					text = text.substring(text.lastIndexOf('\n') + 1);
					if(Screen.onWindows())
						append("\n");
					writer.println(text);
					writer.flush();
				}
				if(ctrl && l){
					clearTerminal();
					ctrl = false;
					l = false;
					e.consume();
				}
			}
		}
	}
}

