package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstBytes extends ConstantPoolInfo {
    
    public int tag() {
        return 2;
    }
    
    public byte[] bytes;
    
    public void init(DataInputStream dis) throws IOException {
        bytes = new byte[dis.readUnsignedShort()];
        dis.readFully(bytes);
    }

    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeShort(bytes.length);
        dos.write(bytes);
    }
    
    public void print(PrintStream out) {
        out.print("!!");
    }
    
}
