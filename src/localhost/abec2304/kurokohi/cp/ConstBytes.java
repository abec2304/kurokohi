package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstBytes extends ConstantPoolInfo {
    
    public int length;
    public byte[] bytes;
    
    public void init(DataInputStream dis) throws IOException {
        length = dis.readUnsignedShort();
        bytes = new byte[length];
        dis.readFully(bytes);
    }
 
}
