package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import localhost.abec2304.kurokohi.util.CharBuffer;

public class ConstUtf8 extends ConstantPoolInfo {
    
    public int length;
    public byte[] bytes;
    public String string;
    
    public void init(DataInputStream dis) throws IOException {
        super.init(dis);
        
        length = dis.readUnsignedShort();
        bytes = new byte[length];
        dis.readFully(bytes);
    }
 
    public String toString() {
        if(string == null)
            return "null";
        
        int len = string.length();
        char[] arr = new char[len + 2];
        arr[0] = '\'';
        arr[len + 1] = '\'';
        string.getChars(0, len, arr, 1);
        return new String(arr);
    }

    public void initString(CharBuffer buffer) throws UTFDataFormatException {
        buffer.reset();
        
        int c, b, a;
        int i = 0;
        
        while(i < length) {
            c = bytes[i] & 0xFF;
            
            if(c >> 7 == 0) {
                i++;
                buffer.append((char)c);
                continue;
            }
            
            switch(c >> 4) {
                case 12:
                case 13:
                    i += 2;
                    if(i > length)
                        throw new UTFDataFormatException("1");
                    b = bytes[i-1];
                    if((b & 0xC0) != 0x80)
                        throw new UTFDataFormatException("2");
                    buffer.append((char)(((c & 0x1F) << 6) | (b & 0x3F)));
                    break;
                case 14:
                    i += 3;
                    if(i > length)
                        throw new UTFDataFormatException("3");
                    b = bytes[i-2];
                    a = bytes[i-1];
                    if(((b & 0xC0) != 0x80) || ((a & 0xC0) != 0x80))
                        throw new UTFDataFormatException("4");
                    buffer.append((char)(((c & 0x0F) << 12) | ((b & 0x3F) << 6) | (a & 0x3F)));
                    break;
                default:
                    throw new UTFDataFormatException("5");
            }
        }
        
        string = buffer.toString();
    }
    
}
