package localhost.abec2304.kurokohi.cp;

import java.io.PrintStream;

public class ConstDouble extends DoubleOrLong {
    
    public void print(PrintStream out) {
        out.print(Double.longBitsToDouble((highBytes << 32) + lowBytes));
        out.print('D');
    }
    
}
