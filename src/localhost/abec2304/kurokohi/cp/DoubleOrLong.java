package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class DoubleOrLong extends ConstantPoolInfo {
    
    public long highBytes;
    public long lowBytes;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        highBytes = dis.readInt() & 0xFFFFFFFFl;
        lowBytes = dis.readInt() & 0xFFFFFFFFl;
    }
    
}
