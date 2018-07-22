package localhost.abec2304.kurokohi.util;

public class CharBuffer {

    private int count;
    private char[] arr = new char[80];
    
    public void append(char c) {
        arr[count++] = c;
    }
    
    public void reset(int len) {
        if(arr.length < len)
            arr = new char[len * 2];
        
        count = 0;
    }
    
    public String toString() {
        return new String(arr, 0, count);
    }
    
}
