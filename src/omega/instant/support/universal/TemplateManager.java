/*
 * TemplateManager
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

package omega.instant.support.universal;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class TemplateManager {

    public synchronized static String getTemplateFilePath(String fileName) {
        if (fileName.contains(".")) {
            fileName = fileName.substring(fileName.lastIndexOf('.'));
        }
        return ".omega-ide" + File.separator + "file-templates" + File.separator + "template" + fileName;
    }

    public synchronized static boolean isTemplateAvailable(String fileName) {
        if (!fileName.contains(".")) {
            return false;
        }
        fileName = fileName.substring(fileName.lastIndexOf('.'));
        return new File(getTemplateFilePath(fileName)).exists();
    }

    public synchronized static String getTemplateText(String fileName) {
        if (fileName.contains(".")) {
            fileName = fileName.substring(fileName.lastIndexOf('.'));
        }
        String text = "";
        try (Scanner reader = new Scanner(new File(getTemplateFilePath(fileName)))) {
            while (reader.hasNextLine()) {
                text += reader.nextLine() + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public synchronized static boolean writeTemplateTo(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(getTemplateText(file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
