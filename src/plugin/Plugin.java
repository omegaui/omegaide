package plugin;
/*
 * If you want to create a plugin for Omega IDE.
 * See the introductory PDF at "https://github.com/omegaui/omegaide-plugins"
*/

public interface Plugin {
     void enable(); // Called when the user enables the plugin
     void disable();// Called when the user disables the plugin
     void init(); // For initializations, called when IDE starts (not if the plugin is disabled)
     default ide.Screen getIDE(){// Returns IDE's instance which has access to every object in the application, you can either use this or ide.Screen.getScreen() both refers to same object
          return ide.Screen.getScreen();
     }
     /*plugin's information to be displayed in the plugin manager*/
     java.awt.image.BufferedImage getImage();// To get plugin's icon must be 32x32 pixels in size if no icon then return null
     java.util.LinkedList<java.awt.image.BufferedImage> getImages();// To get plugin's screenshots or working images if any else null
     String getName();// To get plugin's name
     String getVersion();// To get plugin's version, the version will be the version of IDE for which the plugin was initially written example jarpackager_v1.1-packager.Main-OMEGAIDE.jar 
     String getDescription();// To get plugin's description
     String getAuthor();// To get plugin's author name
     String getCopyright();// To get plugin's copyright details, usually a one line e.g Copyright (C) 2020 Omega UI. All Right Reserved 
}
