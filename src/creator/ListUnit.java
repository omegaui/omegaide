package creator;
public class ListUnit {
     public String ext;
     public String container;
     public String sourceDir;
     public volatile boolean sur = true;
     public ListUnit(String ext, String container, String sourceDir, boolean sur){
     	this.ext = ext;
          this.container = container;
          this.sourceDir = sourceDir;
          this.sur = sur;
     }
}
