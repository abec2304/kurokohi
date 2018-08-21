package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MemberInfo implements Info {
    
    public int accessFlags;
    public int nameIndex;
    public int descriptorIndex;
    public AttributeInfo[] attributes;
    
    public void init(DataInputStream dis) throws IOException {
        accessFlags = dis.readUnsignedShort();
        nameIndex = dis.readUnsignedShort();
        descriptorIndex = dis.readUnsignedShort();
        attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < attributes.length; i++) {
            AttributeInfo attribute = new AttrUnknown();
            attribute.init(dis);
            attributes[i] = attribute;
        }
    }
    
    public void write(DataOutputStream dos) throws IOException {
        dos.writeShort(accessFlags);
        dos.writeShort(nameIndex);
        dos.writeShort(descriptorIndex);
        dos.writeShort(attributes.length);
        for(int i = 0; i < attributes.length; i++)
            attributes[i].write(dos);
    }
    
}
