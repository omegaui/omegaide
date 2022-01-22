/**
* FileOperationManager -- Performs Rename, Copy, Move and SilentMove Operations
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

package omega.io;
import java.util.LinkedList;

import java.io.File;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
public class FileOperationManager {

	public synchronized static void silentMoveFile(File target, File destination){
		try{
			PrintWriter writer = new PrintWriter(destination);
			InputStream in = new FileInputStream(target);
			while(in.available() > 0)
				writer.write(in.read());
			in.close();
			writer.close();

			target.delete();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized static void copyFileToDir(File file, File targetDir) {
		try {
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(targetDir.getAbsolutePath() + File.separator + file.getName());
			while(in.available() > 0)
				out.write(in.read());
			in.close();
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void copyFile(File file, File target) {
		try {
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(target);
			while(in.available() > 0)
				out.write(in.read());
			in.close();
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void writeNewTextToFile(String text, File targetFile){
		try{
			if(!targetFile.exists())
				targetFile.getParentFile().mkdirs();
			
			PrintWriter writer = new PrintWriter(targetFile);
			writer.println(text);
			writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized static LinkedList<File> sort(LinkedList<File> files){
		if(files == null || files.isEmpty())
			return files;
		
		int directories = 0;
		for(File f : files){
			if(f.isDirectory())
				directories++;
		}
		File[] D = new File[directories];
		File[] F = new File[files.size() - directories];
		int fc = 0, dc = 0;
		
		for(File f : files){
			if(f.isDirectory())
				D[dc++] = f;
			else
				F[fc++] = f;
		}
		sort(D);
		sort(F);
		files.clear();
		for(File fx : D)
			files.add(fx);
		for(File fx : F)
			files.add(fx);
		return files;
	}
	
	public synchronized static void sort(File[] F){
		for(int i = 0; i < F.length; i++){
			for(int j = 0; j < F.length - i - 1; j++){
				String name1 = F[j].getName();
				String name2 = F[j + 1].getName();
				if(name1.compareTo(name2) > 0){
					File fx = F[j];
					F[j] = F[j + 1];
					F[j + 1] = fx;
				}
			}
		}
	}
	
}

