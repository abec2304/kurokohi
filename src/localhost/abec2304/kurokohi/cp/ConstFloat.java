package localhost.abec2304.kurokohi.cp;

import java.io.PrintStream;

public class ConstFloat extends IntegerOrFloat {
    
    public int tag() {
        return 4;
    }
    
    public void print(PrintStream out) {
        out.print(Float.intBitsToFloat((int)bytes));
        out.print('F');
    }
    
}
