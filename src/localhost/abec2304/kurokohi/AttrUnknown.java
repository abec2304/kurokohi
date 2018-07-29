package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;

public class AttrUnknown extends AttributeInfo {

    public byte[][] info;
    
    private static final int CHUNK_SIZE = Integer.MAX_VALUE / 32;
    
    public void init(DataInputStream dis) throws IOException {
        attributeNameIndex = dis.readUnsignedShort();
        attributeLength = dis.readInt() & 0xFFFFFFFFL;
        
        if(attributeLength == 0)
            return;
        
        long toRead = attributeLength;
        
        byte[][] pool = new byte[(int)((toRead + (CHUNK_SIZE - 1)) / CHUNK_SIZE)][];
        int i = 0;
        do {
            byte[] chunk = new byte[(int)(toRead & CHUNK_SIZE)];
            dis.readFully(chunk);
            toRead -= chunk.length;
            pool[i++] = chunk;
        } while(toRead != 0);
        
        info = pool;
    }
    
}
