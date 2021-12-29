/**
  * Java Library Reader
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

package omega.instant.support.java.assist;
import omega.Screen;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.LinkedList;
public class ByteReader {
	private Class c;
	public String pack;
	public String access;
	public String modifier;
	public String className;
	public String type = "class";
	public String packagePath;
	public LinkedList<DataMember> dataMembers = new LinkedList<>();
	public LinkedList<ByteReader> internalReaders = new LinkedList<>();
     
	public ByteReader(Class c){
          if(c != null) {
     		this.c = c;
     		this.className = c.getName();
     		if(className.contains(" ")){
     			this.type = className.substring(0, className.indexOf(' '));
     			this.className = className.substring(className.indexOf(' ') + 1);
     		}
     		this.pack = c.getPackage().toString();
               this.modifier = Modifier.toString(c.getModifiers());
               if(modifier.contains(" ")){
                    if(modifier.contains("interface") || modifier.contains("enum") || modifier.contains("record")){
                         this.type = modifier.substring(modifier.lastIndexOf(' ') + 1);
                         this.modifier = modifier.substring(0, modifier.lastIndexOf(' '));
                    }
                    this.access = modifier.substring(0, modifier.indexOf(' '));
                    this.modifier = modifier.substring(modifier.indexOf(' ') + 1);
               }
     		loadInternalClasses();
     		loadFields();
     		loadMethods();
               if(!Assembly.has(className))
                    Assembly.add(className, this);
          }
	}
	public void loadInternalClasses(){
		Class[] internalClasses = c.getDeclaredClasses();
		if(internalClasses != null && internalClasses.length != 0){
			for(Class internalClass : internalClasses)
				internalReaders.add(new ByteReader(internalClass));
		}
	}
	public void loadFields(){
		for(Field f : c.getFields()){
			String access = "";
			String modifier = "";
			String name = "";
			String type = "";
			String member = f.toString();
			if(!member.startsWith("public"))
				continue;
			access = member.substring(0, member.indexOf(' '));
			member = member.substring(member.indexOf(' ') + 1);
			name = member.substring(member.lastIndexOf(' ') + 1);
			if(name.contains("."))
				name = name.substring(name.lastIndexOf('.') + 1);
			member = member.substring(0, member.lastIndexOf(' '));
			type = member.substring(member.lastIndexOf(' ') + 1);
			if(member.contains(" ")){
				member = member.substring(0, member.lastIndexOf(' '));
				modifier = member;
			}
			dataMembers.add(new DataMember(access, modifier, type, name, null));
		}
	}
	public void loadMethods(){
		for(Method m : c.getMethods()) {
			String access = "";
			String modifier = "";
			String name = "";
			String type = "";
			String parameters = "";
			String member = m.toString();
			if(!member.startsWith("public"))
				continue;
			int index = member.indexOf('(');
			if(member.charAt(index + 1) != ')')
				parameters = member.substring(member.indexOf('(') + 1, member.indexOf(')'));
			member = member.substring(0, member.indexOf('('));
			access = member.substring(0, member.indexOf(' '));
			member = member.substring(member.indexOf(' ') + 1);
			name = member.substring(member.lastIndexOf(' ') + 1);
			if(name.contains("."))
				name = name.substring(name.lastIndexOf('.') + 1);
			member = member.substring(0, member.lastIndexOf(' '));
			type = member.substring(member.lastIndexOf(' ') + 1);
			if(member.contains(" ")){
				member = member.substring(0, member.lastIndexOf(' '));
				modifier = member;
			}
			dataMembers.add(new DataMember(access, modifier, type, name + "()", parameters));
		}
	}
	public int count(String line, char ch){
		int c = 0;
		for(char chx : line.toCharArray()){
			if(ch == chx)
				c++;
		}
		return c;
	}
	public LinkedList<DataMember> getDataMembers(String modifier){
		LinkedList<DataMember> members = new LinkedList<>();
		dataMembers.forEach(d->{
			String mod = d.modifier;
			if(!modifier.equals("") && mod.contains(modifier)){
				members.add(d);
			}
			else if(modifier.equals("") && !mod.contains("static")){
				members.add(d);
			}
		});
		return members;
	}
	
	public boolean isInternalReader(String className) {
		for(ByteReader r : internalReaders) {
			String name = r.className.contains("$") ? r.className.substring(r.className.lastIndexOf('$') + 1) : r.className;
			if(name.equals(className))
				return true;
		}
		return false;
	}

	public boolean isSubClass(String className){
		boolean value = false;
		
		Class superClass = c.getSuperclass();
		
		String name = superClass.getName();
		if(name.contains(" "))
			name = name.substring(name.indexOf(' ') + 1);
		
		if(!name.equals("java.lang.Object"))
			value = Screen.getFileView().getJDKManager().prepareReader(name).isSubClass(className);
		
		return isInternalReader(className) || value;
	}
	
	@Override
	public String toString(){
		return "[type - " + type + ", name - " + className + ", modifier - " + modifier + ", access - " + access + "]";
	}
	public void close(){
		dataMembers.clear();
	}
}

