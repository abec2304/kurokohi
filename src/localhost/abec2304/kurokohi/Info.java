package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Info {
 
    void init(DataInputStream dis) throws IOException;
    
    void write(DataOutputStream dos) throws IOException;

}
