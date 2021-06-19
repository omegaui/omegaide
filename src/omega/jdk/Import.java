package omega.jdk;
public class Import {
	public String className;
	public String packageName;
	public String packagePath;
	public String jarPath;
	public boolean module;
	public Import(String path, String jarPath, boolean module){
		this.packagePath = path;
		this.className = path.substring(path.lastIndexOf('.') + 1);
		this.packageName = path.substring(0, path.lastIndexOf('.'));
		this.jarPath = jarPath;
		this.module = module;
	}
	
	public Import(String pack, String name){
		this.className = name;
		this.packageName = pack;
	}
	public String getImport(){
		return (packagePath == null) ? (packageName + "." + className) : packagePath;
	}
    
	public String getClassName() {
		return className;
	}
	
	public String getPackage() {
		return packageName;
	}
	
	@Override
	public String toString(){
		return getImport();
	}
}

