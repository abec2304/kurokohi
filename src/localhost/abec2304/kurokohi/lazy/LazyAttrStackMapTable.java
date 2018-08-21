package localhost.abec2304.kurokohi.lazy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import localhost.abec2304.kurokohi.AttributeInfo;
import localhost.abec2304.kurokohi.AttrUnknown;
import localhost.abec2304.kurokohi.util.MultiByteArrayInputStream;

public class LazyAttrStackMapTable extends AttributeInfo {

    public int[] frames;
    public int[] deltas;
    public int[][][] locals;
    public int[][][] stackItems;
    
    // same
    public static final short SAME_MIN = 0;
    public static final short SAME_MAX = 63;
    
    // has 1 item
    public static final short SAME_LOCALS_1_STACK_MIN = 64;
    public static final short SAME_LOCALS_1_STACK_MAX = 127;
    
    // unused
    public static final short RESERVED_MIN = 128;
    public static final short RESERVED_MAX = 246;
    
    // has delta + 1 item
    public static final short SAME_LOCALS_1_STACK_W = 247;
    
    // has delta
    public static final short CHOP_MIN = 248;
    public static final short CHOP_MAX = 250;
    
    // has delta
    public static final short SAME_W = 251;
    
    // has delta + n items
    public static final short APPEND_MIN = 252;
    public static final short APPEND_MAX = 254;
    
    // has delta, x items, y items
    public static final short FULL_FRAME = 255;
    
    public static final short TYPE_OBJECT = 7;
    public static final short TYPE_UNINITIALIZED = 8;
    
    public void init(AttrUnknown base) throws IOException {
        attributeNameIndex = base.attributeNameIndex;
        attributeLength = base.attributeLength;
        
        InputStream sis = new MultiByteArrayInputStream(base.info);
        DataInputStream dis = new DataInputStream(sis);
        
        int numberOfEntries = dis.readUnsignedShort();
        
        frames = new int[numberOfEntries];
        deltas = new int[numberOfEntries + 1];
        locals = new int[numberOfEntries][][];
        stackItems = new int[numberOfEntries][][];
        
        int offset = -1;
        
        for(int i = 0; i < numberOfEntries; i++) {
            int frameType = dis.readUnsignedByte();
            
            int numberOfStackItems = 0;
            int numberOfLocals = 0;
            
            int offsetDelta;
            if(frameType >= SAME_MIN && frameType <= SAME_MAX) {
                frames[i] = SAME_MIN;
                offsetDelta = frameType;
            } else if(frameType >= SAME_LOCALS_1_STACK_MIN && frameType <= SAME_LOCALS_1_STACK_MAX) {
                frames[i] = SAME_LOCALS_1_STACK_MIN;
                offsetDelta = frameType - SAME_LOCALS_1_STACK_MIN;
                numberOfStackItems = 1;
            } else if(frameType >= APPEND_MIN && frameType <= APPEND_MAX) {
                frames[i] = APPEND_MIN;
                offsetDelta = dis.readUnsignedShort();
                numberOfLocals = frameType - (APPEND_MIN - 1);
            } else {
                frames[i] = frameType;
                if(frameType == SAME_LOCALS_1_STACK_W) {
                    offsetDelta = dis.readUnsignedShort();
                    numberOfStackItems = 1;
                } else if(frameType >= CHOP_MIN && frameType <= CHOP_MAX) {
                    offsetDelta = dis.readUnsignedShort();
                } else if(frameType == SAME_W) {
                    offsetDelta = dis.readUnsignedShort();
                } else if(frameType == FULL_FRAME) {
                    offsetDelta = dis.readUnsignedShort();
                    numberOfLocals = dis.readUnsignedShort();
                    numberOfStackItems = -1;
                } else {
                    throw new IOException("invalid stackmap");
                }
            }
            
            locals[i] = new int[numberOfLocals][2];
            for(int j = 0; j < numberOfLocals; j++) {
                int[] pair = locals[i][j];
                int verificationType = dis.readByte();
                pair[0] = verificationType;
                if(verificationType == TYPE_OBJECT || verificationType == TYPE_UNINITIALIZED)
                    pair[1] = dis.readUnsignedShort();
            }
            
            if(numberOfStackItems == -1)
                numberOfStackItems = dis.readUnsignedShort();
            
            stackItems[i] = new int[numberOfStackItems][2];
            for(int j = 0; j < numberOfStackItems; j++) {
                int[] pair = stackItems[i][j];
                int verificationType = dis.readByte();
                pair[0] = verificationType;
                if(verificationType == TYPE_OBJECT || verificationType == TYPE_UNINITIALIZED)
                    pair[1] = dis.readUnsignedShort();
            }
            
            deltas[i] = offsetDelta;
            offset += offsetDelta + 1;
        }
        
        deltas[numberOfEntries] = offset;
    }
    
    public void write(DataOutputStream dos) throws IOException {
        throw new Error("NYI");
    }
    
    public void recalculateLength() {
        throw new Error("NYI");
    }
    
    public String getName() {
        return "StackMapTable";
    }
    
}
