package plugin;
/*
 * If you want to create a plugin for Omega IDE.
 * See the introductory video
*/

public interface Plugin {
     void enable(); // Called when the user enables the plugin
     void disable();// Called when the user disables the plugin
     void init(); // For initializations, called when IDE starts (not if the plugin is disabled)
     default ide.Screen getIDE(){// Returns IDE's instance which has access to every object in the application, for finding what it can access see access.txt
          return Omega.IDE.screen;
     }
     /*plugin's information to be displayed in the plugin manager*/
     java.awt.image.BufferedImage getImage();// To get plugin's icon must be 32x32 pixels in size 
     java.util.LinkedList<java.awt.image.BufferedImage> getImages();// To get plugin's screenshots or working images if any
     String getName();// To get plugin's name
     String getVersion();// To get plugin's version e.g v1.1, vRolling
     String getDescription();// To get plugin's description
     String getAuthor();// To get plugin's author name
     String getCopyright();// To get plugin's copyright details
}
