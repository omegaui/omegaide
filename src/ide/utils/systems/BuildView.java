package ide.utils.systems;
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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseListener;
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

import ide.Screen;
import ide.utils.Editor;
import ide.utils.ResourceManager;
import ide.utils.UIManager;
import ide.utils.systems.creators.FileOperationManager;
import importIO.JDKReader;

public class BuildView extends View {

	public volatile Process compileProcess;
	public LinkedList<String> classess = new LinkedList<>();
	public PrintArea printArea;
	public static final String SRC_LIST = ".sources";

	public BuildView(String title, Screen window) {
		super(title, window);
		printArea = new PrintArea("Build Process", window);
		setLayout(new FlowLayout());
		setSize(770, 120);
		setLocationRelativeTo(null);
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
               String args = Screen.getFileView().getArgumentManager().compile_time_args;
               printArea.setVisible(true);
               printArea.clear();
               printArea.print("Building Project...\n\"" + args + "\"");
               getScreen().getOperationPanel().addTab("Build", printArea, ()->printArea.stopProcess());
               if(args.trim().equals("")){
                    printArea.print("\'No Compile Time Command Specified!!!\'");
                    printArea.print("Click \"Settings\", then Click \"All Settings\"");
                    printArea.print("And Specify the Compile Time Args or Command");
                    return;
               }
               ide.Screen.getFileView().getArgumentManager().genLists();
               String compileDir = Screen.getFileView().getArgumentManager().compileDir;
               String[] arguments = convertToArray(args.trim());
               try{
                    getScreen().getToolMenu().buildComp.setClickable(false);
                    getScreen().getToolMenu().runComp.setClickable(false);
                    String status = "Successfully";
                    
                    compileProcess = new ProcessBuilder(arguments).directory(new File(compileDir)).start();
                    printArea.setProcess(compileProcess);
                    printArea.clear();
                    printArea.print("Building Project...\n\"" + args + "\"");
                    Scanner inputReader = new Scanner(compileProcess.getInputStream());
                    Scanner errorReader = new Scanner(compileProcess.getErrorStream());
                    while(compileProcess.isAlive()) {
                         while(inputReader.hasNextLine())
                              printArea.print(inputReader.nextLine());
                         while(errorReader.hasNextLine()) {
                              String line = errorReader.nextLine();
                              printArea.print(line);
                         }
                    }
                    inputReader.close();
                    errorReader.close();
                    if(compileProcess.exitValue() != 0)
                         status = "with error(s)";
                    printArea.print("Compilation Completed " + status);
                    compileProcess = null;
                    getScreen().getToolMenu().buildComp.setClickable(true);
                    getScreen().getToolMenu().runComp.setClickable(true);
                    Screen.getProjectView().reload();
                    
               }catch(Exception e){ System.err.println(e); }
          }).start();
     }

     public static String[] convertToArray(String args){
          String token = "";
          LinkedList<String> arguments = new LinkedList<>();
          boolean canRecord = false;
          boolean strRec = false;
     	for(int i = 0; i < args.length(); i++){
               char ch = args.charAt(i);
     		if(!canRecord && (Character.isLetter(ch) || ch == '-')){
                    token = "";
                    canRecord = true;
     		}
               if(ch == '\"' && !strRec){
                    strRec = true;
               }
               else if(strRec){
                    strRec = false;
               }
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
          if(ide.Screen.getFileView().getProjectManager().non_java){
               compile();
               return;
          }
		new Thread(()->{

			try  {
				createClassList();
				if(classess.isEmpty())
					return;
				Screen.getErrorHighlighter().removeAllHighlights();
				optimizeProjectOutputs();
				getScreen().getToolMenu().buildComp.setClickable(false);
				getScreen().getToolMenu().runComp.setClickable(false);
				String status = " Successfully";
				printArea.setVisible(true);
				printArea.print("Building Project....");
				if(Screen.getFileView().getProjectManager().jdkPath == null) {
					printArea.print("No JDK Defined for this Project!\nSelect a JDK from the Project Tab in Settings.");
				}
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
				getScreen().getOperationPanel().addTab("Compilation", printArea, ()->{printArea.stopProcess();});
				printArea.setProcess(compileProcess);
				printArea.print("Using \"" + cmd + "\"");
				String errorlog = "";
				Scanner inputReader = new Scanner(compileProcess.getInputStream());
				Scanner errorReader = new Scanner(compileProcess.getErrorStream());
				while(compileProcess.isAlive()) {
					while(inputReader.hasNextLine())
						printArea.print(inputReader.nextLine());
					while(errorReader.hasNextLine()) {
						String line = errorReader.nextLine();
						printArea.print(line);
						errorlog += line + "\n";
					}
				}
				inputReader.close();
				errorReader.close();
				if(compileProcess.exitValue() != 0) {
					status = " with error(s)";
					Screen.getErrorHighlighter().loadErrors(errorlog);
				}
				printArea.print("Compilation Completed" + status);
				compileProcess = null;
				getScreen().getToolMenu().buildComp.setClickable(true);
				getScreen().getToolMenu().runComp.setClickable(true);
				Screen.getProjectView().reload();
			}catch(Exception e) {e.printStackTrace();}

		}).start();
	}
	
	public static void optimizeProjectOutputs(){
		File outputDir = new File(Screen.getFileView().getProjectPath() + File.separator + "bin");
		LinkedList<File> files = new LinkedList<>();
		loadClassFiles(outputDir, files);
		if(files.isEmpty()) return;
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
	
	public void deleteDir(File file)
	{

		if (file.isDirectory())
		{

			/*
			 * If directory is empty, then delete it
			 */
			if (file.list().length == 0)
			{
				deleteEmptyDir(file);
			}
			else
			{
				// list all the directory contents
				File files[] = file.listFiles();

				for (File fileDelete : files)
				{
					/*
					 * Recursive delete
					 */
					deleteDir(fileDelete);
				}

				/*
				 * check the directory again, if empty then 
				 * delete it.
				 */
				if (file.list().length == 0)
				{
					deleteEmptyDir(file);
				}
			}

		}
		else
		{
			/*
			 * if file, then delete it
			 */
			deleteEmptyDir(file);
		}
	}

	private void deleteEmptyDir(File file)
	{
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
          if(ide.Screen.getFileView().getProjectManager().non_java) return;
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
				}catch(Exception e) {}
			}

			LinkedList<String> paths = new LinkedList<>();
			classess.forEach((c)->{
				String res = "";
				StringTokenizer tokenizer = new StringTokenizer(c, File.separator);
				while(tokenizer.hasMoreTokens()) {
					res += tokenizer.nextToken() + File.separator;
				}
				res = res.substring(0, res.length() - 1);
				res = "\"" + File.separator + res + "\"";
				paths.add(res);
			});
			//Creating SourcePath
			PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath() + File.separator +SRC_LIST));
			paths.forEach(path->writer.println(path));
			writer.close();
		}catch(Exception e) {e.printStackTrace();}
	}

	private static void loadData(LinkedList<String> paths, File[] files) {
		try {
			for (File file : files) {
				paths.add(file.getPath());
			}
		}catch(Exception e){}
	}

	private static void loadData(LinkedList<String> paths0, LinkedList<String> paths1) {
		for(String path0 : paths0)
			paths1.add(path0);
	}

	public class PrintArea extends JPanel {

		private RSyntaxTextArea textArea;
		private Process process;
		private JScrollPane p;

		public PrintArea(String title, Screen window) {
			setLayout(new BorderLayout());
			setLocationRelativeTo( null);
			UIManager.setData(this);
			setPreferredSize(getSize());
			init();
		}

		private void init()
		{
			textArea = new RSyntaxTextArea("Build Starting...");
			textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
			textArea.setAutoscrolls(true);
			Editor.getTheme().apply(textArea);
			textArea.setFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
			textArea.setHighlightCurrentLine(false);
			p = new JScrollPane(textArea);
			p.setAutoscrolls(true);
			add(p, BorderLayout.CENTER);
			comps.add(textArea);
		}

		public void setProcess(Process p) {
			process = p;
               if(!Screen.getFileView().getProjectManager().non_java)
			     textArea.setText("Building Project with JDK v" + JDKReader.version);
               else
                    textArea.setText("Building Project ...");
		}

		public void stopProcess() {
               if(process != null)
			     process.destroyForcibly();
		}

		public String getText() {
			return textArea.getText();
		}

		public void print(String text)
		{
			textArea.append("\n" + text);
			p.repaint();
			p.getVerticalScrollBar().setValue(p.getVerticalScrollBar().getMaximum());
		}

		public void clear() {
			textArea.setText("");
		}

		@Override
		public void addMouseListener(MouseListener l) {
			super.addMouseListener(l);
			textArea.addMouseListener(l);
		}
		
		@Override
		public void setVisible(boolean v) {
			if(v) {
				try {
					Editor.getTheme().apply(textArea);
				}catch(Exception e) {}
			}
			super.setVisible(v);
		}
	}

}
