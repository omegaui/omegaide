package omega.instant.support.java;
import javax.tools.SimpleJavaFileObject;
import java.util.Locale;
import java.util.List;
import javax.tools.Diagnostic;
import omega.highlightUnit.Highlight;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import omega.utils.systems.BuildView;
import java.io.PrintWriter;
import omega.startup.Startup;
import omega.Screen;
import omega.utils.Editor;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import javax.tools.JavaFileObject;
import javax.tools.DiagnosticCollector;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import omega.utils.systems.creators.FileOperationManager;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class JavaSyntaxParser {
	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;

	private static LinkedList<Highlight> highlights = new LinkedList<>();
	
	private static volatile boolean parsing = false;

	private static final File BUILDSPACE_DIR = new File(".omega-ide", "buildspace");
	
	public JavaSyntaxParser(){
		compiler = ToolProvider.getSystemJavaCompiler();
		
		fileManager = compiler.getStandardFileManager(null, null, null);
	}

	public DiagnosticCollector<JavaFileObject> compile(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = prepareBuildSystem();

			if(files.isEmpty())
				return null;

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);
			
			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(".omega-ide" + File.separator + "buildspace" + File.separator + "bin");
			
			getArgs().forEach(options::add);
			
			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diagnostics;
	}
	
     public static LinkedList<String> getArgs(){
     	String depenPath = "";
          
          if(!Screen.getFileView().getProjectManager().jars.isEmpty()) {	
               for(String d : Screen.getFileView().getProjectManager().jars)
                    depenPath += d + omega.Screen.PATH_SEPARATOR;
               
               depenPath += BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar" + omega.Screen.PATH_SEPARATOR;
               
               if(Screen.isNotNull(depenPath))
                    depenPath = depenPath.substring(0, depenPath.length() - 1);
          }
          
     	LinkedList<String> commands = new LinkedList<>();
     	if(Screen.isNotNull(depenPath)){
          	commands.add("-classpath");
          	commands.add(depenPath);
          }

		String modulePath = Screen.getFileView().getDependencyView().getModulePath();
		String modules = Screen.getFileView().getDependencyView().getModules();
          if(Screen.isNotNull(modulePath)){
          	commands.add("--module-path");
          	commands.add(modulePath);
          	commands.add("--add-modules");
          	commands.add(modules);
          }

		Screen.getFileView().getProjectManager().compileTimeFlags.forEach(commands::add);
     	return commands;
     }

	public static void packCompiledCodes(){
		try{
			LinkedList<String> compiledCodes = new LinkedList<>();
			loadFiles(compiledCodes, new File(Screen.getFileView().getProjectPath() + File.separator + "bin"), ".class");

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar"));
			String binDir = Screen.getFileView().getProjectPath() + File.separator + "bin";
			for(String path : compiledCodes){
				String metaPath = path.substring(path.lastIndexOf(binDir) + binDir.length() + 1);
				out.putNextEntry(new ZipEntry(metaPath));

				InputStream in = new FileInputStream(new File(path));
				while(in.available() > 0)
					out.write(in.read());
				out.flush();
				in.close();
			}
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
     
	public static void loadFiles(LinkedList<String> files, File dir, String ext){
		File[] F = dir.listFiles();
		if(F == null || F.length == 0)
			return;
		for(File file : F){
			if(file.isDirectory())
				loadFiles(files, file, ext);
			else if(file.getName().endsWith(ext))
				files.add(file.getAbsolutePath());
		}
	}

	public void parse(){
		if(parsing)
			return;
		new Thread(()->{
			parsing = true;
			DiagnosticCollector<JavaFileObject> diagnostics = compile();
			parsing = false;
			if(diagnostics.getDiagnostics() == null)
				return;
			
			highlights.forEach(h -> h.remove());
			highlights.clear();
			
			List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
			System.out.println("\nAny Diagnostics? " + (!diagnosticList.isEmpty()));
			for(Diagnostic d : diagnosticList) {
				if(d.getKind() != Diagnostic.Kind.ERROR)
					continue;
				int start = (int)d.getStartPosition();
				int end = (int)d.getEndPosition();
				if(start == end)
					end++;
				
				JavaFileObject fileObject = (JavaFileObject)d.getSource();
				String filePath = fileObject.toUri().getPath();
				String srcDir = BUILDSPACE_DIR.getAbsolutePath();
				filePath = Screen.getFileView().getProjectPath() + filePath.substring(srcDir.length());
				Editor editor = Screen.getScreen().getEditor(new File(filePath));
				
				if(editor == null){
					editor = Screen.getScreen().loadFile(new File(filePath));
				}
				
				SquiggleUnderlineHighlightPainter painter = new SquiggleUnderlineHighlightPainter(TOOLMENU_COLOR2);
				painter.setUseFlatLine(true);
				
				Highlight h = new Highlight(editor, painter, start, end);
				h.latest = true;
				h.apply();
				highlights.add(h);
			}
			
			System.out.println("Total Diagnostics : " + diagnosticList.size() + "\nTotal Highlights : " + highlights.size());
		}).start();
	}

	public static Highlight doesOffsetAlreadyExists(int start, int end){
		for(Highlight h : highlights){
			if(h.equals(start, end))
				return h;
		}
		return null;
	}

	public static void prepareBuildSpace(LinkedList<String> compilationUnitFiles){
		try{
			//Cleaning BuildSpace
			Editor.deleteDir(BUILDSPACE_DIR);
			Startup.writeUIFiles();
		
			//Generating BuildSpace
			LinkedList<Editor> editors = Screen.getScreen().getAllEditors();
			for(Editor editor : editors){
				File file = editor.currentFile;
				if(file.getName().endsWith(".java") && file.getAbsolutePath().startsWith(Screen.getFileView().getProjectPath() + File.separator + "src")){
					String filePath = file.getParentFile().getAbsolutePath();
					String projectDir = Screen.getFileView().getProjectPath();
					String metaPath = filePath.substring(filePath.indexOf(projectDir) + projectDir.length());
					String targetPath = BUILDSPACE_DIR.getAbsolutePath() + metaPath;
					File targetDir = new File(targetPath);
					targetDir.mkdirs();
					File targetFile = new File(targetPath, file.getName());
					FileOperationManager.writeNewTextToFile(editor.getText(), targetFile);
					compilationUnitFiles.add(targetFile.getAbsolutePath());
				}
			}

			//Packing Compiled Codes
			packCompiledCodes();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static LinkedList<String> prepareBuildSystem(){
		LinkedList<String> files = new LinkedList<>();
		
		try{
			//Preparing Buildspace ...
			prepareBuildSpace(files);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return files;
	}
}
