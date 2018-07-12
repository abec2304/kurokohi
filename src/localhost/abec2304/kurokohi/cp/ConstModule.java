package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstModule extends ConstantPoolInfo {
    
    public int nameIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        nameIndex = dis.readUnsignedShort();
    }
    
}
