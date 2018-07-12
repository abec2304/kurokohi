package localhost.abec2304.kurokohi.cp;

public class ConstFloat extends IntegerOrFloat {
    
    public String toString() {
        return Float.intBitsToFloat((int)bytes) + "f";
    }
    
}
