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
import omega.utils.DataManager;
import omega.popup.NotificationPopup;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticCollector;
import omega.instant.support.SyntaxParsers;
import omega.instant.support.java.JavaSyntaxParser;
import omega.utils.PrintArea;
import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;
import omega.utils.IconManager;
import omega.jdk.*;
import omega.utils.BuildLog;
import omega.utils.UIManager;
import omega.Screen;
import omega.comp.TextComp;
import omega.utils.Editor;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;

public class RunView extends View {
	
	private static final long serialVersionUID = 1L;
	
	public String mainClassPath = null;
	public String mainClass = "";
	public static String NATIVE_PATH = "";
	public LinkedList<Process> runningApps = new LinkedList<>();
	private static String errorlog = "";
	private String statusX = "";
	private static PrintArea printA;
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
			String args = Screen.getFileView().getArgumentManager().compile_time_args;
			String shell = "sh";
			if(omega.Screen.PATH_SEPARATOR.equals("\\"))
				shell = "cmd.exe";
			if(!args.trim().equals("")){
				Screen.setStatus("Building Project", 45);
				String compileDir = Screen.getFileView().getArgumentManager().compileDir;
				PrintArea printArea = new PrintArea("Build Output", getScreen());
				try {
					Process compileInShell = new ProcessBuilder(shell).directory(new File(compileDir)).start();
					Scanner errorReader = new Scanner(compileInShell.getErrorStream());
					Scanner inputReader = new Scanner(compileInShell.getInputStream());
					printArea.setRunProcess(compileInShell);
					printArea.printText("Building Project ...");
					printArea.printText("Running ... " + args + " ... Directly in your shell!");
					
					runningApps.add(compileInShell);
					
					Screen.setStatus("Building Project -- Double Click to kill this process", 70);
					Screen.getScreen().getBottomPane().setDoubleClickAction(()->{
						Screen.setStatus("Killing Build Process", 10);
						if(compileInShell.isAlive())
							compileInShell.destroyForcibly();
						Screen.setStatus("", 100);
					});
					PrintWriter writer = new PrintWriter(compileInShell.getOutputStream());
					writer.println(args);
					writer.close();
					
					new Thread(()->{
						statusX = "No Errors";
						while(compileInShell.isAlive()) {
							while(errorReader.hasNextLine()) {
								statusX = "Errors";
								printArea.printText(errorReader.nextLine());
							}
						}
						if(!statusX.contains("No")){
							getScreen().getOperationPanel().addTab("Build", printArea, ()->printArea.stopProcess());
						}
						printArea.printText("Compilation finished with \"" + statusX + "\"");
						errorReader.close();
					}).start();
					
					while(compileInShell.isAlive()) {
						while(inputReader.hasNextLine()) {
							String data = inputReader.nextLine();
							printArea.printText(data);
						}
					}
					inputReader.close();
					errorReader.close();
					Screen.setStatus("Building Project", 100);
					if(!statusX.contains("No")){
						getScreen().getToolMenu().buildComp.setClickable(true);
						getScreen().getToolMenu().runComp.setClickable(true);
						return;
					}
				}
				catch(Exception e){ System.err.println(e); }
			}
			
			getScreen().getToolMenu().buildComp.setClickable(true);
			
			Screen.setStatus("Running Project", 56);
			try{
				args = Screen.getFileView().getArgumentManager().run_time_args;
				PrintArea terminal = new PrintArea("Terminal -Closing This Conlose will terminate Execution", getScreen());
				terminal.printText("Running Project...\n" + args );
				terminal.printText("");
				terminal.printText("If your application does terminates on its own or by pressing the \'x\' button (on left)");
				terminal.printText("Then, In that case you need to manually close it.");
				terminal.printText("---<>--------------------------------------<>---");
				terminal.launchAsTerminal();
				terminal.setVisible(true);
				String status = "Successfully";
				
				String name = "Run";
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;
				
				if(args.trim().equals("")){
					terminal.printText("\'No Run Time Command Specified!!!\'");
					terminal.printText("Click \"Settings\", then Click \"All Settings\"");
					terminal.printText("And Specify the Run Time Args or Command");
					return;
				}
				String runDir = Screen.getFileView().getArgumentManager().runDir;
				
				Process runProcess = new ProcessBuilder(shell).directory(new File(runDir)).start();
				terminal.setRunProcess(runProcess);
				getScreen().getOperationPanel().addTab(name, terminal, ()->terminal.stopProcess());
				
				runningApps.add(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				PrintWriter writer = new PrintWriter(runProcess.getOutputStream());
				writer.println(args);
				writer.println("exit");
				writer.flush();
				
				Screen.setStatus("Running Project", 100);
				getScreen().getToolMenu().runComp.setClickable(true);
				
				new Thread(()->{
					String statusX = "No Errors";
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							if(!statusX.equals("Errors")) statusX = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					terminal.printText("---<>--------------------------------------<>---");
					terminal.printText("Program Execution finished with " + statusX);
					errorReader.close();
				}).start();
				
				while(runProcess.isAlive()) {
					while(inputReader.hasNextLine()) {
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				inputReader.close();
				runningApps.remove(runProcess);
				inputReader.close();
				errorReader.close();
				Screen.getProjectView().reload();
			}
			catch(Exception e){ e.printStackTrace(); }
		}).start();
	}
	
	public void justRunNJ(){
		new Thread(()->{
			getScreen().getToolMenu().runComp.setClickable(false);
			getScreen().getOperationPanel().removeTab("Build");
			omega.Screen.getFileView().getArgumentManager().genLists();
			String args = Screen.getFileView().getArgumentManager().run_time_args;
			String shell = "sh";
			if(omega.Screen.PATH_SEPARATOR.equals("\\"))
				shell = "cmd.exe";
			
			Screen.setStatus("Running Project", 56);
			try{
				PrintArea terminal = new PrintArea("Terminal -Closing This Conlose will terminate Execution", getScreen());
				terminal.printText("Running Project...\n" + args );
				terminal.printText("");
				terminal.printText("If your application does terminates on its own or by pressing the \'x\' button (on left)");
				terminal.printText("Then, In that case you need to manually close it.");
				terminal.printText("---<>--------------------------------------<>---");
				terminal.launchAsTerminal();
				terminal.setVisible(true);
				String status = "Successfully";
				
				String name = "Run";
				int count = OperationPane.count(name);
				if(count > -1)
					name = name + " " + count;
				
				if(args.trim().equals("")){
					terminal.printText("\'No Run Time Command Specified!!!\'");
					terminal.printText("Click \"Settings\", then Click \"All Settings\"");
					terminal.printText("And Specify the Run Time Args or Command");
					return;
				}
				String runDir = Screen.getFileView().getArgumentManager().runDir;
				
				Process runProcess = new ProcessBuilder(shell).directory(new File(runDir)).start();
				terminal.setRunProcess(runProcess);
				getScreen().getOperationPanel().addTab(name, terminal, ()->terminal.stopProcess());
				
				runningApps.add(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				PrintWriter writer = new PrintWriter(runProcess.getOutputStream());
				writer.println(args);
				writer.println("exit");
				writer.flush();
				
				Screen.setStatus("Running Project", 100);
				getScreen().getToolMenu().runComp.setClickable(true);
				
				new Thread(()->{
					String statusX = "No Errors";
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							if(!statusX.equals("Errors")) statusX = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					terminal.printText("---<>--------------------------------------<>---");
					terminal.printText("Program Execution finished with " + statusX);
					errorReader.close();
				}).start();
				
				while(runProcess.isAlive()) {
					while(inputReader.hasNextLine()) {
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				inputReader.close();
				runningApps.remove(runProcess);
				inputReader.close();
				errorReader.close();
				Screen.getProjectView().reload();
			}
			catch(Exception e){ e.printStackTrace(); }
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
			try {
				Screen.setStatus("Running Project", 23);
				
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
				
				PrintArea terminal = new PrintArea("", getScreen());
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				terminal.launchAsTerminal();
				
				Screen.setStatus("Running Project", 56);
				if(Screen.getFileView().getProjectManager().jdkPath == null){
					getScreen().getToolMenu().runComp.setClickable(true);
					Screen.setStatus("Please Setup the Project JDK First!", 99);
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
				
				getScreen().getOperationPanel().addTab(name, terminal, ()->terminal.stopProcess());
				
				terminal.setRunProcess(runProcess);
				
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				
				terminal.setVisible(true);
				
				Screen.setStatus("Running Project", 100);
				
				new Thread(()->{
					String status = "No Errors";
					while(runProcess.isAlive())
						{
						while(errorReader.hasNextLine())
							{
							if(!status.equals("Errors")) status = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					terminal.printText("---<>--------------------------------------<>---");
					terminal.printText("Program Execution finished with " + status);
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
			}
			catch(Exception e) {e.printStackTrace();}
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
					Screen.setStatus("Please first select a valid JDK for the project", 10);
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
					.shortMessage("Use it ony when creating artifacts", TOOLMENU_COLOR1)
					.iconButton(IconManager.fluentinfoImage, ()->omega.utils.ToolMenu.instructionWindow.setVisible(true), "See Instructions For More Detail on Tools")
					.build()
					.locateOnBottomLeft()
					.showIt();
				}

				fx = null;
				
				Screen.setStatus("Building Project -- Instant Build", 0);
				DiagnosticCollector<JavaFileObject> diagnostics = SyntaxParsers.javaSyntaxParser.compileFullProject();
				if(diagnostics == null){
					Screen.setStatus("", 100);
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
				
				Screen.setStatus("Building Project, Accomplished Successfully -- Instant Build", 0);
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
					Screen.setStatus("Please first select a valid JDK for the project", 10);
					return;
				}
				String text = DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED) ? "instant-mode-speed" : "instant-mode-accuracy";
				Screen.setStatus("Building Project -- Instant Run : " + text, 0);
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
						
						Screen.setStatus("", 100);
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
					Screen.setStatus("", 100);
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
					Screen.setStatus("Avoid closing editors after editing else instant run will not be able to run successfully.", 10);
					System.gc();
					return;
				}
				Screen.getErrorHighlighter().removeAllHighlights();

				if(!isRunCapable(new File(Screen.getFileView().getProjectPath() + File.separator + "bin"))) {
					Screen.setStatus("None Compiled Codes Present, Aborting Instant Run. Rebuild the Project First -- Instant Run", 0);
					System.gc();
					return;
				}
				
				Screen.setStatus("Running Project -- Instant Run", 50);
				justRun();
				Screen.setStatus("Running Project", 100);
				
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
			try {
				try {
					if(!JDKManager.isJDKPathValid(Screen.getFileView().getProjectManager().jdkPath)){
						Screen.setStatus("Please first select a valid JDK for the project", 10);
						return;
					}
					if(printA != null)
						getScreen().getOperationPanel().removeTab("Compilation");
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
					Screen.setStatus("Building Project -- Double Click to kill this process", percent);
					Screen.getScreen().getBottomPane().setDoubleClickAction(()->{
						new Thread(()->{
							wasKilled = true;
							Screen.setStatus("Killing Build Process", 10);
							if(compileProcess != null && compileProcess.isAlive())
								compileProcess.destroyForcibly();
							Screen.setStatus("", 100);
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
					
					Screen.setStatus("Building Project -- Double Click to kill this process", 90);
					getScreen().getToolMenu().buildComp.setClickable(true);
					Screen.getProjectView().reload();
					if(compileProcess.exitValue() != 0) {
						Screen.setStatus("", 100);
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
					PrintArea p = new PrintArea("Main Class does not exists", getScreen());
					p.setVisible(true);
					getScreen().getOperationPanel().addTab("No Main Config", p, ()->{p.stopProcess();});
					
					if(mainClassPath.equals(Screen.getFileView().getProjectPath() + File.separator + "src.java"))
						p.printText("\"No Main Class Defined for the Project!\" \n\t or \n \"Defined Main Class does not exits!\"");
					
					Screen.setStatus("Running Project Failed", 100);
					getScreen().getToolMenu().runComp.setClickable(true);
					return;
				}
				
				Screen.setStatus("Building Project Completed", 100);
				
				System.gc();
               	
				Screen.setStatus("Running Project", 23);
				
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
				
				PrintArea terminal = new PrintArea("Terminal "+mainClass+"-Closing This Conlose will terminate Execution", getScreen());
				terminal.printText("running \""+mainClass+"\" with JDK v" + Screen.getFileView().getJDKManager().getVersionAsInt());
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				terminal.launchAsTerminal();
				
				Screen.setStatus("Running Project", 56);
				if(Screen.getFileView().getProjectManager().jdkPath == null){
					getScreen().getToolMenu().runComp.setClickable(true);
					Screen.setStatus("Please Setup the Project JDK First!", 99);
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
				getScreen().getOperationPanel().addTab(name, terminal, ()->terminal.stopProcess());
				terminal.setRunProcess(runProcess);
				Scanner inputReader = new Scanner(runProcess.getInputStream());
				Scanner errorReader = new Scanner(runProcess.getErrorStream());
				terminal.setVisible(true);
				Screen.setStatus("Running Project", 100);
				
				new Thread(()->{
					String status = "No Errors";
					while(runProcess.isAlive()) {
						while(errorReader.hasNextLine()) {
							if(!status.equals("Errors")) status = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					terminal.printText("---<>--------------------------------------<>---");
					terminal.printText("Program Execution finished with " + status);
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
			}
			catch(Exception e) {e.printStackTrace();}
		}).start();
	}
	
	public class PrintArea extends JPanel {
		
		private static final long serialVersionUID = 1L;
		private RSyntaxTextArea textArea;
		private Process runProcess;
		private JScrollPane p;
		
		public PrintArea(String title, Screen window) {
			setLayout(new BorderLayout());
			setPreferredSize(getSize());
			init();
		}
		
		private void init() {
			textArea = new RSyntaxTextArea("......................");
			MarkdownTokenMaker.apply(textArea);
			textArea.setAutoscrolls(true);
			textArea.setCaretColor(java.awt.Color.WHITE);
			Editor.getTheme().apply(textArea);
			textArea.setFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
			textArea.setHighlightCurrentLine(false);
			p = new JScrollPane(textArea);
			p.setAutoscrolls(true);
			add(p, BorderLayout.CENTER);
			comps.add(textArea);
		}
		
		@Override
		public void setVisible(boolean v) {
			if(v) {
				UIManager.setData(PrintArea.this);
			}
			super.setVisible(v);
		}
		
		public void setRunProcess(Process p) {
			runProcess = p;
		}
		
		public void stopProcess() {
			if(runProcess != null && runProcess.isAlive()) {
				runProcess.destroyForcibly();
				runProcess.destroyForcibly();
				printText("\"Destroy Command\" Sent to process id \"" + runProcess.pid() + "\"");
			}
		}
		
		@Override
		public void addMouseListener(MouseListener l) {
			super.addMouseListener(l);
			textArea.addMouseListener(l);
		}
		
		public void launchAsTerminal() {
			setAction(()->{
				textArea.setEditable(false);
				UIManager.setData(PrintArea.this);
			});
			JTextField inputField = new JTextField();
			inputField.setText("Input? From Here");
			inputField.addActionListener((e)->{
				if(runProcess == null || !runProcess.isAlive())
					return;
				try {
					PrintWriter writer = new PrintWriter(runProcess.getOutputStream());
					writer.println(inputField.getText());
					writer.flush();
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}
				inputField.setText("");
			});
			inputField.setCaretColor(omega.utils.UIManager.glow);
			inputField.setBackground(omega.utils.UIManager.c2);
			inputField.setForeground(omega.utils.UIManager.glow);
			inputField.setFont(omega.utils.UIManager.PX16);
			add(inputField, BorderLayout.SOUTH);
			comps.add(inputField);
			
			ActionCenter actionCenter = new ActionCenter(()->{stopProcess();run();} ,()->stopProcess());
			add(actionCenter, BorderLayout.WEST);
			comps.add(actionCenter);
		}
		
		public void printText(String text) {
			if(textArea.getText().equals(""))
				textArea.append(text);
			else
				textArea.append("\n"+text);
			textArea.setCaretPosition(textArea.getText().length());
		}
		
		private class ActionCenter extends JComponent{
			protected ActionCenter(Runnable r, Runnable r0) {
				setLayout(new FlowLayout());
				UIManager.setData(this);
				setPreferredSize(new Dimension(40, 100));
				Dimension size = new Dimension(30, 30);
				
				TextComp runComp = new TextComp(IconManager.fluentrunImage, 25, 25, "Re-Run", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, r);
				runComp.setFont(omega.utils.UIManager.PX18);
				runComp.setPreferredSize(size);
				add(runComp);
				
				TextComp clrComp = new TextComp(IconManager.fluentclearImage, 25, 25, "Clear Text", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, ()->textArea.setText(""));
				clrComp.setFont(omega.utils.UIManager.PX18);
				clrComp.setPreferredSize(size);
				add(clrComp);
				
				TextComp terComp = new TextComp(IconManager.fluentcloseImage, 25, 25, "Instant Kill", UIManager.TOOLMENU_COLOR3_SHADE, UIManager.c2, UIManager.TOOLMENU_COLOR3, r0);
				terComp.setFont(omega.utils.UIManager.PX18);
				terComp.setPreferredSize(size);
				add(terComp);
			}
		}
	}
}

