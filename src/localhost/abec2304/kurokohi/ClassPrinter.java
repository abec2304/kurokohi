package localhost.abec2304.kurokohi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    
    public void printPadding(int len) {
        if(len < 0)
            len = 0;
        
        char[] c = new char[len];
        for(int i = 0; i < len; i++)
            c[i] = ' ';
        out.print(c);
    }
    
    public static String getUtf8(ConstantPoolInfo[] cp, int i) {
        if(i > cp.length || !(cp[i] instanceof ConstUtf8))
            return "???";
        
        return ((ConstUtf8)cp[i]).string;
    }
    
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedInputStream bis = new BufferedInputStream(fis);
        print(args[0], bis, System.out);
    }
    
    public static void print(String className, InputStream is, OutputStream out) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        RapidPrinter rp = new RapidPrinter(out);
        rp.printMarker();
        print(className, bis, rp);
    }
    
    public static void print(String className, BufferedInputStream is, PrintStream out) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        ClassFile cf = new ClassFile();
        cf.init(dis);
        ClassPrinter classPrinter = new ClassPrinter(className, cf, out);
        classPrinter.print();
    }
    
    public void print() {
        out.print("class ");
        out.println(className);
        out.print("  magic: 0x");
        out.println(Long.toString(cf.magic, 16));
        out.print("  minor version: ");
        out.println(cf.minorVersion);
        out.print("  major version: ");
        out.println(cf.majorVersion);
        out.print("  flags: 0x");
        out.println(Integer.toString(cf.accessFlags, 16));
        out.print("  this class: #");
        out.println(cf.thisClass);
        out.print("  super class: #");
        out.println(cf.superClass);
        
        String interfaces = "  interfaces:";
        for(int i = 0; i < cf.interfaces.length; i++) {
            String next = " #".concat(Integer.toString(cf.interfaces[i].index));
            interfaces = interfaces.concat(next);
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
            
            String index = Integer.toString(i);
            int indexPad = 7 - index.length();
            printPadding(indexPad);
            out.print("#".concat(index));
            out.print(" = ");
            out.print(type);
            
            if(constant != null) {
                printPadding(19 - type.length());
                out.print(constant.toString());
            }
            
            out.println();
        }
        
        out.println("{");
        out.println();
        
        out.print("fields: ");
        out.println(cf.fieldsCount);
        printMemberArray(cf.fields);
        out.println();
        out.print("methods: ");
        out.println(cf.methodsCount);
        printMemberArray(cf.methods);
        
        out.println("}");
        
        printAttributes(cf.attributes, "");
        
        out.flush();
    }

    public void printMemberArray(MemberInfo[] members) {
        for(int i = 0; i < members.length; i++) {
            MemberInfo member = members[i];
            String memberName = getUtf8(cf.constantPool, member.nameIndex);
            String memberDesc = getUtf8(cf.constantPool, member.descriptorIndex);
            out.println("  #" + member.nameIndex + " //" + memberName);
            out.println("    descriptor: #" + member.descriptorIndex + " //" + memberDesc);
            out.println("    flags: 0x" + Integer.toString(member.accessFlags, 16));
            printAttributes(member.attributes, "    ", member);
            out.println();
        }
    }
    
    public void printAttributes(AttributeInfo[] attributes, String padding) {
        printAttributes(attributes, padding, null);
    }
    
    public void printAttributes(AttributeInfo[] attributes, String padding, Info info) {
        boolean isMethod = info instanceof MethodInfo;
        for(int j = 0; j < attributes.length; j++) {
            AttributeInfo attribute = attributes[j];
            String attributeName = getUtf8(cf.constantPool, attribute.attributeNameIndex);
            out.println(padding + attributeName + ":");
            if(isMethod && attribute instanceof AttrUnknown) {
                if("Code".equals(attributeName)) {
                    try {
                        dumpCode((AttrUnknown)attribute);
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                    continue;
                }
            }
            out.println(padding + "  NYI");
        }
    }
    
    public void dumpCode(AttrUnknown attribute) throws IOException {
        LazyAttrCode code = new LazyAttrCode();
        code.init(attribute);
        
        out.println("      stack=" + code.maxStack + ", locals=" + code.maxLocals + ", len=" + code.codeLength);
        
        CodeWalker walker = new CodeWalker();
        walker.init(code.code);
        boolean newLine = false;
        walk: for(;;) {
            int initial = walker.pos();
            if(walker.stepOver()) {
                String name = Opcode.OPCODE_NAMES[walker.opcode];
                String index = Integer.toString(initial);
                printPadding(10 - index.length());
                out.print(index);
                out.print(": ");
                out.print(name == null ? "null" : name);
                out.print(" //0x");
                out.println(Integer.toString(walker.opcode, 16));
                continue;
            }
            
            for(;;) {
                int nextStep = walker.nextStep();
                if(nextStep == walker.NEXT_STEP) {
                    continue;
                } else if(nextStep == walker.COMPLEX_STEP) {
                    printComplex(walker.multiStep());
                    newLine = true;
                } else if(nextStep == walker.END) {
                    break walk;
                }
                
                if(nextStep != walker.DO_STEP) {
                    continue;
                }
                
                if(newLine) {
                    out.println();
                    newLine = false;
                }
                
                String name = Opcode.OPCODE_NAMES[walker.opcode];
                String index = Integer.toString(initial);
                int indexPad = 10 - index.length();
                printPadding(indexPad);
                out.print(index);
                out.print(": ");
                out.print(name);
                printPadding(16 - name.length());
                out.println(toString(walker.operandValues, ' '));
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

    public void printComplex(Object o) {
        if(o instanceof int[][]) {
            int[][] pairs = (int[][])o;
        
            for(int i = 0; i < pairs.length; i++) {
                out.print(" [" + pairs[i][0] + ":" + pairs[i][1] + "]");
            }
        } else if(o instanceof int[]) {
            int[] array = (int[])o;
            out.print(" [" + toString(array, ',') + "]");
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