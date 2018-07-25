package localhost.abec2304.kurokohi;

public class CodeWalker {

    public byte[] code;
    public int len;
    public int pos;

    public int opcode;
    public int operandsPos;
    public final int[] operandValues = new int[8];
    public byte[] operands;
    
    public static final int END = -1;
    public static final int DO_STEP = 0;
    public static final int DO_MORE = 1;
    
    public static final int NEXT_STEP = 1;
    public static final int COMPLEX_STEP = -1;
    
    public void init(byte[] arr) {
        code = arr;
        len = code.length;
        pos = 0;
    }

    public int stepOver() {
        if(pos == len)
            return END;
        
        opcode = readUnsignedByte();
        operands = Operand.TYPE_OF_OPERANDS[opcode];
        
        if(operands == null)
            return DO_STEP;
        
        operandsPos = 0;
        
        return DO_MORE;
    }
    
    public int nextStep() {
        int operand;
        switch(operands[operandsPos]) {
            case Operand.CONST_S8:
            case Operand.ZERO_S8:
            case Operand.ATYPE_S8:
                operand = readByte();
                break;
            case Operand.CONST_S16:
            case Operand.BRANCH_S16:
                operand = readShort();
                break;
            case Operand.CP_INDEX_U8:
            case Operand.LV_INDEX_U8:
            case Operand.NONZERO_U8:
            case Operand.OPCODE_U8:
                operand = readUnsignedByte();
                break;
            case Operand.CP_INDEX_U16:
            case Operand.LV_INDEX_U16:
                operand = readUnsignedShort();
                break;
            case Operand.BRANCH_S32:
            case Operand.CONST_S32:
                operand = readInt();
                break;
            case Operand.WIDE_IINC_CONST_S16:
                if(operandValues[0] == 132) { // iinc
                    operand = readShort();
                } else {
                    operand = '\uffff' + 1;
                }
                break;
            default:
                operandValues[operandsPos] = -1;
                return COMPLEX_STEP;
        }
        
        operandValues[operandsPos++] = operand;
        
        if(operandsPos < operands.length)
            return NEXT_STEP;
        else
            return DO_STEP;
    }
    
    public Object multiStep() {
        Object array;
        switch(operands[operandsPos]) {
            case Operand.ATABLE:
                int jumps = operandValues[3] - operandValues[2] + 1;
                array = new int[jumps];
                for(int m = 0; m < jumps; m++) {
                    ((int[])array)[m] = readInt();
                }
                break;
            case Operand.ALOOKUP:
                int pairs = operandValues[2];
                array = new int[pairs][];
                for(int m = 0; m < pairs; m++) {
                    int[] pair = {readInt(), readInt()};
                    ((int[][])array)[m] = pair;
                }
                break;
            case Operand.ALIGN4:
                int padding = (((pos | 3) + 1) - pos) % 4;
                array = new int[padding];
                for(int m = 0; m < padding; m++) {
                    ((int[])array)[m] = readByte();
                }
                break;
            default:
                return null;
        }
        
        operandsPos++;
        return array;
    }
    
    public byte readByte() {
        return code[pos++];
    }
    
    public int readUnsignedByte() {
        return code[pos++] & 0xFF;
    }
    
    public short readShort() {
        return (short)(((code[pos++] & 0xFF) << 8) + (code[pos++] & 0xFF));
    }
    
    public int readUnsignedShort() {
        return ((code[pos++] & 0xFF) << 8) + (code[pos++] & 0xFF);
    }
    
    public int readInt() {
        return ((code[pos++] & 0xFF) << 24) + ((code[pos++] & 0xFF) << 16) + ((code[pos++] & 0xFF) << 8) + (code[pos++] & 0xFF);
    }
    
}
