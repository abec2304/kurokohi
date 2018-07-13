package localhost.abec2304.kurokohi;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class CodeWalker {

    public int opcode;
    public int operandsPos;
    public int[] operandValues;

    private byte[] operands;
    private int len;
    private ByteArrayInputStream bais;
    private DataInputStream cis;
    
    public static final int DO_STEP = 1;
    public static final int NEXT_STEP = 2;
    public static final int COMPLEX_STEP = 3;
    public static final int END = 4;
    
    public void init(byte[] code) {
        cis = new DataInputStream(bais = new ByteArrayInputStream(code));
        len = code.length;
    }
    
    public int pos() {
        return len - bais.available();
    }

    public boolean stepOver() throws IOException {
        if(bais.available() == 0) {
            operandsPos = -1;
            return false;
        }
        
        opcode = cis.readUnsignedByte();
        operandsPos = 0;
        operands = Operand.TYPE_OF_OPERANDS[opcode];
        operandValues = operands == null ? null : new int[operands.length];
        
        if(operands == null)
            return true;
        
        return false;
    }
    
    public int nextStep() throws IOException {
        if(operandsPos < 0)
            return END;
        else if(operandsPos == operands.length)
            return DO_STEP;
        
        int operand;
        switch(operands[operandsPos]) {
            case Operand.CONST_S8:
            case Operand.ZERO_S8:
            case Operand.ATYPE_S8:
                operand = cis.readByte();
                break;
            case Operand.CONST_S16:
            case Operand.BRANCH_S16:
                operand = cis.readShort();
                break;
            case Operand.CP_INDEX_U8:
            case Operand.LV_INDEX_U8:
            case Operand.NONZERO_U8:
            case Operand.OPCODE_U8:
                operand = cis.readUnsignedByte();
                break;
            case Operand.CP_INDEX_U16:
            case Operand.LV_INDEX_U16:
                operand = cis.readUnsignedShort();
                break;
            case Operand.BRANCH_S32:
            case Operand.CONST_S32:
                operand = cis.readInt();
                break;
            case Operand.WIDE_IINC_CONST_S16:
                if(operandValues[0] == 132) { // iinc
                    operand = cis.readShort();
                } else {
                    operand = '\uffff' + 1;
                }
                break;
            default:
                return COMPLEX_STEP;
        }
        
        operandValues[operandsPos++] = operand;
        
        if(operandsPos < operands.length)
            return NEXT_STEP;
        else
            return DO_STEP;
    }
    
    public Object multiStep() throws IOException {
        Object array;
        switch(operands[operandsPos]) {
            case Operand.ATABLE:
                int jumps = operandValues[3] - operandValues[2] + 1;
                array = new int[jumps];
                for(int m = 0; m < jumps; m++) {
                    ((int[])array)[m] = cis.readInt();
                }
                break;
            case Operand.ALOOKUP:
                int pairs = operandValues[2];
                array = new int[pairs][];
                for(int m = 0; m < pairs; m++) {
                    int[] pair = {cis.readInt(), cis.readInt()};
                    ((int[][])array)[m] = pair;
                }
                break;
            case Operand.ALIGN4:
                int pos = pos();
                int padding = (((pos | 3) + 1) - pos) % 4;
                array = new int[padding];
                for(int m = 0; m < padding; m++) {
                    ((int[])array)[m] = cis.readByte();
                }
                break;
            default:
                return null;
        }
        
        operandsPos++;
        return array;
    }

}
