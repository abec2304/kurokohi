package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class DoubleOrLong extends ConstantPoolInfo {
    
    public long highBytes;
    public long lowBytes;
    
    public void init(DataInputStream dis) throws IOException {
        highBytes = dis.readInt() & 0xFFFFFFFFL;
        lowBytes = dis.readInt() & 0xFFFFFFFFL;
    }
    
}
