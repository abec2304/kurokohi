package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public abstract class FieldOrMethod extends ConstantPoolInfo {
    
    public int classIndex;
    public int nameAndTypeIndex;
    
    public void init(DataInputStream dis) throws IOException {
        classIndex = dis.readUnsignedShort();
        nameAndTypeIndex = dis.readUnsignedShort();
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(classIndex);
        out.print(".#");
        out.print(nameAndTypeIndex);
    }
    
}
