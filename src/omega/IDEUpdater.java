/*
 * IDEUpdater
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega;

import omega.io.IconManager;
import omega.plugin.Downloader;
import omega.ui.popup.NotificationPopup;

import java.awt.*;
import java.net.URL;
import java.util.Scanner;

import static omega.io.UIManager.*;

public class IDEUpdater {

    public static final String RELEASE_FILE_URL = "https://raw.githubusercontent.com/omegaui/omegaide/main/.release";

    public synchronized static void checkForUpdate() {
        Screen.setStatus("Checking for Update...", 0, IconManager.fluentupdateImage);
        try (Scanner reader = new Scanner(Downloader.openStream(RELEASE_FILE_URL))) {
            String version = reader.nextLine();
            double remoteVersionValue = Double.valueOf(version.substring(1));
            double currentVersionValue = Double.valueOf(Screen.VERSION.substring(1));
            if (remoteVersionValue <= currentVersionValue) {
                Screen.setStatus("No Updates Required!", 0, IconManager.ideImage64);
                NotificationPopup.create(Screen.getScreen())
                        .size(350, 120)
                        .title("Check for Update")
                        .message("IDE is already up-to-date.", TOOLMENU_COLOR1)
                        .shortMessage("Visit project's github page to track changes!", TOOLMENU_COLOR4)
                        .dialogIcon(IconManager.fluentupdateImage)
                        .build()
                        .locateOnBottomLeft()
                        .showIt();
            } else if (remoteVersionValue > currentVersionValue) {
                Screen.setStatus("Update Avaliable!", 0, IconManager.ideImage64);
                NotificationPopup.create(Screen.getScreen())
                        .size(350, 120)
                        .title(reader.nextLine())
                        .message("Omega IDE " + version + " is avaliable now!", TOOLMENU_COLOR2)
                        .shortMessage("Click to visit website!", TOOLMENU_COLOR1)
                        .dialogIcon(IconManager.fluentupdateImage)
                        .iconButton(IconManager.fluentwebImage, () -> {
                            try {
                                Desktop.getDesktop().browse(new URL("https://omegaui.github.io/omegaide").toURI());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .build()
                        .locateOnBottomLeft()
                        .showIt();
            }
        } catch (Exception e) {
            Screen.setStatus("Check your Internet Connection!", 0, IconManager.fluentupdateImage);
            NotificationPopup.create(Screen.getScreen())
                    .size(350, 120)
                    .title("Check for Update")
                    .message("Unable to read Remote Release Data!", TOOLMENU_COLOR2)
                    .shortMessage("Check your Internet Connection.", TOOLMENU_COLOR4)
                    .dialogIcon(IconManager.fluentupdateImage)
                    .build()
                    .locateOnBottomLeft()
                    .showIt();
            e.printStackTrace();
        }
    }
}
