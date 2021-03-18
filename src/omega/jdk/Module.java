package omega.jdk;
import omega.Screen;
import java.util.zip.*;
import java.util.*;
import java.io.*;
public class Module {
	public File moduleFile;
	public String name;
     public String modulePath;
     public String moduleName;
	public LinkedList<Import> classes = new LinkedList<>();
	public Module(File moduleFile){
          this.modulePath = moduleFile.getParentFile().getAbsolutePath();
		this.moduleFile = moduleFile;
		this.name = moduleFile.getName();
          this.moduleName = name.substring(0, name.lastIndexOf('.'));
		readModule();
	}
	public void readModule(){
          Screen.setStatus("Reading Module : " + name, 10);
		try{
			ZipFile zipFile = new ZipFile(moduleFile);
			Enumeration enums = zipFile.entries();
			while(enums.hasMoreElements()) {
				String entry = enums.nextElement().toString();
				String classPath = convertModulePathToPackagePath(entry);
				if(classPath == null)
					continue;
				classes.add(new Import(classPath, moduleFile.getAbsolutePath(), true));
			}
		}
		catch(Exception e){
               Screen.setStatus("Exception while Reading Module : " + name, 10);
			e.printStackTrace();
		}
          Screen.setStatus("", 100);
	}
	public static String convertModulePathToPackagePath(String zipPath){
		if(zipPath == null || !zipPath.startsWith("classes/") || zipPath.contains("$") || !zipPath.endsWith(".class") || zipPath.startsWith("META-INF"))
			return null;
		zipPath = zipPath.substring(8, zipPath.lastIndexOf('.'));
		StringTokenizer tok = new StringTokenizer(zipPath, "/");
		zipPath = "";
		while(tok.hasMoreTokens())
			zipPath += tok.nextToken() + ".";
		zipPath = zipPath.substring(0, zipPath.length() - 1);
		return zipPath.equals("module-info") ? null : zipPath;
	}
	public static String convertJarPathToPackagePath(String zipPath){
		if(zipPath == null || zipPath.contains("$") || !zipPath.endsWith(".class") || zipPath.startsWith("META-INF"))
			return null;
		zipPath = zipPath.substring(0, zipPath.lastIndexOf('.'));
		StringTokenizer tok = new StringTokenizer(zipPath, "/");
		zipPath = "";
		while(tok.hasMoreTokens())
			zipPath += tok.nextToken() + ".";
		zipPath = zipPath.substring(0, zipPath.length() - 1);
		return zipPath.equals("module-info") ? null : zipPath;
	}
	public static String convertSourcePathToPackagePath(String sourcePath){
          if(sourcePath.endsWith(".java"))
               sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf('.'));
          StringTokenizer tok = new StringTokenizer(sourcePath, File.separator);
          sourcePath = "";
          while(tok.hasMoreTokens())
               sourcePath += tok.nextToken() + ".";
          sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
          return (sourcePath.equals("") || !sourcePath.contains(".")) ? null : sourcePath;
	}
	@Override
	public String toString(){
		return moduleFile.getName();
	}
}
