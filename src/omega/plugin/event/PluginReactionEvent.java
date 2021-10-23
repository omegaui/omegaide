package omega.plugin.event;
public record PluginReactionEvent(int type, Object source, Object data) {
	public static final int EVENT_TYPE_PROJECT_CHANGED = 0;
	public static final int EVENT_TYPE_PROJECT_CLOSED = 1;
	public static final int EVENT_TYPE_PROJECT_DELETED = 2;
	
	public static final int EVENT_TYPE_IDE_INITIALIZED = 3;
	public static final int EVENT_TYPE_IDE_MINIMIZED = 4;
	public static final int EVENT_TYPE_IDE_MAXIMIZED = 5;
	public static final int EVENT_TYPE_IDE_DO_LAYOUT = 6;
	public static final int EVENT_TYPE_IDE_LANG_TAG_CHANGED = 7;
	public static final int EVENT_TYPE_IDE_RESTORED = 8;
	public static final int EVENT_TYPE_IDE_CLOSING = 9;
	
	public static final int EVENT_TYPE_EDITOR_CREATED = 10;
	public static final int EVENT_TYPE_EDITOR_SAVED = 11;
	public static final int EVENT_TYPE_EDITOR_RELOADED = 12;
	public static final int EVENT_TYPE_EDITOR_DISCARD = 13;
	public static final int EVENT_TYPE_EDITOR_CLOSED = 14;

	public static boolean isEventTypeValid(int type){
		return type >= EVENT_TYPE_PROJECT_CHANGED && type <= EVENT_TYPE_EDITOR_CLOSED;
	}

	public static PluginReactionEvent genNewInstance(int type, Object source, Object data){
		return new PluginReactionEvent(type, source, data);
	}

}
