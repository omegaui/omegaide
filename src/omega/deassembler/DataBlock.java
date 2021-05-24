/**
  * <one line to give the program's name and a brief idea of what it does.>
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

package omega.deassembler;
/*
It reads variables from a provided method block.
Copyright (C) 2021 Omega UI. All Rights Reserved.
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.util.*;
public class DataBlock {
	public LinkedList<DepthMember> depthMembers = new LinkedList<>();
	public DataMember block;
	public String code;
	public SourceReader reader;
	public DataBlock(SourceReader reader, DataMember block){
		this.reader = reader;
		this.block = block;
		depthMembers.clear();
	}
	public void read(String code){
		if(this.code != null && this.code.equals(code)) return;
		this.code = code;
		//Unpacking DataMembers from _prototype
		if(block.parameterCount > 0){
			if(block.parameters.contains(",") && block.parameters.contains(" ")){
				StringTokenizer tok = new StringTokenizer(block.parameters, ",");
				while(tok.hasMoreTokens()){
					String p = tok.nextToken().trim();
					if(p.contains(" ")){
						String name = p.substring(p.indexOf(' ') + 1).trim();
						String type = reader.evaluateType(p.substring(0, p.indexOf(' ')).trim());
						if(type != null)
							depthMembers.add(new DepthMember("", "", type, name, null, 1));
					}
				}
			}
			else if(block.parameters.contains(" ")){
				String p = block.parameters;
				String name = p.substring(p.indexOf(' ') + 1).trim();
				String type = reader.evaluateType(p.substring(0, p.indexOf(' ')).trim());
				if(type != null)
					depthMembers.add(new DepthMember("", "", type, name, null, 1));
			}
		}
		//Unpacking DataMembers from _block
		StringTokenizer tok = new StringTokenizer(this.code, "\n");
		int braces = 1;
		while(tok.hasMoreTokens()){
			String line = tok.nextToken();
			braces += (count(line, '{') - count(line, '}'));
			if(!line.contains(" ")) continue;
			if(!line.contains("for") && SourceReader.isInnerLine(line)) continue;
			//Unpacking DataMembers from try-with_block
			if(line.contains("try") && line.contains("(") && line.contains(")") && line.contains("=")){
				String part = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
				if(part.contains("=")){ //Verifying the line still contains '='
					part = part.substring(0, part.indexOf('=')).trim();
					String name = part.substring(part.indexOf(' ') + 1).trim();
					String type = reader.evaluateType(part.substring(0, part.indexOf(' ')).trim());
					if(name.contains("(") || name.contains(")"))
						continue;
					if(type != null)
						depthMembers.add(new DepthMember("", "", type, name, null, braces));
				}
				continue;
			}
			//Unpacking DataMembers from catch_block
			if(line.contains("catch") && line.contains("(") && line.contains(")")){
				String part = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
				if(part.contains(" ")){
					String name = part.substring(part.indexOf(' ') + 1).trim();
					String type = reader.evaluateType(part.substring(0, part.indexOf(' ')).trim());
					if(name.contains("(") || name.contains(")"))
						continue;
					if(type != null)
						depthMembers.add(new DepthMember("", "", type, name, null, braces));
				}
				continue;
			}
			//Unpacking DataMembers from _for$loop
			if(line.contains("for") && (line.contains(";") || line.contains(":")) && line.trim().charAt(line.trim().length() - 1) != ';'){
				if(line.contains("(") && line.contains(")")){
					line = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
					if(line.contains(";")){
						if(line.indexOf(';') == 0) continue;
						line = line.substring(0, line.indexOf(';')).trim();
						if(line.contains(",")){
							StringTokenizer ok = new StringTokenizer(line, ",");
							while(ok.hasMoreTokens()){
								String t = ok.nextToken();
								if(t.contains("="))
									t = t.substring(0, t.indexOf('=')).trim();
								if(t.contains(" ")){
									String name = t.substring(t.indexOf(' ') + 1).trim();
									String type = reader.evaluateType(t.substring(0, t.indexOf(' ')).trim());
									if(name.contains("(") || name.contains(")"))
										continue;
									if(type != null)
										depthMembers.add(new DepthMember("", "", type, name, null, braces));
								}
							}
						}
						else if(line.contains(" ")){
							String t = line;
							String name = t.substring(t.indexOf(' ') + 1).trim();
							String type = reader.evaluateType(t.substring(0, t.indexOf(' ')).trim());
							if(name.contains("(") || name.contains(")"))
								continue;
							if(type != null)
								depthMembers.add(new DepthMember("", "", type, name, null, braces));
						}
					}
					else if(line.contains(":")){
						String exp = line.substring(0, line.indexOf(':'));
						String name = exp.substring(exp.indexOf(' ') + 1).trim();
						String type = reader.evaluateType(exp.substring(0, exp.indexOf(' ')).trim());
						if(name.contains("(") || name.contains(")"))
							continue;
						if(type != null)
							depthMembers.add(new DepthMember("", "", type, name, null, braces));
					}
				}
				continue;
			}
			//Unpacking DataMembers from _code$lines
			if(line.contains("=")){
				line = line.substring(0, line.indexOf('=')).trim();
				if(line.contains(" ")){
					String t = line;
					String name = t.substring(t.indexOf(' ') + 1).trim();
					String type = reader.evaluateType(t.substring(0, t.indexOf(' ')).trim());
					if(name.contains("(") || name.contains(")"))
						continue;
					if(type != null)
						depthMembers.add(new DepthMember("", "", type, name, null, braces));
				}
			}
			else if(line.contains(";")){
				line = line.substring(0, line.indexOf(';')).trim();
				if(line.contains(" ")){
					String t = line;
					String name = t.substring(t.indexOf(' ') + 1).trim();
					String type = reader.evaluateType(t.substring(0, t.indexOf(' ')).trim());
					if(name.contains("(") || name.contains(")"))
						continue;
					if(type != null)
						depthMembers.add(new DepthMember("", "", type, name, null, braces));
				}
			}
		}
	}
	public LinkedList<DepthMember> get(String name){
		LinkedList<DepthMember> depthMembers = new LinkedList<>();
		for(DepthMember m : this.depthMembers){
			if(m.name.equals(name)) depthMembers.add(m);
		}
		return depthMembers;
	}
	
	public DepthMember getMatch(String var) {
		for(DepthMember m : depthMembers) {
			if(m.name.equals(var))
				return m;
		}
		return null;
	}
	public DepthMember getLast(){
		if(depthMembers.isEmpty()) {
			System.out.println(block.name + " has no variables");
			return null;
		}
		return depthMembers.getLast();
	}
	public static int calculateDepth(String var, String code){
		int depth = 1;
		
		return depth;
	}
	public int count(String line, char ch){
		int c = 0;
		for(char cx : line.toCharArray()){
			if(cx == ch) c++;
		}
		return c;
	}
}

