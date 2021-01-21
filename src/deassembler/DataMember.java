package deassembler;
import java.util.*;
public class DataMember {
     public String access;
     public String modifier;
     public String type;
     public String name;
     public String parameters;
     public int parameterCount = 0;
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
               else parameterCount = p.split(",").length;
          }
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

     @Override
     public String toString(){
          if(parameters != null){
               return "access - "+access+", modifier - "+modifier+", type - "+type+", name - "+name+", parameters - " + parameters + ", parameterCount - " + parameterCount;
          }
          return "access - "+access+", modifier - "+modifier+", type - "+type+", name - "+name;
     }
}
