/**
* Prepares Auto-Imports
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
package omega.framework;
import omega.utils.ImportResolver;
import omega.jdk.JDKManager;
import omega.jdk.Import;
import omega.utils.DataManager;
import omega.utils.Editor;
import omega.deassembler.SourceReader;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.text.Document;
public class ImportFramework{
	//The Object of the Window that lets you choose the imports
	//when classess in different packages have a same name.
	private static ImportResolver imR = new ImportResolver();
	/**
	* The method that search for classess in the passed code (String text)
	* @param text = The Code
	*/
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
	/**
	* The method removes all the comments, chars & String from the code
	* @param text = The code
	*/
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
	/**
	* The method adds the imports in the editor after rectifying the classess
	* @param classess = The list of classess to be imported
	* @param editor = The Editor in which the imports are to be inserted
	*/
	public static synchronized void addImports(LinkedList<String> classess, Editor editor){
		if(JDKManager.reading) return;
		String PACK = "";
		if(isPackageInformationPresent(editor)) {
			String text = editor.getText();
			int index = getPackageInformationIndex(editor);
			text = text.substring(0, index + 1);
			PACK = text.substring(text.lastIndexOf(" ") + 1, text.lastIndexOf(';'));
		}
		LinkedList<String> unimported = new LinkedList<>();
		
		//Removing Java Lang Classess
		for(String classX : classess) {
			boolean found = false;
			for(Import xm : JDKManager.javaLangPack) {
				String className = xm.getClassName();
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
	/**
	* The method checks whether a specified import is already present in the editor or not
	* @param editor = The Editor in which the searching is to be made
	* @param packName = The package of the class
	* @param className = The name of the Class
	*/
	public static boolean contains(Editor editor, String packName, String className) {
		return editor.getText().contains("import " + packName + ".*;") ||
		editor.getText().contains("import " + packName + "." + className + ";");
	}
	/**
	* The method checks whether the source contains documentation or not.
	* @param editor = The Editor, the contents of which is to be examined.
	*/
	public static boolean isPackageInformationPresent(Editor e){
		StringTokenizer tok = new StringTokenizer(e.getText(), "\n");
		while(tok.hasMoreTokens()){
			String token = tok.nextToken();
			if(token.contains("class "))
				return false;
			if(token.startsWith("package"))
				return true;
		}
		return false;
	}
	/**
	* The method finds the index of package information end
	* @param editor = The Editor, the contents of which is to be examined.
	*/
	public static int getPackageInformationIndex(Editor e){
		int index = 0;
		LinkedList<String> lines = omega.deassembler.CodeTokenizer.tokenizeWithoutLoss(e.getText(), '\n');
		for(String token : lines){
			index += token.length();
			if(token.startsWith("package"))
				break;
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
				String text = editor.getText();
				Document d = editor.getDocument();
				int start = getPackageInformationIndex(editor);
				if(DataManager.isUsingStarImports())
					d.insertString(start, "import " + pack + ".*;\n", null);
				else
					d.insertString(start, "import " + pack + "." + className + ";\n", null);
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
			System.err.println(e);
		}
	}
}
