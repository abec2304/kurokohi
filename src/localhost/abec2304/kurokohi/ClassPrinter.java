package localhost.abec2304.kurokohi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import localhost.abec2304.kurokohi.cp.ConstantPoolInfo;
import localhost.abec2304.kurokohi.cp.ConstUtf8;
import localhost.abec2304.kurokohi.lazy.LazyAttrCode;

public class ClassPrinter {
    
    public String className;
    public ClassFile cf;
    public PrintStream out;
    
    public ClassPrinter(String className, ClassFile cf, PrintStream out) {
        this.className = className;
        this.cf = cf;
        this.out = out;
    }
    
    public static String padBegin(String s, int len) {
        if(s.length() >= len)
            return s;
        
        char[] c = new char[len - s.length()];
        for(int i = 0; i < c.length; i++)
            c[i] = ' ';
        
        return new String(c).concat(s);
    }
    
    public static String padEnd(String s, int len) {
        if(s.length() >= len)
            return s;
        
        char[] c = new char[len - s.length()];
        for(int i = 0; i < c.length; i++)
            c[i] = ' ';
        
        return s.concat(new String(c));
    }
    
    public static String getUtf8(ConstantPoolInfo[] cp, int i) {
        if(i > cp.length || !(cp[i] instanceof ConstUtf8))
            return "???";
        
        return ((ConstUtf8)cp[i]).string;
    }
    
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream(args[0]);
        print(args[0], fis, System.out);
    }
    
    public static void print(String className, InputStream is, PrintStream out) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        ClassFile cf = new ClassFile();
        cf.init(dis);
        //dis.close();
        new ClassPrinter(className, cf, out).print();
    }
    
    public void print() throws IOException {
        out.println("class " + className);
        out.println("  magic: 0x" + Long.toString(cf.magic, 16));
        out.println("  minor version: " + cf.minorVersion);
        out.println("  major version: " + cf.majorVersion);
        out.println("  flags: 0x" + Integer.toString(cf.accessFlags, 16));
        out.println("  this class: #" + cf.thisClass);
        out.println("  super class: #" + cf.superClass);
        
        String interfaces = "  interfaces:";
        for(int i = 0; i < cf.interfaces.length; i++) {
            interfaces += " #" + cf.interfaces[i].index;
        }
        out.println(interfaces);
        
        out.println("Constant pool:");
        for(int i = 0; i < cf.constantPool.length; i++) {
            ConstantPoolInfo constant = cf.constantPool[i];
            
            String type;
            if(constant == null)
                type = "null";
            else
                type = constant.getName();

            if(type == null)
                type = "???";
            
            String text = padBegin("#" + i, 8) + " = " + type;
            
            if(constant != null) {
                text += padBegin(" ", 30 - text.length()) + constant;
            }
            
            out.println(text);
        }
        
        out.println("{");
        out.println();
        
        for(int i = 0; i < cf.fields.length; i++) {
            FieldInfo field = cf.fields[i];
            String fieldName = getUtf8(cf.constantPool, field.nameIndex);
            String fieldDesc = getUtf8(cf.constantPool, field.descriptorIndex);
            out.println("  #" + field.nameIndex + " //" + fieldName);
            out.println("    descriptor: #" + field.descriptorIndex + " //" + fieldDesc);
            out.println("    flags: 0x" + Integer.toString(field.accessFlags, 16));
            printAttributes(field.attributes, "    ");
            out.println();
        }
       
        for(int i = 0; i < cf.methods.length; i++) {
            MethodInfo method = cf.methods[i];
            String methodName = getUtf8(cf.constantPool, method.nameIndex);
            String methodDesc = getUtf8(cf.constantPool, method.descriptorIndex);
            out.println("  #" + method.nameIndex + " //" + methodName);
            out.println("    descriptor: #" + method.descriptorIndex + " //" + methodDesc);
            out.println("    flags: 0x" + Integer.toString(method.accessFlags, 16));
            for(int j = 0; j < method.attributes.length; j++) {
                AttributeInfo attribute = method.attributes[j];
                String attributeName = getUtf8(cf.constantPool, attribute.attributeNameIndex);
                out.println("    " + attributeName + ":");
                if(attribute instanceof AttrUnknown && "Code".equals(attributeName)) {
                    dumpCode((AttrUnknown)attribute);
                } else {
                    out.println("      NYI");
                }
            }
            
            out.println();
        }
        
        out.println("}");
        
        printAttributes(cf.attributes, "");
    }

    public void printAttributes(AttributeInfo[] attributes, String padding) {
        for(int j = 0; j < attributes.length; j++) {
            AttributeInfo attribute = attributes[j];
            String attributeName = getUtf8(cf.constantPool, attribute.attributeNameIndex);
            out.println(padding + attributeName + ":");
            out.println(padding + "  NYI");
        }
    }
    
    public void dumpCode(AttrUnknown attribute) throws IOException {
        LazyAttrCode code = new LazyAttrCode();
        code.init(attribute);
        
        out.println("      stack=" + code.maxStack + ", locals=" + code.maxLocals + ", len=" + code.codeLength);
        
        CodeWalker walker = new CodeWalker(code.code);
        walk: for(;;) {
            int initial = walker.pos();
            if(walker.stepOver()) {
                String padded = padBegin(initial + "", 10);
                String name = Opcode.OPCODE_NAMES[walker.opcode];
                out.println(padded + ": " + name + " //0x" + Integer.toString(walker.opcode, 16));
                continue;
            }
            
            for(;;) {
                int nextStep = walker.nextStep();
                if(nextStep == walker.NEXT_STEP)
                    continue;
                else if(nextStep == walker.COMPLEX_STEP)
                    printComplex(out, walker.multiStep());
                else if(nextStep == walker.END)
                    break walk;
                
                if(nextStep != walker.DO_STEP)
                    continue;
                
                String name = Opcode.OPCODE_NAMES[walker.opcode] + "";
                String padded = padBegin(initial + "", 10);
                out.println(padded + ": " + padEnd(name, 16) + toString(walker.operandValues, ' '));
                break;
            }
        }
        
        int exceptionTableLength = code.exceptionTableLength;
        
        if(exceptionTableLength > 0) {
            out.println("      Exception table:");
            out.println("         from,to,target,type");
        }
        
        for(int k = 0; k < exceptionTableLength; k++) {
            int startPc = code.exceptionTable[k][0];
            int endPc = code.exceptionTable[k][1];
            int handlerPc = code.exceptionTable[k][2];
            int catchType = code.exceptionTable[k][3];
            out.println("         " + startPc + "," + endPc + "," + handlerPc + "," + catchType);
        }
        
        printAttributes(code.attributes, "      ");
    }

    public void printComplex(PrintStream out, Object o) {
        if(o instanceof int[][]) {
            int[][] pairs = (int[][])o;
        
            for(int i = 0; i < pairs.length; i++) {
                out.println("[" + pairs[i][0] + ":" + pairs[i][1] + "]");
            }
        } else if(o instanceof int[]) {
            int[] array = (int[])o;
            out.println("[" + toString(array, ',') + "]");
        } else {
            //
        }
    }
    
    public static String toString(int[] array, char c) {
        if(array == null || array.length < 1)
            return "";
        
        String chr = new Character(c).toString();
        
        String s = Integer.toString(array[0]);
        for(int i = 1; i < array.length; i++) {
            s += chr.concat(Integer.toString(array[i]));
        }
        
        return s;
    }
    
}