package omega.utils;
import omega.Screen;
import omega.deassembler.Assembly;
import java.io.PrintWriter;
import java.io.File;
import java.util.LinkedList;
import omega.database.DataBase;
import omega.database.DataEntry;

public class ProjectDataBase extends DataBase{
	public String compile_time_args;
	public String run_time_args;
	public String jdkPath;
	public String mainClass;
     public File jdk;
     public volatile boolean non_java;
     public LinkedList<String> jars = new LinkedList<>();
     public LinkedList<String> natives = new LinkedList<>();
     public LinkedList<String> resourceRoots = new LinkedList<>();
     public LinkedList<String> modules = new LinkedList<>();

     public ProjectDataBase() {
          super(Screen.getFileView().getProjectPath() + File.separator + ".projectInfo");
          load();
     }

	public void load() {
		jdkPath = getEntryAt("JDK Path", 0) != null ? getEntryAt("JDK Path", 0).getValue() : null;
		compile_time_args = getEntryAt("Compile_Time", 0) != null ? getEntryAt("Compile_Time", 0).getValue() : "";
		run_time_args = getEntryAt("Run_Time", 0) != null ? getEntryAt("Run_Time", 0).getValue() : "";
		mainClass = getEntryAt("Main Class", 0) != null ? getEntryAt("Main Class", 0).getValue() : "";
          non_java = getEntryAt("Non-Java Project", 0) != null ? getEntryAt("Non-Java Project", 0).getValueAsBoolean() : false;
          if(!non_java) {
               jdk = new File(jdkPath != null ? jdkPath : "");
     		try {
     			Screen.getRunView().mainClass = mainClass;
     		}catch(Exception e) {}
          }
          LinkedList<DataEntry> mainEditors = getEntries("Opened Editors on Main Tab Panel");
          LinkedList<DataEntry> rightEditors = getEntries("Opened Editors on Right Tab Panel");
          LinkedList<DataEntry> bottomEditors = getEntries("Opened Editors on Bottom Tab Panel");
          LinkedList<DataEntry> jars = getEntries("Project Classpath : Required Jars");
          LinkedList<DataEntry> natives = getEntries("Project Classpath : Required Native Libraries");
          LinkedList<DataEntry> resourceRoots = getEntries("Project Classpath : Required Resource Roots");
          LinkedList<DataEntry> modules = getEntries("Project Classpath : Required Modules");
          if(mainEditors != null){
             for(DataEntry e : mainEditors) {
                    File f = new File(e.getValue());
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFile(f);
               }
          }
          if(rightEditors != null){
              for(DataEntry e : rightEditors) {
                    File f = new File(e.getValue());
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFileOnRightTabPanel(f);
               }
          }
          if(bottomEditors != null){
              for(DataEntry e : bottomEditors) {
                    File f = new File(e.getValue());
                    if(f.exists())
                         Screen.getFileView().getScreen().loadFileOnBottomTabPanel(f);
               }
          }
          if(jars != null){
               for(DataEntry e : jars){
                    File f = new File(e.getValue());
                    if(f.exists())
                         this.jars.add(e.getValue());
               }
          }
          if(natives != null){
               for(DataEntry e : natives){
                    File f = new File(e.getValue());
                    if(f.exists())
                         this.natives.add(e.getValue());
               }
          }
          if(resourceRoots != null){
               for(DataEntry e : resourceRoots){
                    File f = new File(e.getValue());
                    if(f.exists())
                         this.resourceRoots.add(e.getValue());
               }
          }
          if(modules != null){
               for(DataEntry e : modules){
                    File f = new File(e.getValue());
                    if(f.exists())
                         this.modules.add(e.getValue());
               }
          }
	}

	@Override
	public void save() {
		clear();
		addEntry("JDK Path", jdkPath);
		addEntry("Compile_Time", compile_time_args);
		addEntry("Run_Time", run_time_args);
		addEntry("Main Class", Screen.getRunView().mainClass != null ? Screen.getRunView().mainClass : "");
          addEntry("Non-Java Project", String.valueOf(non_java));
          Screen.getFileView().getScreen().getTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Main Tab Panel", editor.currentFile.getAbsolutePath());
               }
          });
          Screen.getFileView().getScreen().getRightTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Right Tab Panel", editor.currentFile.getAbsolutePath());
               }
          });
          Screen.getFileView().getScreen().getBottomTabPanel().getEditors().forEach(editor->{
               if(editor.currentFile != null) {
                    addEntry("Opened Editors on Bottom Tab Panel", editor.currentFile.getAbsolutePath());
               }
          });
          jars.forEach(path->{
               addEntry("Project Classpath : Required Jars", path);
          });
          natives.forEach(path->{
               addEntry("Project Classpath : Required Native Libraries", path);
          });
          resourceRoots.forEach(path->{
               addEntry("Project Classpath : Required Resource Roots", path);
          });
          modules.forEach(path->{
               addEntry("Project Classpath : Required Modules", path);
          });
		super.save();
	}

     public static void genInfo(String projectPath, boolean non_java){
     	try{
               File file = new File(projectPath + File.separator + ".projectInfo");
               if(file.exists()) return;
     		PrintWriter writer = new PrintWriter(file);
               writer.println(">Non-Java Project");
               writer.println("-" + non_java);
               writer.close();
     	}catch(Exception e){ System.err.println(e); }
     }

     public void setJDKPath(String path){
     	this.jdkPath = path;
          Screen.getFileView().readJDK();
     }
}
