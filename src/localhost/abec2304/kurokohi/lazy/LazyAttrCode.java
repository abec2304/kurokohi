package localhost.abec2304.kurokohi.lazy;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import localhost.abec2304.kurokohi.AttributeInfo;
import localhost.abec2304.kurokohi.AttrUnknown;

public class LazyAttrCode extends AttributeInfo {

    public int maxStack;
    public int maxLocals;
    public long codeLength;
    public byte[] code;
    public int exceptionTableLength;
    public int[][] exceptionTable;
    public int attributesCount;
    public AttributeInfo[] attributes;

    public void init(AttrUnknown base) throws IOException {
        if(base.info == null)
            throw new IllegalArgumentException("uninitialized attribute");
        
        InputStream sis = new BufferedInputStream(new SequenceInputStream(base.info.elements()));
        DataInputStream dis = new DataInputStream(sis);
        
        int maxStack = dis.readUnsignedShort();
        int maxLocals = dis.readUnsignedShort();
        long codeLength = dis.readInt() & 0xFFFFFFFFl;
        
        if(codeLength <= 0 || codeLength >= 65536)
            throw new IllegalArgumentException("codeLength: 0 < " + codeLength + " < 65536 = false");
        
        this.attributeNameIndex = base.attributeNameIndex;
        this.attributeLength = base.attributeLength;
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.codeLength = codeLength;
        
        this.code = new byte[(int)codeLength];
        dis.readFully(code);
        
        exceptionTableLength = dis.readUnsignedShort();
        exceptionTable = new int[exceptionTableLength][];
        for(int i = 0; i < exceptionTableLength; i++) {
            int[] exc = {
                dis.readUnsignedShort(),
                dis.readUnsignedShort(),
                dis.readUnsignedShort(),
                dis.readUnsignedShort()
            };
            exceptionTable[i] = exc;
        }
        
        attributesCount = dis.readUnsignedShort();
        attributes = new AttributeInfo[attributesCount];
        for(int i = 0; i < attributesCount; i++) {
            AttributeInfo attribute = new AttrUnknown();
            attribute.init(dis);
            attributes[i] = attribute;
        }
    }
    
}
