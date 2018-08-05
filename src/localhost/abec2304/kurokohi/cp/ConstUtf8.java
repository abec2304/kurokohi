package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UTFDataFormatException;
import localhost.abec2304.kurokohi.util.CharBuffer;

public class ConstUtf8 extends ConstantPoolInfo {
    
    public int length;
    public byte[] bytes;
    public String string;
    
    public void init(DataInputStream dis) throws IOException {
        length = dis.readUnsignedShort();
        bytes = new byte[length];
        dis.readFully(bytes, 0, length);
    }

    public void print(PrintStream out) {
        if(string == null) {
            out.print("null");
            return;
        }
        
        out.print('\'');
        out.print(string);
        out.print('\'');
    }

    public void initString(CharBuffer buffer) throws UTFDataFormatException {
        if(length == 0) {
            string = "";
            return;
        }
        
        buffer.reset(length);
        
        int c, b, a;
        int i = 0;
        
        do {
            c = bytes[i];
            if(c < 0)
                break;
            i++;
            buffer.append((char)(c & 0xFF));
        } while(i < length);
        
        while(i < length) {
            c = bytes[i] & 0xFF;
            
            switch(c >> 5) {
                case 0:
                case 1:
                case 2:
                case 3:
                    i++;
                    buffer.append((char)c);
                    continue;
                case 6:
                    i += 2;
                    if(i > length)
                        throw new UTFDataFormatException("1");
                    b = bytes[i-1];
                    if((b & 0xC0) != 0x80)
                        throw new UTFDataFormatException("2");
                    buffer.append((char)(((c & 0x1F) << 6) | (b & 0x3F)));
                    continue;
                case 7:
                    if(c >= 240)
                        break;
                    
                    i += 3;
                    if(i > length)
                        throw new UTFDataFormatException("3");
                    b = bytes[i-2];
                    a = bytes[i-1];
                    if(((b & 0xC0) != 0x80) || ((a & 0xC0) != 0x80))
                        throw new UTFDataFormatException("4");
                    buffer.append((char)(((c & 0x0F) << 12) | ((b & 0x3F) << 6) | (a & 0x3F)));
                    continue;
            }
            
            throw new UTFDataFormatException("5");
        }
        
        string = buffer.toString();
    }
    
}
