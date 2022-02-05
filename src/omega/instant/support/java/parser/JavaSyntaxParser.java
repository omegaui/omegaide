/*
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

package omega.instant.support.java.parser;
import omega.ui.component.Editor;

import omega.io.ProjectBuilder;
import omega.io.FileOperationManager;
import omega.io.IconManager;
import omega.io.Startup;

import omega.ui.popup.NotificationPopup;

import omega.instant.support.java.highlighter.JavaErrorData;

import omega.instant.support.Highlight;
import omega.instant.support.LanguageTagView;
import omega.instant.support.AbstractSyntaxParser;

import omega.Screen;

import java.awt.image.BufferedImage;

import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

import java.awt.Image;
import java.awt.Color;

import javax.swing.ImageIcon;

import java.io.File;

import java.util.LinkedList;
import java.util.Locale;
import java.util.List;
import java.util.Scanner;

import javax.tools.StandardJavaFileManager;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static omega.io.UIManager.*;
public class JavaSyntaxParser extends AbstractSyntaxParser{
	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;

	private static LinkedList<Highlight> highlights = new LinkedList<>();
	private static LinkedList<JavaErrorData> datas = new LinkedList<>();

	private int errorCount;
	private int warningCount;

	public static volatile boolean parsing = false;
	public static volatile boolean packingCodes = false;

	public static final File BUILDSPACE_DIR = new File(".omega-ide", "buildspace");

	public static NotificationPopup dynamicBuildInfoPopup =  NotificationPopup.create(Screen.getScreen())
	.size(400, 120)
	.title("Instant Dynamic Compiler", TOOLMENU_COLOR4)
	.message("Instant Mode Speed Requires Pre-Compiled Byte Codes", TOOLMENU_COLOR2)
	.shortMessage("Click to Start a Headless Build", TOOLMENU_COLOR1)
	.dialogIcon(IconManager.fluenterrorImage)
	.iconButton(IconManager.fluentbuildImage, Screen.getProjectBuilder()::compileProject, "Click to Clean & Build")
	.build()
	.locateOnBottomLeft();

	public JavaSyntaxParser(){
		compiler = ToolProvider.getSystemJavaCompiler();
	}

	@Override
	public void parse(){
		if(parsing || packingCodes)
			return;
		new Thread(()->{

			errorCount = 0;
			warningCount = 0;

			parsing = true;
			DiagnosticCollector<JavaFileObject> diagnostics = compileSilently();
			parsing = false;

			LinkedList<Highlight> highlights = new LinkedList<>();

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
				String srcDir = BUILDSPACE_DIR.getAbsolutePath();
				filePath = Screen.getProjectFile().getProjectPath() + filePath.substring(srcDir.length() + (Screen.onWindows() ? 1 : 0));
				Editor editor = Screen.getScreen().getEditor(new File(filePath));

				if(editor == null){
					editor = Screen.getScreen().loadFile(new File(filePath));
				}

				SquiggleUnderlineHighlightPainter painter = new SquiggleUnderlineHighlightPainter(getSuitableColor(d.getKind()));
				painter.setUseFlatLine(d.getKind() == Kind.WARNING);

				Highlight h = new Highlight(editor, painter, start, end, d.getKind() != Kind.ERROR);
				h.setDiagnosticData(d);
				h.setGutterIconInfo(new JavaSyntaxParserGutterIconInfo(editor, d));

				highlights.add(huntHighlight(h));
			}

			LinkedList<Highlight> removableHighlights = new LinkedList<>();
			mainx:
			for(Highlight hx : this.highlights){
				for(Highlight hy : highlights){
					if(hx.equals(hy))
						continue mainx;
				}
				removableHighlights.add(hx);
			}

			resetHighlights(removableHighlights);

			this.highlights = highlights;

			this.highlights.forEach(hx->{
				hx.applyLineColor((int)hx.getDiagnosticData().getLineNumber() - 1, TOOLMENU_COLOR2_SHADE);
				hx.apply();
			});

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
		}).start();
	}

	@Override
	public int getLanguageTag() {
		return LanguageTagView.LANGUAGE_TAG_JAVA;
	}

	public DiagnosticCollector<JavaFileObject> compileSilently(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = prepareBuildSystem();

			if(files.isEmpty()) {
				return null;
			}

			fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);

			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(".omega-ide" + File.separator + "buildspace" + File.separator + "bin");

			getArgs(true).forEach(options::add);

			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

			fileManager.close();
			System.gc();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return diagnostics;
	}

	public DiagnosticCollector<JavaFileObject> compile(){
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		try{
			LinkedList<String> files = prepareBuildSystem();

			if(files.isEmpty()) {
				return null;
			}

			fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);

			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(".omega-ide" + File.separator + "buildspace" + File.separator + "bin");

			getArgs(false).forEach(options::add);

			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

			fileManager.close();
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

			//Removing Old Byte Codes
			for(String filePath : files){
				File file = new File(filePath);
				String name = file.getName();
				name = name.substring(0, name.indexOf('.'));
				String simpleName = name;
				name += ".class";
				String bytePath = file.getParentFile().getAbsolutePath();
				bytePath = bytePath.substring(BUILDSPACE_DIR.getAbsolutePath().length() + 4);
				bytePath = Screen.getProjectFile().getProjectPath() + File.separator + "bin" + bytePath;
				File[] F = new File(bytePath).listFiles();
				if(F == null || F.length == 0)
					continue;
				for(File fx : F){
					if(!fx.getName().endsWith(".class"))
						continue;
					if(fx.getName().equals(name) || (fx.getName().startsWith(simpleName) && fx.getName().charAt(simpleName.length()) == '$')){
						fx.delete();
					}
				}
				F = null;
			}

			System.gc();

			fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);

			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(Screen.getProjectFile().getProjectPath() + File.separator + "bin");

			getArgs(false).forEach(options::add);

			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

			fileManager.close();
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
			Screen.getProjectBuilder().createClassList();
			Scanner reader = new Scanner(new File(Screen.getProjectFile().getProjectPath() + File.separator + ".sources"));
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				if(!line.startsWith("\""))
				continue;
				files.add(line.substring(1, line.length() - 1));
			}
			reader.close();

			if(files.isEmpty()) {
				return null;
			}


			fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);

			LinkedList<String> options = new LinkedList<>();
			options.add("-d");
			options.add(Screen.getProjectFile().getProjectPath() + File.separator + "bin");

			getArgs(false).forEach(options::add);

			ProjectBuilder.optimizeProjectOutputs();

			compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

			fileManager.close();
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
				if(file.getName().endsWith(".java") && file.getAbsolutePath().startsWith(Screen.getProjectFile().getProjectPath() + File.separator + "src")){
					String filePath = file.getParentFile().getAbsolutePath();
					String projectDir = Screen.getProjectFile().getProjectPath();
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

	public static LinkedList<String> getArgs(boolean silentCall){
		String depenPath = "";

		if(!Screen.getProjectFile().getProjectManager().jars.isEmpty()) {
			for(String d : Screen.getProjectFile().getProjectManager().jars)
				depenPath += d + omega.Screen.PATH_SEPARATOR;
		}

		if(!Screen.getProjectFile().getProjectManager().resourceRoots.isEmpty()) {
			for(String d : Screen.getProjectFile().getProjectManager().resourceRoots)
				depenPath += d + omega.Screen.PATH_SEPARATOR;
		}

		File[] files = new File(Screen.getProjectFile().getProjectPath() + File.separator + "bin").listFiles();
		if(files != null && files.length != 0) {
			depenPath += Screen.getProjectFile().getProjectPath() + File.separator + "bin" + omega.Screen.PATH_SEPARATOR;
		}
		else if(!silentCall){
			dynamicBuildInfoPopup.locateOnBottomLeft().showIt();
			Screen.setStatus("Your Must Have Build the Whole Project at least Once for carrying out correct JavaSyntaxParsing and Instant Run", 0, IconManager.fluentbriefImage);
		}

		if(Screen.isNotNull(depenPath))
			depenPath = depenPath.substring(0, depenPath.length() - 1);

		LinkedList<String> commands = new LinkedList<>();
		if(Screen.isNotNull(depenPath)){
			commands.add("-classpath");
			commands.add(depenPath);
		}

		String modulePath = Screen.getProjectFile().getDependencyView().getModulePath();
		String modules = Screen.getProjectFile().getDependencyView().getModules();
		if(Screen.isNotNull(modulePath)){
			commands.add("--module-path");
			commands.add(modulePath);
			commands.add("--add-modules");
			commands.add(modules);
		}

		Screen.getProjectFile().getProjectManager().compileTimeFlags.forEach(commands::add);
		return commands;
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
		String path = Screen.getProjectFile().getProjectPath() + (Screen.onWindows() ? File.separator : "");
		buildSpacePath = buildSpacePath.substring(BUILDSPACE_DIR.getAbsolutePath().length() + (Screen.onWindows() ? 1 : 0));
		return path + buildSpacePath;
	}

	public static void resetHighlights(){
		highlights.forEach(h->{
			h.editor.javaErrorPanel.setVisible(false);
			h.remove();
		});
		highlights.clear();
	}

	public static void resetHighlights(LinkedList<Highlight> highlights){
		highlights.forEach(h->{
			h.editor.javaErrorPanel.setVisible(false);
			h.remove();
		});
		highlights.clear();
	}

	public static Highlight huntHighlight(Highlight h){
		for(Highlight hx : highlights){
			if(hx.equals(h))
				return hx;
		}
		return h;
	}
}
