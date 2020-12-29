package tree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
public class FileTree extends JComponent{
	private File root;
	private LinkedList<Branch> branches = new LinkedList<>();
	private LinkedList<File> expandedRoots = new LinkedList<>();
	private JPanel panel;
	private int layoutX = -Branch.OPTIMAL_HEIGHT;
	private int layoutY = 0;
	private JScrollPane scrollPane;
	private int maxW = 300;
	private int max = 0;
	private int pointer;
	//101820
	public FileTree(String root){
		super();
		setPreferredSize(new Dimension(300, 400));
		if(root != null) {
			this.pointer = 0;
			this.root = new File(root);
			setLayout(new BorderLayout());
			scrollPane = new JScrollPane(panel = new JPanel(null));
               panel.setBackground(ide.utils.UIManager.c2);
			add(scrollPane, BorderLayout.CENTER);
		}
	}

	public void gen(File file){
		if(expandedRoots.contains(file)) return;
		LinkedList<File> files = getContents(file);
		expandedRoots.add(file);
		layoutX += Branch.OPTIMAL_HEIGHT;
		layoutY = 0;
		if(layoutX > max)
			max = layoutX;
		files.forEach(f->{
			Branch b = new Branch(f, (branch)->{
				if(f.isDirectory()){
					if(!expandedRoots.contains(f)){
						genBranch(f);
					}
					else{
						dissloveBranch(f);
					}
				}
				else ide.Screen.getScreen().loadFile(branch.file);
			});
			b.setBounds(layoutX, layoutY, 300, Branch.OPTIMAL_HEIGHT);
			panel.add(b);
			branches.add(b);
			layoutY += Branch.OPTIMAL_HEIGHT;
		});
		if(branches.isEmpty()) return;
		panel.setPreferredSize(new Dimension(max + maxW, branches.getLast().getY() + Branch.OPTIMAL_HEIGHT));
		try {
			ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() + 1);
			ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() - 1);
		}catch(Exception e) {}
		repaint();
	}

	public LinkedList<Branch> getBranches(File file){
		LinkedList<File> files = getContents(file);
		expandedRoots.add(file);
		LinkedList<Branch> branchesX = new LinkedList<>();
		files.forEach(f->{
			Branch b = new Branch(f, (branch)->{
				if(f.isDirectory()){
					if(!expandedRoots.contains(f)){
						genBranch(f);
					}
					else{
						dissloveBranch(f);
					}
				}
				else ide.Screen.getScreen().loadFile(branch.file);
			});
			b.setBounds(layoutX, layoutY, 300, Branch.OPTIMAL_HEIGHT);
			panel.add(b);
			branches.add(b);
			branchesX.add(b);
			layoutY += Branch.OPTIMAL_HEIGHT;
		});
		return branchesX;
	}

	public void genBranch(File f){
		if(expandedRoots.contains(f) || f.getAbsolutePath().equals(root.getAbsolutePath())) return;
		Branch root = null;
		LinkedList<Branch> endBranches = new LinkedList<>();
		for(Branch b : branches){
			if(root == null && b.file.getAbsolutePath().equals(f.getAbsolutePath())){
				root = b;
			}
			else if(root != null){
				endBranches.add(b);
				panel.remove(b);
			}
		}
		for(Branch b : endBranches)
			branches.remove(b);
		layoutX = root.getX() + Branch.OPTIMAL_HEIGHT;
		layoutY = root.getY() + Branch.OPTIMAL_HEIGHT;
		if(layoutX > max)
			max = layoutX;
		LinkedList<Branch> newBranches = getBranches(f);
		for(Branch b : endBranches){
			b.setLocation(b.getX(), b.getY() + (newBranches.size() * Branch.OPTIMAL_HEIGHT));
			panel.add(b);
			branches.add(b);
		}
		panel.setPreferredSize(new Dimension(max + maxW, branches.getLast().getY() + Branch.OPTIMAL_HEIGHT));
		ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() + 1);
		ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() - 1);
		repaint();
	}

	public void dissloveBranch(File f){
		if(!expandedRoots.contains(f)) return;
		expandedRoots.remove(f);
		Branch root = null;
		int layX = -1;
		LinkedList<Branch> children = new LinkedList<>();
		for(Branch b : branches){
			if(root == null && b.file.getAbsolutePath().equals(f.getAbsolutePath())){
				root = b;
				layX = root.getX();
			}
			else if(root != null && b.getX() > layX){
				panel.remove(b);
				children.add(b);
			}
			else if(b.getX() == layX)
				break;
		}
		root = null;
		children.forEach(branches::remove);
		for(Branch b : branches){
			if(root == null && b.file.getAbsolutePath().equals(f.getAbsolutePath())){
				root = b;
			}
			else if(root != null){
				b.setLocation(b.getX(), b.getY() - (children.size() * Branch.OPTIMAL_HEIGHT));
			}
		}
		children.clear();
		panel.setPreferredSize(new Dimension(max + maxW, branches.getLast().getY() + Branch.OPTIMAL_HEIGHT));
		ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() + 1);
		ide.Screen.getScreen().splitPane.setDividerLocation(ide.Screen.getScreen().splitPane.getDividerLocation() - 1);
		repaint();
	}

	private synchronized LinkedList<File> getContents(File file){
		LinkedList<File> files = new LinkedList<>();
		if(file == null) return files;
		File[] F = file.listFiles();
		for(File f : F)
			files.add(f);
		sort(files);
		return files;
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
          }catch(Exception exception){}
	}
	
	@Override
	public void addKeyListener(KeyListener k) {
		super.addKeyListener(k);
		if(panel != null)
			panel.addKeyListener(k);
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
	
	public void moveUp() {
		if(pointer > 0) {
			branches.get(pointer).set(false);
			branches.get(--pointer).set(true);
		}
	}
	
	public void moveDown() {
		if(pointer < branches.size()) {
			branches.get(pointer).set(false);
			branches.get(++pointer).set(true);
		}
	}
	
	public void forceLocate() {
		branches.get(pointer).force();
	}
	
	public void relocate() {
		if(!ide.Screen.getScreen().screenHasProjectView)
		     branches.get(pointer).set(true);
	}

	public FileTree reload() {
		int value = scrollPane.getVerticalScrollBar().getValue();
		FileTree fileTree = new FileTree(root.getAbsolutePath());
		LinkedList<File> expandedRoots = new LinkedList<>();
		for(File f : this.expandedRoots)
			expandedRoots.add(f);
		try {
			fileTree.gen(root);
			expandedRoots.forEach(fileTree::genBranch);
			fileTree.relocate();
		}catch(Exception e) {}
		scrollPane.getVerticalScrollBar().setValue(value);
		return fileTree;
	}
	
	public File getRoot(){
		return root;
	}
}
