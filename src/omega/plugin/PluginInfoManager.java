/**
  * PluginInfo Writer and Reader
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

package omega.plugin;
import java.util.LinkedList;
import java.util.Scanner;

import java.net.URLClassLoader;
import java.net.URL;

import java.io.File;
import java.io.PrintWriter;
import java.io.InputStream;
public class PluginInfoManager {
     public static void write(){
          File dir = new File("/home/arham/omega-ide-plugins");
          File[] files = dir.listFiles((file)->file.getName().endsWith("-OMEGAIDE.jar"));
          System.out.println("Total Plugins : " + files.length);
          try {
               PrintWriter writer = new PrintWriter(new File(".plugInfos"));
               for(File file : files){
                    String fileName = file.getName();
                    String className = fileName.substring(fileName.indexOf('-') + 1, fileName.lastIndexOf('-'));
                    URLClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURL() } );
                    String size = "";
                    double s = file.length() / 1000.0;
                    size = s + " KB";
                    if(s >= 1000) {
                         s /= 1000;
                         size = s + " MB";
                    }
                    if(s >= 1000) {
                         s /= 1000;
                         size = s + " GB";
                    }
                    Plugin plugin = (Plugin)loader.loadClass(className).newInstance();
                    writer.println("---------> Plugin Info Starts");
                    writer.println(fileName);
                    writer.println(plugin.getName());
                    writer.println(plugin.getVersion());
                    writer.println(plugin.getAuthor());
                    writer.println(plugin.getCopyright());
                    writer.println(size);
                    writer.println(plugin.getDescription());
                    writer.println("---------> Plugin Info Ends");
                    writer.flush();
                    loader.close();
               }
               writer.close();
          }
          catch(Exception e){
               e.printStackTrace();
          }
     }

     public static LinkedList<PlugInfo> read(InputStream in){
     	LinkedList<PlugInfo> plugInfos = new LinkedList<>();
          if(in == null)
               return plugInfos;
          try{
          	Scanner reader = new Scanner(in);
               PlugInfo plugInfo = null;
               while(reader.hasNextLine()){
                    String line = reader.nextLine();
                    if(line.equals("---------> Plugin Info Starts")){
                         plugInfo = new PlugInfo();
                         continue;
                    }
                    plugInfo.fileName = line;
                    plugInfo.name = reader.nextLine();
                    plugInfo.version = reader.nextLine();
                    plugInfo.author = reader.nextLine();
                    plugInfo.copyright = reader.nextLine();
                    plugInfo.size = reader.nextLine();
                    plugInfo.desc = "";
                    inner:
                         while(reader.hasNextLine()){
                              line = reader.nextLine();
                              if(line.equals("---------> Plugin Info Ends")) {
                                   plugInfos.add(plugInfo);
                                   break inner;
                              }
                              plugInfo.desc += line + "\n";
                         }
               }
               reader.close();
          }
          catch(Exception e){ 
          	e.printStackTrace();
          }
          return plugInfos;
     }
}

