package creator;
import java.io.PrintWriter;
import omega.database.DataEntry;
import java.util.LinkedList;
import ide.Screen;
import java.io.File;
import omega.database.DataBase;
public class ArgumentManager extends DataBase{
     public String run_time_args;
     public String compile_time_args;
     public String runDir;
     public String compileDir;
     public LinkedList<ListUnit> units = new LinkedList<>();
     public ArgumentManager(){
     	super(Screen.getFileView().getProjectPath() + File.separator + ".args");
          load();
     }

     public void load(){
          compile_time_args = getEntryAt("Compile Time Argument", 0) != null ? getEntryAt("Compile Time Argument", 0).getValue() : "";
          run_time_args = getEntryAt("Run Time Argument", 0) != null ? getEntryAt("Run Time Argument", 0).getValue() : "";                
          compileDir = getEntryAt("Compile Time Working Directory", 0) != null ? getEntryAt("Compile Time Working Directory", 0).getValue() : "";   
          runDir = getEntryAt("Run Time Working Directory", 0) != null ? getEntryAt("Run Time Working Directory", 0).getValue() : "";

          if(!new File(compileDir).exists())
               compileDir = "";
          if(!new File(runDir).exists())
               runDir = "";

          LinkedList<DataEntry> extensions = getEntries("Extensions");
          LinkedList<DataEntry> containers = getEntries("Containers");
          LinkedList<DataEntry> sources = getEntries("Sources");
          LinkedList<DataEntry> bounds = getEntries("Bounds Surrounded");
          if(extensions == null) return;
          for(int i = 0; i < extensions.size(); i++){
               units.add(new ListUnit(extensions.get(i).getValue(), 
                                      containers.get(i).getValue(), 
                                      sources.get(i).getValue(), 
                                      bounds.get(i).getValueAsBoolean()));
          }
     }

     public void genLists(){
     	units.forEach(unit->{
               LinkedList<File> files = new LinkedList<>();
               loadFiles(unit.ext, files, new File(unit.sourceDir));
               if(!files.isEmpty())
                    writeList(unit.container, files, unit.sur);
	     });
     }

     public void writeList(String name, LinkedList<File> files, boolean sur){
     	try{
     		PrintWriter writer = new PrintWriter(new File(Screen.getFileView().getProjectPath() + File.separator + name));
               files.forEach(file->{
                    if(sur)
                         writer.println("\"" + file.getAbsolutePath() + "\"");
                    else
                         writer.println(file.getAbsolutePath());
               });
               writer.close();
     	}catch(Exception e){ System.err.println(e); }
     }

     public void loadFiles(String ext, LinkedList<File> files, File dir){
     	File[] F = dir.listFiles();
          if(F == null || F.length == 0) return;
          for(File fx : F){
               if(!fx.isDirectory() && fx.getName().endsWith(ext))
                    files.add(fx);
               else if(fx.isDirectory())
                    loadFiles(ext, files, fx);
          }
     }

     @Override
     public void save(){
          updateEntry("Compile Time Argument", compile_time_args, 0);
          updateEntry("Run Time Argument", run_time_args, 0);
          updateEntry("Compile Time Working Directory", compileDir, 0);
          updateEntry("Run Time Working Directory", runDir, 0);
          for(int i = 0; i < units.size(); i++){
               ListUnit u = units.get(i);
               updateEntry("Extensions", u.ext, i);
               updateEntry("Containers", u.container, i);
               updateEntry("Sources", u.sourceDir, i);
               updateEntry("Bounds Surrounded", String.valueOf(u.sur), i);
          }
          super.save();
     }
}
