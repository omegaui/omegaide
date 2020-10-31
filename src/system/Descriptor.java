package system;
public final class Descriptor{
	private String pluginName;
	private String pluginPath;
	private String devInfo;
	private String pluginInfo;
	private String pluginVersion;
	private String iconPath;
	private StartupType startupType;
	
	public Descriptor(StartupType type){
		this.startupType = type;
	}
	public String getPluginPath() {
		return pluginPath;
	}
	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}

	public StartupType getStartupType() {
		return startupType;
	}
	public void setStartupType(StartupType startupType) {
		this.startupType = startupType;
	}
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getDevInfo() {
		return devInfo;
	}
	public void setDevInfo(String devInfo) {
		this.devInfo = devInfo;
	}
	public String getPluginInfo() {
		return pluginInfo;
	}
	public void setPluginInfo(String pluginInfo) {
		this.pluginInfo = pluginInfo;
	}
	public String getPluginVersion() {
		return pluginVersion;
	}
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}


}
