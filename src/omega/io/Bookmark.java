package omega.io;
import omega.Screen;

import java.io.File;

import omega.ui.component.Editor;

import java.util.LinkedList;

public class Bookmark {
	public File file;
	public LinkedList<Integer> lines = new LinkedList<>();

	public Bookmark(File file){
		this.file = file;
		this.lines = lines;
	}

	public void apply(Editor editor){
		lines.forEach(line->{
			try{
				editor.getAttachment().getGutter().toggleBookmark(editor.getLineOfOffset(line));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
	}

	public void addLine(int line){
		lines.add(line);
	}

	@Override
	public String toString(){
		String res = file.getAbsolutePath();
		for(int line : lines)
			res += "\n" + line;
		return res;
	}
}
