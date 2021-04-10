package omega.deassembler;
/*
    This class makes a record of all disassembled classess.
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
import java.util.*;
public class Assembly {
	private static final LinkedList<AssemblyUnit> units = new LinkedList<>();

	public static void deassemble(){
		units.clear();
	}

	public static void add(String className, ByteReader reader){
		if(has(className)) return;
		units.add(new AssemblyUnit(className, reader));
	}

	public static ByteReader getReader(String className){
		for(AssemblyUnit unit : units){
			if(unit.className.equals(className)){
				return unit.reader;
			}
		}
		return null;
	}

	public static boolean has(String className){
		for(AssemblyUnit unit : units){
			if(unit.className.equals(className))
				return true;
		}
		return false;
	}
}
