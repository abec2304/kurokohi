package localhost.abec2304.kurokohi;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class RapidPrinter extends PrintStream {

    public IOException exception;
    
    public RapidPrinter(OutputStream out) {
        super(out);
    }

    public void printMarker() {
        try {
            out.write(0xFF);
            out.write(0xFE);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void println() {
        write('\n');
    }
    
    public void write(int b) {
        try {
            out.write(b);
            out.write('\0');
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void println(int num) {
        print(Integer.toString(num, 10));
        println();
    }
    
    public void println(String string) {
        print(string);
        println();
    }
    
    public void print(String string) {
        OutputStream outputStream = out;
        int len = string.length();
        
        try {
            for(int i = 0; i < len; i++) {
                int c = string.charAt(i);
                outputStream.write(c & 0xFF);
                outputStream.write(c >>> 8 & 0xFF);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(char[] array) {
        OutputStream outputStream = out;
        int len = array.length;
        
        try {
            for(int i = 0; i < len; i++) {
                int c = array[i];
                outputStream.write(c & 0xFF);
                outputStream.write(c >>> 8 & 0xFF);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
}
