/**
  * <one line to give the program's name and a brief idea of what it does.>
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
public class DataEntry{
     private String entry;
     private String name;
     public DataEntry(String name, String entry){
          this.name = name;
          this.entry = entry;
     }
     public String getValue(){
          return entry;
     }
     public void setValue(String entry) {
         this.entry = entry;
     }
     public String getName(){
          return name;
     }
     public long getValueAsLong(){
         return Long.valueOf(entry);
    }
     public int getValueAsInt(){
         return Integer.valueOf(entry);
    }
     public double getValueAsDouble(){
          return Double.valueOf(entry);
     }
     public char getValueAsChar(){
          return entry.charAt(0);
     }
     public boolean getValueAsBoolean(){
          return Boolean.valueOf(entry);
     }     
}

