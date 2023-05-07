/**
 * The About Section
 * Copyright (C) 2021 Omega UI
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

package omega.ui.dialog;

import omega.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import static omega.io.UIManager.*;

public class InfoScreen extends JDialog {

    private String title = "Omega IDE";
    private String version = Screen.VERSION;
    private String h1 = "omegaui";
    private String h2 = "github.com/omegaui/omegaide";
    private String p1 = "an instant IDE from the future";

    private static GradientPaint gradient = new GradientPaint(0, 0, back1, 300, 300, back3);
    private static GradientPaint gradient1 = new GradientPaint(100, 150, TOOLMENU_COLOR2, 300, 300, TOOLMENU_COLOR3);

    private BufferedImage image;

    public InfoScreen(Screen screen) {
        super(screen);
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Ubuntu-Bold.ttf")));
        } catch (Exception e) {
            System.err.println(e);
        }

        setUndecorated(true);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 80, 80));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dispose();
            }
        });

        try {
            String ext = isDarkMode() ? "_dark.png" : ".png";
            image = ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon128" + ext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setPaint(gradient);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);

        g.drawImage(image, getWidth() / 2 - 64, 30, 128, 128, null);

        g.setFont(PX26);
        g.setPaint(gradient1);
        g.drawString(title, getWidth() / 2 - g.getFontMetrics().stringWidth(title) / 2, 160 + g.getFontMetrics().getAscent());

        g.setColor(TOOLMENU_COLOR1);
        g.setFont(PX16);
        g.drawString(version, getWidth() / 2 - g.getFontMetrics().stringWidth(version) / 2, 190 + g.getFontMetrics().getAscent());

        g.setFont(PX14);
        g.setColor(glow);
        g.drawString(h1, getWidth() / 2 - g.getFontMetrics().stringWidth(h1) / 2, 220 + g.getFontMetrics().getAscent());
        g.drawString(h2, getWidth() / 2 - g.getFontMetrics().stringWidth(h2) / 2, 240 + g.getFontMetrics().getAscent());

        g.setColor(TOOLMENU_COLOR5);
        g.drawString(p1, getWidth() / 2 - g.getFontMetrics().stringWidth(p1) / 2, 260 + g.getFontMetrics().getAscent());
    }
}

