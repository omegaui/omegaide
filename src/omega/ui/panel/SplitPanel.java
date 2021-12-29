/**
  * SplitPanel
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.ui.panel;
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
