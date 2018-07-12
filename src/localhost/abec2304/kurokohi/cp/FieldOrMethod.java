package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class FieldOrMethod extends ConstantPoolInfo {
    
    public int classIndex;
    public int nameAndTypeIndex;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        classIndex = dis.readUnsignedShort();
        nameAndTypeIndex = dis.readUnsignedShort();
    }
    
    public String toString() {
        return "#" + classIndex + "." + nameAndTypeIndex;
    }
    
}
