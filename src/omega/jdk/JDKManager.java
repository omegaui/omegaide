package omega.jdk;
import omega.Screen;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.*;
import java.io.*;
public class JDKManager {
	private File jdkDir;
	private String version;
	public String java;
	public String javac;
	public String javap;
	public static volatile boolean reading = false;
	public static LinkedList<Module> modules = new LinkedList<>();
	public static LinkedList<Import> imports = new LinkedList<>();
	public static LinkedList<Import> javaLangPack = new LinkedList<>();
	public static LinkedList<Import> sources = new LinkedList<>();
	public JDKManager(File jdkDir){
          imports.clear();
          modules.clear();
          javaLangPack.clear();
          sources.clear();
		this.jdkDir = jdkDir;
		String ext = File.separator.equals("\\") ? ".exe" : "";
		this.java = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "java" + ext;
		this.javac = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "javac" + ext;
		this.javap = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "javap" + ext;
		loadVersionInfo();
		if(isModularJDK())
			readModules();
		else
		     readRTJarFile();
	}
	public void readRTJarFile(){
		Screen.getScreen().getToolMenu().setTask("Reading JDK v" + version);
		String rtJarPath = jdkDir.getAbsolutePath() + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar";
		try{
			JarFile rtJarFile = new JarFile(rtJarPath);
			for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();) {
				JarEntry jarEntry = enums.nextElement();
				String name = jarEntry.getName();
				if(!name.endsWith("/")){
					String classPath = Module.convertJarPathToPackagePath(name);
					if(classPath != null)
						addImport(classPath, rtJarPath, false);
				}
			}
		}
		catch(Exception e) {
			Screen.getScreen().getToolMenu().setTask("Exception while Reading the JDK v" + version);
			e.printStackTrace();
		}
		Screen.getScreen().getToolMenu().setTask("Hover to see Memory Statistics");
	}
	public void readModules(){
		File[] modulesFiles = new File(jdkDir.getAbsolutePath() + File.separator + "jmods").listFiles();
		if(modulesFiles == null || modulesFiles.length == 0)
			return;
		for(File moduleFile : modulesFiles)
			modules.add(new Module(moduleFile));
		modules.forEach(module->module.classes.forEach(this::addImport));
	}
	public void addModule(File moduleFile){
		for(Module module : modules){
			if(module.moduleFile.getAbsolutePath().equals(moduleFile.getAbsolutePath()))
				return;
		}
		Module module = new Module(moduleFile);
		module.classes.forEach(this::addImport);
		modules.add(module);
	}
	public void loadVersionInfo(){
		try{
			Scanner reader = new Scanner(new File(jdkDir.getAbsolutePath() + File.separator + "release"));
			String versionLine = null;
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				if(line.startsWith("JAVA_VERSION")){
					versionLine = line;
					reader.close();
					break;
				}
			}
			if(versionLine != null){
				version = versionLine;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean isModularJDK(){
		File jmodsDir = new File(jdkDir.getAbsolutePath() + File.separator + "jmods");
		return jmodsDir.exists();
	}
	public static boolean isJDK(File dir){
		File[] files = dir.listFiles();
		if(files == null || files.length == 0)
			return false;
		String releaseFilePath = dir.getAbsolutePath() + File.separator + "";
		File releaseFile = new File(releaseFilePath);
		return releaseFile.exists();
	}
	
	public void readSources(String projectPath){
		sources.forEach(imports::remove);
		sources.clear();
		File srcDir = new File(projectPath + File.separator + "src");
		LinkedList<File> sourceFiles = new LinkedList<>();
		loadFiles(srcDir, sourceFiles, ".java");
		if(sourceFiles.isEmpty())
			return;
		sourceFiles.forEach(source->{
			String sourcePath = source.getAbsolutePath();
			sourcePath = sourcePath.substring(sourcePath.indexOf("src") + "src".length() + 1);
			sourcePath = Module.convertSourcePathToPackagePath(sourcePath);
			if(sourcePath != null){
				Import imx = new Import(sourcePath, "project", false);
				sources.add(imx);
				imports.add(imx);
			}
		});
	}
	public void readJar(String path, boolean module){
		Screen.getScreen().getToolMenu().setTask("Reading Jar : " + new File(path).getName());
		try{
			JarFile rtJarFile = new JarFile(path);
			for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();){
				JarEntry jarEntry = enums.nextElement();
				String name = jarEntry.getName();
				if(!name.endsWith("/")){
					String classPath = Module.convertJarPathToPackagePath(name);
					if(classPath != null)
						addImport(classPath, path, module);
				}
			}
		}
		catch(Exception e) {
			Screen.getScreen().getToolMenu().setTask("Exception while Reading Jar : " + new File(path).getName());
			e.printStackTrace();
		}
		Screen.getScreen().getToolMenu().setTask("Hover to see Memory Statistics");
	}
	public static int calculateVersion(File jdkDir){
		String version = "";
		try{
			Scanner reader = new Scanner(new File(jdkDir.getAbsolutePath() + File.separator + "release"));
			String versionLine = null;
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				if(line.startsWith("JAVA_VERSION")){
					versionLine = line;
					reader.close();
					break;
				}
			}
			if(versionLine != null){
				version = versionLine;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		version = version.substring(version.indexOf('\"') + 1, version.lastIndexOf('\"'));
		if(version.startsWith("1."))
			return Integer.parseInt(version.charAt(2) + "");
		else
		return Integer.parseInt(version.substring(0, version.indexOf('.')));
	}
	public void loadFiles(File dir, LinkedList<File> files, String ext){
		File[] F = dir.listFiles();
		if(F == null || F.length == 0) return;
			for(File f : F){
			if(f.isDirectory())
				loadFiles(f, files, ext);
			else if(f.getName().endsWith(ext))
			files.add(f);
		}
	}
	public void addImport(String packagePath, String jarPath, boolean module){
		imports.add(new Import(packagePath, jarPath, module));
		Import im = imports.getLast();
		if(im.packageName.equals("java.lang"))
			javaLangPack.add(im);
	}
	
	public void addImport(Import im){
		imports.add(im);
		if(im.packageName.equals("java.lang"))
			javaLangPack.add(im);
	}
	
	public static LinkedList<Import> getAllImports() {
		return imports;
	}
	
	public static LinkedList<Module> getModules() {
		return modules;
	}
	
	public static LinkedList<Import> getSources() {
		return sources;
	}
	
	public String getVersion() {
		return version;
	}
	public int getVersionAsInt(){
		String version = this.version;
		version = version.substring(version.indexOf('\"') + 1, version.lastIndexOf('\"'));
		if(version.startsWith("1."))
			return Integer.parseInt(version.charAt(2) + "");
		else
		return Integer.parseInt(version.substring(0, version.indexOf('.')));
	}
	public void clear(){
		modules.clear();
		imports.clear();
		sources.clear();
	}
	
}
