package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public abstract class ModuleOrPackage extends ConstantPoolInfo {
    
    public int nameIndex;
    
    public void init(DataInputStream dis) throws IOException {
        nameIndex = dis.readUnsignedShort();
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeShort(nameIndex);
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(nameIndex);
    }
    
}
