package deassembler;
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
			if(unit.className.equals(className))
				return unit.reader;
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
