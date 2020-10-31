package codePoint;
import java.io.File;
import java.util.LinkedList;
import java.util.StringTokenizer;

import deassembler.SourceReader;
import ide.utils.DataManager;
import ide.utils.Editor;
import ide.utils.UIManager;
import importIO.Import;
import importIO.ImportManager;
import ui.ImportResolver;
public class ImportFramework{
	private static ImportResolver imR = new ImportResolver();
	public static LinkedList<String> findClasses(String text){
		LinkedList<String> cls = new LinkedList<>();
		try {
			text = text.substring(text.indexOf("public"));
		}catch(Exception e) {return cls;}
		int pos = 0;
		for(int i = 0; i < text.length(); i++){
			char ch = text.charAt(i);
			if(ch != '\"' && ch != '\'' && ch != '_' && ch != '$' && !Character.isLetterOrDigit(ch)){
				String sx = text.substring(pos, i);
				pos = i + 1;
				if(sx.length() > 0 && Character.isUpperCase(sx.charAt(0)) && !SourceReader.isInnerLine(sx) && !cls.contains(sx))
					cls.add(sx);
			}
		}
		return cls;
	}
	
	public static synchronized String removeUsuals(String text) {
		boolean commentStarts = false;
		StringTokenizer tok = new StringTokenizer(text, "\n");
		text = "";
		while(tok.hasMoreTokens()){
			String line = tok.nextToken().trim();
			//Skipping Strings and characters
			String cLine = line;
			line = "";
			boolean r = true;
			int in = 0;
			for(char ch : cLine.toCharArray()){
				if((ch == '\"' || ch == '\'') && in > 0 && cLine.charAt(in - 1) != '\\')
					r = !r;
				if(r && ch != '\"' && ch != '\''){
					line += ch;
				}
				in++;
			}
			cLine = "";
			//Skipping Comments
			if(line.startsWith("//")) continue;
			if(line.startsWith("/*")){
				commentStarts = true;
				continue;
			}
			else if(line.contains("*/")){
				commentStarts = false;
				continue;
			}
			else if(commentStarts) continue;
			text += line + "\n";
		}
		return text;
	}

	public static synchronized void addImports(LinkedList<String> classess, Editor editor){
		if(ImportManager.reading) return;
		String pack = "";
		final String text = removeUsuals(editor.getText());
		if(text.trim().startsWith("package ") && text.trim().contains(";")) {
			pack = text.trim().substring(text.trim().indexOf("package ") + 8, text.trim().indexOf(';'));
		}
		String className = "";
		String path = "";
		if(editor.currentFile != null) {
			className = editor.currentFile.getName();
			className = className.substring(0, className.indexOf('.'));
			path = editor.currentFile.getAbsolutePath();
			path = path.substring(0, path.lastIndexOf('/'));
		}
		try {
			//Managing single locale imports
			LinkedList<String> counted = new LinkedList<>();
			LinkedList<Import> single_locales = new LinkedList<>();
			for(String cl : classess) {
				if(new File(path+"/"+cl+".java").exists() || ImportManager.getJavaLangPack().contains("java.lang."+cl) || counted.contains(cl)) continue;
				Import i = null;
				int c = 0;
				for(Import im : ImportManager.getAllImports()) {
					if(im.getClassName().equals(cl)) {
						i = im;
						c++;
					}
				}
				counted.add(cl);
				if(c == 1) {
					if(!text.contains("import "+i.getPackage()+".*;") &&
							!text.contains("import "+i.getImport()+";")) {
						single_locales.add(i);
					}
				}
			}
			for(Import im : single_locales) {
				String packageName = im.getPackage();
				if(!packageName.equals("java.lang") && !packageName.equals(pack)) {
					if(!editor.getText().contains("import "+packageName+".*;") && 
							!editor.getText().contains("import "+im.getImport()+";")) {
						if(editor.getText().startsWith("package")) {
							int index = editor.getText().indexOf(';');
							editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar()
									.getValue() + UIManager.fontSize);
							if(DataManager.isUsingStarImports())					
								editor.insert("\nimport "+packageName+".*;", index+1);
							else
								editor.insert("\nimport "+im.getImport()+";", index+1);

						}
						else{
							if(DataManager.isUsingStarImports())					
								editor.insert("import "+packageName+".*;\n", 0);
							else
								editor.insert("\nimport "+im.getImport()+";\n", 0);
							editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar()
									.getValue() + UIManager.fontSize);
						}

					}
				}
				classess.remove(im.getClassName());
			}
			//Managing multiple locale classes
			SourceReader reader = new SourceReader(editor.getText(), true);
			LinkedList<Import> ims = new LinkedList<>();
			for(String cl : classess) {
				if(new File(path+"/"+cl+".java").exists() || ImportManager.getJavaLangPack().contains("java.lang."+cl)) continue;
				for(Import im : ImportManager.getAllImports()) {
					if(!im.getPackage().equals(pack) && !im.getPackage().equals("java.lang") && im.getClassName().equals(cl) && !im.getClassName().equals(className) && reader.getPackage(cl) == null) {
						if(!editor.getText().contains("import "+im.getPackage()+".*;") && !editor.getText().contains("import "+im.getImport()+";")) {
							boolean x = false;
							for(Import ix : ims) {
								if(ix.getImport().equals(im.getImport())){
									x = true;
									break;
								}
							}
							for(SourceReader.Import i : reader.imports) {
								if(i.get().equals(im.getImport())) {
									x = false;
									break;
								}
							}
							if(!x)
								ims.add(im);
						}
					}
				}
			}
			if(ims.isEmpty()) return;
			Import[] imsx = new Import[ims.size()];
			int k = 0;
			for(Import im : ims)
				imsx[k++] = im;
			for(int i = 0; i < imsx.length; i++) {
				for(int j = 0; j < imsx.length - 1 - i; j++) {
					Import ix = imsx[j];
					Import jx = imsx[j + 1];
					if(ix.getPackage().compareTo(jx.getPackage()) > 0) {
						imsx[j] = imsx[j+1];
						imsx[j + 1] = ix;
					}
				}
			}
			ims.clear();
			for(Import im : imsx) {
				ims.add(im);
			}
			LinkedList<Import> imports = imR.resolveImports(ims);
			for(Import im : imports) {
				String packageName = im.getPackage();
				if(!packageName.equals("java.lang") && !packageName.equals(pack)) {
					if(!editor.getText().contains("import "+packageName+".*;") && 
							!editor.getText().contains("import "+im.getImport()+";")) {
						if(editor.getText().startsWith("package")) {
							int index = editor.getText().indexOf(';');
							editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar()
									.getValue() + UIManager.fontSize);
							if(DataManager.isUsingStarImports())					
								editor.insert("\nimport "+packageName+".*;", index+1);
							else
								editor.insert("\nimport "+im.getImport()+";", index+1);

						}
						else{
							if(DataManager.isUsingStarImports())					
								editor.insert("import "+packageName+".*;\n", 0);
							else
								editor.insert("\nimport "+im.getImport()+";\n", 0);
							editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar()
									.getValue() + UIManager.fontSize);
						}
					}
				}
			}
			imports.clear();
		}catch(Exception e) {System.err.println("Concurrent Modification Detected...");}
	}
	//editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar()
		//	.getValue() + UIManager.fontSize);
}
