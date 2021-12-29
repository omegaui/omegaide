/**
  * Hint Maker
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

package omega.instant.support.java.framework;
import omega.Screen;

import omega.ui.component.Editor;

import omega.instant.support.java.assist.ByteReader;
import omega.instant.support.java.assist.DataMember;
import omega.instant.support.java.assist.SourceReader;
import omega.instant.support.java.assist.DataBlock;

import omega.instant.support.java.management.JDKManager;
import omega.instant.support.java.management.Import;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.awt.Color;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.LinkedList;
import java.util.StringTokenizer;

import static omega.instant.support.java.assist.Assembly.*;
import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class CodeFramework {
	private static String text;
	public static String lCode;
	private static int caret;
	public static volatile boolean resolving = false;
	private static Editor editor;

	public static boolean think(Editor e, String text, int caret){
		boolean value = false;
		CodeFramework.editor = e;
		CodeFramework.text = text;
		String code = getCode(text, caret);
		lCode = "";
		if(code != null){
			resolving = true;
			CodeFramework.caret = caret;
			try{
				value = createHints(code);
			}
			catch(Exception ex){
                    ex.printStackTrace();
		     }
			finally {
				resolving = false;
			}
		}
		else 
		     editor.contentWindow.setVisible(false);
		CodeFramework.editor = null;
		return value;
	}

	public static boolean createHints(String code){
		boolean value = false;
		if(Character.isLowerCase(code.charAt(0))){
			if(!(value = genFullPathHints(code))){
				value = genInstanceHints(code);
			}
		}
		else if(Character.isUpperCase(code.charAt(0))){
			if(!(value = genClassHints(code))) {
				value = genInstanceHints(code);
			}
		}
		return value;
	}

	public static boolean genFullPathHints(String code){
		int firstUpperCase = getFirstUpperCase(code);
		if(firstUpperCase == -1) return false;
		int ix = code.indexOf('.', firstUpperCase);
		if(ix < 0) return false;
		String className = code.substring(0, ix).trim();
		LinkedList<DataMember> dataMembers = new LinkedList<>();
		if(!isSource(className)){
			boolean isAvailable = false;
			for(Import im : JDKManager.getAllImports()) {
				if(im.getImport().equals(className)) {
					isAvailable = true;
					break;
				}
			}
			if(!isAvailable) 
			     return false;
			ByteReader reader = null;
			if(has(className)) 
			     reader = getReader(className);
			else 
			     reader = Screen.getFileView().getJDKManager().prepareReader(className);
			dataMembers = reader.getDataMembers("static");
			//Checking whether if there is data after className
			if(code.indexOf('.', ix + 1) >= 0){
				//There are some members after class-path's dot
				code = code.substring(ix + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									if(has(d.type)) reader = getReader(d.type);
									else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
									dataMembers = reader.dataMembers;
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								if(has(d.type)) reader = getReader(d.type);
								else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
								dataMembers = reader.dataMembers;
								in = 1;
								continue inner;
							}
						}
     				if(in == 0) {
     					for(ByteReader r : reader.internalReaders) {
     						if(r.className.equals(member)) {
     							dataMembers = r.dataMembers;
     							least = true;
     						}
     					}
     				} 
     				in = 1;
				}
				if(least) 
				     gen(dataMembers);
                    else {
                         Screen.getScreen().getToolMenu().setTask("Not at least one match found for " + code);
                         editor.contentWindow.setVisible(false);
                    }
			}
			else{
				//There are no members after class-path's dot, so showing all hints
				gen(dataMembers);
			}
		}
		else{
			SourceReader reader = new SourceReader(getContent(className));
			dataMembers = reader.getDataMembers("static");
			//Checking whether if there is data after className
			if(code.indexOf('.', ix + 1) >= 0){
				//There are some members after class-path's dot
				code = code.substring(ix + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				ByteReader readerB = null;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									String pack = null;
									if(reader != null) {
										pack = reader.getPackage(d.type);
										if(!isSource(pack)) {
											if(pack != null) {
												if(has(pack)) readerB = getReader(pack);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
											}
											else {
												if(has(d.type)) readerB = getReader(d.type);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
											}
											dataMembers = readerB.dataMembers;
											reader = null;
										}
										else {
											reader = new SourceReader(getContent(pack));
											dataMembers = reader.dataMembers;
											readerB = null;
										}
									}
									else if(readerB != null) {
										if(has(d.type)) readerB = getReader(d.type);
										else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										reader = null;
										dataMembers = readerB.dataMembers;
									}
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								String pack = null;
								if(reader != null) {
									pack = reader.getPackage(d.type);
									if(!isSource(pack)) {
										if(pack != null) {
											if(has(pack)) readerB = getReader(pack);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
										}
										else {
											if(has(d.type)) readerB = getReader(d.type);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										}
										dataMembers = readerB.dataMembers;
										reader = null;
									}
									else {
										reader = new SourceReader(getContent(pack));
										dataMembers = reader.dataMembers;
										readerB = null;
									}
								}
								else if(readerB != null) {
									if(has(d.type)) readerB = getReader(d.type);
									else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
									reader = null;
									dataMembers = readerB.dataMembers;
								}
								in = 1;
								continue inner;
							}
						}
     				if(in == 0) {
     					for(SourceReader r : reader.internalReaders) {
     						if(r.className.equals(member)) {
     							dataMembers = r.dataMembers;
     							least = true;
     						}
     					}
     				} 
     				in = 1;
				}
				if(least) 
				     gen(dataMembers);
				else {
				     Screen.getScreen().getToolMenu().setTask("Not at least one match found for " + code);
                         editor.contentWindow.setVisible(false);
				}
			}
			else{
				//There are no members after class-path's dot, so showing all hints
				gen(dataMembers);
			}
		}
		return true;
	}

	public static boolean genClassHints(String code){
		SourceReader sourceReader = new SourceReader(text);
		String className = code.substring(0, code.indexOf('.')).trim();
		String path = sourceReader.getPackage(className);
		boolean isInternalReader = sourceReader.isInternalReader(className);
		if(path == null) return false;
		LinkedList<DataMember> dataMembers = new LinkedList<>();
		if(twiceUpperCases(path) && isInternalReader) {
			path = path.substring(0, path.lastIndexOf('.'));
			code = "." + code;
		}
		if(!isSource(path)){
			boolean isAvailable = false;
			for(Import im : JDKManager.getAllImports()) {
				if(im.getImport().equals(path)) {
					isAvailable = true;
					break;
				}
			}
			if(!isAvailable) return false;
			ByteReader reader = null;
			if(has(path)) reader = getReader(path);
			else reader = Screen.getFileView().getJDKManager().prepareReader(path);
			dataMembers = reader.getDataMembers("static");
			//Checking whether if there is data after className
			if(code.indexOf('.', code.indexOf('.') + 1) >= 0){
				//There are some members after class-path's dot
				code = code.substring(code.indexOf('.') + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									if(has(d.type)) reader = getReader(d.type);
									else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
									dataMembers = reader.dataMembers;
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								if(has(d.type)) reader = getReader(d.type);
								else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
								dataMembers = reader.dataMembers;
								in = 1;
								continue inner;
							}
						}
     				if(in == 0) {
     					for(ByteReader r : reader.internalReaders) {
     						if(r.className.equals(member)) {
     							dataMembers = r.dataMembers;
     							least = true;
     						}
     					}
     				} 
     				in = 1;
				}
				if(least) 
				     gen(dataMembers);
                    else {
                         Screen.getScreen().getToolMenu().setTask("Not at least one match found for " + code);
                         editor.contentWindow.setVisible(false);
                    }
			}
			else{
				//There are no members after class-path's dot, so showing all hints
				gen(dataMembers);
			}
		}
		else{
			SourceReader reader = new SourceReader(getContent(path));
			dataMembers = reader.getDataMembers("static");
			//Checking whether if there is data after className
			if(code.indexOf('.', code.indexOf('.') + 1) >= 0){
				//There are some members after class-path's dot
				code = code.substring(code.indexOf('.') + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				ByteReader readerB = null;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									String pack = null;
									if(reader != null) {
										pack = reader.getPackage(d.type);
										if(!isSource(pack)) {
											if(pack != null) {
												if(has(pack)) readerB = getReader(pack);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
											}
											else {
												if(has(d.type)) readerB = getReader(d.type);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
											}
											dataMembers = readerB.dataMembers;
											reader = null;
										}
										else {
											reader = new SourceReader(getContent(pack));
											dataMembers = reader.dataMembers;
											readerB = null;
										}
									}
									else if(readerB != null) {
										if(has(d.type)) readerB = getReader(d.type);
										else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										reader = null;
										dataMembers = readerB.dataMembers;
									}
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								String pack = null;
								if(reader != null) {
									pack = reader.getPackage(d.type);
									if(!isSource(pack)) {
										if(pack != null) {
											if(has(pack)) readerB = getReader(pack);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
										}
										else {
											if(has(d.type)) readerB = getReader(d.type);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										}
										dataMembers = readerB.dataMembers;
										reader = null;
									}
									else {
										reader = new SourceReader(getContent(pack));
										dataMembers = reader.dataMembers;
										readerB = null;
									}
								}
								else if(readerB != null) {
									if(has(d.type)) readerB = getReader(d.type);
									else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
									reader = null;
									dataMembers = readerB.dataMembers;
								}
								in = 1;
								continue inner;
							}
						}
				if(in == 0) {
					for(SourceReader r : reader.internalReaders) {
						if(r.className.equals(member)) {
							dataMembers = r.dataMembers;
							least = true;
						}
					}
				} 
				in = 1;
				}
				if(least) 
				     gen(dataMembers);
                    else {
                         Screen.getScreen().getToolMenu().setTask("Not at least one match found for " + code);
                         editor.contentWindow.setVisible(false);
                    }
			}
			else{
				//There are no members after class-path's dot, so showing all hints
				gen(dataMembers);
			}
		}
		return true;
	}

	public static boolean genInstanceHints(String code){
		String var = code.substring(0, code.indexOf('.')).trim();
		if(var.contains("("))
			var = var.substring(0, var.indexOf('(')) + "()";
		String methVar = "";
		int index = 0;
		int blocks = -1;
		for(int i = 0; i < code.length(); i++){
			char ch = code.charAt(i);
			if(ch == '.' && blocks == -1){
				String x = code.substring(index, i);
				index = i + 1;
				methVar = x.trim();
				break;
			}
			else if(ch == '(') blocks++;
			else if(ch == ')') blocks--;
		}
		int parameterCount = getParameterCount(methVar);
		//Equilizing Text
		String reducedText = text.substring(0, caret);
		String partialText = reducedText;
		int openBraces = count(reducedText, '{');
		int closeBraces = count(reducedText, '}');
		if(openBraces > closeBraces){
			while(openBraces != 0){
				openBraces--;
				partialText += "\n}";
			}
		}
		//Reading from globals and internals
		SourceReader reader = new SourceReader(reducedText);
		DataMember dataMember = null;
		if(reader.recordingInternal){
			reader = new SourceReader(partialText);
			reader = reader.internalReaders.getLast();
			DataBlock block = reader.dataBlocks.getLast();
			if(block != null){
				dataMember = (DataMember)block.getMatch(var);
			}
		}
		else{
			reader = new SourceReader(partialText);
			if(reader.dataBlocks != null){
				DataBlock block = reader.dataBlocks.getLast();
				if(block != null){
					dataMember = (DataMember)block.getMatch(var);
				}
			}
		}
		if(dataMember != null && !dataMember.name.equals(var)) dataMember = null;
		if(dataMember == null){
			SourceReader f_reader = new SourceReader(text);
			reader = new SourceReader(reducedText);
			if(reader.recordingInternal){
				reader = new SourceReader(partialText);
				String classNameOfLastInternalReader = reader.internalReaders.getLast().className;
				SourceReader i = null;
				for(SourceReader ix : f_reader.internalReaders){
					if(ix.className.equals(classNameOfLastInternalReader)){
						i = ix;
						break;
					}
				}
				if(i != null){
					for(DataMember m : i.dataMembers){
						if(!methVar.contains("(")){
							if(m.name.equals(var) && m.parameters == null){
								dataMember = m;
								break;
							}
						}
						else {
							if(m.name.equals(var) && m.parameters != null && m.parameterCount == parameterCount){
								dataMember = m;
								break;
							}
						}
					}
				}
				if(dataMember == null){
					for(DataMember m : f_reader.dataMembers){
						if(!methVar.contains("(")){
							if(m.name.equals(var) && m.parameters == null){
								dataMember = m;
								break;
							}
						}
						else {
							if(m.name.equals(var) && m.parameters != null && m.parameterCount == parameterCount){
								dataMember = m;
								break;
							}
						}
					}
				}
			}
			else{
				reader = new SourceReader(partialText);
				for(DataMember m : f_reader.dataMembers){
					if(!methVar.contains("(")){
						if(m.name.equals(var) && m.parameters == null){
							dataMember = m;
							break;
						}
					}
					else {
						if(m.name.equals(var) && m.parameters != null && m.parameterCount == parameterCount){
							dataMember = m;
							break;
						}
					}
				}
			}
		}
		if(dataMember == null) {
			//Searching in static imports
			SourceReader f_reader = new SourceReader(text);
			for(SourceReader.Import im : f_reader.imports){
				if(im.isStatic){
					if(im.staticMember.equals(var)){
						dataMember = new DataMember("si", "static", im.get(), im.staticMember, "");
						break;
					}
				}
			}
		}
		if(dataMember == null) return false;
		//Generating Hints
		SourceReader f_reader = new SourceReader(text);
		boolean value = true;
		if(dataMember.access.equals("si")){
			//Generating from static hints
			String modCode =  dataMember.type + "." + code;
			value = genFullPathHints(modCode);
		}
		else{
			String type = dataMember.type;
			if(type.contains(".")){
				String cType = type;
				type = cType.substring(cType.lastIndexOf('.') + 1);
				String typeP = f_reader.getPackage(type);
				if(cType.equals(typeP))
					type = cType;
				else
					type = null;
			}
			else {
				type = f_reader.getPackage(type);
			}
			if(type == null) return false;
			//DataMember has a valid type
			String modCode = type + "." + code.substring(code.indexOf('.') + 1);
			value = genAllFullPathHints(modCode);
		}
		return value;
	}

	public static boolean genAllFullPathHints(String code){
		int firstUpperCase = getFirstUpperCase(code);
		if(firstUpperCase == -1) return false;
		int ix = code.indexOf('.', firstUpperCase);
		if(ix < 0) return false;
		String className = code.substring(0, ix);
		LinkedList<DataMember> dataMembers = new LinkedList<>();
		if(!isSource(className)){
			boolean isAvailable = false;
			for(Import im : JDKManager.getAllImports()) {
				if(im.getImport().equals(className)) {
					isAvailable = true;
					break;
				}
			}
			if(!isAvailable) return false;
			ByteReader reader = null;
			if(has(className)) reader = getReader(className);
			else reader = Screen.getFileView().getJDKManager().prepareReader(className);
			dataMembers = reader.dataMembers;
			//Checking whether if there is data after className
			if(code.indexOf('.', ix + 1) >= 0){
				//There are some members after class-path 's dot
				code = code.substring(ix + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									if(has(d.type)) reader = getReader(d.type);
									else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
									dataMembers = reader.dataMembers;
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								if(has(d.type)) reader = getReader(d.type);
								else reader = Screen.getFileView().getJDKManager().prepareReader(d.type);
								dataMembers = reader.dataMembers;
								in = 1;
								continue inner;
							}
						}
     				if(in == 0) {
     					for(ByteReader r : reader.internalReaders) {
     						if(r.className.equals(member)) {
     							dataMembers = r.dataMembers;
     							least = true;
     						}
     					}
     				} 
				     in = 1;
				}
				if(least) gen(dataMembers);
				else System.out.println("Not at least one match found for " + code);
			}
			else{
				//There are no members after class-path 's dot, so showing all hints
				gen(dataMembers);
			}
		}
		else{
			SourceReader reader = new SourceReader(getContent(className));
			dataMembers = reader.dataMembers;
			//Checking whether if there is data after className
			if(code.indexOf('.', ix + 1) >= 0){
				//There are some members after class-path's dot
				code = code.substring(ix + 1);
				LinkedList<String> members = new LinkedList<>();
				int index = 0;
				int blocks = -1;
				for(int i = 0; i < code.length(); i++){
					char ch = code.charAt(i);
					if(ch == '.' && blocks == -1){
						String x = code.substring(index, i);
						index = i + 1;
						members.add(x.trim());
					}
					else if(ch == '(') blocks++;
					else if(ch == ')') blocks--;
				}
				boolean least = false;
				int in = 0;
				ByteReader readerB = null;
				for(String member : members){
					inner:
						for(DataMember d : dataMembers){
							//Checking whether <member> is method
							if(d.name.contains("()") && member.contains("(") && member.contains(")")){
								int parameterCount = getParameterCount(member);
								String name = getName(member);
								if(d.name.equals(name) && d.parameterCount == parameterCount){
									least = true;
									String pack = null;
									if(reader != null) {
										pack = reader.getPackage(d.type);
										if(!isSource(pack)) {
											if(pack != null) {
												if(has(pack)) readerB = getReader(pack);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
											}
											else {
												if(has(d.type)) readerB = getReader(d.type);
												else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
											}
											dataMembers = readerB.dataMembers;
											reader = null;
										}
										else {
											reader = new SourceReader(getContent(pack));
											dataMembers = reader.dataMembers;
											readerB = null;
										}
									}
									else if(readerB != null) {
										if(has(d.type)) readerB = getReader(d.type);
										else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										reader = null;
										dataMembers = readerB.dataMembers;
									}
									in = 1;
									continue inner;
								}
							}
							else if(d.name.equals(member)){
								least = true;
								String pack = null;
								if(reader != null) {
									pack = reader.getPackage(d.type);
									if(!isSource(pack)) {
										if(pack != null) {
											if(has(pack)) readerB = getReader(pack);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(pack);
										}
										else {
											if(has(d.type)) readerB = getReader(d.type);
											else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
										}
										dataMembers = readerB.dataMembers;
										reader = null;
									}
									else {
										reader = new SourceReader(getContent(pack));
										dataMembers = reader.dataMembers;
										readerB = null;
									}
								}
								else if(readerB != null) {
									if(has(d.type)) readerB = getReader(d.type);
									else readerB = Screen.getFileView().getJDKManager().prepareReader(d.type);
									reader = null;
									dataMembers = readerB.dataMembers;
								}
								in = 1;
								continue inner;
							}
						}
     				if(in == 0) {
     					for(SourceReader r : reader.internalReaders) {
     						if(r.className.equals(member)) {
     							dataMembers = r.dataMembers;
     							least = true;
     						}
     					}
     				} 
     				in = 1;
				}
				if(least) 
				     gen(dataMembers);
                    else {
                         Screen.getScreen().getToolMenu().setTask("Not at least one match found for " + code);
                         editor.contentWindow.setVisible(false);
                    }
			}
			else{
				//There are no members after class-path's dot, so showing all hints
				gen(dataMembers);
			}
		}
		return true;
	}

	public static int getParameterCount(String line){
		int parameterCount = 0;
		if(line.contains("(") && line.contains(")")){
			line = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')')).trim();
			if(line.equals("")) return parameterCount;
			int i = 0;
			String parameters = line;
			String p = "";
			int c = -1;
			//Skipping Diamonds
			for(char ch : parameters.toCharArray()){
				if(ch == '<')
					c++;
				else if(ch == '>')
					c--;
				if(c == -1 & ch != '<' & ch != '>')
					p += ch;
				i++;
			}
			if(!p.contains(","))
				parameterCount = 1;
			else 
			     parameterCount = p.split(",").length;
		}
		return parameterCount;
	}

	public static boolean isUpperCaseHintType(DataMember d, String lCode){
		String upperCode = "";
		for(char ch : d.name.toCharArray()){
			if(Character.isUpperCase(ch))
				upperCode += ch;
		}
		return upperCode.startsWith(lCode);
	}

	public static boolean isUpperCaseHintType(String text, String lCode){
		String upperCode = "";
		for(char ch : text.toCharArray()){
			if(Character.isUpperCase(ch))
				upperCode += ch;
		}
		return upperCode.startsWith(lCode);
	}

	public static void gen(LinkedList<DataMember> dataMembers){
		LinkedList<DataMember> mx = new LinkedList<>();
          lCode = getCodeIgnoreDot(editor.getText(), editor.getCaretPosition());
		if(lCode != null){
			dataMembers.forEach(d->{
				if(d.name.contains(lCode) || isUpperCaseHintType(d, lCode))
				     mx.add(d);
			});
			editor.contentWindow.genView(mx, Screen.getScreen().getGraphics());
			return;
		}
		editor.contentWindow.genView(dataMembers, Screen.getScreen().getGraphics());
	}

	public static void gen(LinkedList<DataMember> dataMembers, Editor editor){
		LinkedList<DataMember> mx = new LinkedList<>();
          lCode = getCodeIgnoreDot(editor.getText(), editor.getCaretPosition());
		if(lCode != null){
			dataMembers.forEach(d->{
				if(d.name.contains(lCode) || isUpperCaseHintType(d, lCode))
				     mx.add(d);
			});
			editor.contentWindow.genView(mx, omega.Screen.getScreen().getGraphics());
			return;
		}
		editor.contentWindow.genView(dataMembers, omega.Screen.getScreen().getGraphics());
	}

	public static String getName(String line){
		return line.substring(0, line.indexOf('(')).trim() + "()";
	}

	public static String getContent(String className){
		File file = null;
		if(className.contains(".")) {
			LinkedList<String> files = new LinkedList<>();
			StringTokenizer tok = new StringTokenizer(className, ".");
			while(tok.hasMoreTokens())
				files.add(tok.nextToken());
			String path = Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator;
			for(String f : files) {
				path += f + File.separator;
			}
			path = path.substring(0, path.length() - 1).trim();
			files.clear();
			file =  new File(path + ".java");
		}
		else {
			file = new File(Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator + className + ".java");
		}
		String code = "";
		for(Editor ex : Screen.getScreen().getTabPanel().getEditors()) {
			if(ex.currentFile.getAbsolutePath().equals(file.getAbsolutePath()))
				return ex.getText();
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String token = reader.readLine();
			while(token != null) {
				code += token + "\n";
				token = reader.readLine();
			}
			reader.close();
		}catch(Exception e) {System.out.println(e.getMessage());}
		return code;
	}

	public static File getFile(String className){
		File file = null;
		if(className.contains(".")) {
			LinkedList<String> files = new LinkedList<>();
			StringTokenizer tok = new StringTokenizer(className, ".");
			while(tok.hasMoreTokens())
				files.add(tok.nextToken());
			String path = Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator;
			for(String f : files) {
				path += f + File.separator;
			}
			path = path.substring(0, path.length() - 1).trim();
			files.clear();
			file =  new File(path + ".java");
		}
		else {
			file = new File(Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator + className + ".java");
		}
		return file.exists() ? file : null;
	}

	public static boolean isSource(String className){
		if(className == null) return false;
		if(Screen.getFileView().getProjectPath() == null) return false;
		if(className.contains(".")) {
			LinkedList<String> files = new LinkedList<>();
			StringTokenizer tok = new StringTokenizer(className, ".");
			while(tok.hasMoreTokens())
				files.add(tok.nextToken());
			String path = Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator;
			for(String f : files) {
				path += f + File.separator;
			}
			path = path.substring(0, path.length() - 1).trim();
			files.clear();
			return new File(path+".java").exists();
		}
		else {
			return new File(Screen.getFileView().getProjectPath() + File.separator + "src" + File.separator + className + ".java").exists();
		}
	}

	public static int getFirstUpperCase(String line){
		for(int i = 0; i < line.length(); i++){
			if(Character.isUpperCase(line.charAt(i)))
				return i;
		}
		return -1;
	}

     public static String getCode(String text, int caret){
          text = text.substring(0, caret);
          if(text.contains("{")) text = text.substring(text.lastIndexOf('{') + 1);
          if(text.contains("\n")) text = text.substring(text.lastIndexOf('\n') + 1);
          if(text.endsWith("}")) text = text.substring(0, text.indexOf('}'));
          text = text.trim();
          //Checking whether <text> is frame-able or not
          if(count(text, '(') > count(text, ')')){
          	int openParenthesisCount = count(text, '(');
			int closeParenthesisCount = count(text, ')');
			if(closeParenthesisCount < openParenthesisCount && closeParenthesisCount != 0){
				int extraParenthesis = openParenthesisCount - closeParenthesisCount;
				int index = text.indexOf('(');
				if(extraParenthesis > 1){
					index = 0;
					while(extraParenthesis-- > 0){
						index = text.indexOf("(", index + 1);
					}
				}
				else {
					while(--extraParenthesis > 0){
						index = text.indexOf("(", index + 1);
					}
				}
				text = text.substring(index + 1);
			}
			if(text.contains(",")){
				String mockText = text.substring(text.lastIndexOf(',') + 1);
				openParenthesisCount = count(mockText, '(');
				closeParenthesisCount = count(mockText, ')');
				if(closeParenthesisCount == openParenthesisCount) {
		               if(text.contains(","))
		                    text = text.substring(text.lastIndexOf(',') + 1).trim();
				}
			}
			else if(text.contains("(")){
	          	openParenthesisCount = count(text, '(');
				closeParenthesisCount = count(text, ')');
                    int extraParenthesis = openParenthesisCount - closeParenthesisCount;
				int index = -1;
				if(extraParenthesis >= 1){
					while(extraParenthesis-- > 0){
						index = text.indexOf("(", index + 1);
					}
				}
				text = text.substring(index + 1);
			}
          }
          if(text.contains("="))
               text = text.substring(text.lastIndexOf('=') + 1).trim();
          if(text.contains("+"))
               text = text.substring(text.lastIndexOf('+') + 1).trim();
          if(text.contains("-"))
               text = text.substring(text.lastIndexOf('-') + 1).trim();
          if(text.contains("*"))
               text = text.substring(text.lastIndexOf('*') + 1).trim();
          if(text.contains("/"))
               text = text.substring(text.lastIndexOf('/') + 1).trim();
          if(text.contains("^"))
               text = text.substring(text.lastIndexOf('^') + 1).trim();
          if(text.contains("<"))
               text = text.substring(text.lastIndexOf('<') + 1).trim();
          if(text.contains(">"))
               text = text.substring(text.lastIndexOf('>') + 1).trim();
          if(text.contains(";"))
               text = text.substring(text.lastIndexOf(';') + 1).trim();
          if(text.startsWith("return "))
          	text = text.substring("return ".length()).trim();
          if(text.startsWith("continue "))
          	text = text.substring("continue ".length()).trim();
          if(text.startsWith("break "))
          	text = text.substring("break ".length()).trim();
          if(text.contains("this."))
          	text = text.substring(text.lastIndexOf("this.") + "this.".length()).trim();
          if(text.startsWith("!"))
          	text = text.substring(text.lastIndexOf('!') + 1).trim();
          if(text.contains("?"))
          	text = text.substring(text.lastIndexOf('?') + 1).trim();
          if(text.contains(":"))
          	text = text.substring(text.lastIndexOf(':') + 1).trim();
          if(!text.contains("."))
               text = null;
          return text;
     }
    	public static String getCodeDoNotEliminateDot(String text, int caret){
          text = text.substring(0, caret);
          if(text.contains("{")) text = text.substring(text.lastIndexOf('{') + 1);
          if(text.contains("\n")) text = text.substring(text.lastIndexOf('\n') + 1);
          if(text.endsWith("}")) text = text.substring(0, text.indexOf('}'));
          text = text.trim();
          //Checking whether <text> is frame-able or not
          if(count(text, '(') > count(text, ')')){
               text = text.substring(text.lastIndexOf('(') + 1).trim();
               if(text.contains(","))
                    text = text.substring(text.lastIndexOf(',') + 1).trim();
          }
          if(text.contains("="))
               text = text.substring(text.lastIndexOf('=') + 1).trim();
          if(text.contains("+"))
               text = text.substring(text.lastIndexOf('+') + 1).trim();
          if(text.contains("-"))
               text = text.substring(text.lastIndexOf('-') + 1).trim();
          if(text.contains("*"))
               text = text.substring(text.lastIndexOf('*') + 1).trim();
          if(text.contains("/"))
               text = text.substring(text.lastIndexOf('/') + 1).trim();
          if(text.contains("^"))
               text = text.substring(text.lastIndexOf('^') + 1).trim();
          if(text.contains("<"))
               text = text.substring(text.lastIndexOf('<') + 1).trim();
          if(text.contains(">"))
               text = text.substring(text.lastIndexOf('>') + 1).trim();
          if(text.contains(";"))
               text = text.substring(text.lastIndexOf(';') + 1).trim();
          if(text.contains("|"))
               text = text.substring(text.lastIndexOf('|') + 1).trim();
          if(text.contains("&"))
               text = text.substring(text.lastIndexOf('&') + 1).trim();
          if(text.contains("%"))
               text = text.substring(text.lastIndexOf('%') + 1).trim();
          if(text.contains("@"))
               text = text.substring(text.lastIndexOf('@') + 1).trim();
          if(text.startsWith("new "))
               text = text.substring(text.lastIndexOf("new ") + 3).trim();
          if(text.startsWith("return "))
          	text = text.substring("return ".length()).trim();
          if(text.startsWith("continue "))
          	text = text.substring("continue ".length()).trim();
          if(text.startsWith("break "))
          	text = text.substring("break ".length()).trim();
          if(text.contains("this."))
          	text = text.substring(text.lastIndexOf("this.") + "this.".length()).trim();
          if(text.startsWith("!"))
          	text = text.substring(text.lastIndexOf('!') + 1).trim();
          if(text.contains("?"))
          	text = text.substring(text.lastIndexOf('?') + 1).trim();
          if(text.contains(":"))
          	text = text.substring(text.lastIndexOf(':') + 1).trim();
          return text;
     }

	public static String getCodeIgnoreDot(String text, int caret){
		text = text.substring(0, caret);
		if(text.contains("{")) text = text.substring(text.lastIndexOf('{') + 1);
		if(text.contains("\n")) text = text.substring(text.lastIndexOf('\n') + 1);
		if(text.endsWith("}")) text = text.substring(0, text.indexOf('}'));
		text = text.trim();
		//Checking whether <text> is frame-able or not
		if(count(text, '(') > count(text, ')')){
			text = text.substring(text.lastIndexOf('(') + 1).trim();
			if(text.contains(","))
				text = text.substring(text.lastIndexOf(',') + 1).trim();
		}
		//Skipping operators
		if(text.contains("="))
			text = text.substring(text.lastIndexOf('=') + 1).trim();
		if(text.contains("+"))
			text = text.substring(text.lastIndexOf('+') + 1).trim();
		if(text.contains("-"))
			text = text.substring(text.lastIndexOf('-') + 1).trim();
		if(text.contains("*"))
			text = text.substring(text.lastIndexOf('*') + 1).trim();
		if(text.contains("/"))
			text = text.substring(text.lastIndexOf('/') + 1).trim();
		if(text.contains("^"))
			text = text.substring(text.lastIndexOf('^') + 1).trim();
		if(text.contains("<"))
			text = text.substring(text.lastIndexOf('<') + 1).trim();
		if(text.contains(">"))
			text = text.substring(text.lastIndexOf('>') + 1).trim();
		if(text.contains(";"))
			text = text.substring(text.lastIndexOf(';') + 1).trim();
          if(text.contains(" "))
               text = text.substring(text.lastIndexOf(' ') + 1).trim();
		if(text.contains("."))
			text = text.substring(text.lastIndexOf('.') + 1).trim();;
		return text;
	}

	public static String getLastCodeIgnoreDot(String text, int caret){
		text = text.substring(0, caret);
		if(text.contains("{")) text = text.substring(text.lastIndexOf('{') + 1);
		if(text.contains("\n")) text = text.substring(text.lastIndexOf('\n') + 1);
		if(text.endsWith("}")) text = text.substring(0, text.indexOf('}'));
		text = text.trim();
		//Checking whether <text> is frame-able or not
		if(count(text, '(') > count(text, ')')){
			text = text.substring(text.lastIndexOf('(') + 1).trim();
			if(text.contains(","))
				text = text.substring(text.lastIndexOf(',') + 1).trim();
		}
		if(text.contains(";"))
			text = text.substring(text.lastIndexOf(';') + 1).trim();
		if(text.contains("."))
			text = text.substring(text.lastIndexOf('.') + 1).trim();
		return text;
	}

	public static String getLastCode(String text, int caret){
		if(text.contains("."))
			text = text.substring(text.lastIndexOf('.') + 1, caret);
		if(text.equals(""))
			text = null;
		return text;
	}

	public static String completeCode(String reducedText) {
		int openBraces = count(reducedText, '{');
		int closeBraces = count(reducedText, '}');
		if(openBraces > closeBraces){
			while(openBraces != 0){
				openBraces--;
				reducedText += "\n}";
			}
		}
		return reducedText;
	}

	public static boolean twiceUpperCases(String line) {
		int i = 0;
		boolean dot = false;
		for(char ch : line.toCharArray()) {
			if(ch == '.') dot = true;
			if(Character.isUpperCase(ch) && dot) {
				dot = false;
				i++;
				break;
			}
		}
		return i == 2;
	}

	public static int count(String line, char ch){
		int c = 0;
		for(char cx : line.toCharArray()){
			if(ch == cx)
				c++;
		}
		return c;
	}

	public static String replace(String text, char x, char y){
		String result = "";
		for(char ch : text.toCharArray()){
			if(ch == x)
				ch = y;
			result += ch;
		}
		return result;
	}
	
	public static int getUnpairedParanthesis(String text){
		int index = -1;
		for(int i = 0; i < text.length() - 1; i++){
			char c1 = text.charAt(i);
			char c2 = text.charAt(i + 1);
			
			if(c1 == '(' && Character.isLetterOrDigit(c2))
				index = i + 1;
		}
		return index;
	}

	public static String checkUnpairedParanthesis(String text){
		int index = getUnpairedParanthesis(text);
		return index > -1 ? text.substring(index) : text;
	}
}

