package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstMethodType extends ConstantPoolInfo {
    
    public int tag() {
        return 16;
    }
    
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        descriptorIndex = dis.readUnsignedShort();
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeShort(descriptorIndex);
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(descriptorIndex);
    }
    
}
