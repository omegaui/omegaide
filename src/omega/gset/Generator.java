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

package omega.gset;
import omega.Screen;
import java.util.StringTokenizer;
import omega.deassembler.Assembly;
import omega.deassembler.DataMember;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import omega.deassembler.ByteReader;
/*
    The framework that works behind Getter/Setter/Override/Implement
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
public class Generator {
     public static GSView gsView;
     public static OverView overView;
     public static ByteReader reader;
     
     public static void init(Screen screen){
           gsView = new GSView(screen);
           overView = new OverView(screen);
     }
     
     public static String getTabs(RSyntaxTextArea textArea, int caret){
          String text = textArea.getText().substring(0, caret);
          text = text.substring(text.lastIndexOf('\n') + 1);
     	return text;
     }

     public static String getSuitableName(String name){
	     if(Character.isLowerCase(name.charAt(0)))
               return name.charAt(0) + "";
          if(name.contains("<"))
               return name.substring(0, 1).toLowerCase() + name.substring(1, name.indexOf("<"));
          if(name.contains("["))
               return name.substring(0, 1).toLowerCase() + name.substring(1, name.indexOf("[")) + "Array";
          return name.substring(0, 1).toLowerCase() + name.substring(1);
     }
     
     public static String toUpperCase(String name){
     	return name.substring(0, 1).toUpperCase() + name.substring(1);
     }

     public static boolean isMemberOfObject(DataMember d){
     	if(reader == null){
               if(Assembly.has("java.lang.Object"))
                    reader = Assembly.getReader("java.lang.Object");
               else
                    reader = omega.Screen.getFileView().getJDKManager().prepareReader("java.lang.Object");
     	}
          for(DataMember dx : reader.dataMembers){
               if(dx.toString().equals(d.toString()))
                    return true;
          }
          return false;
     }

     public static void implement(DataMember d, RSyntaxTextArea textArea){
          int caret = textArea.getCaretPosition();
          String tabs = getTabs(textArea, caret);
          String meth = "@Override\n" + tabs + "public";
          if(d.parameters == null) return;
          String rep = "";
          String name = d.name.substring(0, d.name.indexOf('('));
          rep += name + "(";
          String parameters = d.parameters;
          StringTokenizer tok = new StringTokenizer(parameters, ", ");
          while(tok.hasMoreTokens()){
               String token = tok.nextToken();
               if(token.contains("."))
                    token = token.substring(token.lastIndexOf('.') + 1).trim();
               String data = getSuitableName(token);
               try{
                    if(Character.isLetter(parameters.substring(parameters.indexOf(token) + token.length()).trim().charAt(0)))
                         data = tok.nextToken();
               }catch(Exception ex){}
               rep += token + " " + data + ", ";
          }
          if(rep.contains(","))
               rep = rep.substring(0, rep.lastIndexOf(','));
          rep += ")";
          String type = d.type;
          if(type.contains("."))
               type = type.substring(type.lastIndexOf('.') + 1);
          meth = meth.trim() + " " + type + " " + rep + " {";
          boolean hasReturn = !type.equals("void");
          String content = "\n" + tabs + tabs;
          if(hasReturn){
               content  += "return null;";
          }
          content += "\n" + tabs + "}\n" + tabs;
          meth = meth.trim() + content;
          textArea.insert(meth, caret);
     }
     
     public static void genGetter(DataMember d, RSyntaxTextArea textArea, String access){
          int caret = textArea.getCaretPosition();
          String tabs = getTabs(textArea, caret);
          String meth = access + " " + d.modifier;
          meth = meth.trim() + " " + d.type;
          String name = (d.type.equals("boolean") ? "is" : "get") + toUpperCase(d.name) + "() {";
          meth = meth.trim() + " " + name;
          meth += "\n" + tabs + tabs + "return " + d.name + ";\n" + tabs + "}\n" + tabs;
          textArea.insert(meth, caret);
     }
     
     public static void genSetter(DataMember d, RSyntaxTextArea textArea, String access, String className){
          int caret = textArea.getCaretPosition();
          String tabs = getTabs(textArea, caret);
          String meth = access + " " + d.modifier;
          meth = meth.trim() + " void";
          String name = "set" + toUpperCase(d.name) + "(" + d.type + " " + d.name + ") {";
          meth = meth.trim() + " " + name;
          String s = d.modifier.contains("static") ? className : "this";
          meth += "\n" + tabs + tabs + s + "." + d.name + " = " + d.name + ";\n" + tabs +"}\n" + tabs;
          textArea.insert(meth, caret);
     }
}

