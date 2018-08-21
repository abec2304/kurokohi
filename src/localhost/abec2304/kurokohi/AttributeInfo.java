package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AttributeInfo implements Info {

    public int attributeNameIndex;
    public long attributeLength;
    
    public void init(DataInputStream dis) throws IOException {
        throw new IOException("nothing to read");
    }
    
    public void write(DataOutputStream dos) throws IOException {
        throw new IOException("nothing to write");
    }
    
    public abstract void recalculateLength();
    
    public void writeHeader(DataOutputStream dos) throws IOException {
        dos.writeShort(attributeNameIndex);
        dos.writeInt((int)attributeLength);
    }
    
    public String getName() {
        return null;
    }
    
}
