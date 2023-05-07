/**
 * Checks for license agreement and Writes IDE resources.
 * Copyright (C) 2022 Omega UI
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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import omega.Screen;
import omega.plugin.management.PluginManager;
import omegaui.component.TextComp;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.modes.MarkdownTokenMaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import static omega.io.UIManager.*;

public class Startup extends JDialog {
    private static BufferedImage image;
    private TextComp closeBtn;
    private RSyntaxTextArea textArea;
    private TextComp acceptComp;
    private static String LICENSE_TEXT = "";

    public Startup(Screen screen) {
        super(screen, true);
        try {
            image = ImageIO.read(getClass().getResourceAsStream(isDarkMode() ? "/omega_ide_icon64_dark.png" : "/omega_ide_icon64.png"));
            Scanner reader = new Scanner(getClass().getResourceAsStream("/LICENSE"));
            while (reader.hasNextLine()) {
                LICENSE_TEXT += reader.nextLine() + "\n";
            }
            reader.close();
            LICENSE_TEXT += "\n**Copyright 2022 Omega UI. All Right Reserved.**\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUndecorated(true);
        setSize(650, 550);
        setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
        JPanel panel = new JPanel(null);
        panel.setBackground(c2);
        setContentPane(panel);
        setLayout(null);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setBackground(c2);
        init();
        setVisible(true);
    }

    public void init() {
        closeBtn = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, () -> System.exit(0));
        closeBtn.setBounds(getWidth() - 30, 0, 30, 30);
        closeBtn.setFont(PX18);
        closeBtn.setArc(0, 0);
        add(closeBtn);

        JScrollPane scrollPane = new JScrollPane(textArea = new RSyntaxTextArea(LICENSE_TEXT) {
            @Override
            public Color getForegroundForToken(Token token) {
                if (token.isIdentifier())
                    return TOOLMENU_COLOR3;
                return super.getForegroundForToken(token);
            }

            @Override
            public Font getFontForTokenType(int type) {
                if (type == Token.IDENTIFIER)
                    return PX14;
                return super.getFontForTokenType(type);
            }
        });
        scrollPane.setBounds(50, 100, getWidth() - 100, getHeight() - 200);
        scrollPane.setBackground(c2);

        try {
            if (isDarkMode())
                Theme.load(getClass().getResourceAsStream("/dark.xml")).apply(textArea);
            else
                Theme.load(getClass().getResourceAsStream("/idea.xml")).apply(textArea);
        } catch (Exception e) {
            e.printStackTrace();
        }

        textArea.setFont(PX14);
        textArea.setCaretPosition(0);
        textArea.setAntiAliasingEnabled(true);

        MarkdownTokenMaker.apply(textArea);

        textArea.setEditable(false);
        add(scrollPane);

        acceptComp = new TextComp("I Accept", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, () -> {
            try {
                new File(".omega-ide" + File.separator + ".firststartup").createNewFile();
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        acceptComp.setBounds(getWidth() / 2 - 50, getHeight() - 40, 100, 40);
        acceptComp.setFont(PX16);
        add(acceptComp);

        TextComp imageComp = new TextComp(image, 64, 64, c2, c2, c2, null);
        imageComp.setBounds(0, 0, 66, 66);
        imageComp.setClickable(false);
        add(imageComp);

        TextComp textComp = new TextComp("Omega IDE", c2, TOOLMENU_COLOR1, TOOLMENU_COLOR1, null);
        textComp.setBounds(getWidth() / 2 - 165, 0, 330, 50);
        textComp.setClickable(false);
        textComp.setFont(PX28);
        textComp.setArc(0, 0);
        add(textComp);

        TextComp licComp = new TextComp("license agreement", c2, TOOLMENU_COLOR2, TOOLMENU_COLOR2, () -> {
        });
        licComp.setBounds(getWidth() / 2 - 150, 50, 300, 30);
        licComp.setClickable(false);
        licComp.setFont(PX18);
        licComp.setArc(0, 0);
        add(licComp);
    }

    public static void checkStartup(Screen screen) {
        if (!new File(".omega-ide" + File.separator + ".firststartup").exists()) {
            Screen.pickTheme(AppDataManager.getTheme());
            Screen.getUIManager().loadData();
            try {
                if (UIManager.isDarkMode())
                    FlatDarkLaf.install();
                else
                    FlatLightLaf.install();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Startup(screen).repaint();
        }
    }

    public static void createBuildSpace() {
        File f = new File(".omega-ide", "buildspace");
        f.mkdir();
        f = new File(".omega-ide" + File.separator + "buildspace", "src");
        f.mkdir();
        f = new File(".omega-ide" + File.separator + "buildspace", "bin");
        f.mkdir();
    }

    public static void writeUIFiles() {
        File f = new File(".omega-ide");
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(".omega-ide", "out");
        if (!f.exists())
            f.mkdir();
        f = new File(".omega-ide", ".launch-scripts");
        if (!f.exists())
            f.mkdir();
        if (!new File(".omega-ide", ".ui").exists()) {
            omega.io.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".ui", ".omega-ide/.ui");
        }
        if (!new File(".omega-ide", ".preferences").exists()) {
            omega.io.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".preferences", ".omega-ide/.preferences");
        }
        if (!new File(".omega-ide", ".snippets").exists()) {
            omega.io.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".snippets", ".omega-ide/.snippets");
        }
        if (!new File(".omega-ide", ".processExecutionData").exists()) {
            omega.io.UIManager.loadDefaultFile(".omega-ide" + File.separator + ".processExecutionData", ".omega-ide/.processExecutionData");
        }
        f = new File(".omega-ide" + File.separator + "dictionary", "english_dic.zip");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            omega.io.UIManager.loadDefaultFile(".omega-ide" + File.separator + "dictionary" + File.separator + "english_dic.zip", ".omega-ide/dictionary/english_dic.zip");
        }
        createBuildSpace();
        if (!PluginManager.PLUGINS_DIRECTORY.exists())
            PluginManager.PLUGINS_DIRECTORY.mkdirs();
        f = new File(".omega-ide" + File.separator + ".generated-pty-native-libs");
        if (!f.exists()) {
            System.out.println("Writing Native Files for Terminal Emulation ...");
            System.out.println("It's a one time process!");

            System.out.println("> For Linux");
            loadDefaultFile("linux" + File.separator + "x86" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/linux/x86/libpty.so");
            loadDefaultFile("linux" + File.separator + "x86-64" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/linux/x86-64/libpty.so");

            System.out.println("> For FreeBSD");
            loadDefaultFile("freebsd" + File.separator + "x86" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/freebsd/x86/libpty.so");
            loadDefaultFile("freebsd" + File.separator + "x86-64" + File.separator + "libpty.so", ".omega-ide/pty4j-libs/freebsd/x86-64/libpty.so");

            System.out.println("> For MacOS");
            loadDefaultFile("darwin" + File.separator + "libpty.dylib", ".omega-ide/pty4j-libs/darwin/libpty.dylib");

            System.out.println("> For Windows(32-bit)");
            loadDefaultFile("win" + File.separator + "x86" + File.separator + "winpty.dll", ".omega-ide/pty4j-libs/win/x86/winpty.dll");
            loadDefaultFile("win" + File.separator + "x86" + File.separator + "winpty-agent.exe", ".omega-ide/pty4j-libs/win/x86/winpty-agent.exe");

            System.out.println("> For Windows(64-bit)");
            loadDefaultFile("win" + File.separator + "x86-64" + File.separator + "cyglaunch.exe", ".omega-ide/pty4j-libs/win/x86-64/cyglaunch.exe");
            loadDefaultFile("win" + File.separator + "x86-64" + File.separator + "win-helper.dll", ".omega-ide/pty4j-libs/win/x86-64/win-helper.dll");
            loadDefaultFile("win" + File.separator + "x86-64" + File.separator + "winpty.dll", ".omega-ide/pty4j-libs/win/x86-64/winpty.dll");
            loadDefaultFile("win" + File.separator + "x86-64" + File.separator + "winpty-agent.exe", ".omega-ide/pty4j-libs/win/x86-64/winpty-agent.exe");

            System.out.println("Writing Native Files for Terminal Emulation ... Done!");

            try {
                f.createNewFile();
            } catch (Exception e) {
                System.err.println("An Exception occured in generating the \".generated-pty-native-libs\" file.");
                System.err.println("This usually means that you are running the IDE in an non-owned directory");
                System.err.println("due to this the system denied permission for creating the file, ");
                System.err.println("try running the IDE in an owned directory (like your home folder). If still this exception is occuring then, ");
                System.err.println("Please Open an issue with this log message on https://github.com/omegaui/omegaide");
                System.err.println(e);
            }
            try {
                System.out.println("Writing Plugin List ...");
                f = new File(".omega-ide", ".pluginDB");
                f.createNewFile();
                System.out.println("Writing Plugin List ... Done!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        f = new File(".omega-ide", "git-scripts");
        if (!f.exists()) {
            System.out.println("Writing Shell Scripts ...");
            f.mkdir();

            System.out.println("> For Windows");
            writeGitShellScript("windows", "git_add.bat");
            writeGitShellScript("windows", "git_commit.bat");
            writeGitShellScript("windows", "git_gen_branch.bat");
            writeGitShellScript("windows", "git_init.bat");
            writeGitShellScript("windows", "git_push.bat");
            writeGitShellScript("windows", "git_setup_remote.bat");
            writeGitShellScript("windows", "git_switch_branch.bat");

            System.out.println("> For Unix");
            writeGitShellScript("unix", "git_add.sh");
            writeGitShellScript("unix", "git_commit.sh");
            writeGitShellScript("unix", "git_gen_branch.sh");
            writeGitShellScript("unix", "git_init.sh");
            writeGitShellScript("unix", "git_push.sh");
            writeGitShellScript("unix", "git_setup_remote.sh");
            writeGitShellScript("unix", "git_switch_branch.sh");

            System.out.println("Writing Shell Scripts ... Done!");
            if (!Screen.onWindows())
                System.out.println("Note: You must make the scripts located in .omega-ide/git-scripts/unix executable before using GitHubClientWindow.");
        }

        f = new File(".omega-ide", "file-templates");
        if (!f.exists()) {
            System.out.println("Creating File Template Storage Directory ...");
            f.mkdir();
            System.out.println("Creating File Template Storage Directory ... Done!");
        }

        System.out.println("Launching Omega IDE ...");
    }

    public static void writeGitShellScript(String platform, String script) {
        loadDefaultFile(".omega-ide" + File.separator + "git-scripts" + File.separator + platform + File.separator + script, ".omega-ide/git-scripts/" + platform + "/" + script);
    }

    public static void checkDirectory(String path) {
        File f = new File(path);
        if (!f.exists())
            f.mkdir();
    }
}

