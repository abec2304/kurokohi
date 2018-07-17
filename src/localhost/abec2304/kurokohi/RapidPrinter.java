package localhost.abec2304.kurokohi;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class RapidPrinter extends PrintStream {

    public IOException exception;
    
    public RapidPrinter(OutputStream out) {
        super(out, false);
    }

    public void superClose() {
        super.close();
    }
    
    public void init(OutputStream out) {
        this.out = out;
    }
    
    public void printMarker() {
        try {
            out.write(0xFF);
            out.write(0xFE);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void write(int by) {
        try {
            out.write(by);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void write(byte[] arr, int off, int len) {
        try {
            out.write(arr, off, len);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void flush() {
        try {
            out.flush();
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void close() {
        flush();
        try {
            out.close();
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public boolean checkError() {
        flush();
        return exception != null && !(exception instanceof InterruptedIOException);
    }
    
    public void print(Object obj) {
        print(String.valueOf(obj));
    }
    
    public void print(String str) {
        OutputStream outputStream = out;
        int len = str.length();
        
        try {
            for(int i = 0; i < len; i++) {
                int c = str.charAt(i);
                outputStream.write(c & 0xFF);
                outputStream.write(c >>> 8 & 0xFF);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(char[] arr) {
        OutputStream outputStream = out;
        int len = arr.length;
        
        try {
            for(int i = 0; i < len; i++) {
                int c = arr[i];
                outputStream.write(c & 0xFF);
                outputStream.write(c >>> 8 & 0xFF);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(char chr) {
        try {
            out.write(chr & 0xFF);
            out.write(chr >>> 8 & 0xFF);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(int num) {
        print(Integer.toString(num, 10));
    }
    
    public void print(long num) {
        print(Long.toString(num, 10));
    }
    
    public void print(float num) {
        print(Float.toString(num));
    }
    
    public void print(double num) {
        print(Double.toString(num));
    }
    
    public void print(boolean bool) {
        print(String.valueOf(bool));
    }
    
    public void println() {
        try {
            out.write('\n');
            out.write(0);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void println(Object obj) {
        print(obj);
        println();
    }   
    
    public void println(String str) {
        print(str);
        println();
    }
    
    public void println(char[] arr) {
        print(arr);
        println();
    }
    
    public void println(char chr) {
        print(chr);
        println();
    }
    
    public void println(int num) {
        print(num);
        println();
    }
    
    public void println(long num) {
        print(num);
        println();
    }
    
    public void println(float num) {
        print(num);
        println();
    }
    
    public void println(double num) {
        print(num);
        println();
    }
    
    public void println(boolean bool) {
        print(bool);
        println();
    }
    
}
