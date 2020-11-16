package importIO;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ide.Screen;

public class ImportManager{

	public static String projectPath;
	private static File file;
	public static final LinkedList<Import> imports = new LinkedList<>();
	public static final LinkedList<Import> javase = new LinkedList<>();
	public static final LinkedList<Import> ext = new LinkedList<>();
	public static final LinkedList<Import> sources = new LinkedList<>();
	public static final LinkedList<String> javaLangPack = new LinkedList<>();
	public Thread readingThread = null;
	public static volatile boolean reading = false;
	public static volatile boolean readingNatives = false;

	public void reload(String projectPath) {
		ImportManager.projectPath = projectPath;
		init();
	}

	private void init()
	{
		File libRoot = new File(projectPath + "/lib");
		readingThread = new Thread(()->{
			if(reading) return;
			reading = true;
			readingNatives = true;
			try {
				readNative();
				readingNatives = false;
				ext.clear();
				readLibrary(libRoot);
				//Sort hints
				readSource(this);
				for(Import im : ext){
					Screen.setStatus("Resolving Additional Project Libraries", (ext.indexOf(im) * 100) / ext.size());
					imports.add(im);
				}
				Screen.setStatus("Finishing Up. Starting Deassembler!!", 100);
			}catch(Exception e) {e.printStackTrace();}
			readingNatives = false;
			reading = false;
			readingThread = null;
			JDKReader.wasSame = true;
		});
		readingThread.start();
	}

	public void readNative() {
		if(JDKReader.wasSame) {
			int lastPositionOfJDKByteCode = javase.size();
			int size = imports.size();
			int index = -1  * (lastPositionOfJDKByteCode - size);
			while(index > 0) {
				try {
					imports.remove(imports.size() - index--);
				}catch(Exception e) {System.err.println("Exception while removing "+index + 1);}
			}
			return;
		}
		javase.clear();
		imports.clear();
		int index = 0;
		int last = 0;
		int size = JDKReader.getPackages().size();
		LinkedList<String> packs = JDKReader.getPackages();
		for(String im : packs) {
			int p = (++index * 100) / size;
			if(p > last) {
				last = p;
				Screen.setStatus("Resolving JDK Sources ("+index+" of "+size+")", last);
			}
			try {
				if(!Character.isDigit(im.charAt(im.length() - 1)) && im.charAt(im.length() - 2) != '.') {
					String pack = im.substring(0, im.lastIndexOf('.'));
					String name = im.substring(im.lastIndexOf('.') + 1);
					Import i = new Import(pack, name);
					javase.add(i);
					imports.add(i);
					if(pack.equals("java.lang"))
						javaLangPack.add(pack+"."+name);
				}
			}catch(Exception e) {e.printStackTrace();}
		}
		Screen.setStatus("Resolving JDK Sources", 100);
	}

	public static void readSource(ImportManager imM)
	{
		if(Screen.getFileView().getProjectPath() != null) {
			file = new File(Screen.getFileView().getProjectPath());
			if(!file.exists()) return;
		}
		for(Import im : sources)
			imports.remove(im);
		sources.clear();
		File srcRoot = new File(file.getAbsolutePath() + "/src");
		String[] filePaths = srcRoot.list();
		if(filePaths.length == 0)
			return;
		LinkedList<String> paths = new LinkedList<>();
		for(String path : filePaths) {
			paths.add(srcRoot.getAbsolutePath()+"/"+path);
		}

		//File Path Listing
		LinkedList<File> files = new LinkedList<>();
		files = getAllFiles(paths, files);

		files.forEach(file->{
			String name = file.getName();
			try {
				name = name.substring(0, name.length() - 5);
				Import n = new Import(getPackagePathOfSource(file), name);
				if(n != null) {
					try {
						sources.add(n);
					}catch(Exception e) {System.err.println("No Project SDK found!");}
				}
			}catch(Exception exc) {}
		});
		try {
			for(Import im : sources){
				Screen.setStatus("Resolving Project Sources", (sources.indexOf(im) * 100) / sources.size());
				imports.add(im);
			}
		}catch(Exception e) {}
		Screen.setStatus("Resolving Project Sources", 100);
	}

	private void readLibrary(File libRoot)
	{
		LinkedList<String> paths = null;
		try {
			paths = Screen.getFileView().getDependencyManager().dependencies;
			if(paths.size() == 0) {
				return;
			}	
		}catch(Exception e) {return;}

		//ClassPath Listing
		LinkedList<File> jarFiles = new LinkedList<>();
		for(String path : paths) {
			if(path.endsWith(".jar"))
				jarFiles.add(new File(path));
		}
		int size = jarFiles.size();
		jarFiles.forEach(jarFile -> {
			Screen.setStatus("Reading Libraries "+jarFile.getName()+"("+(jarFiles.indexOf(jarFile)+1)+" of "+size+")", ((jarFiles.indexOf(jarFile)) * 100)/size);
			try{
				JarFile jar = new JarFile(jarFile.getAbsolutePath());
				for(Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/") && name.contains(".class")) {
						Import n = getImportOfByte(name);
						if(n != null) {
							n.jarPath = jarFile.getAbsolutePath();
							if(!ext.contains(n))
								ext.add(n);
						}
					}
				}
			}catch(Exception e){e.printStackTrace();}
		});
	}

	public synchronized void addImport(Import im) {
		imports.add(im);
	}

	public static LinkedList<String> getJavaLangPack(){
		return javaLangPack;
	}

	public void readAgain()
	{
		imports.clear();
		init();
	}

	private static LinkedList<File> getAllFiles(LinkedList<String> paths, LinkedList<File> files)
	{
		if(paths.size() == 0)
			return files;

		LinkedList<String> paths0 = new LinkedList<>();

		for(String path : paths)
		{
			File file = new File(path);
			if(file.isDirectory())
			{
				for(String path0 : file.list())
				{
					paths0.add(file.getAbsolutePath() + "/" + path0);
				}
			}
			else
				files.add(file);
		}
		return getAllFiles(paths0, files);
	}

	private static String getPackagePathOfSource(File file)
	{
		String res = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
		StringTokenizer tokenizer = new StringTokenizer(res, "/");
		res = "";
		boolean canRecord = false;
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(canRecord)
				res += token + ".";
			else if(token.equals("src"))
				canRecord = true;
		}
		if(!canRecord)
		{
			try{
				throw new Exception("File is not a source FIle");
			}
			catch(Exception e){e.printStackTrace();}
		}
		if(res.length() != 0)
			return res.substring(0, res.length() - 1);
		return res;
	}

	private Import getImportOfByte(String fullPath)
	{
		Import im = null;
		if(fullPath.equals("module-info.class")) return im;
		String res = "";
		fullPath = replaceInnerSource(fullPath);
		res = fullPath.substring(0, fullPath.length() - 6);
		String packageName = res.substring(0, res.lastIndexOf('.'));
		String srcName = res.substring(res.lastIndexOf('.') + 1);
		im = new Import(packageName, srcName);
		return im;
	}

	private String replaceInnerSource(String srcName)
	{
		String res = "";
		boolean canRecord = true;
		for(int i = 0; i < srcName.length() - 1; i++)
		{
			char ch = srcName.charAt(i);
			if(ch == '$' || ch == '/')
				res += '.';
			else if(canRecord)
				res += ch;
			else
				canRecord = true;
			if(ch == '$' && Character.isDigit(srcName.charAt(i+1)))
				canRecord = false;
		}
		res += srcName.charAt(srcName.length() - 2);
		return res;
	}

	public static synchronized LinkedList<Import> getAllImports()
	{
		return imports;
	}

}
