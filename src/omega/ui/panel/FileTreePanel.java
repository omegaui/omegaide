/*
  * FileTreePanel
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

package omega.ui.panel;
import omega.io.FileOperationManager;

import omegaui.component.FlexPanel;

import omega.ui.component.FileTreeBranch;

import omega.Screen;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BorderLayout;

import java.util.LinkedList;
import java.util.StringTokenizer;

import java.io.File;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class FileTreePanel extends AbstractFileTreePanel{
	
	private File root;
	private LinkedList<FileTreeBranch> branches = new LinkedList<>();
	
	private FlexPanel flexPanel;
	private JPanel panel;
	private JScrollPane scrollPane;
	
	private int blockX;
	private int blockY;
	
	private int optimumBranchSize = 25;
	private int gap = 5;
	
	public FileTreePanel(){
		super(new BorderLayout());
		setLayout(null);
		setBackground(c2);
		setSize(300, 100);
		setPreferredSize(getSize());
		
		flexPanel = new FlexPanel(null, TOOLMENU_COLOR3_SHADE, null);
		flexPanel.setArc(0, 0);
		add(flexPanel, BorderLayout.CENTER);
		
		panel = new JPanel(null);
		panel.setBackground(c2);
		
		flexPanel.add(scrollPane = new JScrollPane(panel));
		
		scrollPane.setBackground(c2);
		scrollPane.setBorder(BorderFactory.createLineBorder(c2, 2));
	}
	
	@Override
	public void init(File parentDirectory){
		this.root = null;

		panel.removeAll();
		branches.clear();
		
		blockX = blockY = 0;
		
		FileTreeBranch branch = new FileTreeBranch(this, parentDirectory);
		branch.setLocation(0, 0);
		branch.setRootMode(true);
		branch.lockMode();
		panel.add(branch);
		branches.add(branch);

		genBranch(parentDirectory);
		
		this.root = parentDirectory;
	}
	
	public void genBranch(File root){
		if(this.root != null && this.root.getAbsolutePath().equals(root.getAbsolutePath()))
			return;
		
		LinkedList<File> files = getFiles(root);
		if(files.isEmpty()){
			return;
		}
		
		FileTreeBranch rootBranch = findBranch(root);
		int rootIndex = branches.indexOf(rootBranch) + 1;
		
		if(rootBranch.isExpanded()){
			collapseBranch(rootBranch);
			return;
		}
		rootBranch.setExpanded(true);
		
		blockX = rootBranch.getX() + optimumBranchSize;
		blockY = rootBranch.getY() + optimumBranchSize + gap;
		
		jumpBranchesAfter(rootBranch, files.size() * (optimumBranchSize + gap));
		
		for(File file : files){
			FileTreeBranch branch = new FileTreeBranch(this, file);
			branch.setLocation(blockX, blockY);
			panel.add(branch);
			branches.add(rootIndex++, branch);
			
			blockY += optimumBranchSize + gap;
		}
		
		computePreferredSize();
	}
	
	public void genBranchNoCollapse(File root){
		if(this.root != null && this.root.getAbsolutePath().equals(root.getAbsolutePath()))
			return;
		
		LinkedList<File> files = getFiles(root);
		if(files.isEmpty()){
			return;
		}
		
		FileTreeBranch rootBranch = findBranch(root);
		int rootIndex = branches.indexOf(rootBranch) + 1;
		
		if(rootBranch.isExpanded())
			return;
		
		rootBranch.setExpanded(true);
		
		blockX = rootBranch.getX() + optimumBranchSize;
		blockY = rootBranch.getY() + optimumBranchSize + gap;
		
		jumpBranchesAfter(rootBranch, files.size() * (optimumBranchSize + gap));
		
		for(File file : files){
			FileTreeBranch branch = new FileTreeBranch(this, file);
			branch.setLocation(blockX, blockY);
			panel.add(branch);
			branches.add(rootIndex++, branch);
			
			blockY += optimumBranchSize + gap;
		}
		
		computePreferredSize();
	}
	
	public void collapseBranch(FileTreeBranch branch){
		LinkedList<FileTreeBranch> subBranches = findSubBranches(branch);
		subBranches.forEach(panel::remove);
		subBranches.forEach(branches::remove);
		
		jumpBranchesAfter(branch, - (subBranches.size() * (optimumBranchSize + gap)));
		
		subBranches.clear();
		
		branch.setExpanded(false);
		
		computePreferredSize();
	}
	
	public void expandBranch(FileTreeBranch branch){
		genBranch(branch.getFile());
	}
	
	public void jumpBranchesAfter(FileTreeBranch branch, int jumpRange){
		for(int i = branches.indexOf(branch) + 1; i < branches.size(); i++){
			FileTreeBranch bx = branches.get(i);
			bx.setLocation(bx.getX(), bx.getY() + jumpRange);
		}
	}
	
	public void onFileBranchClicked(FileTreeBranch branch){
		Screen.getScreen().loadFile(branch.getFile());
	}
	
	public LinkedList<File> getFiles(File dir){
		LinkedList<File> files = new LinkedList<>();
		File[] F = dir.listFiles();
		if(F == null || F.length == 0)
			return files;
		for(File fx : F)
			files.add(fx);
		return FileOperationManager.sort(files);
	}
	
	public LinkedList<FileTreeBranch> findSubBranches(FileTreeBranch branch){
		LinkedList<FileTreeBranch> results = new LinkedList<>();
		for(int i = branches.indexOf(branch) + 1; i < branches.size(); i++){
			FileTreeBranch bx = branches.get(i);
			if(branch.isParentOf(bx))
				results.add(bx);
			else
				break;
		}
		return results;
	}
	
	public FileTreeBranch findBranch(File file){
		for(FileTreeBranch b : branches){
			if(b.getFile().getAbsolutePath().equals(file.getAbsolutePath()))
				return b;
		}
		
		return null;
	}
	
	public int getLargestBranchLastXPoint(){
		int x = branches.getFirst().getWidth();
		for(FileTreeBranch bx : branches){
			if(bx.getX() + bx.getWidth() > x){
				x = bx.getX() + bx.getWidth();
			}
		}
		return x + optimumBranchSize;
	}
	
	public int getLastBranchLastYPoint(){
		FileTreeBranch bx = branches.getLast();
		return bx.getY() + bx.getHeight() + 2;
	}
	
	public void computePreferredSize(){
		int w = getLargestBranchLastXPoint();
		int h = getLastBranchLastYPoint();
		
		panel.setSize(w, h);
		panel.setPreferredSize(panel.getSize());
		
		Screen.getScreen().splitPane.setDividerLocation((w >= 300) ? (w + 25) : 300);
	}

	public void navigateTo(File file){
		FileTreeBranch bx = findBranch(file);
		if(bx != null){
			makeActive(bx);
			return;
		}
		String path = file.getAbsolutePath();
		String relativePath = path.substring(root.getAbsolutePath().length());
		StringTokenizer pathTokenizer = new StringTokenizer(relativePath, File.separator);

		path = root.getAbsolutePath();
		
		while(pathTokenizer.hasMoreTokens()){
			path += File.separator + pathTokenizer.nextToken();
			genBranchNoCollapse(new File(path));
		}

		bx = findBranch(file);
		if(bx != null){
			makeActive(bx);
		}
	}

	public void navigateTo(FileTreeBranch bx){
		navigateForward(bx);
		navigateBackward(bx);
	}

	public void navigateForward(FileTreeBranch bx){
		int value = scrollPane.getVerticalScrollBar().getValue();
		while(!scrollPane.getViewport().getViewRect().contains(bx.getBounds()) && value != scrollPane.getVerticalScrollBar().getMaximum()){
			value += optimumBranchSize + gap;
			scrollPane.getVerticalScrollBar().setValue(value);
		}
	}

	public void navigateBackward(FileTreeBranch bx){
		int value = scrollPane.getVerticalScrollBar().getValue();
		while(!scrollPane.getViewport().getViewRect().contains(bx.getBounds()) && value > 0){
			value -= (optimumBranchSize + gap);
			scrollPane.getVerticalScrollBar().setValue(value);
		}
	}

	public void makeActive(FileTreeBranch bx){
		bx.grabFocus();
		navigateTo(bx);
		new Thread(()->{
			try{
				Thread.sleep(250);
			}
			catch(Exception e){
			}
			bx.repaint();
			repaint();
		}).start();
	}
	
	@Override
	public void setVisible(boolean value){
		super.setVisible(value);
		computePreferredSize();
	}
	
	@Override
	public void layout(){
		flexPanel.setBounds(0, 0, getWidth(), getHeight());
		scrollPane.setBounds(5, 5, flexPanel.getWidth() - 10, flexPanel.getHeight() - 10);
		super.layout();
	}
	
	@Override
	public void refresh(){
	
		if(Screen.getProjectFile().getJDKManager() != null)
			Screen.getProjectFile().getJDKManager().readSources(root.getAbsolutePath());
		Screen.getProjectFile().getSearchWindow().cleanAndLoad(new File(Screen.getProjectFile().getProjectPath()));

		LinkedList<File> expandedDirectories = new LinkedList<>();
		for(int i = 1; i < branches.size(); i++){
			FileTreeBranch bx = branches.get(i);
			if(bx.isExpanded())
				expandedDirectories.add(bx.getFile());
		}

		int scrollPosition = scrollPane.getVerticalScrollBar().getValue();

		FileTreePanel newFileTreePanel = new FileTreePanel();
		
		newFileTreePanel.init(root);

		for(File dir : expandedDirectories){
			newFileTreePanel.genBranch(dir);
		}

		expandedDirectories.clear();

		newFileTreePanel.scrollPane.getVerticalScrollBar().setValue(scrollPosition);

		newFileTreePanel.setVisible(isVisible());
		Screen.getProjectFile().setFileTreePanel(newFileTreePanel);
		if(newFileTreePanel.isVisible()){
			newFileTreePanel.computePreferredSize();
		}
	}
	
	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g);
		g.dispose();
	}

	public void transferFocusToPreviousBranch(FileTreeBranch currentBranch){
		int currentBranchIndex = branches.indexOf(currentBranch);
		if(currentBranchIndex == 0)
			return;
		
		FileTreeBranch bx = branches.get(currentBranchIndex - 1);
		bx.grabFocus();
		bx.repaint();

		if(!scrollPane.getViewport().getViewRect().contains(bx.getBounds()))
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - optimumBranchSize - gap);
	}

	public void transferFocusToNextBranch(FileTreeBranch currentBranch){
		int currentBranchIndex = branches.indexOf(currentBranch);
		if(currentBranchIndex == branches.size() - 1)
			return;
		
		FileTreeBranch bx = branches.get(currentBranchIndex + 1);
		bx.grabFocus();
		bx.repaint();
		
		if(!scrollPane.getViewport().getViewRect().contains(bx.getBounds()))
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + optimumBranchSize + gap);
	}
	
	public java.io.File getRoot() {
		return root;
	}
	
	public synchronized static void loadFilesIncludeSubDirectories(LinkedList<File> files, File file, String ext){
		if(file == null)
			return;
		File[] F = file.listFiles();
		for(File f : F){
			if(f.isDirectory())
				loadFilesIncludeSubDirectories(files, f, ext);
			else if(f.getName().endsWith(ext))
				files.add(f);
		}
	}
}
