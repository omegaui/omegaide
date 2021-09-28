/**
* A Custom Local DataBase Management System
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

package omega.database;
import java.util.LinkedList;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
public class DataBase{
	private File dataBaseFile;
	private LinkedList<DataEntry> entries = new LinkedList<>();
	private LinkedList<String> names = new LinkedList<>();
	private static final char VALUE_BORDER = '-';
	private static final char NAME_BORDER = '>';
	
	public DataBase(String baseName){
		dataBaseFile = new File(baseName);
		if(dataBaseFile.exists())
			readDataBase();
	}
	
	private void readDataBase(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(dataBaseFile));
			String line = br.readLine();
			String currentName = null;
			while(line != null){
				if(line.length() > 0 && line.charAt(0) == NAME_BORDER){
					currentName = line.substring(1);
					addName(currentName);
				}
				else if(line.length() > 0 && line.charAt(0) == VALUE_BORDER && currentName != null){
					entries.add(new DataEntry(currentName, line.substring(1)));
				}
				line = br.readLine();
			}
			br.close();
		}
		catch(Exception e){
			new Thread(()->e.printStackTrace()).start();
		}
	}
	
	public void addEntry(String set, String value){
		entries.add(new DataEntry(set, value));
		addName(set);
	}
	
	public void updateEntry(String set, String value, int index) {
		if(!names.contains(set)) {
			addEntry(set, value);
			return;
		}
		DataEntry e = getEntryAt(set, index);
		if(e != null) {
			e.setValue(value);
		}
		else {
			addEntry(set, value);
		}
	}
	
	public void clear() {
		names.clear();
		entries.clear();
	}
	
	private void addName(String set){
		if(!names.contains(set)){
			names.add(set);
		}
	}
	
	public synchronized void save(){
		try{
			PrintWriter writer = new PrintWriter(new FileOutputStream(dataBaseFile));
			for(String name : names){
				LinkedList<DataEntry> en = getEntries(name);
				if(en == null)
					continue;
				writer.println(NAME_BORDER+name);
				for(DataEntry e : en)
					writer.println(VALUE_BORDER+e.getValue());
			}
			writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public DataEntry getEntryAt(String setName, int index){
		int i = -1;
		for(DataEntry e : entries){
			if(e.getName().equals(setName)){
				i++;
				if(i == index)
					return e;
			}
		}
		return null;
	}
	
	public LinkedList<DataEntry> getEntries(String set){
		if(!names.contains(set)) {
			return null;
		}
		LinkedList<DataEntry> entriesX = new LinkedList<>();
		for(DataEntry e : entries){
			if(e.getName().equals(set))
				entriesX.add(e);
		}
		return entriesX;
	}
	
	public LinkedList<String> getDataSetNames(){
		return names;
	}
	
	public LinkedList<DataEntry> getAllEntries(){
		return entries;
	}
	
	public File getDataBaseFile() {
		return dataBaseFile;
	}
}

