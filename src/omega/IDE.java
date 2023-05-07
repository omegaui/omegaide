/**
 * The Main Class
 * Copyright (C) 2021 Omega UI
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega;

public class IDE {

    //The Object of the Main Window
    public static Screen screen;

    /*
     * The main method
     */
    public static void main(String[] args) {
        screen = new Screen();
    }

    public static void exit() {
        screen.dispose();
    }

    public static void restart() {
        new Thread(() -> {
            try {
                if (Screen.onWindows())
                    new ProcessBuilder("java", "-jar", "Omega IDE.jar").start();
                else
                    new ProcessBuilder("omega-ide").start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        screen.dispose();
    }
}
