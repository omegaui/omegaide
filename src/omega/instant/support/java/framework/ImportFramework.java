/*
 * Prepares Auto-Imports
 * Copyright (C) 2022 Omega UI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.instant.support.java.framework;
import omega.ui.component.Editor;

import omega.io.DataManager;
import omega.io.IconManager;

import omega.instant.support.java.management.JDKManager;
import omega.instant.support.java.management.Import;

import omega.instant.support.java.assist.CodeTokenizer;
import omega.instant.support.java.assist.SourceReader;

import omega.ui.dialog.ImportResolver;

import java.io.PrintWriter;

import omega.Screen;

import javax.swing.text.Document;

import java.util.LinkedList;
public class ImportFramework {

	//The Object of the Window that lets you choose the imports
	//when classes in different packages have a same name.
	private static ImportResolver imR = new ImportResolver();

	/**
	 * Prepares an arbitary list of imported classes in the given text
	 */
	public static LinkedList<String> fastListContainedClasses(String text){
		LinkedList<String> imports = new LinkedList<>();
		try{
			text = text.substring(0, text.indexOf("public "));
			LinkedList<String> lines = CodeTokenizer.tokenize(text, '\n');
			for(String line : lines){
				if(!line.startsWith("import "))
					continue;
				text = line.substring(line.lastIndexOf('.') + 1, line.indexOf(';'));
				if(text.isBlank())
					continue;
				if(Character.isLetter(text.charAt(0)))
					imports.add(text);
			}
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		return imports;
	}

	/*
	 * The constant holding the allowed symbols in Identifier naming rules
	 */
	public static final String ALLOWED_IDENTIFIER_SYMBOLS = "$_";

	/*
	 * Method to check whether the specified symbol is allowed in Identifier name or not.
	 * @param ch = the symbol
	 */
	public static boolean isAllowed(char ch){
		return ALLOWED_IDENTIFIER_SYMBOLS.contains(ch + "");
	}

	/*
	 * The method that search for classes in the passed code (String text)
	 * @param text = The Code
	 */
	public static LinkedList<String> findClasses(String text){
		text = removeUsuals(text);
		LinkedList<String> cls = new LinkedList<>();
		try {
			text = text.substring(text.indexOf("public"));
		}
		catch(Exception e) {
			return cls;
		}
		LinkedList<String> tokens = CodeTokenizer.tokenizeWithoutLoss(text, '\n');
		int pos;
		int currentCharIndex = -1;
		char ch;
		String sx;
		for(String line : tokens){
			pos = 0;
			sx = "";
			for(int i = 0; i < line.length(); i++){
				ch = line.charAt(i);
				currentCharIndex++;
				if(Character.isLetterOrDigit(ch) || isAllowed(ch)){
					sx += ch;
				}
				else {
					sx = sx.trim();
					pos = currentCharIndex - sx.length() - 1;
					if(sx.length() > 0 && pos > -1 && text.charAt(pos) != '.'){
						if(Character.isUpperCase(sx.charAt(0))){
							if(!cls.contains(sx)){
								cls.add(sx);
							}
						}
					}
					sx = "";
				}
			}
		}
		tokens.clear();
		return cls;
	}

	/*
	 * The method removes all the comments, chars & String from the code
	 * @param text = The code
	 */
	public static synchronized String removeUsuals(String text) {
		boolean commentStarts = false;
		LinkedList<String> tokens = CodeTokenizer.tokenize(text, '\n');
		text = "";
		for(String line : tokens){
			line = line.trim();
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
			if(line.startsWith("//"))
				continue;
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
		tokens = CodeTokenizer.tokenize(text, '\n');
		text = "";
		int index;
		for(String line : tokens){
			if(line.contains("//")){
				line = line.substring(0, line.indexOf("//")).trim();
			}
			text += line + "\n";
		}
		return text;
	}

	/**
	 * The method adds the imports in the editor after rectifying the classes
	 * @param classes = The list of classes to be imported
	 * @param editor = The Editor in which the imports are to be inserted
	 */
	public static synchronized void addImports(LinkedList<String> classes, Editor editor){
		if(JDKManager.reading)
			return;
		Screen.setStatus("Resolving Imports ... Keep Editing meanwhile!", 0, IconManager.fluentinfoImage);
		String PACK = "";
		int index = getPackageInformationIndex(editor);
		if(index != 0) {
			String text = editor.getText();
			text = text.substring(0, index + 1);
			PACK = text.substring(text.lastIndexOf(" ") + 1, text.lastIndexOf(';'));
		}
		LinkedList<String> unimported = new LinkedList<>();
		SourceReader reader = new SourceReader(editor.getText());

		//Removing Java Lang Classess & SubClasses
		main:
		for(String classX : classes) {
			for(Import xm : JDKManager.javaLangPack) {
				if(xm.getClassName().equals(classX))
					continue main;
			}
			if(reader.isSubClass(classX))
				continue main;
			unimported.add(classX);
		}
		classes.clear();
		//Managing Classess with Same Name but different Package
		LinkedList<LinkedList<String>> coexistingClassess = new LinkedList<>();
		main:
		for(String classX : unimported) {
			LinkedList<String> bases = new LinkedList<>();
			inner:
			for(Import im : JDKManager.getAllImports()) {
				if(im.getClassName().equals(classX)) {
					if(im.getPackage().equals(PACK) || contains(editor, im.getPackage(), im.getClassName()) || CodeFramework.isSource(PACK + "." + im.getClassName())) continue main;
					try {
						for(String baseX : bases){
							if(baseX.equals(im.getImport()))
								continue inner;
						}
						bases.add(im.getImport());
					}
					catch(Exception e) {

					}
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
		//Inserting the single bases and storing the multi bases classes
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
		Screen.setStatus(null, 100, null);
	}
	/**
	 * The method checks whether a specified import is already present in the editor or not
	 * @param editor = The Editor in which the searching is to be made
	 * @param packName = The package of the class
	 * @param className = The name of the Class
	 */
	public static boolean contains(Editor editor, String packName, String className) {
		return
		editor.getText().contains("import " + packName + ".*;")
		||
		editor.getText().contains("import " + packName + "." + className + ";");
	}

	/**
	 * The method checks whether the source contains documentation or not.
	 * @param editor = The Editor, the contents of which is to be examined.
	 */
	public static boolean isPackageInformationPresent(Editor e){
		int index = getPackageInformationIndex(e);
		return index != 0;
	}

	/**
	 * The method finds the index of package information end
	 * Like for The Current Class the index is : 719
	 * @param editor = The Editor, the contents of which is to be examined.
	 */
	public static synchronized int getPackageInformationIndex(Editor e){
		int index = 0;
		LinkedList<String> lines = CodeTokenizer.tokenizeWithoutLoss(e.getText(), '\n', "package ");
		for(String line : lines){
			index += line.length();
		}
		return index;
	}

	/**
	 * The method inserts the imports in the specified editor
	 * @param editor = The Editor in which the import to be inserted
	 * @param pack = The package of the class
	 * @param className = The name of the class
	 */
	public static void insertImport(Editor editor, String pack, String className) {
		try {
			if(isPackageInformationPresent(editor)) {
				int caretPos = editor.getCaretPosition();
				String text = editor.getText();
				Document d = editor.getDocument();
				var info = getInsertIndex(editor, pack, className);
				int start = info.index;
				String insertedText = "";
				if(DataManager.isUsingStarImports())
					d.insertString(start, insertedText = (!info.startChar.equals("") ? info.startChar : "") + "import " + pack + ".*;" + (!info.endChar.equals("") ? info.endChar : ""), null);
				else
					d.insertString(start, insertedText = (!info.startChar.equals("") ? info.startChar : "") + "import " + pack + "." + className + ";" + (!info.endChar.equals("") ? info.endChar : ""), null);
				editor.setCaretPosition(caretPos + insertedText.length());
			}
			else {
				Document d = editor.getDocument();
				if(DataManager.isUsingStarImports())
					d.insertString(0, "import " + pack + ".*;\n", null);
				else
					d.insertString(0, "import " + pack + "." + className + ";\n", null);
			}
			editor.getAttachment().getVerticalScrollBar().setValue(editor.getAttachment().getVerticalScrollBar().getValue() - 1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The Method which sorts Imports
	 *
	 * Suppose If a file has these many imports already present
	 *
	 * import java.awt.Event;
	 * import javax.swing.JFrame;
	 * import java.nio.Files;
	 * import java.nio.Path;
	 *
	 * And the new class to be imported is java.lang.Ant
	 * The New Sequence will be
	 *
	 * import java.awt.Event;
	 *
	 * import javax.swing.JFrame;
	 *
	 * import java.nio.Files;
	 * import java.nio.Path;
	 *
	 * import java.lang.Ant;
	 *
	 * Or If the new class to be imported is java.awt.Button
	 * Then, New Sequence will be
	 *
	 * import java.awt.Button;
	 * import java.awt.Event;
	 *
	 * import javax.swing.JFrame;
	 *
	 * import java.nio.Files;
	 * import java.nio.Path;
	 *
	 */
	public static IndexInfo getInsertIndex(Editor editor, String pack, String className){
		//@link getPackageInformationIndex(omega.utils.Editor)
		String startChar = "";
		String endChar = "";
		int index = getPackageInformationIndex(editor);
		int lineIndex = index;

		//Packing Already Present Imports Inside the editor
		/*
		 * Packaging Starts
		 */
		LinkedList<ImportInfo> infos = new LinkedList<>();
		String text = editor.getText();
		String line = text.substring(lineIndex, lineIndex = text.indexOf('\n', lineIndex + 1));
		String pAck;
		String name;
		boolean sourceStartReached = false;
		try{
			while(line.endsWith(";") || line.trim().equals("") || !sourceStartReached){
				sourceStartReached = line.contains("public ");
				if(sourceStartReached)
					break;

				if(line.trim().equals("")){
					line = text.substring(lineIndex + 1, text.indexOf(';', lineIndex) + 1).trim();
					continue;
				}

				line = line.substring(line.indexOf(' '), line.indexOf(';')).trim();
				if(line.startsWith("static "))
					line = line.substring(line.indexOf(' ')).trim();
				pAck = line.substring(0, line.lastIndexOf('.'));
				name = line.substring(line.lastIndexOf('.') + 1);
				infos.add(new ImportInfo(pAck, name, lineIndex));
				line = text.substring(lineIndex, lineIndex = text.indexOf('\n', lineIndex + 1));
			}
		}
		catch(Exception e){
			System.out.println(sourceStartReached);
			System.out.println(line);
			e.printStackTrace();
		}
		/*
		 * Packaging Completes Here
		 */

		//If infos is empty this means there aren't any imports in the editor
		if(!infos.isEmpty()){
			//Checking whether pack is already present
			int groupIndex = -1;
			for(ImportInfo im : infos){
				if(im.pack.equals(pack)){
					groupIndex = im.offset;
					break;
				}
			}

			//If pack is already present
			if(groupIndex != -1){
				//Calculating the correct index of className in the group present
				/*
				 * See Method Documentation
				 */
				for(ImportInfo im : infos){
					if(im.pack.equals(pack)){
						index = im.offset;
					}
				}
				startChar = "\n";
			}
			else{
				endChar = "\n\n";
			}
		}
		else{
			startChar = "";
			endChar = "\n";
		}
		//Releasing Memory
		infos.clear();

		return new IndexInfo(index, startChar, endChar);
	}

	private static class IndexInfo{
		int index;
		String startChar = "";
		String endChar = "";

		public IndexInfo(int index, String startChar, String endChar){
			this.index = index;
			this.startChar = startChar;
			this.endChar = endChar;
		}
	}

	//Import Container
	private static class ImportInfo{
		String pack;
		String name;
		int offset;
		public ImportInfo(String pack, String name, int offset){
			this.pack = pack;
			this.name = name;
			this.offset = offset;
		}

		@Override
		public String toString(){
			return pack + "." + name;
		}
	}
}
