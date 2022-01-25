package omega.io;
import java.io.File;

import omega.Screen;

import omegaui.dynamic.database.DataBase;
import omegaui.dynamic.database.DataEntry;

import org.fife.ui.rtextarea.GutterIconInfo;

import java.util.LinkedList;

import omega.ui.component.Editor;

public class BookmarksManager {

	public static final String BOOKMARK_DATASET_NAME = "Editor Bookmarks";
	
	public static final LinkedList<Bookmark> bookmarks = new LinkedList<>();

	public static synchronized void deleteAll(){
		bookmarks.clear();
	}
	
	public static synchronized void saveBookmarks(Editor editor){		
		removeBookmarkHistory(editor);
		
		GutterIconInfo[] infos = editor.getAttachment().getGutter().getBookmarks();
		if(infos == null || infos.length == 0)
			return;
		
		Bookmark bookmark = new Bookmark(editor.currentFile);

		for(GutterIconInfo info : infos)
			bookmark.addLine(info.getMarkedOffset());

		bookmarks.add(bookmark);
	}

	public static synchronized void readBookmarks(DataBase dataBase){
		deleteAll();
		
		LinkedList<DataEntry> entries = dataBase.getEntries(BOOKMARK_DATASET_NAME);
		
		if(entries == null || entries.size() == 0)
			return;
		
		entries.forEach(entry->{
			if(entry.lines().size() > 1){
				File file = new File(entry.lines().get(0));
				if(file.exists()){
					Bookmark bookmark = new Bookmark(file);
					
					for(int i = 1; i < entry.lines().size(); i++)
						bookmark.addLine(Integer.parseInt(entry.lines().get(i)));
					
					bookmarks.add(bookmark);
				}
			}
		});
	}

	public static synchronized void saveBookmarks(DataBase dataBase){
		bookmarks.forEach((bookmark)->{
			dataBase.addEntry(BOOKMARK_DATASET_NAME, bookmark.toString());
		});
	}

	public static synchronized void removeBookmarkHistory(Editor editor){
		Bookmark bx = null;
		for(Bookmark bookmark : bookmarks){
			if(bookmark.file.getAbsolutePath().equals(editor.currentFile.getAbsolutePath())){
				bx = bookmark;
				break;
			}
		}
		if(bx != null)
			bookmarks.remove(bx);
	}

	public static synchronized void markAll(Editor editor){
		for(Bookmark bookmark : bookmarks){
			if(bookmark.file.getAbsolutePath().equals(editor.currentFile.getAbsolutePath())){
				bookmark.apply(editor);
				return;
			}
		}
	}
}
