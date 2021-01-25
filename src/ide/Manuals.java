package ide;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.File;
import ide.utils.UIManager;
import java.awt.Desktop;
public class Manuals {
     public static final void showBasicManual(){
     	try{
               Desktop.getDesktop().open(UIManager.loadDefaultFile(".omega-ide" + File.separator + "Basic Manual.pdf"));
     	}catch(Exception e){ System.err.println(e); }
     }
}
