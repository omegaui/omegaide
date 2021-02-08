package Omega;
/*
    Omega.IDE, this class launhces a new instance of Omega IDE.
    This is the main-class of the project.
    This is named like Omega.IDE to make it overcome the StartUPWMClassName
          warning on the Linux Desktop without any illegal reflection t.
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
public class IDE {
     
	public static ide.Screen screen; //The Object of the Main Window

     /*
      * The main method
     */
	public static void main(String[] args) {
		screen = new ide.Screen();
	}
}