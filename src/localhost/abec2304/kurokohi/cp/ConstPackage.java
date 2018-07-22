package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstPackage extends ConstantPoolInfo {
    
    public int nameIndex;
    
    public void init(DataInputStream dis) throws IOException {
        nameIndex = dis.readUnsignedShort();
    }
    
}
