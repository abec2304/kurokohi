package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public abstract class DynamicMember extends ConstantPoolInfo {
    
    public int bootstrapMethodAttrIndex;
    public int nameAndTypeIndex;
    
    public void init(DataInputStream dis) throws IOException {
        bootstrapMethodAttrIndex = dis.readUnsignedShort();
        nameAndTypeIndex = dis.readUnsignedShort();
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeShort(bootstrapMethodAttrIndex);
        dos.writeShort(nameAndTypeIndex);
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(bootstrapMethodAttrIndex);
        out.print(".#");
        out.print(nameAndTypeIndex);
    }
    
}
