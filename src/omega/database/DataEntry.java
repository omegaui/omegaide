package omega.database;
public class DataEntry{
     private String entry;
     private String name;
     public DataEntry(String name, String entry){
          this.name = name;
          this.entry = entry;
     }
     public String getValue(){
          return entry;
     }
     public void setValue(String entry) {
         this.entry = entry;
     }
     public String getName(){
          return name;
     }
     public long getValueAsLong(){
         return Long.valueOf(entry);
    }
     public int getValueAsInt(){
         return Integer.valueOf(entry);
    }
     public double getValueAsDouble(){
          return Double.valueOf(entry);
     }
     public char getValueAsChar(){
          return entry.charAt(0);
     }
     public boolean getValueAsBoolean(){
          return Boolean.valueOf(entry);
     }     
}