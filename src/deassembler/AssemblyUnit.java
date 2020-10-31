package deassembler;
public class AssemblyUnit{
     public String className;
     public ByteReader reader;
     public AssemblyUnit(String className, ByteReader reader){
          this.className = className;
          this.reader = reader;
     }
}