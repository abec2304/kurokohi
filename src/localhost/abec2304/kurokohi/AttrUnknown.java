package localhost.abec2304.kurokohi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

public class AttrUnknown extends AttributeInfo {

    public Vector info;
    
    private static final int CHUNK_SIZE = Integer.MAX_VALUE / 32;
    
    public void init(DataInputStream dis) throws IOException {
        attributeNameIndex = dis.readUnsignedShort();
        attributeLength = dis.readInt() & 0xFFFFFFFFL;
        
        long toRead = attributeLength;
        Vector vector = new Vector();
        for(;;) {
            byte[] chunk = new byte[(int)Math.min(CHUNK_SIZE, toRead)];
            dis.readFully(chunk);
            toRead -= chunk.length;
            vector.addElement(new ByteArrayInputStream(chunk));
            
            if(toRead == 0)
                break;
        }
        
        info = vector;
    }
    
}
