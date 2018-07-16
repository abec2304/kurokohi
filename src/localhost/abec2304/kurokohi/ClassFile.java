package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;
import localhost.abec2304.kurokohi.cp.ConstantPoolInfo;

public class ClassFile {
    
    public long magic;
    public int minorVersion;
    public int majorVersion;
    public int constantPoolCount;
    public ConstantPoolInfo[] constantPool;
    public int accessFlags;
    public int thisClass;
    public int superClass;
    public int interfacesCount;
    public InterfaceInfo[] interfaces; // move array to class
    public int fieldsCount;
    public FieldInfo[] fields;
    public int methodsCount;
    public MethodInfo[] methods;
    public int attributesCount;
    public AttributeInfo[] attributes;
    
    public void init(DataInputStream dis) throws IOException {
        magic = dis.readInt() & 0xFFFFFFFFl;
        minorVersion = dis.readUnsignedShort();
        majorVersion = dis.readUnsignedShort();
        constantPoolCount = dis.readUnsignedShort();
        constantPool = new ConstantPoolInfo[constantPoolCount];
        for(int i = 1; i < constantPoolCount; i++) {
            dis.mark(1);
            int tag = dis.readUnsignedByte();
            dis.reset();
            
            Class clazz;
            if(tag < ConstantPoolInfo.MAPPING.length) {
                clazz = ConstantPoolInfo.MAPPING[tag];
            } else {
                clazz = null;
            }
            
            if(clazz == null)
                throw new IOException("truncated constant pool");
            
            try {
                Info info = (Info)clazz.newInstance();
                info.init(dis);
                constantPool[i] = (ConstantPoolInfo)info;
            } catch(IllegalAccessException iae) {
                iae.printStackTrace();
            } catch(InstantiationException ie) {
                ie.printStackTrace();
            }
            
            if(tag == 5 || tag == 6)
                i++;
        }
        accessFlags = dis.readUnsignedShort();
        thisClass = dis.readUnsignedShort();
        superClass = dis.readUnsignedShort();
        interfacesCount = dis.readUnsignedShort();
        interfaces = new InterfaceInfo[interfacesCount];
        for(int i = 0; i < interfacesCount; i++) {
            Info info = new InterfaceInfo();
            info.init(dis);
            interfaces[i] = (InterfaceInfo)info;
        }
        fieldsCount = dis.readUnsignedShort();
        fields = new FieldInfo[fieldsCount];
        for(int i = 0; i < fieldsCount; i++) {
            Info info = new FieldInfo();
            info.init(dis);
            fields[i] = (FieldInfo)info;
        }
        methodsCount = dis.readUnsignedShort();
        methods = new MethodInfo[methodsCount];
        for(int i = 0; i < methodsCount; i++) {
            Info info = new MethodInfo();
            info.init(dis);
            methods[i] = (MethodInfo)info;
        }
        attributesCount = dis.readUnsignedShort();
        attributes = new AttributeInfo[attributesCount];
        for(int i = 0; i < attributesCount; i++) {
            Info info = new AttrUnknown();
            info.init(dis);
            attributes[i] = (AttributeInfo)info;
        }
    }
    
}
