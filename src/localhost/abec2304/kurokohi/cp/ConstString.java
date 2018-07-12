package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstString extends ConstantPoolInfo {
    
    public int stringIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        stringIndex = dis.readUnsignedShort();
    }
    
    public String toString() {
        return "#" + stringIndex;
    }
    
}
