package localhost.abec2304.kurokohi.cp;

import java.io.PrintStream;

public class ConstLong extends DoubleOrLong {
    
    public void print(PrintStream out) {
        out.print((highBytes << 32) + lowBytes);
        out.print('L');
    }
    
}
