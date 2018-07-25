package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstNameAndType extends ConstantPoolInfo {
    
    public int nameIndex;
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        nameIndex = dis.readUnsignedShort();
        descriptorIndex = dis.readUnsignedShort();
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(nameIndex);
        out.print(":#");
        out.print(descriptorIndex);
    }
    
}
