package localhost.abec2304.kurokohi.lazy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import localhost.abec2304.kurokohi.AttributeInfo;
import localhost.abec2304.kurokohi.AttrUnknown;
import localhost.abec2304.kurokohi.util.MultiByteArrayInputStream;

public class LazyAttrCode extends AttributeInfo {
    
    public int maxStack;
    public int maxLocals;
    public byte[] code;
    public int[][] exceptionTable;
    public AttributeInfo[] attributes;

    public boolean pre1_0;
    
    public void init(AttrUnknown base) throws IOException {
        attributeNameIndex = base.attributeNameIndex;
        attributeLength = base.attributeLength;
        
        InputStream sis = new MultiByteArrayInputStream(base.info);
        DataInputStream dis = new DataInputStream(sis);
        
        long codeLength;
        if(pre1_0) {
            maxStack = dis.readUnsignedByte();
            maxLocals = dis.readUnsignedByte();
            codeLength = dis.readUnsignedShort();
        } else {
            maxStack = dis.readUnsignedShort();
            maxLocals = dis.readUnsignedShort();
            codeLength = dis.readInt() & 0xFFFFFFFFL;
        }
        
        if(codeLength == 0 || codeLength >= 65536)
            throw new IOException("codeLength must be 1-65535, was " + codeLength);
        
        code = new byte[(int)codeLength];
        dis.readFully(code);
        
        exceptionTable = new int[dis.readUnsignedShort()][4];
        for(int i = 0; i < exceptionTable.length; i++) {
            int[] entry = exceptionTable[i];
            entry[0] = dis.readUnsignedShort();
            entry[1] = dis.readUnsignedShort();
            entry[2] = dis.readUnsignedShort();
            entry[3] = dis.readUnsignedShort();
        }
        
        attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < attributes.length; i++) {
            AttributeInfo attribute = new AttrUnknown();
            attribute.init(dis);
            attributes[i] = attribute;
        }
    }
    
    public void write(DataOutputStream dos) throws IOException {
        writeHeader(dos);
        if(pre1_0) {
            dos.writeByte(maxStack);
            dos.writeByte(maxLocals);
            dos.writeShort(code.length);
        } else {
            dos.writeShort(maxStack);
            dos.writeShort(maxLocals);
            dos.writeInt(code.length);
        }
        dos.write(code);
        dos.writeShort(exceptionTable.length);
        for(int i = 0; i < exceptionTable.length; i++) {
            int[] entry = exceptionTable[i];
            dos.writeShort(entry[0]);
            dos.writeShort(entry[1]);
            dos.writeShort(entry[2]);
            dos.writeShort(entry[3]);
        }
        dos.writeShort(attributes.length);
        for(int i = 0; i < attributes.length; i++)
            attributes[i].write(dos);
    }
    
    public void recalculateLength() {
        if(pre1_0)
            attributeLength = 4;
        else
            attributeLength = 8;
        
        attributeLength += code.length;
        attributeLength += 2;
        attributeLength += 4 * exceptionTable.length;
        attributeLength += 2; 
        attributeLength += 6 * attributes.length;
        
        for(int i = 0; i < attributes.length; i++)
            attributeLength += attributes[i].attributeLength;
    }
    
    public String getName() {
        return "Code";
    }
    
}
