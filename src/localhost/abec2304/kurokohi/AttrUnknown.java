package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;

public class AttrUnknown extends AttributeInfo {

    public byte[][] info;
    
    private static final int CHUNK_SIZE = Integer.MAX_VALUE / 32;
    
    public void init(DataInputStream dis) throws IOException {
        attributeNameIndex = dis.readUnsignedShort();
        long toRead = attributeLength = dis.readInt() & 0xFFFFFFFFL;
        
        if(toRead == 0)
            return;
        
        byte[][] pool = new byte[(int)((toRead + (CHUNK_SIZE - 1)) / CHUNK_SIZE)][];
        int i = 0;
        do {
            int len = (int)(toRead & CHUNK_SIZE);
            byte[] chunk = new byte[len];
            dis.readFully(chunk, 0, len);
            toRead -= chunk.length;
            pool[i++] = chunk;
        } while(toRead != 0);
        
        info = pool;
    }
    
}
