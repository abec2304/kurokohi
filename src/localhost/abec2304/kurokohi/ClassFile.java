package localhost.abec2304.kurokohi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import localhost.abec2304.kurokohi.cp.ConstantPoolInfo;
import localhost.abec2304.kurokohi.cp.ConstUtf8;
import localhost.abec2304.kurokohi.cp.DoubleOrLong;
import localhost.abec2304.kurokohi.util.CharBuffer;

public class ClassFile implements Info {
    
    public long magic;
    public int minorVersion;
    public int majorVersion;
    public ConstantPoolInfo[] constantPool;
    public int accessFlags;
    public int thisClass;
    public int superClass;
    public int[] interfaces;
    public FieldInfo[] fields;
    public MethodInfo[] methods;
    public AttributeInfo[] attributes;
    
    public void init(DataInputStream dis) throws IOException {
        CharBuffer charBuffer = new CharBuffer();
        
        magic = dis.readInt() & 0xFFFFFFFFL;
        minorVersion = dis.readUnsignedShort();
        majorVersion = dis.readUnsignedShort();
        constantPool = new ConstantPoolInfo[dis.readUnsignedShort()];
        
        int n = 1;
        while(n < constantPool.length) {
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
        interfaces = new int[dis.readUnsignedShort()];
        for(int i = 0; i < interfaces.length; i++) {
            interfaces[i] = dis.readUnsignedShort();
        }
        fields = new FieldInfo[dis.readUnsignedShort()];
        for(int i = 0; i < fields.length; i++) {
            Info info = new FieldInfo();
            info.init(dis);
            fields[i] = (FieldInfo)info;
        }
        methods = new MethodInfo[dis.readUnsignedShort()];
        for(int i = 0; i < methods.length; i++) {
            Info info = new MethodInfo();
            info.init(dis);
            methods[i] = (MethodInfo)info;
        }
        attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < attributes.length; i++) {
            Info info = new AttrUnknown();
            info.init(dis);
            attributes[i] = (AttributeInfo)info;
        }
    }
    
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt((int)magic);
        dos.writeShort(minorVersion);
        dos.writeShort(majorVersion);
        dos.writeShort(constantPool.length);
        for(int i = 0; i < constantPool.length; i++) {
            if(constantPool[i] == null)
                continue;
            
            constantPool[i].write(dos);
        }
        dos.writeShort(accessFlags);
        dos.writeShort(thisClass);
        dos.writeShort(superClass);
        dos.writeShort(interfaces.length);
        for(int i = 0; i < interfaces.length; i++)
            dos.writeShort(interfaces[i]);
        dos.writeShort(fields.length);
        for(int i = 0; i < fields.length; i++)
            fields[i].write(dos);
        dos.writeShort(methods.length);
        for(int i = 0; i < methods.length; i++)
            methods[i].write(dos);
        dos.writeShort(attributes.length);
        for(int i = 0; i < attributes.length; i++)
            attributes[i].write(dos);
    }
    
    public String getUtf8(int i) {
        ConstantPoolInfo cpi;
        if(i > constantPool.length || !((cpi = constantPool[i]) instanceof ConstUtf8))
            return "???";
        
        return ((ConstUtf8)cpi).string;
    }
    
}
