package localhost.abec2304.kurokohi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import localhost.abec2304.kurokohi.util.NullOutputStream;

public class RapidPrinter extends PrintStream {

    public OutputStream outs;
    public IOException exception;
    
    private static final OutputStream NULL_STREAM = new NullOutputStream();
    
    private static final byte[] MARKER = {-1, -2};
    private static final byte[] NEW_LINE = {'\n', 0};
    private static final byte[] MINUS = {'-', 0};
    private static final byte[][] DIGITS = {
        {'0', 0}, {'1', 0}, {'2', 0}, {'3', 0}, {'4', 0},
        {'5', 0}, {'6', 0}, {'7', 0}, {'8', 0}, {'9', 0},
        {'1', 0, '0', 0}, {'1', 0, '1', 0}, {'1', 0, '2', 0},
        {'1', 0, '3', 0}, {'1', 0, '4', 0}, {'1', 0, '5', 0},
        {'1', 0, '6', 0}, {'1', 0, '7', 0}, {'1', 0, '8', 0},
        {'1', 0, '9', 0}, {'2', 0, '0', 0}, {'2', 0, '1', 0},
        {'2', 0, '2', 0}, {'2', 0, '3', 0}, {'2', 0, '4', 0},
        {'2', 0, '5', 0}, {'2', 0, '6', 0}, {'2', 0, '7', 0},
        {'2', 0, '8', 0}, {'2', 0, '9', 0}, {'3', 0, '0', 0},
        {'3', 0, '1', 0}, {'3', 0, '2', 0}, {'3', 0, '3', 0},
        {'3', 0, '4', 0}, {'3', 0, '5', 0}, {'3', 0, '6', 0},
        {'3', 0, '7', 0}, {'3', 0, '8', 0}, {'3', 0, '9', 0},
        {'4', 0, '0', 0}, {'4', 0, '1', 0}, {'4', 0, '2', 0},
        {'4', 0, '3', 0}, {'4', 0, '4', 0}, {'4', 0, '5', 0},
        {'4', 0, '6', 0}, {'4', 0, '7', 0}, {'4', 0, '8', 0},
        {'4', 0, '9', 0}, {'5', 0, '0', 0}, {'5', 0, '1', 0},
        {'5', 0, '2', 0}, {'5', 0, '3', 0}, {'5', 0, '4', 0},
        {'5', 0, '5', 0}, {'5', 0, '6', 0}, {'5', 0, '7', 0},
        {'5', 0, '8', 0}, {'5', 0, '9', 0}, {'6', 0, '0', 0},
        {'6', 0, '1', 0}, {'6', 0, '2', 0}, {'6', 0, '3', 0},
        {'6', 0, '4', 0}, {'6', 0, '5', 0}, {'6', 0, '6', 0},
        {'6', 0, '7', 0}, {'6', 0, '8', 0}, {'6', 0, '9', 0},
        {'7', 0, '0', 0}, {'7', 0, '1', 0}, {'7', 0, '2', 0},
        {'7', 0, '3', 0}, {'7', 0, '4', 0}, {'7', 0, '5', 0},
        {'7', 0, '6', 0}, {'7', 0, '7', 0}, {'7', 0, '8', 0},
        {'7', 0, '9', 0}, {'8', 0, '0', 0}, {'8', 0, '1', 0},
        {'8', 0, '2', 0}, {'8', 0, '3', 0}, {'8', 0, '4', 0},
        {'8', 0, '5', 0}, {'8', 0, '6', 0}, {'8', 0, '7', 0},
        {'8', 0, '8', 0}, {'8', 0, '9', 0}, {'9', 0, '0', 0},
        {'9', 0, '1', 0}, {'9', 0, '2', 0}, {'9', 0, '3', 0},
        {'9', 0, '4', 0}, {'9', 0, '5', 0}, {'9', 0, '6', 0},
        {'9', 0, '7', 0}, {'9', 0, '8', 0}, {'9', 0, '9', 0}
    };
    
    public RapidPrinter() {
        super(NULL_STREAM, false);
        super.close();
        out = null;
    }
    
    public void init(OutputStream outputStream) {
        outs = outputStream;
    }
    
    public void printMarker() {
        try {
            outs.write(MARKER);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void write(int by) {
        try {
            outs.write(by);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void write(byte[] arr) {
        try {
            outs.write(arr);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void write(byte[] arr, int off, int len) {
        try {
            outs.write(arr, off, len);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void flush() {
        try {
            outs.flush();
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void close() {
        if(outs == null)
            return;
        
        flush();
        try {
            outs.close();
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
        OutputStream outs = this.outs;
        
        if(str == null)
            str = "null";
        int len = str.length();
        
        try {
            for(int i = 0; i < len; i++) {
                int c = str.charAt(i);
                outs.write(c);
                outs.write(c >> 8);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(char[] arr) {
        OutputStream outs = this.outs;
        int len = arr.length;
        
        try {
            for(int i = 0; i < len; i++) {
                int c = arr[i];
                outs.write(c);
                outs.write(c >> 8);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(char chr) {
        try {
            outs.write(chr);
            outs.write(chr >> 8);
        } catch(IOException ioe) {
            exception = ioe;
        }
    }
    
    public void print(int num) {
        try {
            if(num < 0) {
                outs.write(MINUS);
                if(num == Integer.MIN_VALUE) {
                    print("2147483648");
                    return;
                }
                num *= -1;
            }
            
            if(num < 100) {
                outs.write(DIGITS[num]);
            } else if(num < 10000) {
                outs.write(DIGITS[num / 100]);
                outs.write(DIGITS[num % 100]);
            } else if(num < 1000000) {
                outs.write(DIGITS[num / 10000]);
                outs.write(DIGITS[num % 10000 / 100]);
                outs.write(DIGITS[num % 100]);
            } else if(num < 100000000) {
                outs.write(DIGITS[num / 1000000]);
                outs.write(DIGITS[num % 1000000 / 10000]);
                outs.write(DIGITS[num / 100 % 100]);
                outs.write(DIGITS[num % 100]);
            } else {
                outs.write(DIGITS[num / 100000000]);
                outs.write(DIGITS[num % 100000000 / 1000000]);
                outs.write(DIGITS[num % 1000000 / 10000]);
                outs.write(DIGITS[num / 100 % 100]);
                outs.write(DIGITS[num % 100]);
            }
        } catch(IOException ioe) {
            exception = ioe;
        }
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
            outs.write(NEW_LINE, 0, 2);
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
