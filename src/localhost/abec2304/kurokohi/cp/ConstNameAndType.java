package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstNameAndType extends ConstantPoolInfo {
    
    public int tag() {
        return 12;
    }
    
    public int nameIndex;
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        nameIndex = dis.readUnsignedShort();
        descriptorIndex = dis.readUnsignedShort();
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeShort(nameIndex);
        dos.writeShort(descriptorIndex);
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(nameIndex);
        out.print(":#");
        out.print(descriptorIndex);
    }
    
}
