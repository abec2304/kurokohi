package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstMethodType extends ConstantPoolInfo {
    
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        descriptorIndex = dis.readUnsignedShort();
    }
    
}
