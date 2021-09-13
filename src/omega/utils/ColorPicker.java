/**
  * ColorPicker
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
package omega.utils;
import java.awt.datatransfer.StringSelection;

import java.awt.geom.RoundRectangle2D;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import omega.comp.FlexPanel;
import omega.comp.TextComp;
import omega.comp.NoCaretField;

import javax.swing.JFrame;
import javax.swing.JComponent;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class ColorPicker extends JFrame{
	
	private Color color;
	private Color previewColor;
	private Color initialColor = TOOLMENU_COLOR3;

	private TextComp titleComp;
	
	private ColorComp redTileComp;
	private ColorComp greenTileComp;
	private ColorComp blueTileComp;

	private TextComp bigPreviewComp;
	
	private TextComp redValueLabel;
	private TextComp greenValueLabel;
	private TextComp blueValueLabel;
	private TextComp alphaValueLabel;

	private NoCaretField redValueField;
	private NoCaretField greenValueField;
	private NoCaretField blueValueField;
	private NoCaretField alphaValueField;
	
	private NoCaretField colorCodeField;

	private TextComp copyHexComp;
	private TextComp copyRgbComp;
	private TextComp closeComp;

	private int alphaValue = 255;
	
	public ColorPicker(){
		super("Color Picker");
		setUndecorated(true);
		setResizable(false);
		setSize(400, 320);
		setLocationRelativeTo(null);
		FlexPanel panel = new FlexPanel(null, c2, c2);
		panel.setArc(0, 0);
		setContentPane(panel);
		setLayout(null);
		setBackground(c2);
		setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
	}

	public void init(){
		titleComp = new TextComp("Color Picker", c2, c2, TOOLMENU_COLOR3, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);
		
		//Creating Color Tiles
		redTileComp = new ColorComp(Color.RED, 255, this::genPreview);
		redTileComp.setBounds(10, 40, 20, 256);
		add(redTileComp);
		
		greenTileComp = new ColorComp(Color.GREEN, 255, this::genPreview);
		greenTileComp.setBounds(35, 40, 20, 256);
		add(greenTileComp);
		
		blueTileComp = new ColorComp(Color.BLUE, 255, this::genPreview);
		blueTileComp.setBounds(60, 40, 20, 256);
		add(blueTileComp);

		bigPreviewComp = new TextComp("", c2, c2, c2, null){
			@Override
			public void draw(Graphics2D g){
				g.setColor(Color.WHITE);
				g.fillRoundRect(getWidth()/2 - 25, getHeight()/2 - 25, 50, 50, arcX, arcY);
				g.setColor(color1);
				g.fillRoundRect(getWidth()/2 - 25/2, getHeight()/2 - 25/2, 25, 25, arcX, arcY);
			}
		};
		bigPreviewComp.setBounds((getWidth() + 100)/2 - 60, 50, 100, 100);
		bigPreviewComp.setClickable(false);
		add(bigPreviewComp);

		redValueLabel = new TextComp("Red", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		redValueLabel.setBounds(100, 200, 80, 25);
		redValueLabel.setFont(PX14);
		redValueLabel.setArc(5, 5);
		redValueLabel.setClickable(false);
		add(redValueLabel);

		greenValueLabel = new TextComp("Green", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		greenValueLabel.setBounds(100, 230, 80, 25);
		greenValueLabel.setFont(PX14);
		greenValueLabel.setArc(5, 5);
		greenValueLabel.setClickable(false);
		add(greenValueLabel);

		blueValueLabel = new TextComp("Blue", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		blueValueLabel.setBounds(100, 260, 80, 25);
		blueValueLabel.setFont(PX14);
		blueValueLabel.setArc(5, 5);
		blueValueLabel.setClickable(false);
		add(blueValueLabel);

		alphaValueLabel = new TextComp("Alpha", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		alphaValueLabel.setBounds(100, 290, 80, 25);
		alphaValueLabel.setFont(PX14);
		alphaValueLabel.setArc(5, 5);
		alphaValueLabel.setClickable(false);
		add(alphaValueLabel);

		redValueField = new NoCaretField("255", "0", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		redValueField.setBounds(200, 200, 100, 25);
		redValueField.setFont(PX14);
		redValueField.setOnAction(()->{
			String text = redValueField.getText();
			try{
				int value = text.equals("") ? 0 : Integer.parseInt(text);
				if(value >= 0 && value <= 255)
					redTileComp.pointer = value;
				else
					value = 1/0;

				redTileComp.repaint();
				genPreview();
			}
			catch(Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		});
		add(redValueField);

		greenValueField = new NoCaretField("255", "0", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		greenValueField.setBounds(200, 230, 100, 25);
		greenValueField.setFont(PX14);
		greenValueField.setOnAction(()->{
			String text = greenValueField.getText();
			try{
				int value = text.equals("") ? 0 : Integer.parseInt(text);
				if(value >= 0 && value <= 255)
					greenTileComp.pointer = value;
				else
					value = 1/0;

				greenTileComp.repaint();
				genPreview();
			}
			catch(Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		});
		add(greenValueField);

		blueValueField = new NoCaretField("255", "0", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		blueValueField.setBounds(200, 260, 100, 25);
		blueValueField.setFont(PX14);
		blueValueField.setOnAction(()->{
			String text = blueValueField.getText();
			try{
				int value = text.equals("") ? 0 : Integer.parseInt(text);
				if(value >= 0 && value <= 255)
					blueTileComp.pointer = value;
				else
					value = 1/0;

				blueTileComp.repaint();
				genPreview();
			}
			catch(Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		});
		add(blueValueField);

		alphaValueField = new NoCaretField("255", "0", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		alphaValueField.setBounds(200, 290, 100, 25);
		alphaValueField.setFont(PX14);
		alphaValueField.setOnAction(()->{
			String text = alphaValueField.getText();
			try{
				int value = text.equals("") ? 0 : Integer.parseInt(text);
				if(value >= 0 && value <= 255)
					alphaValue = value;
				else
					value = 1/0;

				genPreview();
			}
			catch(Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		});
		add(alphaValueField);

		colorCodeField = new NoCaretField("#FFFFFF", TOOLMENU_COLOR4, c2, TOOLMENU_COLOR1);
		colorCodeField.setBounds(getWidth() - 100, 75, 100, 25);
		colorCodeField.setFont(PX14);
		colorCodeField.setOnAction(()->{
			try{
				initialColor = Color.decode(colorCodeField.getText());
				pickColor();
			}
			catch(Exception e){
				Toolkit.getDefaultToolkit().beep();
			}
		});
		add(colorCodeField);

		copyHexComp = new TextComp("Copy Hex", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\"#" + Integer.toHexString(previewColor.getRGB()).substring(2) + "\""), null);
		});
		copyHexComp.setBounds(getWidth() - 90, getHeight() - 90, 80, 25);
		copyHexComp.setFont(PX14);
		copyHexComp.setArc(5, 5);
		add(copyHexComp);

		copyRgbComp = new TextComp("Copy RGB", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(previewColor.getRed() + ", " + previewColor.getGreen() + ", " + previewColor.getBlue() + ", " + previewColor.getAlpha()), null);
		});
		copyRgbComp.setBounds(getWidth() - 90, getHeight() - 60, 80, 25);
		copyRgbComp.setFont(PX14);
		copyRgbComp.setArc(5, 5);
		add(copyRgbComp);
		
		closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
			dispose();
		});
		closeComp.setBounds(getWidth() - 90, getHeight() - 30, 80, 25);
		closeComp.setFont(PX14);
		closeComp.setArc(5, 5);
		add(closeComp);
	}

	public void genPreview(){
		previewColor = new Color(redTileComp.pointer, greenTileComp.pointer, blueTileComp.pointer, alphaValue);
		redValueField.setText(redTileComp.pointer + "");
		greenValueField.setText(greenTileComp.pointer + "");
		blueValueField.setText(blueTileComp.pointer + "");

		colorCodeField.setText("#" + Integer.toHexString(previewColor.getRGB()).substring(2));
		colorCodeField.repaint();
		
		bigPreviewComp.setColors(previewColor, previewColor, previewColor);
		bigPreviewComp.repaint();
		titleComp.setColors(c2, c2, previewColor);
		titleComp.repaint();
	}

	public Color pickColor(){
		this.color = initialColor;
		this.previewColor = initialColor;
		
		redTileComp.pointer = color.getRed();
		greenTileComp.pointer = color.getGreen();
		blueTileComp.pointer = color.getBlue();
		alphaValue = color.getAlpha();
		
		genPreview();
		
		redTileComp.repaint();
		greenTileComp.repaint();
		blueTileComp.repaint();
		alphaValueField.repaint();
		
		setVisible(true);
		return previewColor != null ? previewColor : initialColor;
	}

	private class ColorComp extends JComponent {
		int pointer;
		Color color;

		ColorComp(Color color, int p, Runnable r){
			this.color = color;
			this.pointer = p;
			addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					pointer = e.getY() < 0 ? 0 : e.getY();
					if(pointer > 255)
						pointer = 255;
					repaint();
					r.run();
				}
			});
			addMouseMotionListener(new MouseAdapter(){
				@Override
				public void mouseDragged(MouseEvent e){
					pointer = e.getY() < 0 ? 0 : e.getY();
					if(pointer > 255)
						pointer = 255;
					repaint();
					r.run();
				}
			});
		}

		@Override
		public void paint(Graphics graphics){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(c2);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
			g.setPaint(new GradientPaint(0, 0, Color.BLACK, getWidth(), getHeight(), color));
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
			g.setColor(c2);
			g.drawLine(0, pointer, getWidth(), pointer);
			g.dispose();
		}
	}
}
