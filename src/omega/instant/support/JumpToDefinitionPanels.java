package omega.instant.support;
import omega.Screen;

import omega.instant.support.java.misc.JavaJumpToDefinitionPanel;

import omega.ui.component.Editor;

import java.util.LinkedList;
public final class JumpToDefinitionPanels {
	public static LinkedList<Class<? extends AbstractJumpToDefinitionPanel>> jumpToDefinitionPanels = new LinkedList<>();

	static{
		add(JavaJumpToDefinitionPanel.class);
	}

	public static synchronized void add(Class<? extends AbstractJumpToDefinitionPanel> clz){
		if(!jumpToDefinitionPanels.contains(clz))
			jumpToDefinitionPanels.add(clz);
	}

	public static synchronized AbstractJumpToDefinitionPanel get(Editor editor){
		try{
			for(Class clz : jumpToDefinitionPanels){
				AbstractJumpToDefinitionPanel panel = (AbstractJumpToDefinitionPanel)clz.getDeclaredConstructors()[0].newInstance(editor);
				if(panel.canRead(editor))
					return panel;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized void putToView(AbstractJumpToDefinitionPanel panel){
		Screen.getScreen().toggleLeftComponent(panel);
	}
}
