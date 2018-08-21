package localhost.abec2304.kurokohi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import localhost.abec2304.kurokohi.cp.ConstantPoolInfo;
import localhost.abec2304.kurokohi.cp.ConstUtf8;
import localhost.abec2304.kurokohi.lazy.LazyAttrCode;
import localhost.abec2304.kurokohi.util.MultiByteArrayInputStream;

public class ClassPrinter {
    
    public String className;
    public ClassFile cf;
    public PrintStream out;
    public boolean pre1_0;
    
    private final CodeWalker codeWalker = new CodeWalker();
    
    public void init(String className, ClassFile cf, PrintStream out) {
        this.className = className;
        this.cf = cf;
        this.out = out;
        
        pre1_0 = cf.majorVersion == 45 && cf.minorVersion < 3;
    }
    
    public void printPadding(int len) {
        if(len < 0)
            len = 0;
        
        for(int i = 0; i < len; i++)
            out.print(' ');
    }
    
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream out = new FileOutputStream(FileDescriptor.out);
        PrintStream ps = new PrintStream(new BufferedOutputStream(out), false);
        print(args[0], bis, ps);
    }
    
    public static void printEncoded(String className, InputStream is, OutputStream out) throws IOException {
        RapidPrinter rp = new RapidPrinter();
        rp.init(out);
        rp.printMarker();
        print(className, is, rp);
    }
    
    public static void print(String className, InputStream is, PrintStream out) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        ClassFile cf = new ClassFile();
        cf.init(dis);
        ClassPrinter classPrinter = new ClassPrinter();
        classPrinter.init(className, cf, out);
        classPrinter.print();
    }
    
    public static void print(String className, ClassFile cf, PrintStream out) throws IOException {
        ClassPrinter classPrinter = new ClassPrinter();
        classPrinter.init(className, cf, out);
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
        
        out.print("  interfaces:");
        for(int i = 0; i < cf.interfaces.length; i++) {
            out.print(" #");
            out.print(cf.interfaces[i]);
        }
        out.println();
        
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
            
            int indexPad = 7 - numCharsForIndex(i);
            printPadding(indexPad);
            out.print('#');
            out.print(i);
            out.print(" = ");
            out.print(type);
            
            if(constant != null) {
                printPadding(19 - type.length());
                constant.print(out);
            }
            
            out.println();
        }
        
        out.println('{');
        out.println();
        
        out.print("fields: ");
        out.println(cf.fields.length);
        printMemberArray(cf.fields, 1);
        out.println();
        out.print("methods: ");
        out.println(cf.methods.length);
        printMemberArray(cf.methods, 2);
        
        out.println('}');
        
        printAttributes(cf.attributes, "", 3);
        
        out.flush();
    }
    
    public void printMemberArray(MemberInfo[] members, int type) {
        for(int i = 0; i < members.length; i++) {
            MemberInfo member = members[i];
            String memberName = cf.getUtf8(member.nameIndex);
            String memberDesc = cf.getUtf8(member.descriptorIndex);
            out.print("  #");
            out.print(member.nameIndex);
            out.print(" //");
            out.println(memberName);
            out.print("    descriptor: #");
            out.print(member.descriptorIndex);
            out.print(" //");
            out.println(memberDesc);
            out.print("    flags: 0x");
            out.println(Integer.toString(member.accessFlags, 16));
            printAttributes(member.attributes, "    ", type);
            out.println();
        }
    }
    
    public void printAttributes(AttributeInfo[] attributes, String padding, int type) {
        for(int j = 0; j < attributes.length; j++) {
            AttributeInfo attribute = attributes[j];
            String attributeName = cf.getUtf8(attribute.attributeNameIndex);
            out.print(padding);
            out.print('#');
            out.print(attribute.attributeNameIndex);
            out.print(':');
            out.print(" //");
            out.println(attributeName);
            
            if(attribute.attributeLength == 0) {
                continue;
            }
            
            if(attribute instanceof AttrUnknown) {
                if(type > 0 && "Signature".equals(attributeName)) {
                    printSimpleAttribute(padding, (AttrUnknown)attribute);
                    continue;
                } else if(type == 1 && "ConstantValue".equals(attributeName)) {
                    printSimpleAttribute(padding, (AttrUnknown)attribute);
                    continue;
                } else if(type == 2 && "Code".equals(attributeName)) {
                    try {
                        dumpCode((AttrUnknown)attribute, codeWalker);
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    } catch(ArrayIndexOutOfBoundsException aioobe) {
                        aioobe.printStackTrace();
                    }
                    continue;
                } else if(type == 3 && "SourceFile".equals(attributeName)) {
                    printSimpleAttribute(padding, (AttrUnknown)attribute);
                    continue;
                }
            }
            
            out.print(padding);
            out.println("  NYI");
        }
    }
    
    public void printSimpleAttribute(String padding, AttrUnknown attribute) {
        InputStream is = new MultiByteArrayInputStream(attribute.info);
        DataInputStream dis = new DataInputStream(is);
        
        int index;
        try {
            index = dis.readUnsignedShort();
        } catch(IOException ioe) {
            index = -1;
        }
        
        out.print(padding);
        out.print("  #");
        out.println(index);
    }
    
    public static int numCharsForIndex(int n) {
        return n < 10 ? 1 : n < 100 ? 2 : n < 1000 ? 3 : 4;
    }
    
    public void dumpCode(AttrUnknown attribute, CodeWalker codeWalker) throws IOException {
        LazyAttrCode code = new LazyAttrCode();
        code.pre1_0 = pre1_0;
        code.init(attribute);
        
        out.print("      stack=");
        out.print(code.maxStack);
        out.print(", locals=");
        out.print(code.maxLocals);
        out.print(", len=");
        out.println(code.code.length);
        
        codeWalker.init(code.code);
        for(;;) {
            int initial = codeWalker.pos;
            
            int action = codeWalker.stepOver();
            if(action == codeWalker.DO_STEP) {
                String name = Opcode.OPCODE_NAMES[codeWalker.opcode];
                printPadding(10 - numCharsForIndex(initial));
                out.print(initial);
                out.print(": ");
                out.print(name);
                out.print(" //0x");
                out.println(Integer.toString(codeWalker.opcode, 16));
                continue;
            } else if(action < codeWalker.DO_STEP) { // END
                break;
            }
            
            String name = Opcode.OPCODE_NAMES[codeWalker.opcode];
            printPadding(10 - numCharsForIndex(initial));
            out.print(initial);
            out.print(": ");
            out.print(name);
            printPadding(15 - name.length());
            
            for(;;) {
                action = codeWalker.nextStep();
                if(action > codeWalker.DO_STEP) { // NEXT_STEP
                    out.print(' ');
                    out.print(codeWalker.operandValues[codeWalker.operandsPos - 1]);
                    continue;
                } else if(action < codeWalker.DO_STEP) { // COMPLEX_STEP
                    printComplex(codeWalker.multiStep());
                    if(codeWalker.operandsPos == codeWalker.operands.length) {
                        out.println();
                        break;
                    }
                    continue;
                }

                out.print(' ');
                out.println(codeWalker.operandValues[codeWalker.operandsPos - 1]);
                break;
            }
        }
        
        int exceptionTableLength = code.exceptionTable.length;
        
        if(exceptionTableLength > 0) {
            printExceptionTable(code.exceptionTable, exceptionTableLength);
        }
        
        printAttributes(code.attributes, "      ", 0);
    }

    public void printExceptionTable(int[][] table, int len) {
        out.println("      Exception table:");
        out.println("         from,to,target,type");
        
        for(int i = 0; i < len; i++) {
            int[] entry = table[i];
            out.print("         ");
            out.print(entry[0]);
            out.print(',');
            out.print(entry[1]);
            out.print(',');
            out.print(entry[2]);
            out.print(',');
            out.println(entry[3]);
        }
    }
    
    public void printComplex(Object o) {
        if(o instanceof int[][]) {
            int[][] pairs = (int[][])o;
        
            for(int i = 0; i < pairs.length; i++) {
                int[] pair = pairs[i];
                out.print(" [");
                out.print(pair[0]);
                out.print(':');
                out.print(pair[1]);
                out.print(']');
            }
        } else if(o instanceof int[]) {
            int[] array = (int[])o;
            out.print(" [");
            printIntArray(array, array.length, ',');
            out.print("]");
        } else {
            //
        }
    }
    
    public void printIntArray(int[] array, int len, char c) {
        if(array == null || len < 1)
            return;
        
        out.print(array[0]);
        
        for(int i = 1; i < len; i++) {
            out.print(c);
            out.print(array[i]);
        }
    }
    
}