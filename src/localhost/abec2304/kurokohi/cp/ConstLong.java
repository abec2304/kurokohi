package localhost.abec2304.kurokohi.cp;

public class ConstLong extends DoubleOrLong {
    
    public String toString() {
        return (highBytes << 32) + lowBytes + "L";
    }
    
}
