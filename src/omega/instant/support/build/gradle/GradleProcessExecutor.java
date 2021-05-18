package omega.instant.support.build.gradle;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
public class GradleProcessExecutor {
	public static Process init(File dir){
		Process p = null;
		try{
			p = new ProcessBuilder("gradle", "init").directory(dir).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	public static Process run(File dir){
		Process p = null;
		try{
			return new ProcessBuilder("gradle", "run").directory(dir).start();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	public static Process build(File dir){
		Process p = null;
		try{
			return new ProcessBuilder("gradle", "build").directory(dir).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}
	public static boolean isErrorOccured(Process p){
		Scanner errorReader = getErrorScanner(p);
		while(p.isAlive()){
			if(errorReader.hasNextLine())
				return true;
		}
		errorReader.close();
		return false;
	}
	public static PrintWriter getWriter(Process p){
		try{
			return new PrintWriter(p.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scanner getErrorScanner(Process p){
		try{
			return new Scanner(p.getErrorStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scanner getInputScanner(Process p){
		try{
			return new Scanner(p.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
