package omega.utils;
import java.awt.Component;

import javax.swing.JSplitPane;
public class SplitPanel extends JSplitPane{
	
	private int splitType;
	private int dividerSize;
	
	public SplitPanel(int splitType){
		super(splitType);
		this.splitType = splitType;
		this.dividerSize = getDividerSize();
	}
	
	public void analyzeDivider(){
		if(splitType == HORIZONTAL_SPLIT){
			if(getLeftComponent() == null || getRightComponent() == null || getLeftComponent().getWidth() == 0 || getRightComponent().getWidth() == 0 || !getLeftComponent().isVisible() || !getRightComponent().isVisible()){
				dividerSize = getDividerSize() > 0 ? getDividerSize() : dividerSize;
				setDividerSize(0);
			}
			else
				setDividerSize(dividerSize);
		}
		else{
			if(getTopComponent() == null || getBottomComponent() == null || getBottomComponent().getWidth() == 0 || getTopComponent().getWidth() == 0 || !getTopComponent().isVisible() || !getBottomComponent().isVisible()){
				dividerSize = getDividerSize() > 0 ? getDividerSize() : dividerSize;
				setDividerSize(0);
			}
			else
				setDividerSize(dividerSize);
		}
	}

	@Override
	public void remove(Component component){
		super.remove(component);
		layout();
	}

	@Override
	public void setLeftComponent(Component component){
		super.setLeftComponent(component);
		layout();
	}

	@Override
	public void setRightComponent(Component component){
		super.setRightComponent(component);
		layout();
	}

	@Override
	public void setTopComponent(Component component){
		super.setTopComponent(component);
		layout();
	}

	@Override
	public void setBottomComponent(Component component){
		super.setBottomComponent(component);
		layout();
	}
	
	@Override
	public void layout(){
		analyzeDivider();
		super.layout();
	}
}
