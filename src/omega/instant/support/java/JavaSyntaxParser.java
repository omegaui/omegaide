/**
* The Java Syntax Parser
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

package omega.instant.support.java;
import java.util.Scanner;
import java.util.zip.ZipFile;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Image;
import org.fife.ui.rtextarea.GutterIconInfo;
import javax.swing.ImageIcon;
import omega.utils.IconManager;
import java.awt.Color;
import javax.tools.SimpleJavaFileObject;
import java.util.Locale;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
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
	private static LinkedList<JavaErrorData> datas = new LinkedList<>();
	private static LinkedList<JavaSyntaxParserGutterIconInfo> gutterIconInfos = new LinkedList<>();
	
	private int errorCount;
	private int warningCount;
	
	public static volatile boolean parsing = false;
	public static volatile boolean packingCodes = false;
	
	public static final File BUILDSPACE_DIR = new File(".omega-ide", "buildspace");
	
	public JavaSyntaxParser(){
		compiler = ToolProvider.getSystemJavaCompiler();
		
		fileManager = compiler.getStandardFileManager(null, null, null);
	}
	
	public synchronized void parse(){
		if(parsing || packingCodes)
			return;
		new Thread(()->{
			
			errorCount = 0;
			warningCount = 0;
			
			parsing = true;
			DiagnosticCollector<JavaFileObject> diagnostics = compile();
			parsing = false;
			
			highlights.forEach(h -> h.remove());
			highlights.clear();
			
			gutterIconInfos.forEach(info->info.editor.getAttachment().getGutter().removeTrackingIcon(info.gutterIconInfo));
			
			if(diagnostics == null || diagnostics.getDiagnostics() == null)
				return;
			
			List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
			for(Diagnostic d : diagnosticList) {
				if(d.getKind() != Kind.ERROR && d.getKind() != Kind.WARNING && d.getKind() != Kind.MANDATORY_WARNING)
					continue;
				
				int start = (int)d.getStartPosition();
				int end = (int)d.getEndPosition();
				
				if(start == end){
					start--;
					end++;
				}
				
				JavaFileObject fileObject = (JavaFileObject)d.getSource();
				String filePath = fileObject.toUri().getPath();
				System.out.println("Got Source Path : " + filePath);
				String srcDir = BUILDSPACE_DIR.getAbsolutePath();
				filePath = Screen.getFileView().getProjectPath() + filePath.substring(srcDir.length());
				System.out.println("New Path : " + filePath);
				Editor editor = Screen.getScreen().getEditor(new File(filePath));
				
				if(editor == null){
					editor = Screen.getScreen().loadFile(new File(filePath));
				}
				
				try{
					ImageIcon icon = new ImageIcon(getSuitableIcon(d.getKind()));
					icon = new ImageIcon(icon.getImage().getScaledInstance(editor.getFont().getSize(), editor.getFont().getSize(), Image.SCALE_SMOOTH));
					gutterIconInfos.add(new JavaSyntaxParserGutterIconInfo(editor.getAttachment().getGutter().addLineTrackingIcon((int)(d.getLineNumber() - 1), icon, d.getMessage(Locale.ROOT)), editor));
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				SquiggleUnderlineHighlightPainter painter = new SquiggleUnderlineHighlightPainter(getSuitableColor(d.getKind()));
				painter.setUseFlatLine(d.getKind() == Kind.WARNING);
				
				Highlight h = new Highlight(editor, painter, start, end, d.getKind() != Kind.ERROR);
				h.apply();
				highlights.add(h);
			}
			
			datas.forEach(data->data.resetData());
			datas.clear();
			
			main:
			for(Highlight h : highlights){
				for(JavaErrorData data : datas){
					if(data.editor == h.editor)
						continue main;
				}
				JavaErrorData data = new JavaErrorData();
				data.editor = h.editor;
				datas.add(data);
			}
			
			for(Highlight h : highlights){
				for(JavaErrorData data : datas){
					data.add(h);
				}
			}
			
			datas.forEach(data->{
				data.setData();
			});
			
			int totalErrors = 0;
			int totalWarnings = 0;
			for(JavaErrorData data : datas){
				totalErrors += data.errorCount;
				totalErrors += data.warningCount;
			}
			
			if(totalErrors > 0 || totalWarnings > 0)
				omega.Screen.getScreen().getToolMenu().setTask(totalErrors + " Error(s), " + totalWarnings + " Warning(s)");
			else
				omega.Screen.getScreen().getToolMenu().setTask("Currently Opened Codes are Error Free!");
			
			System.gc();
		}).start();
	}
	
	public DiagnosticCollector<JavaFileObject> compile(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = prepareBuildSystem();
			
			if(files.isEmpty()) {
				return null;
			}
			
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);
			
			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(".omega-ide" + File.separator + "buildspace" + File.separator + "bin");
			
			getArgs().forEach(options::add);
			
			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
			System.gc();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diagnostics;
	}
	
	public DiagnosticCollector<JavaFileObject> compileAndSaveToProjectBin(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = prepareBuildSystem();
			
			if(files.isEmpty()) {
				return null;
			}
			
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);
			
			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(Screen.getFileView().getProjectPath() + File.separator + "bin");
			
			getArgs().forEach(options::add);
			
			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
			System.gc();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diagnostics;
	}
	
	public DiagnosticCollector<JavaFileObject> compileFullProject(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = new LinkedList<>();
			Screen.getBuildView().createClassList();
			Scanner reader = new Scanner(new File(Screen.getFileView().getProjectPath() + File.separator + ".sources"));
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				if(!line.startsWith("\""))
					continue;
				files.add(line.substring(1, line.length() - 1));
			}
			
			if(files.isEmpty()) {
				return null;
			}
			
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);
			
			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(Screen.getFileView().getProjectPath() + File.separator + "bin");
			
			getArgs().forEach(options::add);
			
			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
			System.gc();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diagnostics;
	}
	
	public static LinkedList<String> prepareBuildSystem(){
		LinkedList<String> files = new LinkedList<>();
		
		try{
			//Preparing Buildspace ...
			prepareBuildSpace(files);
			
			//Packing Compiled Codes
			if(!new File(".omega-ide" + File.separator + "buildspace", "compiled.jar").exists())
				packCompiledCodes();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return files;
	}
	
	public static void prepareBuildSpace(LinkedList<String> compilationUnitFiles){
		try{
			//Cleaning BuildSpace
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static LinkedList<String> getArgs(){
		String depenPath = "";
		
		if(!Screen.getFileView().getProjectManager().jars.isEmpty()) {
			for(String d : Screen.getFileView().getProjectManager().jars)
				depenPath += d + omega.Screen.PATH_SEPARATOR;
			
			if(new File(BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar").exists()) {
				depenPath += BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar" + omega.Screen.PATH_SEPARATOR;
			}
			
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
		if(packingCodes)
			return;
		packingCodes = true;
		File zipFile = new File(BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar");
		try{
			LinkedList<String> compiledCodes = new LinkedList<>();
			loadFiles(compiledCodes, new File(Screen.getFileView().getProjectPath() + File.separator + "bin"), ".class");
			
			if(compiledCodes.isEmpty()){
				Screen.setStatus("Your Must Have Build the Whole Project at least Once for carrying out correct JavaSyntaxParsing and Instant Run", 0);
				packingCodes = false;
				return;
			}
			
			Startup.writeUIFiles();
			
			if(zipFile.exists())
				zipFile.delete();
			
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(BUILDSPACE_DIR.getAbsolutePath() + File.separator + "compiled.jar"));
			String binDir = Screen.getFileView().getProjectPath() + File.separator + "bin";
			for(String path : compiledCodes){
				int per = ((compiledCodes.indexOf(path) + 1) * 100)/ compiledCodes.size();
				Screen.setStatus("Packing Compiled Codes for Java Syntax Parsing " + per + "%", per);
				String metaPath = path.substring(path.lastIndexOf(binDir) + binDir.length() + 1);
				
				ZipEntry entry = new ZipEntry(metaPath);
				entry.setCompressedSize(-1);
				
				out.putNextEntry(entry);
				
				InputStream in = new FileInputStream(new File(path));
				while(in.available() > 0)
					out.write(in.read());
				
				out.closeEntry();
				
				in.close();
			}
			out.flush();
			out.finish();
			out.close();
		}
		catch(Exception e){
			if(zipFile.exists())
				zipFile.delete();
		}
		packingCodes = false;
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
	
	public static Color getSuitableColor(Kind kind){
		if(kind == Kind.ERROR || kind == Kind.MANDATORY_WARNING)
			return TOOLMENU_COLOR2;
		return TOOLMENU_COLOR4;
	}
	
	public static BufferedImage getSuitableIcon(Kind kind){
		if(kind == Kind.ERROR || kind == Kind.MANDATORY_WARNING)
			return IconManager.fluenterrorImage;
		return IconManager.fluentwarningImage;
	}

	public static String convertToProjectPath(String buildSpacePath){
		if(!buildSpacePath.startsWith(BUILDSPACE_DIR.getAbsolutePath()))
			return buildSpacePath;
		String path = Screen.getFileView().getProjectPath();
		buildSpacePath = buildSpacePath.substring(buildSpacePath.indexOf(BUILDSPACE_DIR.getAbsolutePath()) + BUILDSPACE_DIR.getAbsolutePath().length());
		return path + buildSpacePath;
	}
}
