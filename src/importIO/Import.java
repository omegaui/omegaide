package importIO;

public class Import {
     String packageName;
     String srcName;
     public String jarPath = "system";
     public Import(String pack, String src){
          this.packageName = pack;
          for(int i = 0; i < 10; i++){
               if(src.endsWith("."+i)){
                    src = src.substring(0, src.length() - 2);
                    break;
               }
          }
          this.srcName = src;
     }
     
     public String getPackage() {
   	  return packageName;
     }
     
     public String getClassName() {
   	  return srcName;
     }

     public String getImport()
     {
          return packageName+"."+srcName;
     }
}
