package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstMethodHandle extends ConstantPoolInfo {
    
    public int referenceKind;
    public int referenceIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        referenceKind = dis.readUnsignedByte();
        referenceIndex = dis.readUnsignedShort();
    }
    
}
