package omega.plugin;
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
import java.net.*;
import java.io.*;
import java.util.*;
public class PlugInfoWriter {
     public static void write(String plugDir){
          try{
               File[] files = new File(plugDir).listFiles();
               if(files == null || files.length == 0) return;
               String message = "";
               PrintWriter writer = new PrintWriter(".plugset");
               for(File f : files){
                    if(!f.getName().endsWith("-OMEGAIDE.jar")) continue;
                    String className = f.getName();
                    className = className.substring(className.indexOf('-') + 1);
                    className = className.substring(0, className.lastIndexOf('-'));
                    ClassLoader loader = URLClassLoader.newInstance(new URL[]{ f.toURL() });
                    Plugin p = (Plugin)loader.loadClass(className).newInstance();
                    String size = (double)f.length() / 1000.0 + " kB";
                    writer.println(p.getName() + "<<>>" + f.getName() + "<<>>" + size);
                    writer.println(p.getDescription());
                    writer.println("----");
               }
               writer.close();
          }catch(Exception e){ System.err.println(e); }
          return;
     }
     
     public static LinkedList<PlugInfo> read(File file){
     	LinkedList<PlugInfo> infos = new LinkedList<>();
          try{
               Scanner reader = new Scanner(file);
               boolean readDes = false;
               PlugInfo i = null;
               while(reader.hasNextLine()){
               	String line = reader.nextLine();
                    if(line.equals("----")) readDes = false;
                    if(line.contains("<<>>") && !readDes){
                         String name = line.substring(0, line.indexOf("<<>>"));
                         String fileName = line.substring(line.indexOf("<<>>") + 4, line.lastIndexOf("<<>>"));
                         String size = line.substring(line.lastIndexOf("<<>>") + 4);
                         i = new PlugInfo();
                         i.name = name;
                         i.fileName = fileName;
                         i.size = size;
                         readDes = true;
                         infos.add(i);
                    }
                    else if(readDes){
                         i.desc += line + "\n";
                    }
               }
               reader.close();
          }catch(Exception e){ System.err.println(e); }
          return infos;
     }
}
