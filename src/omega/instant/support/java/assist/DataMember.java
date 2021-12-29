/*
    Stores the variable data.
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
package omega.instant.support.java.assist;
import java.util.LinkedList;
public class DataMember {
     public String access;
     public String modifier;
     public String type;
     public String name;
     public String parameters;
     public int parameterCount = 0;
     public int lineNumber;
     private LinkedList<String> modifiers = new LinkedList<>();
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
     }

     public DataMember(String access, String modifier, String type, String name, String parameters, int lineN){
     	this(access, modifier, type, name, parameters);
     	setLineNumber(lineN);
     }
     public String getData(){
          String name = this.name;
          if(name.contains("()")){
               name = name.substring(0, name.lastIndexOf('('));
               return access + " " + modifier + " " + type + " " + name + "(" + parameters + ")";
          }
          return access + " " + modifier + " " + type + " " + name;
     }
     
     public String getRepresentableValue() {
    	     if(access.equals("custom hint"))
    		     return name + " - " + type;
    	     if(name.contains(".") & parameters != null)
    		     return null;
    	     if(parameterCount != 0) {
               String x = name.substring(0, name.indexOf('('));
               return x + "(" + parameters + ")" + " - " + type;
    	     }
    	     return name + " - " + type;
     }

     public boolean isMethod(){
     	return parameters != null;
     }

     @Override
     public String toString(){
          if(parameters != null){
               return "access - "+access+", modifier - "+modifier+", type - "+type+", name - "+name+", parameters - " + parameters + ", parameterCount - " + parameterCount;
          }
          return "access - "+access+", modifier - "+modifier+", type - "+type+", name - "+name;
     }

     public void setLineNumber(int lineN){
     	lineNumber = lineN;
     }
}

