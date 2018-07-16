package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import localhost.abec2304.kurokohi.Info;

public abstract class ConstantPoolInfo implements Info {

    public static String[] NAMES = {
        /*00*/null,
        /*01*/"Utf8",
        /*02*/"Bytes",
        /*03*/"Integer",
        /*04*/"Float",
        /*05*/"Long",
        /*06*/"Double",
        /*07*/"Class",
        /*08*/"String",
        /*09*/"Fieldref",
        /*10*/"Methodref",
        /*11*/"InterfaceMethodref",
        /*12*/"NameAndType",
        /*13*/null,
        /*14*/null,
        /*15*/"MethodHandle",
        /*16*/"MethodType",
        /*17*/"Dynamic",
        /*18*/"InvokeDynamic",
        /*19*/"Module",
        /*20*/"Package"
    };

    // must be after NAMES
    public static final Class[] MAPPING = getMapping();
    
    public int tag;
    
    public void init(DataInputStream dis) throws IOException {
        tag = dis.readUnsignedByte();
    }

    public String getName() {
        return tag < NAMES.length && tag >= 0 ? NAMES[tag] : null;
    }
    
    public static void main(String[] args) {
        for(int i = 0; i < MAPPING.length; i++) {
            System.out.println(i + "[" + MAPPING[i] + "]");
        }
    }

    private static Class[] getMapping() {
        String cls = new ConstClass().getClass().getName();
        String prefix = cls.substring(0, cls.indexOf("Const") + 5);
        
        Class[] classes = new Class[NAMES.length];
        for(int i = 0; i < NAMES.length; i++) {
            if(NAMES[i] == null)
                continue;
            
            try {
                classes[i] = Class.forName(prefix + NAMES[i]);
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
        
        return classes;
    }
    
}
