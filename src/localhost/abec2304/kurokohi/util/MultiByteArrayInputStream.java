package localhost.abec2304.kurokohi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MultiByteArrayInputStream extends ByteArrayInputStream {
    
    public int index;
    public byte[][] pool;
    
    public MultiByteArrayInputStream(byte[][] pool) {
        super(pool[0]);
        this.pool = pool;
    }
    
    public boolean advance() {
        if(index == pool.length) {
            return false;
        }

        index++;
        if(index < pool.length) {
            buf = pool[index];
            return true;
        }
        
        return false;
    }
    
    public void zero() {
        pos = 0;
        buf = pool[index = 0];
        count = buf.length;
    }
    
    public int read() {
        if(pos >= count) {
            if(!advance())
                return -1;
        }
        
        return buf[pos++] & 0xFF;
    }
    
    public int read(byte[] b, int i, int j) {
        if(pos >= count) {
            if(!advance())
                return 0;
        }
        
        if(pos + j > count)
            j = count - pos;
        
        if(j <= 0)
            return 0;
        
        System.arraycopy(buf, pos, b, i, j);
        pos += j;
        return j;
    }
    
    public int available() {
        return count - pos;
    }
    
    public boolean markSupported() {
        return false;
    }
    
}
