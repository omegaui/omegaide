/**
 * Common FileUtils.
 * @author: omegaui
 * Copyright (C) 2023 Omega UI

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

package omegaui.io.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * omegaui.io.storage.FileUtils
 * Contains common file utility methods.
 */
public class FileUtils {
    /**
     * Constructs a path by joining nodes.
     * @param paths array of path
     * @return path
     */
    public static String join(String... paths){
        StringBuilder result = new StringBuilder();
        for(String path : paths){
            result.append(path).append(File.separator);
        }
        return result.substring(0, result.length() - 1);
    }
    /**
     * Reads and returns the content of a text file.
     * @param file File Object
     * @return String contents
     */
    public static String read(File file){
        if(!file.exists()){
            return null;
        }
        String content = "";
        try (Scanner reader = new Scanner(file)) {
            while(reader.hasNextLine()){
                content += reader.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
    /**
     * Writes content to a text file.
     * Returns true on successful write operation.
     * @param file File Object
     * @param content content of the file
     * @return boolean
     */
    public static boolean write(File file, String content){
        File parent = file.getParentFile();
        if(parent != null && !parent.exists()) {
            if(!parent.mkdirs()){
                System.err.println("Cannot construct the parent directories: " + file);
                return false;
            }
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(content);
        } catch (FileNotFoundException e) {
            System.err.println("File not Found: " + file);
            return false;
        }
        return true;
    }
}
