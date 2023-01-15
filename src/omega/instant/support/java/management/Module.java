/*
 * Java Module Reader
 * Copyright (C) 2022 Omega UI

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

package omega.instant.support.java.management;
import omega.io.IconManager;

import java.util.zip.ZipFile;

import java.util.LinkedList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import java.io.File;

import omega.Screen;
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
			zipFile.close();
		}
		catch(Exception e){
			Screen.setStatus("Exception while Reading Module : " + name, 10, IconManager.fluentbrokenbotImage);
			e.printStackTrace();
		}
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
		return (!sourcePath.contains(".")) ? null : sourcePath;
	}

	@Override
	public String toString(){
		return moduleFile.getName();
	}
}

