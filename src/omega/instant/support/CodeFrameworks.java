/*
 * CodeFrameworks
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
import omega.ui.component.Editor;

import java.util.LinkedList;

import omega.instant.support.java.framework.CodeFramework;
public final class CodeFrameworks {
	public static CodeFramework javaCodeFramework = new CodeFramework();

	public static LinkedList<AbstractCodeFramework> codeFrameworks = new LinkedList<>();

	static{
		add(javaCodeFramework);
	}

	public static boolean isResolving(){
		for(AbstractCodeFramework codeFramework : codeFrameworks){
			if(codeFramework.isResolving())
				return true;
		}
		return false;
	}

	public static boolean think(Editor editor, String text, int caret){
		for(AbstractCodeFramework codeFramework : codeFrameworks){
			if(codeFramework.canThink(editor))
				return codeFramework.think(editor, text, caret);
		}
		return false;
	}

	public static synchronized void add(AbstractCodeFramework codeFramework){
		if(!codeFrameworks.contains(codeFramework))
			codeFrameworks.add(codeFramework);
	}
}
