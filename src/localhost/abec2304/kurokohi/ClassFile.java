package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.IOException;
import localhost.abec2304.kurokohi.cp.ConstantPoolInfo;
import localhost.abec2304.kurokohi.cp.ConstUtf8;
import localhost.abec2304.kurokohi.cp.DoubleOrLong;
import localhost.abec2304.kurokohi.util.CharBuffer;

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
    public int[] interfaces;
    public int fieldsCount;
    public FieldInfo[] fields;
    public int methodsCount;
    public MethodInfo[] methods;
    public int attributesCount;
    public AttributeInfo[] attributes;
    
    public void init(DataInputStream dis) throws IOException {
        CharBuffer charBuffer = new CharBuffer();
        
        magic = dis.readInt() & 0xFFFFFFFFL;
        minorVersion = dis.readUnsignedShort();
        majorVersion = dis.readUnsignedShort();
        constantPoolCount = dis.readUnsignedShort();
        constantPool = new ConstantPoolInfo[constantPoolCount];
        
        int n = 1;
        while(n < constantPoolCount) {
            int tag = dis.readUnsignedByte();  
            
            ConstantPoolInfo info = ConstantPoolInfo.getConstant(tag);
            
            if(info == null)
                throw new IOException("truncated constant pool");
            
            info.init(dis);
            constantPool[n] = info;
            
            if(info instanceof ConstUtf8) {
                try {
                    ((ConstUtf8)info).initString(charBuffer);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            } else if(info instanceof DoubleOrLong) {
                n += 2;
                continue;
            }
            
            n += 1;
        }
        
        accessFlags = dis.readUnsignedShort();
        thisClass = dis.readUnsignedShort();
        superClass = dis.readUnsignedShort();
        interfacesCount = dis.readUnsignedShort();
        interfaces = new int[interfacesCount];
        for(int i = 0; i < interfacesCount; i++) {
            interfaces[i] = dis.readUnsignedShort();
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
