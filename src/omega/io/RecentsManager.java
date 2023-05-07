/**
 * RecentsManager
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

package omega.io;

import omega.Screen;

import java.io.*;
import java.util.LinkedList;

public class RecentsManager {

    public final static LinkedList<String> RECENTS = new LinkedList<>();
    private static final String RECENTS_DATABASE = ".omega-ide" + File.separator + ".recents";
    private Screen screen;

    public RecentsManager(Screen screen) {
        this.screen = screen;
        loadData();
    }

    public void loadData() {
        RECENTS.clear();
        File file = new File(RECENTS_DATABASE);
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String token = reader.readLine();
            while (token != null) {
                add(token);
                token = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveData() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(RECENTS_DATABASE));
            RECENTS.forEach((r) -> writer.println(r));
            writer.close();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void add(String path) {
        if (!RECENTS.contains(path)) {
            RECENTS.add(path);
            File file = new File(path);
            if (RECENTS.size() > 20) {
                if (file.isDirectory()) {
                    if (getNumberOfProjects() > 10)
                        removeFirstProject();
                    else if (getNumberOfFiles() > 10)
                        removeFirstFile();
                }
            }
        }
    }

    public static void removeAllProjects() {
        LinkedList<String> paths = new LinkedList<>();
        File file = null;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isDirectory())
                paths.add(path);
        }
        for (String path : paths) {
            RECENTS.remove(path);
        }
        Screen.getRecentsManager().saveData();
    }

    public static void removeAllFiles() {
        LinkedList<String> paths = new LinkedList<>();
        File file = null;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isFile())
                paths.add(path);
        }
        for (String path : paths) {
            RECENTS.remove(path);
        }
        Screen.getRecentsManager().saveData();
    }

    private static void removeFirstProject() {
        File file = null;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isDirectory())
                break;
        }
        if (file == null)
            return;
        RECENTS.remove(file.getAbsolutePath());
    }

    private static void removeFirstFile() {
        File file = null;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isFile())
                break;
        }
        if (file == null)
            return;
        RECENTS.remove(file.getAbsolutePath());
    }

    public static int getNumberOfProjects() {
        int n = 0;
        File file;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isDirectory())
                n++;
        }
        return n;
    }

    public static int getNumberOfFiles() {
        int n = 0;
        File file;
        for (String path : RECENTS) {
            file = new File(path);
            if (file.isFile())
                n++;
        }
        return n;
    }

}
