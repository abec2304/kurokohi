package localhost.abec2304.kurokohi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

public class AttrUnknown extends AttributeInfo {

    public Vector info;
    
    public void init(DataInputStream dis) throws IOException {
        attributeNameIndex = dis.readUnsignedShort();
        attributeLength = dis.readInt() & 0xFFFFFFFFL;
        
        long toRead = attributeLength;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Vector vector = new Vector();
        byte[] buffer = new byte[8192];
        for(;;) {
            int max = (int)Math.min(buffer.length, toRead);
            int read = dis.read(buffer, 0, max);
            baos.write(buffer, 0, read);
            
            toRead -= read;
            if(toRead == 0) {
                vector.addElement(new ByteArrayInputStream(baos.toByteArray()));
                break;
            }
            
            // approx. 64mb
            if(baos.size() >= Integer.MAX_VALUE / 32) {
                vector.addElement(new ByteArrayInputStream(baos.toByteArray()));
                baos = new ByteArrayOutputStream();
            }
        }
        
        info = vector;
    }
    
}
