/**
  * The Default File Selection Dialog of Omega IDE
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
import java.awt.geom.RoundRectangle2D;

import omega.tree.Branch;

import omega.Screen;

import java.awt.image.BufferedImage;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import java.util.LinkedList;

import omega.comp.TextComp;
import omega.comp.NoCaretField;
import omega.comp.ToggleComp;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;

import static omega.utils.UIManager.*;
public class FileSelectionDialog extends JDialog{

     private TextComp titleComp;
     private TextComp selectComp;
     private TextComp cancelComp;
     private JScrollPane scrollPane;
     private JPanel panel;
     private NoCaretField selectionField;
     private TextComp levelComp;
     private TextComp homeComp;

     private LinkedList<TextComp> items = new LinkedList<>();
     private LinkedList<File> selections = new LinkedList<>();

     public static final String ALL_EXTENSIONS = ".*";

     private int state = 0;

     public volatile boolean allowDirectories = true;
     

     private File currentDir = new File(System.getProperty("user.home"));
     private String[] extensions;

     private int pressX;
     private int pressY;

     private int block = 0;
     
     public FileSelectionDialog(JFrame f){
          super(f, true);
          setUndecorated(true);
          JPanel panel = new JPanel(null);
          panel.setBackground(c2);
          setContentPane(panel);
          setSize(500, 400);
          setLocationRelativeTo(null);
          setBackground(c2);
          setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
          setLayout(null);
          init();
     }

     public FileSelectionDialog(JDialog d){
     	super(d, true);
          setUndecorated(true);
          JPanel panel = new JPanel(null);
          panel.setBackground(c2);
          setContentPane(panel);
          setSize(500, 400);
          setLocationRelativeTo(null);
          setBackground(c2);
          setLayout(null);
          init();
     }

     public void init(){
          titleComp = new TextComp("", back2, back2, glow, null);
          titleComp.setBounds(0, 0, getWidth() - 50 - 60, 30);
          titleComp.setFont(PX14);
          titleComp.setClickable(false);
          titleComp.setArc(0, 0);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
                    pressX = e.getX();
                    pressY = e.getY();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
                    setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);

          cancelComp = new TextComp("Cancel", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
               selections.clear();
               dispose();
          });
          cancelComp.setBounds(getWidth() - 50, 0, 50, 30);
          cancelComp.setFont(PX14);
          cancelComp.setArc(0, 0);
          add(cancelComp);

          levelComp = new TextComp(IconManager.fluentlevelupImage, 25, 25, "Move One Level Up", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->{
               if(currentDir == null)
                    return;
               String path = currentDir.getAbsolutePath();
               if(File.pathSeparator.equals(":") && !path.equals("/")){
                    if(count('/', path) != 1)
                         currentDir = new File(path.substring(0, path.lastIndexOf('/')));
                    else 
                         currentDir = new File("/");
               }
               else if(File.pathSeparator.equals(";")){
                    if(path.contains("\\"))
                         currentDir = new File(path.substring(0, path.lastIndexOf('\\')));
                    else{
                         items.forEach(panel::remove);
                         items.clear();

                         block = 0;
                    }
               }
               if(state == 0)
                    selectFiles();
               else if(state == 1)
                    selectDirectories();
               else if(state == 2)
                    selectFilesAndDirectories();
          });
          levelComp.setBounds(getWidth() - 50 - 60, 0, 30, 30);
          levelComp.setFont(PX14);
          levelComp.setArc(0, 0);
          add(levelComp);

          homeComp = new TextComp(IconManager.fluenthomeImage, 25, 25, "Go Home", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->{
               currentDir = new File(System.getProperty("user.home"));
               if(state == 0)
                    selectFiles();
               else if(state == 1)
                    selectDirectories();
               else if(state == 2)
                    selectFilesAndDirectories();
          });
          homeComp.setBounds(getWidth() - 50 - 30, 0, 30, 30);
          homeComp.setFont(PX14);
          homeComp.setArc(0, 0);
          add(homeComp);

          selectionField = new NoCaretField("", "or Enter the path manually and hit enter", TOOLMENU_COLOR2, back2, TOOLMENU_COLOR3);
          selectionField.setBounds(0, getHeight() - 30, getWidth() - 50, 30);
          selectionField.setOnAction(()->{
               File file = new File(selectionField.getText());
               if(file.exists()) {
                    if(state == 0){
                         if(file.isDirectory()){
                              currentDir = file;
                              selectFiles();
                         }
                         else{
                              selections.add(file);
                              dispose();
                         }
                    }
                    else if(state == 1){
                         if(file.isDirectory()){
                              selections.add(file);
                              dispose();
                         }
                         else
                              selectionField.notify("Directory Expected!");
                    }
                    else if(state == 2){
                         selections.add(file);
                         dispose();
                    }
               }
               else{
               	Toolkit.getDefaultToolkit().beep();
               }
          });
          selectionField.setFont(PX14);
          add(selectionField);
          addKeyListener(selectionField);

          selectComp = new TextComp("Done", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR3, this::dispose);
          selectComp.setBounds(getWidth() - 50, getHeight() - 30, 50, 30);
          selectComp.setFont(PX14);
          selectComp.setArc(0, 0);
          add(selectComp);

          scrollPane = new JScrollPane(panel = new JPanel(null){
               GradientPaint paint = new GradientPaint(0, 0, TOOLMENU_COLOR2, 500, 310, TOOLMENU_COLOR3);
               String hint = "No Content to Display";
               @Override
               public void paint(Graphics graphics){
                    if(items.isEmpty()){
                         Graphics2D g = (Graphics2D)graphics;
                         g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                         g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                         g.setColor(c2);
                         g.fillRect(0, 0, getWidth(), getHeight());
                         g.setPaint(paint);
                         g.setFont(PX20);
                         g.drawString(hint, getWidth()/2 - g.getFontMetrics().stringWidth(hint)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
                    }
                    else
                         super.paint(graphics);
               }
          });
          scrollPane.setBounds(0, 30, getWidth(), getHeight() - 60);
          scrollPane.setBackground(c2);
          scrollPane.setBorder(null);
          add(scrollPane);
          panel.setBackground(c2);
          panel.setPreferredSize(new Dimension(490, 290));
          panel.setBorder(null);
     }

	@Override
     public void setTitle(String title){
     	super.setTitle(title);
          titleComp.setText(title);
     }

     public void setFileExtensions(String... extensions){
          this.extensions = extensions;
     }

     public boolean isDirectoriesAllowed() {
          return allowDirectories;
     }
     
     public void setAllowDirectories(boolean allowDirectories) {
          this.allowDirectories = allowDirectories;
     }

     public void setCurrentDirectory(File dir){
          if(!dir.exists())
               return;
          currentDir = dir;
     }

     public boolean isExtentionAllowed(File file){
          if(extensions == null || extensions.length == 0 || extensions[0].equals(ALL_EXTENSIONS))
               return true;
          if(file.isDirectory() && isDirectoriesAllowed())
               return true;
          for(String ext : extensions){
               if(file.getName().endsWith(ext))
                    return true;
          }
          return false;
     }

     public LinkedList<File> selectFiles(){
          state = 0;
          selections.clear();
          items.forEach(panel::remove);
          items.clear();
          block = 0;

          if(currentDir == null)
               currentDir = new File(System.getProperty("user.home"));

          File[] F = currentDir.listFiles();
          if(F == null || F.length == 0)
               return selections;

          LinkedList<File> files = new LinkedList<>();
          for(File f : F)
               files.add(f);

          F = null;
          sort(files);

          Color c1 = null;
          Color c2 = UIManager.c2;
          Color c3 = null;
          for(File file : files){
               if(isExtentionAllowed(file)){
                    if(file.isDirectory()){
                         c1 = TOOLMENU_COLOR1_SHADE;
                         c3 = TOOLMENU_COLOR1;
                    }
                    else{
                         c1 = TOOLMENU_COLOR2_SHADE;
                         c3 = TOOLMENU_COLOR2;
                    }
                    String meta = (file.listFiles() != null && file.listFiles().length != 0) ? "" : " - Empty";
                    if(!file.isDirectory())
                         meta = "";
                    ToggleComp comp = new ToggleComp(Branch.getPreferredImage(file), 25, 25, file.getName() + meta, c1, c2, c3, false);
                    if(!file.isDirectory()){
                         comp.setOnToggle((value)->{
                              comp.setColors(comp.color1, comp.color3, comp.color2);
                              if(value)
                                   selections.add(file);
                              else
                                   selections.remove(file);
                         });
                    }
                    else
                         comp.toggleEnabled = false;
                    comp.setBounds(0, block, 490, 25);
                    if(file.isDirectory()){
                         comp.addMouseListener(new MouseAdapter(){
                              @Override
                              public void mousePressed(MouseEvent e){
                                   if(file.isDirectory() && file.listFiles() != null && file.listFiles().length != 0){
                                        currentDir = file;
                                        selectFiles();
                                   }
                              }
                         });
                    }
                    comp.setFont(PX14);
                    comp.setArc(0, 0);
                    panel.add(comp);
                    items.add(comp);
                    block += 25;
               }
          }
          files.clear();
          panel.setPreferredSize(new Dimension(490, block < 290 ? 290 : block));
          triggerRepaint();
          setVisible(true);
          return selections;
     }
     
     public LinkedList<File> selectDirectories(){
          state = 1;
          setFileExtensions(ALL_EXTENSIONS);
          selections.clear();
          items.forEach(panel::remove);
          items.clear();
          block = 0;

          if(currentDir == null)
               currentDir = new File(System.getProperty("user.home"));

          File[] F = currentDir.listFiles();
          if(F == null || F.length == 0)
               return selections;

          LinkedList<File> files = new LinkedList<>();
          for(File f : F)
               files.add(f);

          F = null;
          sort(files);

          Color c1 = null;
          Color c2 = UIManager.c2;
          Color c3 = null;
          for(File file : files){
               if(isExtentionAllowed(file)){
                    if(file.isDirectory()){
                         c1 = TOOLMENU_COLOR1_SHADE;
                         c3 = TOOLMENU_COLOR1;
                    }
                    else{
                         c1 = TOOLMENU_COLOR2_SHADE;
                         c3 = TOOLMENU_COLOR2;
                    }
                    String meta = (file.listFiles() != null && file.listFiles().length != 0) ? "" : " - Empty";
                    if(!file.isDirectory())
                         meta = "";
                    ToggleComp comp = new ToggleComp(Branch.getPreferredImage(file), 25, 25, file.getName() + meta, c1, c2, c3, false);
                    if(file.isDirectory()){
                         comp.setOnToggle((value)->{
                              comp.setColors(comp.color1, comp.color3, comp.color2);
                              if(value)
                                   selections.add(file);
                              else
                                   selections.remove(file);
                         });
                    }
                    else
                         comp.toggleEnabled = false;
                    comp.setBounds(0, block, 490, 25);
                    if(file.isDirectory()){
                         comp.addMouseListener(new MouseAdapter(){
                              @Override
                              public void mousePressed(MouseEvent e){
                                   if(e.getButton() == 1 && e.getClickCount() == 2 && file.isDirectory() && file.listFiles() != null && file.listFiles().length != 0){
                                        currentDir = file;
                                        selectDirectories();
                                   }
                              }
                         });
                    }
                    comp.setFont(PX14);
                    comp.setArc(0, 0);
                    panel.add(comp);
                    items.add(comp);
                    block += 25;
               }
          }
          files.clear();
          panel.setPreferredSize(new Dimension(490, block < 290 ? 290 : block));
          triggerRepaint();
          setVisible(true);
          return selections;
     }
     
     public LinkedList<File> selectFilesAndDirectories(){
          state = 2;
          selections.clear();
          items.forEach(panel::remove);
          items.clear();
          block = 0;

          if(currentDir == null)
               currentDir = new File(System.getProperty("user.home"));

          File[] F = currentDir.listFiles();
          if(F == null || F.length == 0)
               return selections;

          LinkedList<File> files = new LinkedList<>();
          for(File f : F)
               files.add(f);

          F = null;
          sort(files);

          Color c1 = null;
          Color c2 = UIManager.c2;
          Color c3 = null;
          for(File file : files){
               if(isExtentionAllowed(file)){
                    if(file.isDirectory()){
                         c1 = TOOLMENU_COLOR1_SHADE;
                         c3 = TOOLMENU_COLOR1;
                    }
                    else{
                         c1 = TOOLMENU_COLOR2_SHADE;
                         c3 = TOOLMENU_COLOR2;
                    }
                    String meta = (file.listFiles() != null && file.listFiles().length != 0) ? "" : " - Empty";
                    if(!file.isDirectory())
                         meta = "";
                    ToggleComp comp = new ToggleComp(Branch.getPreferredImage(file), 25, 25, file.getName() + meta, c1, c2, c3, false);
                    comp.setOnToggle((value)->{
                         comp.setColors(comp.color1, comp.color3, comp.color2);
                         if(value)
                              selections.add(file);
                         else
                              selections.remove(file);
                    });
                    comp.setBounds(0, block, 490, 25);
                    if(file.isDirectory()){
                         comp.addMouseListener(new MouseAdapter(){
                              @Override
                              public void mousePressed(MouseEvent e){
                                   if(e.getButton() == 1 && e.getClickCount() == 2 && file.isDirectory() && file.listFiles() != null && file.listFiles().length != 0){
                                        currentDir = file;
                                        selectFilesAndDirectories();
                                   }
                              }
                         });
                    }
                    comp.setFont(PX14);
                    comp.setArc(0, 0);
                    panel.add(comp);
                    items.add(comp);
                    block += 25;
               }
          }
          files.clear();
          panel.setPreferredSize(new Dimension(490, block < 290 ? 290 : block));
          triggerRepaint();
          setVisible(true);
          return selections;
     }

     @Override
     public void paint(Graphics g){
          super.paint(g);
          scrollPane.repaint();
          panel.repaint();
          items.forEach(item->item.repaint());
     }

     public static synchronized void sort(LinkedList<File> files){
          try{
               final LinkedList<File> tempFiles = new LinkedList<>();
               final LinkedList<File> tempDirs = new LinkedList<>();
               files.forEach(f->{
                    if(f.isDirectory()) tempDirs.add(f);
                    else tempFiles.add(f);
               });
               files.clear();
               File[] F = new File[tempFiles.size()];
               int k = -1;
               for(File fx : tempFiles)
                    F[++k] = fx;
               File[] D = new File[tempDirs.size()];
               k = -1;
               for(File fx : tempDirs)
                    D[++k] = fx;
               sort(F);
               sort(D);
               LinkedList<File> dots = new LinkedList<>();
               for(File f : D){
                    if(f.getName().startsWith(".")) dots.add(f);
                    else files.add(f);
               }
               for(File f : dots){
                    files.add(f);
               }
               dots.clear();
               for(File f : F){
                    if(f.getName().startsWith(".")) dots.add(f);
                    else files.add(f);
               }
               for(File f : dots){
                    files.add(f);
               }
               tempFiles.clear();
               tempDirs.clear();
               dots.clear();
          }
          catch(Exception exception){
               
          }
     }

     private static void sort(File[] files){
          for(int i = 0; i < files.length; i++){
               for(int j = 0; j < files.length - 1 - i; j++){
                    File x = files[j];
                    File y = files[j + 1];
                    if(x.getName().compareTo(y.getName()) > 0){
                         files[j] = y;
                         files[j + 1] = x;
                    }
               }
          }
     }

     private static int count(char ch, String line){
          int c = 0;
          for(char cx : line.toCharArray()){
               if(cx == ch)
                    c++;
          }
          return c;
     }

     public synchronized void triggerRepaint(){
          new Thread(()->{
               try{
                    scrollPane.repaint();
                    panel.repaint();
                    items.forEach(item->item.repaint());
               }
               catch(Exception e){ 
                    
               }
          }).start();
     }
}

