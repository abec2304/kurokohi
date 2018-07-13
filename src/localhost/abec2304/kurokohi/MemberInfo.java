package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class MemberInfo implements Info {
    
    public int accessFlags;
    public int nameIndex;
    public int descriptorIndex;
    public int attributesCount;
    public AttributeInfo[] attributes;
    
    public void init(DataInputStream dis) throws IOException {
        accessFlags = dis.readUnsignedShort();
        nameIndex = dis.readUnsignedShort();
        descriptorIndex = dis.readUnsignedShort();
        attributesCount = dis.readUnsignedShort();
        attributes = new AttributeInfo[attributesCount];
        for(int i = 0; i < attributesCount; i++) {
            AttributeInfo attribute = new AttrUnknown();
            attribute.init(dis);
            attributes[i] = attribute;
        }
    }
    
}
