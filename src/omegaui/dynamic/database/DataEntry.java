/**
  * DataEntry
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

package omegaui.dynamic.database;
import java.util.LinkedList;
import java.util.Scanner;
public class DataEntry{
	
	private String dataSetName;
	private String value;
	
	public LinkedList<String> lines = new LinkedList<>();
	
	public DataEntry(String dataSetName, String value){
		this.dataSetName = dataSetName;
		setValue(value);
	}

	public void evaluateLines(){
		if(value == null)
			return;
		lines.clear();
		if(value.contains("\n")){
			lines.add(value.substring(0, value.indexOf("\n")));
			try(Scanner reader = new Scanner(value.substring(value.indexOf("\n") + 1))){
				while(reader.hasNextLine()){
					String text = reader.nextLine();
					lines.add(text);
				}
			}
		}
		else
			lines.add(value);
	}

	protected void setValue(String value){
		this.value = value;
		evaluateLines();
	}

	public String dataSetName(){
		return dataSetName;
	}

	public String getValue(){
		return value;
	}

	public LinkedList<String> lines(){
		return lines;
	}
	
	public long getValueAsLong(){
		return Long.valueOf(value);
	}
	
	public int getValueAsInt(){
		return Integer.valueOf(value);
	}
	
	public double getValueAsDouble(){
		return Double.valueOf(value);
	}
	
	public char getValueAsChar(){
		return value.charAt(0);
	}
	
	public boolean getValueAsBoolean(){
		return Boolean.valueOf(value);
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof DataEntry)
			return obj.toString().equals(toString());
		return super.equals(obj);
	}
	
	public boolean equals(String dataSetName, String value){
		return this.dataSetName.equals(dataSetName) && this.value.equals(value);
	}
	
	@Override
	public String toString(){
		return dataSetName + " -> " + value;
	}
	
	public String toDataForm(){
		if(lines.isEmpty())
			return null;
		String result = DataBase.VALUE_PREFIX + lines.get(0);
		for(int i = 1; i < lines().size(); i++){
			result += "\n" + DataBase.LINE_PREFIX + lines.get(i);
		}
		return result;
	}
}
