package deassembler;
public class DepthMember extends DataMember{
     public int depth = 0;
     public DepthMember(String access, String modifier, String type, String name, String parameters, int depth){
          super(access, modifier, type, name, parameters);
          this.depth = depth;
     }

     @Override
     public String toString(){
          return super.toString() + ", depth - " + depth;
     }
}