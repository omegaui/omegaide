/*
 * JumpToDefinitionPanels
 * Copyright (C) 2022 Omega UI

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

package omega.instant.support;

import omega.Screen;
import omega.instant.support.java.misc.JavaJumpToDefinitionPanel;
import omega.ui.component.Editor;

import java.util.LinkedList;

public final class JumpToDefinitionPanels {
    public static LinkedList<Class<? extends AbstractJumpToDefinitionPanel>> jumpToDefinitionPanels = new LinkedList<>();

    static {
        add(JavaJumpToDefinitionPanel.class);
    }

    public static synchronized void add(Class<? extends AbstractJumpToDefinitionPanel> clz) {
        if (!jumpToDefinitionPanels.contains(clz))
            jumpToDefinitionPanels.add(clz);
    }

    public static synchronized AbstractJumpToDefinitionPanel get(Editor editor) {
        try {
            for (Class clz : jumpToDefinitionPanels) {
                AbstractJumpToDefinitionPanel panel = (AbstractJumpToDefinitionPanel) clz.getDeclaredConstructors()[0].newInstance(editor);
                if (panel.canRead(editor))
                    return panel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void putToView(AbstractJumpToDefinitionPanel panel) {
        Screen.getScreen().toggleLeftComponent(panel);
    }
}
