package plugin;
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

     public static void main(String[] args){
     	LinkedList<PlugInfo> infos = read(new File(".plugset"));
          infos.forEach(i->{
               System.out.println(i.name);
               System.out.println(i.fileName);
               System.out.println(i.size);
               System.out.println(i.desc);
          });
     }
}
