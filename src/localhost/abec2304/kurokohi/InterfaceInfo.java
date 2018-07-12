package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;

public class InterfaceInfo implements Info {

    public int index;
    
    public void init(DataInputStream dis) throws IOException {
        index = dis.readUnsignedShort();
    }

}
