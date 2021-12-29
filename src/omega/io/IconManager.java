/**
* IconManager
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

package omega.io;
import javax.imageio.ImageIO;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;

import java.awt.image.BufferedImage;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class IconManager {
	
	//Fluent Icon Pack
	public static BufferedImage fluentfolderImage = getFluentIcon("program-50.png");
	public static BufferedImage fluentfileImage = getFluentIcon("code-file-50.png");
	public static BufferedImage fluentsourcefileImage = getFluentIcon("source-code-set-96.png");
	public static BufferedImage fluentnewfolderImage = getFluentIcon("add-folder-50.png");
	public static BufferedImage fluentnewfileImage = getFluentIcon("add-file-50.png");
	public static BufferedImage fluentconsoleImage = getFluentIcon("console-50.png");
	public static BufferedImage fluenthelpImage = getFluentIcon("help-50.png");
	public static BufferedImage fluentinfoImage = getFluentIcon("info-popup-50.png");
	public static BufferedImage fluentsaveImage = getFluentIcon("save-50.png");
	public static BufferedImage fluentyoutubeImage = getFluentIcon("youtube-studio-50.png");
	public static BufferedImage fluentupdateImage = getFluentIcon("installing-updates-50.png");
	public static BufferedImage fluentclearImage = getFluentIcon("broom-50.png");
	public static BufferedImage fluentlaunchImage = getFluentIcon("launch-50.png");
	public static BufferedImage fluentrunImage = getFluentIcon("play-50.png");
	public static BufferedImage fluentbuildImage = getFluentIcon("hammer-50.png");
	public static BufferedImage fluentpackageImage = getFluentIcon("box-64.png");
	public static BufferedImage fluentstructureImage = getFluentIcon("structure-96.png");
	public static BufferedImage fluentsearchImage = getFluentIcon("inspect-code-50.png");
	public static BufferedImage fluentprojectstructureImage = getFluentIcon("file-explorer-96.png");
	public static BufferedImage fluentcloseImage = getFluentIcon("close-window-50.png");
	public static BufferedImage fluentclearSymbolImage = getFluentIcon("clear-symbol-48.png");
	public static BufferedImage fluentskipImage = getFluentIcon("skip-48.png");
	public static BufferedImage fluentpauseImage = getFluentIcon("pause-48.png");
	public static BufferedImage fluentsettingsImage = getFluentIcon("gear-50.png");
	public static BufferedImage fluentresourceImage = getFluentIcon("natural-food-48.png");
	public static BufferedImage fluentoutImage = getFluentIcon("out-48.png");
	public static BufferedImage fluentanyfileImage = getFluentIcon("file-64.png");
	public static BufferedImage fluentimagefileImage = getFluentIcon("panorama-96.png");
	public static BufferedImage fluentsadImage = getFluentIcon("sad-48.png");
	public static BufferedImage fluentdemonImage = getFluentIcon("demon-48.png");
	public static BufferedImage fluentpoisonImage = getFluentIcon("poison-48.png");
	public static BufferedImage fluentevilImage = getFluentIcon("evil-48.png");
	public static BufferedImage fluentbrokenbotImage = getFluentIcon("broken-robot-50.png");
	public static BufferedImage fluentwarningImage = getFluentIcon("warning-48.png");
	public static BufferedImage fluenterrorImage = getFluentIcon("error-30.png");
	public static BufferedImage fluentlevelupImage = getFluentIcon("up-3-50.png");
	public static BufferedImage fluentplainfolderImage = getFluentIcon("folder-50.png");
	public static BufferedImage fluenthomeImage = getFluentIcon("home-folder-48.png");
	public static BufferedImage fluentwebImage = getFluentIcon("web-shield-50.png");
	public static BufferedImage fluentwindowsImage = getFluentIcon("windows-10-50.png");
	public static BufferedImage fluentlinuxImage = getFluentIcon("penguin-50.png");
	public static BufferedImage fluentshellImage = getFluentIcon("run-command-96.png");
	public static BufferedImage fluentmacImage = getFluentIcon("apple-logo-50.png");
	public static BufferedImage fluentgradleImage = getFluentIcon("elephant-50.png");
	public static BufferedImage fluentsourceImage = getFluentIcon("source-code-50.png");
	public static BufferedImage fluentbinaryImage = getFluentIcon("venn-diagram-50.png");
	public static BufferedImage fluentlogImage = getFluentIcon("log-48.png");
	public static BufferedImage fluentemptylogImage = getFluentIcon("empty-log-48.png");
	public static BufferedImage fluentarchiveImage = getFluentIcon("archive-48.png");
	public static BufferedImage fluentxmlImage = getFluentIcon("xml-transformer-48.png");
	public static BufferedImage fluentmoduleImage = getFluentIcon("module-48.png");
	public static BufferedImage fluentbookmarkImage = getFluentIcon("bookmark-48.png");
	public static BufferedImage fluentemptyBoxImage = getFluentIcon("empty-box-96.png");
	public static BufferedImage fluentsparkleImage = getFluentIcon("sparkle-96.png");
	public static BufferedImage fluentwindRoseImage = getFluentIcon("wind-rose-96.png");
	
	public static BufferedImage fluentenergyImage = getFluentIcon("energy-64.png");
	public static BufferedImage fluentatomicImage = getFluentIcon("atomic-60.png");
	public static BufferedImage fluentpowerImage = getFluentIcon("power-60.png");
	public static BufferedImage fluentthunderboltImage = getFluentIcon("thuder-bolt-96.png");
	public static BufferedImage fluenttargetImage = getFluentIcon("target-100.png");
	public static BufferedImage fluenttabsHolderIcon = getFluentIcon("apps-tab-96.png");
	
	public static BufferedImage fluentcImage = getFluentIcon("c-programming-48.png");
	public static BufferedImage fluentcplusplusImage = getFluentIcon("c++-48.png");
	public static BufferedImage fluentdartImage = getFluentIcon("flutter-48.png");
	public static BufferedImage fluentgroovyImage = getFluentIcon("groovy-48.png");
	public static BufferedImage fluentjavaImage = getFluentIcon("java-48.png");
	public static BufferedImage fluentkotlinImage = getFluentIcon("kotlin-48.png");
	public static BufferedImage fluentpythonImage = getFluentIcon("python-48.png");
	public static BufferedImage fluentrustImage = getFluentIcon("rust-48.png");
	public static BufferedImage fluentjuliaImage = getFluentIcon("julia-48.png");
	public static BufferedImage fluentanylangImage = getFluentIcon("any-lang-48.png");
	
	public static BufferedImage fluentrsyntaxtextareaLogo = getFluentIcon("rsyntaxtextarea-logo.png");
	public static BufferedImage fluentjavaLogo = getFluentIcon("java-logo.png");
	public static BufferedImage fluentflatlafNonOfficialLogo = getFluentIcon("flatlaf.png");
	public static BufferedImage fluentapacheLogo = getFluentIcon("apache-logo.png");
	public static BufferedImage fluentjetbrainsLogo = getFluentIcon("jetbrains-logo.png");
	public static BufferedImage fluenticons8Logo = getFluentIcon("icons8-240.png");
	public static BufferedImage fluentgithubLogo = getFluentIcon("github-240.png");
	public static BufferedImage fluentjavamascotLogo = getFluentIcon("java-mascot-logo.png");
	public static BufferedImage fluentgoogleguavaLogo = getFluentIcon("google-guava-logo.png");
	
	public static BufferedImage fluentvariableImage = getFluentIcon("variable-48.png");
	public static BufferedImage fluentconstantImage = getFluentIcon("final-state-48.png");
	public static BufferedImage fluentvolatileImage = getFluentIcon("volatile-48.png");
	public static BufferedImage fluentsyncImage = getFluentIcon("sync-48.png");
	public static BufferedImage fluentmethodImage = getFluentIcon("method-48.png");
	public static BufferedImage fluentnewItemImage = getFluentIcon("new-item-48.png");
	
	public static BufferedImage fluentanimationImage = getFluentIcon("physics-96.png");
	
	public static BufferedImage fluentcommaImage = getFluentIcon("comma-50.png");
	public static BufferedImage fluentgasImage = getFluentIcon("gas-48.png");
	public static BufferedImage fluentrocketImage = getFluentIcon("rocket-64.png");
	public static BufferedImage fluentrocketbuildImage = getFluentIcon("rocket-build-64.png");
	public static BufferedImage fluentlightningboltImage = getFluentIcon("lightning-bolt-48.png");
	public static BufferedImage fluentspeedImage = getFluentIcon("speed-47.png");
	public static BufferedImage fluenttesttubeImage = getFluentIcon("test-tube-48.png");
	public static BufferedImage fluentquickmodeonImage = getFluentIcon("quick-mode-on-48.png");
	public static BufferedImage fluentphoenixImage = getFluentIcon("phoenix-48.png");
	public static BufferedImage fluentbriefImage = getFluentIcon("brief-48.png");
	public static BufferedImage fluentbulletedlistImage = getFluentIcon("bulleted-list-64.png");
	public static BufferedImage fluentusermanualImage = getFluentIcon("user-manual-64.png");
	public static BufferedImage fluentcolorwheelImage = getFluentIcon("color-wheel-2-48.png");
	public static BufferedImage fluentfocusImage = getFluentIcon("goal-48.png");
	public static BufferedImage fluentnormalScreenImage = getFluentIcon("normal-screen-48.png");
	
	public static BufferedImage fluentclassFileImage = getFluentIcon("class-48.png");
	public static BufferedImage fluentinterfaceFileImage = getFluentIcon("signal-48.png");
	public static BufferedImage fluentannotationFileImage = getFluentIcon("note-48.png");
	public static BufferedImage fluentenumFileImage = getFluentIcon("category-48.png");
	public static BufferedImage fluentrecordFileImage = getFluentIcon("burn-cd-48.png");
	public static BufferedImage fluentcategoryImage = getFluentIcon("categorize-48.png");
	public static BufferedImage fluentdesktopImage = getFluentIcon("imac-48.png");
	public static BufferedImage fluentcopyImage = getFluentIcon("copy-to-clipboard-48.png");
	public static BufferedImage fluenteditFileImage = getFluentIcon("edit-file-48.png");
	public static BufferedImage fluentrenameImage = getFluentIcon("rename-48.png");
	
	
	//The Default Icon Pack -- mixed
	public static BufferedImage newImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage projectImage = fluentfolderImage;
	public static BufferedImage fileImage = fluentfileImage;
	public static BufferedImage runImage = fluentrunImage;
	public static BufferedImage buildImage = fluentbuildImage;
	public static BufferedImage closeImage = fluentcloseImage;
	public static BufferedImage settingsImage = fluentsettingsImage;
	public static BufferedImage showImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage hideImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage infoImage = fluenthelpImage;
	public static BufferedImage classImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage recordImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage enumImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage interImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage annImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage packImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage methImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage varImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	public static BufferedImage ideImage64 = getImage("/omega_ide_icon64.png");
	public static BufferedImage ideImage500 = getImage("/omega_ide_icon500.png");
	static{
		paintNewImage(newImage.getGraphics());
		paintShowImage(showImage.getGraphics());
		paintHideImage(hideImage.getGraphics());
		paintCharImage(classImage.getGraphics(), "C");
		paintCharImage(recordImage.getGraphics(), "R");
		paintCharImage(enumImage.getGraphics(), "E");
		paintCharImage(interImage.getGraphics(), "I");
		paintCharImage(annImage.getGraphics(), "A");
		paintCharImage(packImage.getGraphics(), "P");
		paintCharImage(methImage.getGraphics(), "{}");
		paintCharImage(varImage.getGraphics(), "V");
	}
	
	public static void paintCharImage(Graphics graphics, String ch){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(TOOLMENU_COLOR4);
		g.fillRoundRect(0, 0, 16, 16, 5, 5);
		g.setColor(getForeground());
		g.setFont(PX16);
		g.drawString(ch, 8 - g.getFontMetrics().stringWidth(ch)/2, (16 - g.getFontMetrics().getHeight())/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
	}
	
	public static void paintHideImage(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(TOOLMENU_COLOR4);
		g.fillRoundRect(0, 0, 16, 16, 5, 5);
		g.setColor(getForeground());
		g.fillOval(4, 4, 8, 8);
		g.setColor(TOOLMENU_COLOR4);
		g.fillOval(6, 6, 6, 6);
	}
	
	public static void paintShowImage(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(TOOLMENU_COLOR3);
		g.fillRoundRect(0, 0, 16, 16, 5, 5);
		g.setColor(getForeground());
		g.fillOval(2, 2, 12, 12);
	}
	
	public static void paintNewImage(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(TOOLMENU_COLOR1);
		g.fillRoundRect(0, 0, 16, 16, 6, 6);
		g.setColor(getForeground());
		g.fillRect(8, 2, 1, 11);
		g.fillRect(3, 7, 11, 1);
	}
	
	public static Color getBackground(){
		return c3;
	}
	
	public static Color getForeground(){
		return c2;
	}
	
	public static final Icon show = new ImageIcon(showImage);
	public static final Icon hide = new ImageIcon(hideImage);
	
	private static final Icon getIcon(String path) {
		if(isDarkMode()) {
			path = path.substring(0, path.lastIndexOf('.'));
			path += "_dark.png";
		}
		try {
			BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
			return new ImageIcon(image);
	}catch(Exception e) {e.printStackTrace();}
		return null;
	}
	
	private static final BufferedImage getImage(String path) {
		if(isDarkMode()) {
			path = path.substring(0, path.lastIndexOf('.'));
			path += "_dark.png";
		}
		try {
			return ImageIO.read(IconManager.class.getResource(path));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final ImageIcon getImageIcon(String path) {
		if(isDarkMode() && !path.contains("Theme")) {
			path = path.substring(0, path.lastIndexOf('.'));
			path += "_dark.png";
		}
		try {
			BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
			return new ImageIcon(image);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage getFluentIcon(String name){
		try{
			return ImageIO.read(IconManager.class.getResourceAsStream("/fluent-icons/icons8-" + name));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

