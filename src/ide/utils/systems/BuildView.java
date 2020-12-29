package ide.utils.systems;

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
import ide.utils.systems.creators.RefractionManager;
import importIO.JDKReader;

public class BuildView extends View {

	public volatile Process compileProcess;
	public LinkedList<String> classess = new LinkedList<>();
	public PrintArea printArea;
	public static final String SRC_LIST = ".sources";

	public BuildView(String title, Screen window) 
	{
		super(title, window);
		printArea = new PrintArea("Build Process", window);
		setLayout(new FlowLayout());
		setSize(770, 120);
		setLocationRelativeTo(null);
	}

	public void createJar() {
		new Thread(()->{

			try {
				if(Screen.getFileView().getProjectPath() == null || !new File(Screen.getFileView().getProjectPath()).exists())
					return;

				printArea.setVisible(true);
				getScreen().getOperationPanel().addTab("Create Jar", printArea, ()->{});
				printArea.print("Please compile the project explicity before creating a jar file");
				printArea.print("---------------------------------------------------");
				String jarName = Screen.getFileView().getProjectPath()+"/out/"+Screen.getFileView().getProjectName()+".jar";
				File jar = new File(jarName);
				if(jar.exists())
					jar.delete();
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(jarName));
				for(String path : Screen.getFileView().getDependencyManager().dependencies) {
					printArea.print("Extracting Jar File : \""+path.substring(path.lastIndexOf('/'))+"\"");
					extractJar(Screen.getFileView().getProjectPath()+"/bin", path);
					printArea.clear();
				}
				BuildView.copyResources(printArea);
				LinkedList<File> files = new LinkedList<>();
				loadAllFiles(Screen.getFileView().getProjectPath()+"/bin", files);
				for(File file : files) {
					String name = file.getAbsolutePath();
					name = name.substring(name.indexOf("/bin/") + "/bin/".length());
					if(name.endsWith("/MANIFEST.MF")) continue;
					ZipEntry zipEntry = new ZipEntry(name);
					out.putNextEntry(zipEntry);
					printArea.print("Adding Resource File : \"" + name + "\"");
					try {
						InputStream o = new FileInputStream(file.getAbsolutePath());
						while(o.available() > 0)
							out.write(o.read());
						o.close();
					}catch(Exception ex) {ex.printStackTrace();}
				}
				if(Screen.getRunView().mainClass != null && !Screen.getRunView().mainClass.equals("")) {
					printArea.print("Creating MANIFEST.MF File in \"META-INF\"");
					File file = new File(Screen.getFileView().getProjectPath()+"/bin/META-INF/");
					if(!file.exists())
						file.mkdir();
					PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath()+"/bin/META-INF/MANIFEST.MF"));
					writer.println("Manifest-Version: 1.0");
					writer.println("Main-Class: "+Screen.getRunView().mainClass+"\n");
					writer.close();
					printArea.print("----------------------");
					printArea.print("Manifest-Version: 1.0");
					printArea.print("Main-Class: "+Screen.getRunView().mainClass);
					printArea.print("----------------------");
					//Creating Manifest
					file = new File(Screen.getFileView().getProjectPath()+"/bin/META-INF/MANIFEST.MF");
					String name = Screen.getFileView().getProjectPath()+"/bin/META-INF/MANIFEST.MF";
					name = name.substring(name.indexOf("/bin/") + "/bin/".length());
					ZipEntry zipEntry = new ZipEntry(name);
					out.putNextEntry(zipEntry);
					try {
						InputStream o = new FileInputStream(file.getAbsolutePath());
						while(o.available() > 0)
							out.write(o.read());
						o.close();
					}catch(Exception ex) {ex.printStackTrace();}
				}
				else {
					printArea.print("No Main-Class found");
					printArea.print("Jar Manifest Information was not created");
					printArea.print("Without any Main-Class, the jar created will not run.");
					printArea.print("It can only be used as a library");
					printArea.print("If you want the jar to behave as an application");
					printArea.print("then, please mark or run a class as Main (Run as Main)");
				}
				out.close();
				files.clear();
				printArea.print("Jar File is created, it is in directory \"out\" of this Project");
				Screen.getProjectView().reload();
			}catch(Exception e) {e.printStackTrace();};

		}).start();
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

	public void extractJar(String destinationDir, String jarPath)
	{
		try {

			File file = new File(jarPath);
			JarFile jar = new JarFile(file);
			printArea.print("----------Extracting "+jar.getName()+"----------");
			for(Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements() ;) {
				JarEntry entry = enums.nextElement();
				String fileName = destinationDir + File.separator + entry.getName();
				if(fileName.endsWith("/") && !fileName.contains("META_INF") && !new File(fileName).exists())
				{
					printArea.print("Creating package : "+entry.getName());
					new File(fileName).mkdir();
				}
				else
					printArea.print("Package already exists : \""+entry.getName()+"\"");
			}

			for(Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements() ;)
			{
				JarEntry entry = enums.nextElement();
				String fileName = destinationDir + File.separator + entry.getName();
				if(!fileName.endsWith("/"))
				{
					if(!new File(fileName).exists()){
						InputStream is = jar.getInputStream(entry);
						FileOutputStream fos = new FileOutputStream(new File(fileName));
						printArea.print("Creating resource : \""+entry.getName()+"\"");
						while(is.available() > 0)
						{
							fos.write(is.read());
						}
						fos.close();
						is.close();
					}
					else
						printArea.print("Resource already exists : \""+entry.getName()+"\"");
				}
			}
			jar.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void compileProject() {
		if(compileProcess != null) {
			if(compileProcess.isAlive())
				return;
		}
		new Thread(()->{

			try  {
				getScreen().saveAllEditors();
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
					jdkPath = String.copyValueOf(jdkPath.toCharArray()) + "/bin/";

				String cmd = "";
				String depenPath = "";
				if(!Screen.getFileView().getDependencyManager().dependencies.isEmpty()) {
					for(String d : Screen.getFileView().getDependencyManager().dependencies) {
						depenPath += d + ":";
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
				printArea.print("Compilation Completed"+status);
				compileProcess = null;
				getScreen().getToolMenu().buildComp.setClickable(true);
				getScreen().getToolMenu().runComp.setClickable(true);
				Screen.getProjectView().reload();
			}catch(Exception e) {e.printStackTrace();}

		}).start();
	}
	
	public static void optimizeProjectOutputs(){
		File outputDir = new File(Screen.getFileView().getProjectPath() + "/bin");
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

	public static int copyResources(ide.utils.systems.RunView.PrintArea printArea) {
		int value = -1;
		if(ResourceManager.roots.isEmpty()) return value;
		for(String root : ResourceManager.roots) {
			File f = new File(root);
			if(f.exists()) {
				File[] files = f.listFiles();
				LinkedList<File> folders = new LinkedList<>();
				LinkedList<File> fs = new LinkedList<>();
				getAllFolders(files, folders);
				getAllFiles(files, fs);
				folders.forEach(file->{
					String path = file.getAbsolutePath();
					String name = file.getName();
					String pointName = path.substring(path.indexOf(root) + root.length());
					pointName = Screen.getFileView().getProjectPath() + "/bin" + pointName;
					printArea.printText("Creating " + name + " As in \""+file.getAbsolutePath()+"\"");
					RefractionManager.copy(file, new File(pointName));
				});
				fs.forEach(file->{
					String path = file.getAbsolutePath();
					String name = file.getName();
					String pointName = path.substring(path.indexOf(root) + root.length());
					pointName = Screen.getFileView().getProjectPath() + "/bin" + pointName;
					printArea.printText("Copying " + name + " from \""+file.getAbsolutePath()+"\"");
					RefractionManager.copy(file, new File(pointName));
				});
			}
			else {
				value = 0;
				printArea.printText("Resource-Root-Does-Not-Exists : "+f.getName());
			}
		}
		return value;
	}

	public static int copyResources(ide.utils.systems.BuildView.PrintArea printArea) {
		int value = -1;
		if(ResourceManager.roots.isEmpty()) return value;
		for(String root : ResourceManager.roots) {
			File f = new File(root);
			if(f.exists()) {
				File[] files = f.listFiles();
				LinkedList<File> folders = new LinkedList<>();
				LinkedList<File> fs = new LinkedList<>();
				getAllFolders(files, folders);
				getAllFiles(files, fs);
				folders.forEach(file->{
					String path = file.getAbsolutePath();
					String name = file.getName();
					String pointName = path.substring(path.indexOf(root) + root.length());
					pointName = Screen.getFileView().getProjectPath() + "/bin" + pointName;
					printArea.print("Creating " + name + " As in \""+file.getAbsolutePath()+"\"");
					RefractionManager.copy(file, new File(pointName));
				});
				fs.forEach(file->{
					String path = file.getAbsolutePath();
					String name = file.getName();
					String pointName = path.substring(path.indexOf(root) + root.length());
					pointName = Screen.getFileView().getProjectPath() + "/bin" + pointName;
					printArea.print("Copying " + name + " from \""+file.getAbsolutePath()+"\"");
					RefractionManager.copy(file, new File(pointName));
				});
			}
			else {
				value = 0;
				printArea.print("Resource-Root-Does-Not-Exists : "+f.getName());
			}
		}
		return value;
	}

	public void createClassList()
	{
		classess.clear();
		try {
			LinkedList<String> dirs = new LinkedList<>();
			loadData(dirs, new File(Screen.getFileView().getProjectPath()+"/src").listFiles());

			while(!dirs.isEmpty())
			{
				try {
					for(String path : dirs)
					{
						File file = new File(path);
						if(!file.isDirectory())
						{
							if(path.endsWith(".java"))
							{
								classess.add(path);
							}
						}
						else
						{
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
				StringTokenizer tokenizer = new StringTokenizer(c, "/");
				while(tokenizer.hasMoreTokens()) {
					res+=tokenizer.nextToken()+"/";
				}
				res = res.substring(0, res.length() - 1);
				res = "\"/"+res+"\"";
				paths.add(res);
			});
			//Creating SourcePath
			PrintWriter writer = new PrintWriter(new FileOutputStream(Screen.getFileView().getProjectPath()+"/"+SRC_LIST));
			paths.forEach(path->writer.println(path));
			writer.close();
		}catch(Exception e) {e.printStackTrace();}
	}

	private static void loadData(LinkedList<String> paths, File[] files)
	{
		try {
			for (File file : files) {
				paths.add(file.getPath());
			}
		}catch(Exception e){}
	}

	private static void loadData(LinkedList<String> paths0, LinkedList<String> paths1)
	{
		for(String path0 : paths0)
		{
			paths1.add(path0);
		}
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
			textArea.setText("Building Project with JDK v" + JDKReader.version);
		}

		public void stopProcess() {
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
