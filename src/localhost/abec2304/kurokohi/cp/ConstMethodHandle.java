package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ConstMethodHandle extends ConstantPoolInfo {
    
    public int tag() {
        return 15;
    }
    
    public int referenceKind;
    public int referenceIndex;
    
    public void init(DataInputStream dis) throws IOException {
        referenceKind = dis.readUnsignedByte();
        referenceIndex = dis.readUnsignedShort();
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeTag(dos);
        dos.writeByte(referenceKind);
        dos.writeShort(referenceIndex);
    }
    
    public void print(PrintStream out) {
        out.print('#');
        out.print(referenceKind);
        out.print(".#");
        out.print(referenceIndex);
    }
    
}
