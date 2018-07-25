package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class IntegerOrFloat extends ConstantPoolInfo {

    public long bytes;
    
    public void init(DataInputStream dis) throws IOException {
        bytes = dis.readInt() & 0xFFFFFFFFL;
    }

}
