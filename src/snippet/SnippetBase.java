package snippet;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.*;
public class SnippetBase {
	private static File dataBase = new File(".snippets");
	private static LinkedList<Snippet> snipps = new LinkedList<>();
	private static final String BASE = ">:>:>";
	private static final String CARET = "<:<:<";

	public static synchronized LinkedList<Snippet> getAll() {
		return snipps;
	}
	
	public static boolean add(String base, String code, int caret, int line){
		if(hasSnippet(base)) return false;
		Snippet snip = new Snippet();
		snip.base = base;
		snip.code = code;
		snip.caret = caret;
		snip.line = line;
		snipps.add(snip);
		save();
		return true;
	}

	public static void remove(String base){
		if(hasSnippet(base)){
			for(Snippet s : snipps){
				if(s.base.equals(base)){
					snipps.remove(s);
					save();
					return;
				}
			}
		}
	}

	public static void loadDefault() {
		try {
			Scanner reader = new Scanner(SnippetBase.class.getResourceAsStream("/.snippets"));
			PrintWriter writer = new PrintWriter(dataBase);
			while(reader.hasNextLine()) {
				writer.println(reader.nextLine());
			}
			writer.close();
			reader.close();
			load();
		}catch(Exception e) {System.err.println(e);}
	}
	
	public static void load(){
		if(!dataBase.exists()) {
			loadDefault();
			return;
		}
		try{
			snipps.clear();
			Scanner reader = new Scanner(dataBase);
			Snippet snip = null;
			while(reader.hasNextLine()){
				String line = reader.nextLine();
				if(line.startsWith(BASE)){
					snip = new Snippet();
					snip.base = line.substring(BASE.length());
				}
				else if(snip != null && !line.startsWith(CARET)){
					snip.code += line + "\n";
				}
				if(line.startsWith(CARET)){
					try{
						snip.caret = Integer.parseInt(line.substring(CARET.length(), line.indexOf(',')));
						snip.line = Integer.parseInt(line.substring(line.indexOf(',') + 1));
						snip.code = snip.code.substring(0, snip.code.lastIndexOf('\n'));
						snipps.add(snip);
						snip = null;
					}
					catch(Exception e){
						System.err.println(e);
						return;
					}
				}
			}
			reader.close();
		}catch(Exception e){System.err.println(e);}
	}

	public static void save(){
		if(snipps.isEmpty()) {
			return;
		}
		try{
			PrintWriter writer = new PrintWriter(dataBase);
			for(Snippet snip : snipps){
				writer.println(BASE + snip.base);
				writer.println(snip.code);
				writer.println(CARET + snip.caret + "," + snip.line);
			}
			writer.close();
		}catch(Exception e){System.err.println(e);}
	}

	public static void insertSnippet(JTextArea textArea, String base, int index, String tabs){
		for(Snippet s : snipps){
			if(s.base.equals(base)){
				Document doc = textArea.getDocument();
				try{
					doc.remove(index, base.length());
				}
				catch(Exception e){
					System.err.println(e);
					return;
				}
				StringTokenizer tok = new StringTokenizer(s.code, "\n");
				int c = 0;
				while(tok.hasMoreTokens()) {
					String line = tok.nextToken();
					if(c == 0) {
						textArea.insert(line, index);
						if(tok.hasMoreTokens())
							textArea.insert("\n", textArea.getCaretPosition());
					}
					else {
						if(tok.hasMoreTokens())
							textArea.insert(tabs + line + "\n", textArea.getCaretPosition());
						else
							textArea.insert(tabs + line, textArea.getCaretPosition());
					}
					if(tok.hasMoreTokens())
						c = 1;
				}
				if(s.line == 0)
					textArea.setCaretPosition(index + s.caret);
				else if(s.line > 0 && !tabs.equals(""))
					textArea.setCaretPosition(index + s.caret + (analyzeTabs(tabs, s.line).length() - analyzeTabs(tabs, 0).length()));
				else
					textArea.setCaretPosition(index + s.caret);
				return;
			}
		}
	}
	
	private static String analyzeTabs(String tabs, int line) {
		String res = "";
		for(char ch : tabs.toCharArray()) {
			if(ch == '\t')
				res += "    ";
			else
				res += ch;
		}
		if(line == 0) return res;
		final String RES = res;
		while(line > 0) {
			line--;
			res += RES;
		}
		return res;
	}

	public static boolean hasSnippet(String base){
		for(Snippet s : snipps){
			if(s.base.equals(base)) return true;
		}
		return false;
	}
}
