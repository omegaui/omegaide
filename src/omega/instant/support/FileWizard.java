/*
 * The FileWizard
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

package omega.instant.support;

import omega.Screen;
import omega.instant.support.universal.TemplateManager;
import omega.io.IconManager;
import omega.ui.dialog.FileSelectionDialog;
import omegaui.component.EdgeComp;
import omegaui.component.TextComp;
import omegaui.listener.KeyStrokeListener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_UP;
import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class FileWizard extends JDialog {

    private TextComp iconComp;

    private JTextField nameField;
    private TextComp typeComp;
    private EdgeComp typeLabel;

    private TextComp createComp;
    private TextComp closeComp;

    public TextComp parentRoot;
    public TextComp typeBtn;

    public TextComp directoryComp;
    public TextComp classComp;
    public TextComp recordComp;
    public TextComp interfaceComp;
    public TextComp enumComp;
    public TextComp annotationComp;
    public TextComp customFileComp;

    public FileWizard(JFrame f) {
        super(f, true);
        setLayout(null);
        setUndecorated(true);
        setSize(400, 120);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(null);
        panel.setBackground(back2);
        setContentPane(panel);
        setLayout(null);
        setBackground(back2);
        setResizable(false);
        init();
    }

    private void init() {
        iconComp = new TextComp(IconManager.fluentnewItemImage, 48, 48, back2, c2, c2, null);
        iconComp.setBounds(5, 5, 50, 50);
        iconComp.setArc(5, 5);
        iconComp.setClickable(false);
        iconComp.attachDragger(this);
        iconComp.setEnter(true);
        add(iconComp);

        final FileSelectionDialog fileC = new FileSelectionDialog(this);
        fileC.setTitle("Select a directory as source root");

        parentRoot = new TextComp(IconManager.fluentsourceImage, 20, 20, TOOLMENU_COLOR3_SHADE, back2, c2, () -> {
            LinkedList<File> selections = fileC.selectDirectories();
            if (!selections.isEmpty()) {
                parentRoot.setToolTipText(selections.get(0).getAbsolutePath());
            }
        });
        parentRoot.setBounds(5, getHeight() - 35, 30, 30);
        parentRoot.setArc(5, 5);
        add(parentRoot);

        nameField = new JTextField();
        nameField.setBackground(c2);
        nameField.setCaretColor(TOOLMENU_COLOR2);
        nameField.setForeground(TOOLMENU_COLOR3);
        nameField.setBounds(65, 5, getWidth() - 100, 30);
        nameField.setFont(PX14);
        nameField.addActionListener((e) -> createComp.runnable.run());
        KeyStrokeListener typeNavigationListener = new KeyStrokeListener(nameField);
        typeNavigationListener.putKeyStroke((e) -> {
            String currentType = typeBtn.getText();
            currentType = switch (currentType) {
                case "directory" -> "class";
                case "class" -> "record";
                case "record" -> "interface";
                case "interface" -> "@interface";
                case "@interface" -> "enum";
                case "enum" -> "Custom File";
                default -> "directory";
            };
            typeBtn.setText(currentType);
        }, VK_DOWN);
        typeNavigationListener.putKeyStroke((e) -> {
            String currentType = typeBtn.getText();
            currentType = switch (currentType) {
                case "class" -> "directory";
                case "record" -> "class";
                case "interface" -> "record";
                case "@interface" -> "interface";
                case "enum" -> "@interface";
                case "Custom File" -> "enum";
                default -> "Custom File";
            };
            typeBtn.setText(currentType);
        }, VK_UP);
        nameField.addKeyListener(typeNavigationListener);
        add(nameField);

        typeLabel = new EdgeComp("Type", back2, TOOLMENU_GRADIENT, glow, null);
        typeLabel.setBounds(65, 45, 80, 25);
        typeLabel.setFont(UBUNTU_PX14);
        typeLabel.setLookLikeLabel(true);
        add(typeLabel);

        createComp = new TextComp(IconManager.fluentnewfileImage, 25, 25, "Create", TOOLMENU_COLOR3_SHADE, back2, c2, this::create);
        createComp.setBounds(getWidth() - 35, getHeight() - 35, 30, 30);
        createComp.setArc(5, 5);
        add(createComp);

        closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, back2, back2, c2, this::dispose);
        closeComp.setBounds(getWidth() - 35, 5, 30, 30);
        closeComp.setArc(5, 5);
        add(closeComp);

        typeBtn = new TextComp("class", back2, back2, TOOLMENU_COLOR1, null) {
            @Override
            public void draw(Graphics2D g) {
                g.drawImage(IconManager.fluentcategoryImage, 0, 0, 25, 25, null);
            }
        };
        typeBtn.setBounds(150, typeLabel.getY(), 140, 25);
        typeBtn.setClickable(false);
        typeBtn.alignX = 30;
        typeBtn.setFont(PX14);
        typeBtn.setArc(0, 0);
        add(typeBtn);

        directoryComp = new TextComp(IconManager.fluentfolderImage, 25, 25, "Directory", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("directory"));
        directoryComp.setBounds(75, parentRoot.getY(), 30, 30);
        directoryComp.setArc(5, 5);
        add(directoryComp);

        classComp = new TextComp(IconManager.fluentclassFileImage, 25, 25, "Class", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("class"));
        classComp.setBounds(directoryComp.getX() + directoryComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        classComp.setArc(5, 5);
        add(classComp);

        recordComp = new TextComp(IconManager.fluentrecordFileImage, 25, 25, "Record", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("record"));
        recordComp.setBounds(classComp.getX() + classComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        recordComp.setArc(5, 5);
        add(recordComp);

        interfaceComp = new TextComp(IconManager.fluentinterfaceFileImage, 25, 25, "Interface", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("interface"));
        interfaceComp.setBounds(recordComp.getX() + recordComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        interfaceComp.setArc(5, 5);
        add(interfaceComp);

        annotationComp = new TextComp(IconManager.fluentannotationFileImage, 25, 25, "Annotation", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("@interface"));
        annotationComp.setBounds(interfaceComp.getX() + interfaceComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        annotationComp.setArc(5, 5);
        add(annotationComp);

        enumComp = new TextComp(IconManager.fluentenumFileImage, 25, 25, "Enum", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("enum"));
        enumComp.setBounds(annotationComp.getX() + annotationComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        enumComp.setArc(5, 5);
        add(enumComp);

        customFileComp = new TextComp(IconManager.fileImage, 25, 25, "Custom File", TOOLMENU_COLOR3_SHADE, back2, c2, () -> typeBtn.setText("Custom File"));
        customFileComp.setBounds(enumComp.getX() + enumComp.getWidth() + 5, parentRoot.getY(), 30, 30);
        customFileComp.setArc(5, 5);
        add(customFileComp);

        putAnimationLayer(iconComp, getImageSizeAnimationLayer(25, -6, true), ACTION_MOUSE_PRESSED);
        putAnimationLayer(parentRoot, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(createComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(closeComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(directoryComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(classComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(recordComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(interfaceComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(annotationComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(enumComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(customFileComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
    }

    public void create() {
        if (parentRoot.getToolTipText() == null) return;
        if (!new File(parentRoot.getToolTipText()).exists()) {
            nameField.setText("The Root Directory Does not exists");
            return;
        }
        String type = typeBtn.getText();
        if (type.equals("directory")) {
            File dir = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
            dir.mkdir();
            Screen.getProjectFile().getFileTreePanel().refresh();
            return;
        }
        if (!type.equals("Custom File")) {
            String text = nameField.getText();
            if (!text.contains(".")) {
                nameField.setText("Specify a package as pack.Class");
                return;
            }
            final String PATH = text.substring(0, text.lastIndexOf('.'));
            String path = PATH;
            StringTokenizer tokenizer = new StringTokenizer(path, ".");
            path = parentRoot.getToolTipText() + File.separator;
            while (tokenizer.hasMoreTokens()) {
                path += tokenizer.nextToken() + File.separator;
                File file = new File(path);
                if (!file.exists())
                    file.mkdir();
            }

            dispose();

            File src = new File(path + text.substring(text.lastIndexOf('.') + 1) + ".java");
            createSRCFile(src, type, PATH, text.substring(text.lastIndexOf('.') + 1));
        } else {
            File file = new File(parentRoot.getToolTipText() + File.separator + nameField.getText());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    if (TemplateManager.isTemplateAvailable(nameField.getText())) {
                        if (!TemplateManager.writeTemplateTo(file)) {
                            System.err.println("Failed to write Template to " + file);
                        }
                    }
                } catch (Exception ex) {
                    nameField.setText("Access Denied");
                    ex.printStackTrace();
                }
                try {
                    Screen.getProjectFile().getFileTreePanel().refresh();
                    Screen.getScreen().loadFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                nameField.setText("File Already Exists");
        }
    }

    public void show(String type) {
        typeBtn.setText(type);
        if (Screen.getProjectFile().getProjectPath() != null && new File(Screen.getProjectFile().getProjectPath()).exists())
            parentRoot.setToolTipText(Screen.getProjectFile().getProjectPath() + File.separator + "src");
        setVisible(true);
    }

    public static void createSRCFile(File file, String type, String pack, String name) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            String header = type;
            if (!header.equals("")) {
                writer.println("package " + pack + ";");
                writer.println("public " + header + " " + name + " {\n}");
            }
            writer.close();
            omega.Screen.getScreen().loadFile(file);
            Screen.getProjectFile().getFileTreePanel().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        typeBtn.repaint();
    }
}
