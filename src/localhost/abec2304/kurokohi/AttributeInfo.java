package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class AttributeInfo implements Info {

    public int attributeNameIndex;
    public long attributeLength;
    
    public void init(DataInputStream dis) throws IOException {
        throw new IOException("nothing to read");
    }
    
}
