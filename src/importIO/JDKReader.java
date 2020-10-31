package importIO;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
public class JDKReader{
	private static final LinkedList<String> packages = new LinkedList<>();
	public static int last = 0;
	public static int version = 0;
	public static volatile boolean wasSame = false;
	public static final LinkedList<String> getPackages(){
		return packages;
	}
	public static void read(String path){
		if(path == null) return;
		File jdk = new File(path);
		if(!jdk.exists()) return;
		File releaseFile = new File(path+"/release");
		version = 0;
		try{
			Scanner reader = new Scanner(releaseFile);
			while(reader.hasNextLine()){
				String s = reader.nextLine();
				String cmd = "JAVA_VERSION=";
				if(s.startsWith("JAVA_VERSION=")){
					s = s.substring(s.indexOf(cmd) + cmd.length());
					s = s.substring(s.indexOf('\"') + 1, s.lastIndexOf('\"'));
					if(s.startsWith("1.")){
						s = s.charAt(2) + "";
						version = Integer.valueOf(s);
					}
					else{
						if(s.contains("."))
							s = s.substring(0, s.indexOf('.'));
						version = Integer.valueOf(s);
					}
					break;
				}
			}
			reader.close();
		}catch(Exception e){e.printStackTrace();}
		if(version == last) wasSame = true;
		if(version == 0 || version == last) return;
		wasSame = false;
		last = version;
		packages.clear();
		if(version < 9){
			try{
				JarFile jarFile = new JarFile(path+"/jre/lib/rt.jar");
				for(Enumeration<JarEntry> enums = jarFile.entries(); enums.hasMoreElements();){
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/") && name.contains(".class")){
						addPackage(getPackage(name));
					}
				}
				jarFile.close();
			}catch(Exception e){e.printStackTrace();}
		}
		else{
			try{
				File modDir = new File(path+"/jmods");
				File[] mods = modDir.listFiles();
				for(File module : mods){
					ZipFile zipFile = new ZipFile(module);
					inner:
						for(Enumeration entries = zipFile.entries(); entries.hasMoreElements();){
							String entry = entries.nextElement().toString();
							if(entry == null) continue inner;
							String name = entry;
							if(name.startsWith("classes")) {
								name = name.substring(8);
								name = getPackage(name);
								if(name != null){
									String packagePath = getPackage(name);
									addPackage(packagePath);
								}
							}
						}
					zipFile.close();
				}
			}catch(Exception e){e.printStackTrace();}
		}
	}

	public static void addPackage(String pack) {
		packages.add(pack);
	}
	
	private static String getPackage(String name){
		if(name.contains("module-info.class")) return null;
		try{
			name = name.substring(0, name.indexOf(".class"));
		}catch(Exception e){}
		name = name.replaceAll("/", ".");
		name = name.replaceAll("$", ".");
		if(name.endsWith("."))
			name = name.substring(0, name.length() - 1);
		if(name.charAt(name.length() - 2) == '$')
			name = name.substring(0, name.length() - 2);
		if(name.contains("$")){
			String copN = name;
			name = name.substring(0, name.lastIndexOf('$'));
			name = name + "." + copN.substring(copN.lastIndexOf('$') + 1);
		}
		if(Character.isDigit(name.charAt(name.length() - 1)))
			name = name.substring(0, name.length() - 2);
		if(name.endsWith(".")) {
			name = name.substring(0, name.lastIndexOf('.'));
		}
		return name;
	}
}
