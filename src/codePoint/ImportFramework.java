package codePoint;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.text.Document;

import deassembler.CodeFramework;
import deassembler.SourceReader;
import ide.utils.DataManager;
import ide.utils.Editor;
import importIO.Import;
import importIO.ImportManager;
import ui.ImportResolver;
public class ImportFramework{
	private static ImportResolver imR = new ImportResolver();
	public static LinkedList<String> findClasses(String text){
          text = removeUsuals(text);
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
		String PACK = "";
		if(editor.getText().startsWith("package")) {
			PACK = editor.getText().substring(editor.getText().indexOf(" ") + 1, editor.getText().indexOf(';'));
		}
		LinkedList<String> unimported = new LinkedList<>();
		//Removing Java Lang Classess
		for(String classX : classess) {
			boolean found = false;
			for(String langClass : ImportManager.javaLangPack) {
				String className = langClass.substring(langClass.lastIndexOf('.') + 1);
				if(className.equals(classX)) {
					found = true;
					break;
				}
			}
			if(!found)
				unimported.add(classX);
		}
		classess.clear();
		//Managing Classess with Same Name but different Package
		LinkedList<LinkedList<String>> coexistingClassess = new LinkedList<>();
          main:
		for(String classX : unimported) {
			LinkedList<String> bases = new LinkedList<>();
			inner:
			for(Import im : ImportManager.getAllImports()) {
				if(im.getClassName().equals(classX)) {
					if(im.getPackage().equals(PACK) || contains(editor, im.getPackage(), im.getClassName()) || CodeFramework.isSource(PACK + "." + im.getClassName())) continue main;
					try {
						for(String baseX : bases)
							if(baseX.equals(im.getImport())) continue inner;
						if(!CodeFramework.isSource(im.getImport()))
							ClassLoader.getSystemClassLoader().loadClass(im.getImport());
						bases.add(im.getImport());
					}catch(Exception e) { }
				}
			}
			if(!bases.isEmpty())
				coexistingClassess.add(bases);
		}
		//Removing Multiple Existence
		for(LinkedList<String> bases : coexistingClassess) {
			if(bases.size() == 1) continue;
			for(int i = 0; i < bases.size(); i++) {
				for(int j = 0; j < bases.size() - i - 1; j++) {
					if(bases.get(j).equals(bases.get(j + 1)))
						bases.remove(j);
				}
			}
		}
		//Managing Classess with Multi and Single Base Package
		//Inserting the single bases and storing the multi bases classess
		LinkedList<Import> multiBases = new LinkedList<>();
		main:
			for(LinkedList<String> bases : coexistingClassess) {
				for(String pack : bases) {
					String base = pack.substring(0, pack.lastIndexOf('.'));
					if(base.equals(PACK) || contains(editor, base, pack.substring(pack.lastIndexOf('.') + 1))) {
						bases.clear();
						continue main;
					}
					if(bases.size() == 1) {
						String className = pack.substring(pack.lastIndexOf('.') + 1);
						if(!contains(editor, base, className)) {
							insertImport(editor, base, className);
						}
						bases.clear();
					}
					else {
						outer:
							for(String classX : bases) {
								String packName = classX.substring(0, classX.lastIndexOf('.'));
								String className = classX.substring(classX.lastIndexOf('.') + 1);
								if(contains(editor, packName, className)) break;
								Import im = new Import(packName, className);
								for(Import x : multiBases) {
									if(x.getImport().equals(im.getImport()))
										continue outer;
								}
								multiBases.add(im);
							}
					}
				}
			}
		LinkedList<Import> selectedPackages = imR.resolveImports(multiBases);
		for(Import im : selectedPackages) {
			if(!contains(editor, im.getPackage(), im.getClassName())) {
				insertImport(editor, im.getPackage(), im.getClassName());
			}
		}
	}

	public static boolean contains(Editor editor, String packName, String className) {
		return editor.getText().contains("import " + packName + ".*;") || 
				editor.getText().contains("import " + packName + "." + className + ";");
	}

	public static void insertImport(Editor editor, String pack, String className) {
		try {
			if(editor.getText().startsWith("package")) {
				String text = editor.getText();
				Document d = editor.getDocument();
				int start = text.indexOf(';') + 1;
				if(DataManager.isUsingStarImports())
					d.insertString(start, "\nimport " + pack + ".*;", null);
				else
					d.insertString(start, "\nimport " + pack + "." + className + ";", null);
			}
			else {
				Document d = editor.getDocument();
				if(DataManager.isUsingStarImports())
					d.insertString(0, "import " + pack + ".*;\n", null);
				else
					d.insertString(0, "import " + pack + "." + className + ";\n", null);
			}
			editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar().getValue() - 1);
		}catch(Exception e) { System.err.println(e); }
	}
}



















