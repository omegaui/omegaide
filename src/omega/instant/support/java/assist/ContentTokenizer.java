/**
* ContentAssist Invoker.
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
* along with this program.  If not, see http://www.gnu.org/licenses.
*/

package omega.instant.support.java.assist;
import omega.instant.support.java.highlighter.BasicCodeHighlighter;

import omega.instant.support.AbstractContentTokenizer;
import omega.instant.support.CodeFrameworks;

import omega.instant.support.java.management.Import;
import omega.instant.support.java.management.JDKManager;

import omega.Screen;

import omega.ui.component.Editor;

import omega.instant.support.java.framework.CodeFramework;
import omega.instant.support.java.framework.ImportFramework;

import omega.io.SnippetBase;
import omega.io.DataManager;

import java.util.LinkedList;
import java.util.StringTokenizer;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import static omega.io.UIManager.*;
public class ContentTokenizer extends AbstractContentTokenizer{
	
	public static boolean isConditionalCode(String code){
		return isObjectInstantiationCode(code);
	}
	
	public static boolean isObjectInstantiationCode(String code){
		boolean hasSpace = code.contains(" ");
		boolean hasEqual = code.contains("=");
		boolean hasEqualsAfterSpace = false;
		boolean hasEqualAtLast = false;
		if(hasSpace && hasEqual)
			hasEqualsAfterSpace = code.indexOf(' ') < code.indexOf('=');
		if(hasEqual)
			hasEqualAtLast = code.charAt(code.length() - 1) == '=';
		return hasSpace && hasEqual && hasEqualsAfterSpace && hasEqualAtLast;
	}
	
	public static boolean genConditionalHints(Editor e, String code){
		if(isObjectInstantiationCode(code)){
			String className = code.substring(0, code.indexOf(' ')).trim();
			SourceReader reader = new SourceReader(e.getText());
			String im = reader.getPackage(className);
			if(im == null || className.equals("var"))
				return false;
			LinkedList<DataMember> dataMembers = new LinkedList<>();
			dataMembers.addFirst(new DataMember("", "", "Object Instantiation", "new " + className + "();", ""));
			reader.dataMembers.forEach(dataMember->{
				String type = dataMember.type;
				if(type.contains(className)) {
					if(type.contains("."))
						type = type.substring(type.lastIndexOf('.') + 1).trim();
					if(type.equals(className.trim()))
						dataMembers.add(dataMember);
				}
			});
			e.contentWindow.genView(dataMembers);
			return true;
		}
		return false;
	}

	@Override
	public boolean canArrangeTokens(Editor editor) {
		return editor.currentFile != null && editor.currentFile.getName().endsWith(".java");
	}
	
	public static void arrangeTokens(Editor e, String text){
		if(text.equals("")) {
			e.contentWindow.setVisible(false);
			return;
		}
		StringTokenizer tok = new StringTokenizer(e.getText(), "`-=[\\;,.\"\'/]~!@#%^&*()+{}|:<>?)\n ");
		
		LinkedList<DataMember> dataMembers = new LinkedList<>();
		LinkedList<DataMember> tokens = new LinkedList<>();
		while(tok.hasMoreTokens()){
			final String token = tok.nextToken().trim();
			if(token.equals("") || !token.contains(text) || token.equals(text)) continue;
			DataMember d = new DataMember("", "", "", token, null){
				@Override
				public String getRepresentableValue(){
					return token;
				}
			};
			tokens.add(d);
		}
		
		ContentWindow.sort(tokens);
		main:
		for(DataMember token : tokens){
			for(DataMember dx : dataMembers){
				if(dx.name.equals(token.name))
					continue main;
			}
			dataMembers.add(token);
		}
		tokens.clear();
		
		SnippetBase.getAll().forEach((snippet)->{
			dataMembers.add(new DataMember("", "", "Snippet", snippet.toString(), null));
		});
		
		if(!dataMembers.isEmpty())
			CodeFramework.gen(dataMembers, e);
		else
			e.contentWindow.setVisible(false);
	}

	@Override
	public void arrangeTokens(Editor e) {
		if(!e.currentFile.getName().endsWith(".java") || Screen.getProjectFile().getProjectManager().isLanguageTagNonJava() || !DataManager.isContentModeJava()){
			arrangeTokens(e, CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition()));
			return;
		}
		
		String code = e.getText();
		code = code.substring(0, e.getCaretPosition());
		code = code.substring(code.lastIndexOf('\n') + 1).trim();
		
		if(genConditionalHints(e, code)) {
			return;
		}
		
		String text = CodeFramework.getCodeDoNotEliminateDot(e.getText(), e.getCaretPosition());
		if(text == null || text.equals("")) {
			e.contentWindow.setVisible(false);
			return;
		}
		
		if(Screen.getProjectFile().getJDKManager() == null && !Screen.getProjectFile().getProjectManager().isLanguageTagNonJava())
			return;
		
		if(!text.contains(".")) {
			if(text.contains(" "))
				text = text.substring(text.lastIndexOf(' ') + 1).trim();
			LinkedList<DataMember> dataMembers = new LinkedList<>();
			
			if(couldBeSomeClass(text)){
				for(Import im : JDKManager.imports){
					if(CodeFramework.isUpperCaseHintType(im.className, text)){
						DataMember dx = new DataMember("", "", im.packageName, im.className, null);
						dx.setExtendedInsertion(()->{
							String PACK = "";
							int index = ImportFramework.getPackageInformationIndex(e);
							String textX = e.getText();
							if(index != 0) {
								textX = textX.substring(0, index + 1);
								PACK = textX.substring(textX.lastIndexOf(" ") + 1, textX.lastIndexOf(';'));

								textX = e.getText().substring(index);
								textX = textX.substring(0, textX.indexOf("public "));
							}
							if(!PACK.equals(im.packageName) && !textX.contains("." + im.className + ";") && !ImportFramework.contains(e, im.packageName, im.className)){
								ImportFramework.insertImport(e, im.packageName, im.className);
							}
						});
						dataMembers.add(dx);
					}
				}
			}
			if(!dataMembers.isEmpty()){
				e.contentWindow.genView(dataMembers);
				return;
			}
			SourceReader reader = new SourceReader(e.getText());
			LinkedList<DataMember> staticMembers = new LinkedList<>();
			if(!text.contains(".")){
				LinkedList<String> decompiledStaticDataFromClasses = new LinkedList<>();
				//Adding static data members & methods
				for(SourceReader.Import im : reader.imports){
					if(im.isStatic){
						if(decompiledStaticDataFromClasses.contains(im.pack))
							continue;
						
						if(CodeFramework.isSource(im.pack))
							staticMembers = new SourceReader(CodeFramework.getContent(im.pack)).getDataMembers("static");
						else
							staticMembers = Screen.getProjectFile().getJDKManager().prepareReader(im.pack).getDataMembers("static");
						decompiledStaticDataFromClasses.add(im.pack);
						
						for(DataMember dx : staticMembers){
							dataMembers.add(dx);
						}
						
						staticMembers.clear();
					}
				}
			}
			//Searching whether you need Class names as suggestion
			LinkedList<SourceReader.Import> imports = new LinkedList<>();
			main:
			for(SourceReader.Import im : reader.imports){
				if(!im.isStatic && (im.name.contains(text) || CodeFramework.isUpperCaseHintType(im.name, text))){
					for(SourceReader.Import x : imports){
						if(x.get().equals(im.get()))
							continue main;
					}
					imports.add(im);
				}
			}
			
			SourceReader.Import[] I = new SourceReader.Import[imports.size()];
			int k = 0;
			for(SourceReader.Import im : imports)
				I[k++] = im;
			imports.clear();
			for(int i = 0; i < k; i++){
				for(int j = 0; j < k - i - 1; j++){
					String x = I[j].name;
					String y = I[j + 1].name;
					if(x.compareTo(y) > 0){
						SourceReader.Import ix = I[j];
						I[j] = I[j + 1];
						I[j + 1] = ix;
					}
				}
			}
			for(SourceReader.Import im : I)
				imports.add(im);
			
			I = null;
			for(SourceReader.Import im : imports){
				dataMembers.add(new DataMember("", "class", im.get(), im.name, null));
			}
			
			for(DataMember m : reader.dataMembers) {
				if(m.name.contains(text) || CodeFramework.isUpperCaseHintType(m, text)) {
					dataMembers.add(m);
				}
			}
			
			String reducedText = e.getText().substring(0, e.getCaretPosition());
			reducedText = CodeFramework.completeCode(reducedText);
			reader = new SourceReader(reducedText);
			DataBlock block = null;
			if(!reader.dataBlocks.isEmpty())
				block = reader.dataBlocks.getLast();
			
			if(!reader.recordingInternal) {
				if(block != null) {
					for(DataMember m : block.depthMembers) {
						if(m.name.contains(text)) {
							inner:
							for(DataMember mx : dataMembers){
								if(mx.name.equals(m.name) && mx.parameterCount == m.parameterCount){
									dataMembers.remove(mx);
									break inner;
								}
							}
							dataMembers.add(m);
						}
					}
				}
			}
			if(!dataMembers.isEmpty())
				CodeFramework.gen(dataMembers, e);
			else
				e.contentWindow.setVisible(false);
		}
		else if(!CodeFrameworks.javaCodeFramework.think(e, e.getText(), e.getCaretPosition()))
			arrangeTokens(e, CodeFramework.getCodeIgnoreDot(e.getText(), e.getCaretPosition()));
	}

	public static boolean couldBeSomeClass(String text){
		return Screen.isNotNull(text) && Character.isLetter(text.charAt(0)) && Character.isUpperCase(text.charAt(0)) && text.length() > 1;
	}
	
}
