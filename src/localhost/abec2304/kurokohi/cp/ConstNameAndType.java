package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstNameAndType extends ConstantPoolInfo {
    
    public int nameIndex;
    public int descriptorIndex;
    
    public void init(DataInputStream dis) throws IOException {
        nameIndex = dis.readUnsignedShort();
        descriptorIndex = dis.readUnsignedShort();
    }
    
    public String toString() {
        return "#" + nameIndex + ":#" + descriptorIndex;
    }
    
}
