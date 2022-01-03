/**
* AbstractContentTokenizer
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
* along with this program.  If not, see http://www.gnu.org/licenses.
*/

package omega.instant.support;
import omega.ui.component.Editor;

/*
 * This class can be used to create a minimal code assist i.e to create a limited content assist.
 * It is optional to create a CodeFramework class.
 * See omega.instant.support.java.assist.ContentTokenizer to learn how to implement this.
 */

public abstract class AbstractContentTokenizer {
	/*
	 * Returns Whether the Hints can be made, depending on the editor data!
	 */
	public abstract boolean canArrangeTokens(Editor editor);
	
	/*
	 * Creates Hints and Show Them using the CodeFramework.gen() method.
	 */
	public abstract void arrangeTokens(Editor editor);
}
