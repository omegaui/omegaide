package omega.plugin;
import java.net.*;
import java.io.*;
public class Download {
     public static InputStream openStream(String url){
     	InputStream in = null;
          try{
          	in = new BufferedInputStream(new URL(url).openStream());
          }
          catch(Exception e){ 
          	System.err.println(e); 
          }
          return in;
     }
}
