package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstMethodType extends ConstantPoolInfo {
    
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        descriptorIndex = dis.readUnsignedShort();
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(descriptorIndex);
    }
    
}
