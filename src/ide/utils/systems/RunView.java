package ide.utils.systems;

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

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;
import importIO.JDKReader;
import settings.comp.TextComp;
import tabPane.IconManager;

public class RunView extends View{

	private static final long serialVersionUID = 1L;

	public String mainClassPath = null;
	public volatile String mainClass = "";
	public static String NATIVE_PATH = "";
	public volatile LinkedList<Process> runningApps = new LinkedList<>();
	private static volatile String errorlog = "";
	private static PrintArea printA;
	private Process compileProcess = null;
	private Process runProcess = null;

	public RunView(String title, Screen window, boolean canRun) {
		super(title, window);
	}

	public void setMainClass(String mainClass)
	{
		this.mainClass = mainClass;
		Screen.getProjectView().setTitleMainClass();
	}

	public void setMainClassPath(String mainClassPath)
	{
		if(!mainClassPath.endsWith(".java"))
			return;
		mainClass = "";
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(mainClassPath, File.separator);
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(canRecord)
			{
				mainClass += token +".";
			}
			else
			{
				if(token.equals("src"))
					canRecord = true;
			}
		}
		mainClass = mainClass.substring(0, mainClass.length() - 6);
		Screen.getProjectView().setTitleMainClass();
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
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(canRecord)
			{
				mainClass += token +".";
			}
			else
			{
				if(token.equals("src"))
					canRecord = true;
			}
		}
		mainClass = mainClass.substring(0, mainClass.length() - 6);
		Screen.getProjectView().setTitleMainClass();
	}

	public void run() {
		new Thread(()->{
			String mainClass = this.mainClass;
			String mainClassPath = this.mainClassPath;

			try
			{
				try
				{
					if(printA != null)
						getScreen().getOperationPanel().removeTab("Compilation");
					getScreen().saveAllEditors();
					Screen.getBuildView().createClassList();
					if(Screen.getBuildView().classess.isEmpty())
						return;
					Screen.getErrorHighlighter().removeAllHighlights();
					BuildView.optimizeProjectOutputs();
					getScreen().getToolMenu().buildComp.setClickable(false);
					getScreen().getToolMenu().runComp.setClickable(false);
					PrintArea printArea = new PrintArea("Compile-Time Errors", getScreen());
					printA = printArea;
					errorlog = "";
					int percent = (int)Math.ceil(Math.random() * 70);
					Screen.setStatus("Building Project", percent);
					String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
					if(jdkPath != null && new File(jdkPath).exists())
						jdkPath = String.copyValueOf(jdkPath.toCharArray()) + File.separator + "bin" + File.separator;
					
					String cmd = "";
					String depenPath = "";
					if(!Screen.getFileView().getDependencyManager().dependencies.isEmpty()) {
						for(String d : Screen.getFileView().getDependencyManager().dependencies) {
							depenPath += d + ide.Screen.PATH_SEPARATOR;
						}
						if(!depenPath.equals("")) {
							depenPath = depenPath.substring(0, depenPath.length() - 1);
						}
					}
					cmd  += " " + Screen.getFileView().getProjectManager().compile_time_args;
					if(jdkPath != null && new File(jdkPath).exists())
						cmd = jdkPath + "javac";
					else
						cmd = "javac" + cmd;
					if(depenPath != null && !depenPath.equals("")) {
						if(Screen.getFileView().getModuleManager().getModularPath() != null && Screen.getFileView().getModuleManager().getModularNames() != null) {
							if(!Screen.getFileView().getProjectManager().compile_time_args.trim().equals("")) {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", "-classpath", depenPath, 
										"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
										"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
										Screen.getFileView().getProjectManager().compile_time_args, "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}else {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", "-classpath", depenPath,
										"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
										"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),"@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}
						}
						else {
							if(!Screen.getFileView().getProjectManager().compile_time_args.trim().equals("")) {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", "-classpath", depenPath,
										Screen.getFileView().getProjectManager().compile_time_args, "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}else {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", "-classpath", depenPath, "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}
						}
					}
					else {
						if(Screen.getFileView().getModuleManager().getModularPath() != null && Screen.getFileView().getModuleManager().getModularNames() != null) {
							if(!Screen.getFileView().getProjectManager().compile_time_args.trim().equals("")) {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin",
										"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
										"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
										Screen.getFileView().getProjectManager().compile_time_args, "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}else {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin",
										"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
										"--add-modules", Screen.getFileView().getModuleManager().getModularNames(), "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}
						}
						else {
							if(!Screen.getFileView().getProjectManager().compile_time_args.trim().equals("")) {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", 
										Screen.getFileView().getProjectManager().compile_time_args, "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}else {
								compileProcess = new ProcessBuilder(
										cmd, "-d", "bin", "@"+BuildView.SRC_LIST
										).directory(new File(Screen.getFileView().getProjectPath())).start();
							}
						}
					}
					runningApps.add(compileProcess);
					printArea.printText("Building....");
					Scanner inputReader = new Scanner(compileProcess.getInputStream());
					Scanner errorReader = new Scanner(compileProcess.getErrorStream());
					new Thread(()->{
						while(compileProcess.isAlive())
						{
							while(errorReader.hasNextLine()) {
								String line = errorReader.nextLine();
								printArea.printText(line);
								errorlog += line + "\n";
							}
						}
						errorReader.close();
					}).start();
					while(compileProcess.isAlive())
					{
						while(inputReader.hasNextLine())
							printArea.printText(inputReader.nextLine());
					}
					inputReader.close();
					Screen.setStatus("Building Project", 90);
					getScreen().getToolMenu().buildComp.setClickable(true);
					Screen.getProjectView().reload();
					if(compileProcess.exitValue() != 0) {
						Screen.setStatus("Building Project Failed", 100);
						runningApps.remove(compileProcess);
						getScreen().getToolMenu().runComp.setClickable(true);
						Screen.getErrorHighlighter().loadErrors(errorlog);
						getScreen().getOperationPanel().addTab("Compilation", printArea, ()->{printArea.stopProcess();});
						return;
					}
					Screen.getErrorHighlighter().removeAllHighlights();
				}catch(Exception e2) {e2.printStackTrace();}

				getScreen().getOperationPanel().removeTab("Compilation");
				if(mainClass == null)
				{
					PrintArea p = new PrintArea("Main Class does not exists", getScreen());
					p.setVisible(true);
					getScreen().getOperationPanel().addTab("No Main Config", p, ()->{p.stopProcess();});
					if(mainClassPath.equals(Screen.getFileView().getProjectPath() + "/src.java"))
						p.printText("\"No Main Class Defined for the Project!\" \n\tor\n \"Defined Main Class does not exits!\"");
					Screen.setStatus("Running Project Failed", 100);
					getScreen().getToolMenu().runComp.setClickable(true);
					return;
				}

				Screen.setStatus("Building Project Completed", 100);

				System.gc();

				Screen.setStatus("Running Project", 23);

				NATIVE_PATH = "";
				for(String d : Screen.getFileView().getNativesManager().natives) {
					NATIVE_PATH += d + ide.Screen.PATH_SEPARATOR;
				}
				if(!NATIVE_PATH.equals("")) {
					NATIVE_PATH = NATIVE_PATH.substring(0, NATIVE_PATH.length() - 1);
					NATIVE_PATH = NATIVE_PATH + ide.Screen.PATH_SEPARATOR + "$PATH";
				}
				else
					NATIVE_PATH = "$PATH";
				String depenPath = "";
				for(String d : Screen.getFileView().getDependencyManager().dependencies)
					depenPath += d + ide.Screen.PATH_SEPARATOR;
				for(String d : ide.utils.ResourceManager.roots)
					depenPath += d + ide.Screen.PATH_SEPARATOR;
				if(!depenPath.equals("")) {
					depenPath = depenPath.substring(0, depenPath.length() - 1);
				}
				PrintArea terminal = new PrintArea("Terminal "+mainClass+"-Closing This Conlose will terminate Execution", getScreen());
				terminal.printText("running \""+mainClass+"\" with JDK v" + JDKReader.version);
				terminal.printText("");
				terminal.printText("---<>--------------------------------------<>---");
				terminal.launchAsTerminal();

				Screen.setStatus("Running Project", 56);
				String jdkPath = String.copyValueOf(Screen.getFileView().getProjectManager().jdkPath.toCharArray());
				String cmd = null;
				if(jdkPath != null && new File(jdkPath).exists())
					cmd = jdkPath + File.separator + "bin" + File.separator + "java";
				else
					cmd = "java" + cmd;
				if(depenPath != null && !depenPath.equals("")) {
					if(Screen.getFileView().getModuleManager().getModularPath() != null && Screen.getFileView().getModuleManager().getModularNames() != null) {
						if(!Screen.getFileView().getProjectManager().run_time_args.trim().equals("")) {
							runProcess = new ProcessBuilder(
									cmd, "-classpath", depenPath + ide.Screen.PATH_SEPARATOR + ".",
									"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
									"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
									"-Djava.library.path="+NATIVE_PATH+"", Screen.getFileView().getProjectManager().run_time_args, mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}else {
							runProcess = new ProcessBuilder(
									cmd, "-classpath", depenPath + ide.Screen.PATH_SEPARATOR + ".", 
									"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
									"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
									"-Djava.library.path="+NATIVE_PATH+"", mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}
					}
					else {
						if(!Screen.getFileView().getProjectManager().run_time_args.trim().equals("")) {
							runProcess = new ProcessBuilder(
									cmd, "-classpath", depenPath + ide.Screen.PATH_SEPARATOR + ".", "-Djava.library.path="+NATIVE_PATH+"", 
									Screen.getFileView().getProjectManager().run_time_args, mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}else {
							runProcess = new ProcessBuilder(
									cmd, "-classpath", depenPath + ide.Screen.PATH_SEPARATOR + ".", "-Djava.library.path="+NATIVE_PATH+"", mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}
					}
				}
				else {
					if(Screen.getFileView().getModuleManager().getModularPath() != null && Screen.getFileView().getModuleManager().getModularNames() != null) {
						if(!Screen.getFileView().getProjectManager().run_time_args.trim().equals("")) {
							runProcess = new ProcessBuilder(
									cmd, 
									"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
									"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
									"-Djava.library.path=\""+NATIVE_PATH+"\"", 
									Screen.getFileView().getProjectManager().run_time_args, mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}else {
							runProcess = new ProcessBuilder(
									cmd, 
									"--module-path", Screen.getFileView().getModuleManager().getModularPath(),
									"--add-modules", Screen.getFileView().getModuleManager().getModularNames(),
									"-Djava.library.path=\""+NATIVE_PATH+"\"", mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}
					}
					else {
						if(!Screen.getFileView().getProjectManager().run_time_args.trim().equals("")) {
							runProcess = new ProcessBuilder(
									cmd, "-Djava.library.path=\""+NATIVE_PATH+"\"", 
									Screen.getFileView().getProjectManager().run_time_args, mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}else {
							runProcess = new ProcessBuilder(
									cmd, "-Djava.library.path=\""+NATIVE_PATH+"\"", mainClass
									).directory(new File(Screen.getFileView().getProjectPath()+"/bin")).start();
						}
					}
				}
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
					while(runProcess.isAlive())
					{
						while(errorReader.hasNextLine())
						{
							if(!status.equals("Errors")) status = "Errors";
							terminal.printText(errorReader.nextLine());
						}
					}
					terminal.printText("Program Execution finished with "+status);
					errorReader.close();
				}).start();

				getScreen().getToolMenu().runComp.setClickable(true);
				while(runProcess.isAlive())
				{
					while(inputReader.hasNextLine())
					{
						String data = inputReader.nextLine();
						terminal.printText(data);
					}
				}
				inputReader.close();
				runningApps.remove(runProcess);
				Screen.getProjectView().reload();
				terminal.printText("---<>--------------------------------------<>---");
			}
			catch(Exception e) {e.printStackTrace();}
		}).start();
	}

	public class PrintArea extends JPanel {

		private static final long serialVersionUID = 1L;
		private RSyntaxTextArea textArea;
		private Process runProcess;
		private JScrollPane p;

		public PrintArea(String title, Screen window) 
		{
			setLayout(new BorderLayout());
			setPreferredSize(getSize());
			init();
		}

		private void init()
		{
			textArea = new RSyntaxTextArea("......................");
			textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
			textArea.setAutoscrolls(true);
			textArea.setCaretColor(java.awt.Color.WHITE);
			Editor.getTheme().apply(textArea);
			textArea.setFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
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

		public void setRunProcess(Process p)
		{
			runProcess = p;
		}

		public void stopProcess() {
			try {
				runProcess.destroyForcibly();
			}catch(Exception e) {}
		}

		@Override
		public void addMouseListener(MouseListener l) {
			super.addMouseListener(l);
			textArea.addMouseListener(l);
		}
		
		public void launchAsTerminal() 		{
			setAction(()->{
				textArea.setText("Running......\""+mainClass+"\"\n");
				textArea.setEditable(false);
				UIManager.setData(PrintArea.this);
			});
			JTextField inputField = new JTextField();
			inputField.setText("Input? From Here");
			inputField.addActionListener((e)->{

				if(runProcess == null)
					return;
				else if(!runProcess.isAlive())
					return;

				System.out.println("\""+inputField.getText()+"\"");
				try {						
					PrintWriter writer = new PrintWriter(runProcess.getOutputStream());
					writer.println(inputField.getText());
					writer.flush();
				}catch(Exception e1) {e1.printStackTrace();}
				inputField.setText("");
			});
			inputField.setCaretColor(java.awt.Color.YELLOW);
			UIManager.setData(inputField);
               inputField.setFont(settings.Screen.PX18);
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

				TextComp runComp = new TextComp(">", UIManager.c2, UIManager.c1, UIManager.c3, r);
				runComp.setFont(settings.Screen.PX18);
				runComp.setPreferredSize(size);
				add(runComp);
				
				TextComp terComp = new TextComp("x", UIManager.c2, UIManager.c1, UIManager.c3, r0);
				terComp.setFont(settings.Screen.PX18);
				terComp.setPreferredSize(size);
				add(terComp);
			}
		}
	}
}
