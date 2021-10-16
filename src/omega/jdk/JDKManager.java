/**
  * IDE's JDK Reader
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

package omega.jdk;
import omega.framework.CodeFramework;

import omega.utils.IconManager;

import omega.Screen;

import java.util.jar.JarFile;
import java.util.jar.JarEntry;

import omega.deassembler.JarLoader;
import omega.deassembler.ByteReader;

import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Scanner;

import java.io.File;
public class JDKManager {
	public interface Condition{
		boolean accept(File file);
	}
	public File jdkDir;
	
	public String version;
	public String java;
	public String javac;
	public String javap;
	
	public static volatile boolean reading = false;
	
	private static LinkedList<Module> modules = new LinkedList<>();
	
	public static LinkedList<Import> imports = new LinkedList<>();
	public static LinkedList<Import> javaLangPack = new LinkedList<>();
	public static LinkedList<Import> sources = new LinkedList<>();
	
	public static JarLoader systemJarLoader;
	public static JarLoader dependencyJarLoader;
	public static JarLoader resourceCodeLoader;
	public JDKManager(File jdkDir){
		imports.clear();
		modules.clear();
		javaLangPack.clear();
		sources.clear();
		this.jdkDir = jdkDir;
		String ext  = File.separator.equals("\\") ? ".exe" : "";
		this.java   = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "java" + ext;
		this.javac  = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "javac" + ext;
		this.javap  = jdkDir.getAbsolutePath() + File.separator + "bin" + File.separator + "javap" + ext;
          try{	
               if(isJDKPathValid(jdkDir.getAbsolutePath())){
     		     loadVersionInfo();
          		if(isModularJDK())
          			readModules();
          		else
          			readRTJarFile();
               }
          }
          catch(Exception e){
          	e.printStackTrace();
          }
	}
	public void readRTJarFile(){
		Screen.setStatus("Reading JDK v" + version, 10, IconManager.fluentjavaImage);
		String rtJarPath = jdkDir.getAbsolutePath() + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar";
		try{
			systemJarLoader = new JarLoader(rtJarPath);
			try(JarFile rtJarFile = new JarFile(rtJarPath)){
				for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();) {
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/")){
						String classPath = Module.convertJarPathToPackagePath(name);
						if(classPath != null){
							addImport(classPath, rtJarPath, false);
							systemJarLoader.putClassName(classPath);
						}
					}
				}
			}
		}
		catch(Exception e) {
			Screen.setStatus("Exception while Reading JDK v" + version, 99, IconManager.fluentbrokenbotImage);
			e.printStackTrace();
		}
		Screen.setStatus("", 100, null);
	}
	public void readModules(){
		File[] modulesFiles = new File(jdkDir.getAbsolutePath() + File.separator + "jmods").listFiles();
		if(modulesFiles == null || modulesFiles.length == 0)
			return;
		for(File moduleFile : modulesFiles)
			modules.add(new Module(moduleFile));
		systemJarLoader = JarLoader.prepareSystemModuleLoader();
		modules.forEach((module)->{
			module.classes.forEach(c->{
				addImport(c);
				systemJarLoader.putClassName(c.getImport());
			});
		});

		//Losing Not Needed Memory
		modules.forEach(module->module.classes.clear());
		modules.clear();
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
	public ByteReader prepareReader(String name){
		if(systemJarLoader != null) {
			for(String className : systemJarLoader.classNames){
				if(className.equals(name)){
					return systemJarLoader.loadReader(name);
				}
			}
		}
		if(dependencyJarLoader != null) {
			for(String className : dependencyJarLoader.classNames){
				if(className.equals(name)){
					return dependencyJarLoader.loadReader(name);
				}
			}
		}
		if(resourceCodeLoader != null) {
			for(String className : resourceCodeLoader.classNames){
				if(className.equals(name)){
					return resourceCodeLoader.loadReader(name);
				}
			}
		}
		return null;
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
		String releaseFilePath = dir.getAbsolutePath() + File.separator + "release";
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
	
	public void readResources(String projectPath, LinkedList<String> resourceRoots){
		LinkedList<File> byteCodeFiles = new LinkedList<>();
		resourceCodeLoader = new JarLoader(resourceRoots);
		resourceRoots.forEach((resourceDir)->{
			byteCodeFiles.clear();
			loadFiles(new File(resourceDir), byteCodeFiles, ".class", (file)->!file.getName().contains("$"));
			if(!byteCodeFiles.isEmpty()){
				byteCodeFiles.forEach((file)->{
					String path = file.getAbsolutePath();
					String qualifiedPath = path.substring(resourceDir.length() + 1, path.lastIndexOf("."));
					String className = CodeFramework.replace(qualifiedPath, File.separatorChar, '.');
					Import im = new Import(className, resourceDir, false);
					imports.add(im);
					resourceCodeLoader.classNames.add(className);
				});
			}
		});
	}

	public synchronized String checkInResourceRoots(String pack, String className){
		for(String name : resourceCodeLoader.classNames){
			if(name.startsWith(pack + ".") && name.endsWith("." + className))
				return name;
		}
		return null;
	}
	
	public void readJar(String path, boolean module){
		Screen.setStatus("Reading Jar : " + new File(path).getName(), 10, IconManager.fluentjavaImage);
		try{
			try(JarFile rtJarFile = new JarFile(path)){
				for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();){
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/")) {
						String classPath = Module.convertJarPathToPackagePath(name);
						if(classPath != null) {
							addImport(classPath, path, module);
						}
					}
				}
			}
		}
		catch(Exception e) {
			Screen.setStatus("Exception while Reading Jar : " + new File(path).getName(), 12, IconManager.fluentbrokenbotImage);
			e.printStackTrace();
		}
		Screen.setStatus("", 100, null);
	}
	
	public static int calculateVersion(File jdkDir){
          if(!isJDKPathValid(jdkDir.getAbsolutePath())) {
               Screen.setStatus("Please first select a valid JDK for the project", 10, IconManager.fluentbrokenbotImage);
               return 0;
          }
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
		else if(version.contains("."))
			return Integer.parseInt(version.substring(0, version.indexOf('.')));
		else
			return Integer.parseInt(version);
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
	
	public void loadFiles(File dir, LinkedList<File> files, String ext, Condition condition){
		File[] F = dir.listFiles();
		if(F == null || F.length == 0) return;
		for(File f : F){
			if(f.isDirectory())
				loadFiles(f, files, ext);
			else if(f.getName().endsWith(ext) && condition.accept(f))
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
		else if(version.contains("."))
			return Integer.parseInt(version.substring(0, version.indexOf('.')));
		else
			return Integer.parseInt(version);
	}
	
	public void clear(){
		modules.clear();
		imports.clear();
		javaLangPack.clear();
		sources.clear();
		if(dependencyJarLoader != null)
			dependencyJarLoader.close();
		if(resourceCodeLoader != null)
			resourceCodeLoader.close();
	}
	
	public void prepareDependencyLoader(LinkedList<String> paths){
		dependencyJarLoader = new JarLoader(paths);
	}
	
	public static boolean isJDKPathValid(String path){
		File file = new File(path);
		return path != null && file.exists() && isJDK(file);
	}
}

