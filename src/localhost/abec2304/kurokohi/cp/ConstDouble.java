package localhost.abec2304.kurokohi.cp;

public class ConstDouble extends DoubleOrLong {
    
    public String toString() {
        return Double.longBitsToDouble((highBytes << 32) + lowBytes) + "d";
    }
    
}
