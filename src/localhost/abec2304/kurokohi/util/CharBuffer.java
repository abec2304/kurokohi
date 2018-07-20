package localhost.abec2304.kurokohi.util;

public class CharBuffer {

    private int count;
    private char[] arr = new char[30];
    
    private void canFit() {
        if(count < arr.length)
            return;

        char[] tmp = new char[arr.length * 2];
        System.arraycopy(arr, 0, tmp, 0, arr.length);
        arr = tmp;
    }
    
    public void append(char c) {
        canFit();
        arr[count++] = c;
    }
    
    public void reset() {
        count = 0;
    }
    
    public String toString() {
        return new String(arr, 0, count);
    }
    
}
