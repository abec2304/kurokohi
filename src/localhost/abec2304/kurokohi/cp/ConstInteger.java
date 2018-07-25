package localhost.abec2304.kurokohi.cp;

import java.io.PrintStream;

public class ConstInteger extends IntegerOrFloat {
    
    public void print(PrintStream out) {
        out.print((int)bytes);
    }
    
}
