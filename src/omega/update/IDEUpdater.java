package omega.update;

import static omega.comp.Animations.*;
import static omega.utils.UIManager.*;

import java.awt.Desktop;
import java.net.URL;
import java.util.Scanner;
import omega.Screen;
import omega.plugin.Downloader;
import omega.popup.NotificationPopup;
import omega.utils.IconManager;

public class IDEUpdater {

  public static final String RELEASE_FILE_URL =
    "https://raw.githubusercontent.com/omegaui/omegaide/main/.release";

  public static synchronized void checkForUpdate() {
    Screen.setStatus(
      "Checking for Update...",
      0,
      IconManager.fluentupdateImage
    );
    try (
      Scanner reader = new Scanner(Downloader.openStream(RELEASE_FILE_URL))
    ) {
      String version = reader.nextLine();
      double remoteVersionValue = Double.valueOf(version.substring(1));
      double currentVersionValue = Double.valueOf(Screen.VERSION.substring(1));
      if (remoteVersionValue <= currentVersionValue) {
        Screen.setStatus("No Updates Required!", 0, IconManager.ideImage64);
        NotificationPopup
          .create(Screen.getScreen())
          .size(350, 120)
          .title("Check for Update")
          .message("IDE is already up-to-date.", TOOLMENU_COLOR1)
          .shortMessage(
            "Visit project's github page to track changes!",
            TOOLMENU_COLOR4
          )
          .dialogIcon(IconManager.fluentupdateImage)
          .build()
          .locateOnBottomLeft()
          .showIt();
      } else if (remoteVersionValue > currentVersionValue) {
        Screen.setStatus("Update Avaliable!", 0, IconManager.ideImage64);
        NotificationPopup
          .create(Screen.getScreen())
          .size(350, 120)
          .title(reader.nextLine())
          .message(
            "Omega IDE " + version + " is avaliable now!",
            TOOLMENU_COLOR2
          )
          .shortMessage("Click to visit website!", TOOLMENU_COLOR1)
          .dialogIcon(IconManager.fluentupdateImage)
          .iconButton(
            IconManager.fluentwebImage,
            () -> {
              try {
                Desktop
                  .getDesktop()
                  .browse(
                    new URL("https://omegaui.github.io/omegaide").toURI()
                  );
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          )
          .build()
          .locateOnBottomLeft()
          .showIt();
      }
    } catch (Exception e) {
      Screen.setStatus(
        "Check your Internet Connection!",
        0,
        IconManager.fluentupdateImage
      );
      NotificationPopup
        .create(Screen.getScreen())
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
