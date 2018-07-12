package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;

public class ConstUtf8 extends ConstantPoolInfo {
    
    public int length;
    public byte[] bytes;
    public String string;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        
        dis.mark(2);
        length = dis.readUnsignedShort();
        dis.reset();
        
        dis.mark(2 + length);
        dis.readFully(new byte[2]);
        bytes = new byte[length];
        dis.readFully(bytes);
        dis.reset();
        
        dis.mark(2 + length);
        try {
            string = dis.readUTF();
        } catch(IOException ioe) {
            dis.reset();
        }
    }
 
    public String toString() {
        return string == null ? "null" : "'" + string + "'";
    }
 
}
