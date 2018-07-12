package localhost.abec2304.kurokohi;

public class AccessFlag {

    // all types
    public static final int PUBLIC = 0x0001;
    public static final int FINAL = 0x0010;
    public static final int SYNTHETIC = 0x1000;

    // class
    public static final int SUPER = 0x0020;
    public static final int MODULE = 0x8000;

    // field
    public static final int VOLATILE = 0x0040;
    public static final int TRANSIENT = 0x0080;

    // method
    public static final int SYNCHRONIZED = 0x0020;
    public static final int BRIDGE = 0x0040;
    public static final int VARARGS = 0x0080;
    public static final int NATIVE = 0x0100;
    public static final int STRICT = 0x0800;

    // nested class, field, method
    public static final int PRIVATE = 0x0002;
    public static final int PROTECTED = 0x0004;
    public static final int STATIC = 0x0008;

    // nested class, class
    public static final int INTERFACE = 0x0200;
    public static final int ANNOTATION = 0x2000;

    // nested class, class, field
    public static final int ENUM = 0x4000;

    // nested class, class, method
    public static final int ABSTRACT = 0x0400;

    public static int set(int acc, int flag) {
        return acc | flag;
    }
    
    public static int unset(int acc, int flag) {
        if(flag != (acc & flag))
            return acc;
        
        return acc ^ flag;
    }
    
}
