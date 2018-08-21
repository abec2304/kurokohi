package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AttrUnknown extends AttributeInfo {

    public byte[][] info;
    
    private static final int CHUNK_SIZE = Integer.MAX_VALUE / 2;
    
    public void init(DataInputStream dis) throws IOException {
        attributeNameIndex = dis.readUnsignedShort();
        long toRead = attributeLength = dis.readInt() & 0xFFFFFFFFL;
        
        if(toRead == 0)
            return;
        
        byte[][] pool = new byte[(int)((toRead + (CHUNK_SIZE - 1)) / CHUNK_SIZE)][];
        int i = 0;
        do {
            int len = toRead > CHUNK_SIZE ? CHUNK_SIZE : (int)toRead;
            
            byte[] chunk = new byte[len];
            dis.readFully(chunk, 0, len);
            toRead -= len;
            
            pool[i++] = chunk;
        } while(toRead != 0);
        
        info = pool;
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeHeader(dos);
        if(attributeLength == 0)
            return;
        
        for(int i = 0; i < info.length; i++)
            dos.write(info[i]);
    }
    
    public void recalculateLength() {
        attributeLength = 0;
        if(info == null)
            return;
        
        for(int i = 0; i < info.length; i++)
            attributeLength += info[i].length;
    }
    
}
