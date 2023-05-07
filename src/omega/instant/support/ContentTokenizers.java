/*
 * ContentTokenizers
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package omega.instant.support;

import omega.instant.support.java.assist.ContentTokenizer;
import omega.ui.component.Editor;

import java.util.LinkedList;

public class ContentTokenizers {
    public static ContentTokenizer javaContentTokenizer = new ContentTokenizer();

    public static LinkedList<AbstractContentTokenizer> contentTokenizers = new LinkedList<>();

    static {
        add(javaContentTokenizer);
    }

    public static synchronized void arrangeTokens(Editor editor) {
        for (AbstractContentTokenizer contentTokenizer : contentTokenizers) {
            if (contentTokenizer.canArrangeTokens(editor)) {
                contentTokenizer.arrangeTokens(editor);
                return;
            }
        }

        //Gets called when there is no defined framework for a source file(like in case of a .txt file).
        javaContentTokenizer.arrangeTokens(editor);
    }

    public static synchronized void add(AbstractContentTokenizer contentTokenizer) {
        if (!contentTokenizers.contains(contentTokenizer))
            contentTokenizers.add(contentTokenizer);
    }
}
