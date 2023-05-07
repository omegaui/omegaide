/*
 * Renders Markdown using commonmark-java
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

package omega.ui.panel;

import omega.Screen;
import omega.io.IconManager;
import omega.ui.component.Editor;
import omega.ui.window.EditorPreviewWindow;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

import static omega.io.UIManager.c2;
import static omega.io.UIManager.isDarkMode;

public class MarkdownPreviewPanel extends AbstractPreviewPanel {
    public JEditorPane textArea;
    public JScrollPane scrollPane;

    public static String GITHUB_MARKDOWN_CSS;

    public String lastText = "";
    public String lastParsedText = "";

    static {
        String text = "";
        try {
            Scanner reader = new Scanner(MarkdownPreviewPanel.class.getResourceAsStream("/github-markdown-" + (isDarkMode() ? "dark" : "light") + ".css"));
            while (reader.hasNextLine())
                text += reader.nextLine() + "\n";
            reader.close();
            GITHUB_MARKDOWN_CSS = "<style>\n" + text + "</style>";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MarkdownPreviewPanel() {
        setLayout(new BorderLayout());
        setBackground(c2);
        init();
    }

    @Override
    public boolean canCreatePreview(File file) {
        return file.getName().endsWith(".md");
    }

    @Override
    public void genPreview(Editor editor, EditorPreviewWindow previewWindow) {
        new Thread(() -> {
            File file = editor.currentFile;
            try {
                Screen.setStatus("Generating Preview for Markdown File " + file.getName(), 10, IconManager.fluentbookmarkImage);

                String text = editor.getText();
                String parsedText = lastParsedText;

                if (!lastText.equals(text)) {
                    lastText = text;
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(text);
                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                    parsedText = renderer.render(document);
                    parsedText = GITHUB_MARKDOWN_CSS + "\n" + parsedText;
                    textArea.setText(parsedText);
                }

                scrollPane.getVerticalScrollBar().setValue(0);

                previewWindow.setTitle("Viewing " + file.getName());

                Screen.setStatus(null, 100, null);
            } catch (Exception e) {
                Screen.setStatus("An Error Occured while generating preview for " + file.getName(), 10, IconManager.fluenterrorImage);
                e.printStackTrace();
            }
        }).start();
    }

    public void init() {
        textArea = new JEditorPane() {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        textArea.setBackground(c2);
        textArea.setEditorKit(new HTMLEditorKit());
        textArea.setEditable(false);

        add(scrollPane = new JScrollPane(textArea), BorderLayout.CENTER);

        scrollPane.setBorder(null);
    }
}
