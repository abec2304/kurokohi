package localhost.abec2304.kurokohi.lazy;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import localhost.abec2304.kurokohi.AttributeInfo;
import localhost.abec2304.kurokohi.AttrUnknown;
import localhost.abec2304.kurokohi.util.MultiByteArrayInputStream;

public class LazyAttrCode extends AttributeInfo {

    public int maxStack;
    public int maxLocals;
    public long codeLength;
    public byte[] code;
    public int exceptionTableLength;
    public int[][] exceptionTable;
    public int attributesCount;
    public AttributeInfo[] attributes;

    public boolean pre1_0;
    
    public void init(AttrUnknown base) throws IOException {
        if(base.info == null)
            base.info = new byte[0][];
        
        InputStream sis = new MultiByteArrayInputStream(base.info);
        DataInputStream dis = new DataInputStream(sis);
        
        if(pre1_0) {
            maxStack = dis.readUnsignedByte();
            maxLocals = dis.readUnsignedByte();
            codeLength = dis.readUnsignedShort();
        } else {
            maxStack = dis.readUnsignedShort();
            maxLocals = dis.readUnsignedShort();
            codeLength = dis.readInt() & 0xFFFFFFFFL;
        }
        
        if(codeLength <= 0 || codeLength >= 65536)
            throw new IOException("codeLength: 0 < " + codeLength + " < 65536 = false");
        
        this.attributeNameIndex = base.attributeNameIndex;
        this.attributeLength = base.attributeLength;
        
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
