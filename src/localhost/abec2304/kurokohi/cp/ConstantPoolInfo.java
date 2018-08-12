package localhost.abec2304.kurokohi.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import localhost.abec2304.kurokohi.Info;

public abstract class ConstantPoolInfo implements Info, Cloneable {

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
    private static final ConstantPoolInfo[] NEWMAPPING = getNewMapping();
    
    public int tag;

    public String getName() {
        return tag < NAMES.length && tag >= 0 ? NAMES[tag] : null;
    }
    
    public static void main(String[] args) {
        for(int i = 0; i < NEWMAPPING.length; i++) {
            System.out.println(i + "[" + NEWMAPPING[i] + "]");
        }
    }
    
    public static ConstantPoolInfo getConstant(int tag) {
        ConstantPoolInfo info;
        if(tag < NEWMAPPING.length) {
            info = NEWMAPPING[tag];
        } else {
            return null;
        }
        
        if(info != null) {
            info = (ConstantPoolInfo)info.clone();
            info.tag = tag;
            return info;
        }
        
        return null;
    }
    
    private static ConstantPoolInfo[] getNewMapping() {
        String cls = new ConstClass().getClass().getName();
        String prefix = cls.substring(0, cls.indexOf("Const") + 5);
        
        ConstantPoolInfo[] instances = new ConstantPoolInfo[NAMES.length];
        for(int i = 0; i < NAMES.length; i++) {
            if(NAMES[i] == null)
                continue;
            
            try {
                instances[i] = (ConstantPoolInfo)Class.forName(prefix + NAMES[i]).newInstance();
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch(IllegalAccessException iae) {
                iae.printStackTrace();
            } catch(InstantiationException ie) {
                ie.printStackTrace();
            }
        }
        
        return instances;
    }
    
    protected Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException cnse) {
            return null;
        }
    }
    
    public abstract void print(PrintStream out);
    
}
