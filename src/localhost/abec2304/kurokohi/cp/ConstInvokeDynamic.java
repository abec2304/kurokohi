package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstInvokeDynamic extends ConstantPoolInfo {
    
    public int bootstrapMethodAttrIndex;
    public int nameAndTypeIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        bootstrapMethodAttrIndex = dis.readUnsignedShort();
        nameAndTypeIndex = dis.readUnsignedShort();
    }
    
}
