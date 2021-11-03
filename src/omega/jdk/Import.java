/**
  * Import
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
import java.io.File;
public class Import {
	public String className;
	public String packageName;
	public String packagePath;
	public String jarPath;
	
	public boolean module;

	public boolean staticImport;
	
	public Import(String path, String jarPath, boolean module){
		this.packagePath = path;
		this.className = path.substring(path.lastIndexOf('.') + 1);
		this.packageName = path.substring(0, path.lastIndexOf('.'));
		this.jarPath = jarPath;
		this.module = module;
	}

	public Import(String path, String jarPath, boolean module, boolean isStatic){
		this(path, jarPath, module);
		staticImport = isStatic;
	}
	
	public Import(String pack, String name){
		this.className = name;
		this.packageName = pack;
	}
	public String getImport(){
		return (packagePath == null) ? (packageName + "." + className) : packagePath;
	}
    
	public String getClassName() {
		return className;
	}
	
	public String getPackage() {
		return packageName;
	}

	public boolean isStatic(){
		return staticImport;
	}

	public String getModuleName(){
		return jarPath.substring(jarPath.lastIndexOf(File.separator) + 1, jarPath.lastIndexOf('.'));
	}
	
	@Override
	public String toString(){
		return getImport();
	}
}

