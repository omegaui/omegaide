/**
* Executes run, build and init gradle commands.
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

package omega.instant.support.build.gradle;
import java.util.Scanner;

import omega.utils.DataManager;

import java.io.File;
import java.io.PrintWriter;
public class GradleProcessExecutor {
	private static String ext = File.pathSeparator.equals(":") ? "" : ".bat";
	
	public static Process init(File dir){
		Process p = null;
		try{
			p = new ProcessBuilder("gradle" + ext, "init").directory(dir).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	
	public static Process run(File dir){
		Process p = null;
		try{
			return new ProcessBuilder(DataManager.getGradleCommand() + ext, "run").directory(dir).start();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	
	public static Process build(File dir){
		Process p = null;
		try{
			return new ProcessBuilder(DataManager.getGradleCommand() + ext, "build").directory(dir).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	
	public static boolean isErrorOccured(Process p){
		Scanner errorReader = getErrorScanner(p);
		while(p.isAlive()){
			if(errorReader.hasNextLine())
				return true;
		}
		errorReader.close();
		return false;
	}
	
	public static PrintWriter getWriter(Process p){
		try{
			return new PrintWriter(p.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scanner getErrorScanner(Process p){
		try{
			return new Scanner(p.getErrorStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scanner getInputScanner(Process p){
		try{
			return new Scanner(p.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

