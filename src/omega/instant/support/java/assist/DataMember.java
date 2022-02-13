/*
 * Stores the variable data.
 * Copyright (C) 2022 Omega UI

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
import java.util.LinkedList;
public class DataMember {
	
	public String access;
	public String modifier;
	public String type;
	public String name;
	public String parameters;
	public String representableValue;
	public String data;
	public int parameterCount = 0;
	public int lineNumber;
	public LinkedList<String> modifiers = new LinkedList<>();

	public Runnable extendedInsertion = ()->{};
	
	public DataMember(String access, String modifier, String type, String name, String parameters){
		this.access = access;
		this.type = type;
		this.modifier = modifier;
		this.name = name;
		if(parameters != null)
			this.parameters = parameters.trim();
		if(modifier.contains(" ")){
			String[] mods = modifier.split(" ");
			for(String mod : mods)
				modifiers.add(mod.trim());
		}
		else {
			modifiers.add(modifier);
		}
		if(parameters != null && !parameters.equals("")){
			//Skipping Diamonds
			int i = 0;
			String p = "";
			int c = -1;
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

		if(access.equals("custom hint")){
			representableValue = name + " - " + type;
		}
		else if(name.contains(".") & parameters != null){
			representableValue = null;
		}
		else if(parameterCount != 0) {
			String x = name.substring(0, name.indexOf('('));
			representableValue = x + "(" + parameters + ")" + " - " + type;
		}
		else{
			representableValue =  name + " - " + type;
		}

		
		if(name.contains("()")){
			name = name.substring(0, name.lastIndexOf('('));
			data = access + " " + modifier + " " + type + " " + name + "(" + parameters + ")";
		}
		else {
			data = access + " " + modifier + " " + type + " " + name;
		}
	}
	
	public DataMember(String access, String modifier, String type, String name, String parameters, int lineN){
		this(access, modifier, type, name, parameters);
		setLineNumber(lineN);
	}
	
	public String getData(){
		return data;
	}
	
	public String getRepresentableValue() {
		return representableValue;
	}
	
	public boolean isMethod(){
		return parameters != null;
	}
	
	@Override
	public String toString(){
		if(parameters != null){
			return "access - " + access + ", modifier - " + modifier + ", type - " + type + ", name - " + name + ", parameters - " + parameters + ", parameterCount - " + parameterCount;
		}
		return "access - " + access + ", modifier - " + modifier + ", type - " + type + ", name - " + name;
	}
	
	public void setLineNumber(int lineN){
		lineNumber = lineN;
	}

	public java.lang.Runnable getExtendedInsertion() {
		return extendedInsertion;
	}
	
	public void setExtendedInsertion(java.lang.Runnable extendedInsertion) {
		this.extendedInsertion = extendedInsertion;
	}
	
}

