package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstString extends ConstantPoolInfo {
    
    public int stringIndex;
    
    public void init(DataInputStream dis) throws IOException {
        stringIndex = dis.readUnsignedShort();
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(stringIndex);
    }
    
}
