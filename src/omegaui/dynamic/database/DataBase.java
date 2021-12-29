package omegaui.dynamic.database;
import java.util.Scanner;
import java.util.LinkedList;

import java.io.File;
import java.io.PrintWriter;
public class DataBase {
	public static final char SET_PREFIX = '>';
	public static final char VALUE_PREFIX = '-';
	public static final char LINE_PREFIX = '|';
	
	private File file;
	
	private LinkedList<DataEntry> entries = new LinkedList<>();
	private LinkedList<String> dataSetNames = new LinkedList<>();
	
	public DataBase(String filePath){
		this(new File(filePath));
	}
	
	public DataBase(File file){
		this.file = file;
		triggerLoad();
	}
	
	private void triggerLoad(){
		if(file != null && file.exists())
			load();
		else{
			try{
				throw new Exception(file + " doesn't exists!");
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void load(){
		try(Scanner reader = new Scanner(file)){
			String text;
			String dataSetName = "";
			String value = "";
			boolean canRecord = false;
			boolean gotLinePrefix = true;
			while(reader.hasNextLine()){
				text = reader.nextLine();
				if(text.trim().equals(""))
					continue;
				
				if(canRecord){
					if(text.charAt(0) == VALUE_PREFIX){
						if(!value.equals(""))
							addEntry(dataSetName, value);
						value = text.length() == 1 ? "" : text.substring(1);
						
						if(!reader.hasNextLine() && !value.equals("")){
							addEntry(dataSetName, value);
							canRecord = false;
						}
						else
							continue;
					}
					else if(text.charAt(0) == LINE_PREFIX){
						value += '\n' + (text.length() == 1 ? "" : text.substring(1));
						gotLinePrefix = true;
						if(!reader.hasNextLine()){
							addEntry(dataSetName, value);
							break;
						}
						continue;
					}
					else if(text.charAt(0) == SET_PREFIX){
						canRecord = false;
						gotLinePrefix = false;
						addEntry(dataSetName, value);
						dataSetName = "";
						value = "";
					}
				}
				
				if(!canRecord && text.charAt(0) == SET_PREFIX){
					dataSetName = text.substring(1);
					canRecord = true;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void save(){
		try(PrintWriter writer = new PrintWriter(file)){
			getDataSetNames().forEach(dataSetName->{
				writer.println(DataBase.SET_PREFIX + dataSetName);
				getEntries(dataSetName).forEach((entry)->{
					writer.println(entry.toDataForm());
				});
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public File getDataBaseFile() {
		return file;
	}
	
	public void clear(){
		entries.clear();
		dataSetNames.clear();
	}
	
	public void updateEntry(String dataSetName, String value, int index){
		DataEntry entry = getEntryAt(dataSetName, index);
		if(entry != null)
			entry.setValue(value);
		else{
			addEntry(dataSetName, value);
		}
	}
	
	public void addEntry(String dataSetName, String value){
		if(!dataSetNames.contains(dataSetName))
			dataSetNames.add(dataSetName);
		entries.add(new DataEntry(dataSetName, value));
	}
	
	public boolean hasEntry(String dataSetName, String value){
		for(DataEntry entryX : entries){
			if(entryX.equals(dataSetName, value))
				return true;
		}
		return false;
	}
	
	public boolean hasEntry(DataEntry entry){
		for(DataEntry entryX : entries){
			if(entryX.equals(entry))
				return true;
		}
		return false;
	}
	
	public LinkedList<DataEntry> getEntries() {
		return entries;
	}
	
	public LinkedList<String> getEntriesAsString(String dataSetName) {
		LinkedList<String> results = new LinkedList<>();
		for(DataEntry entry : entries){
			if(entry.dataSetName().equals(dataSetName))
				results.add(entry.getValue());
		}
		return results;
	}
	
	public LinkedList<DataEntry> getEntries(String dataSetName) {
		LinkedList<DataEntry> results = new LinkedList<>();
		for(DataEntry entry : entries){
			if(entry.dataSetName().equals(dataSetName))
				results.add(entry);
		}
		return results;
	}
	
	public DataEntry getEntryAt(String dataSetName, int index){
		LinkedList<DataEntry> dataSetNames = getEntries(dataSetName);
		if(index == dataSetNames.size())
			return null;
		return dataSetNames.get(index);
	}
	
	public DataEntry getEntryAt(String dataSetName){
		LinkedList<DataEntry> dataSetNames = getEntries(dataSetName);
		if(dataSetNames.size() == 0)
			return null;
		return dataSetNames.get(0);
	}
	
	public LinkedList<String> getDataSetNames(){
		return dataSetNames;
	}
}
