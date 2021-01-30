package creator;
import ide.Screen;
import java.io.File;
import omega.database.DataBase;
public class ArgumentManager extends DataBase{
     public String run_time_args;
     public String compile_time_args;
     public String runDir;
     public String compileDir;
     public ArgumentManager(){
     	super(Screen.getFileView().getProjectPath() + File.separator + ".args");
          run_time_args = "";
          compile_time_args = "";
          runDir = "";
          compileDir = "";
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
     }

     @Override
     public void save(){
          updateEntry("Compile Time Argument", compile_time_args, 0);
          updateEntry("Run Time Argument", run_time_args, 0);
          updateEntry("Compile Time Working Directory", compileDir, 0);
          updateEntry("Run Time Working Directory", runDir, 0);
          super.save();
     }
}
