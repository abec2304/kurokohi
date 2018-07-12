package localhost.abec2304.kurokohi.util;

import java.util.Hashtable;

public class IntegerCache {
 
    private static Hashtable table = new Hashtable();
    
    public static Integer valueOf(int i) {
        Integer key = new Integer(i);
        if(i < -128 || i > 256)
            return key;
        
        Integer value = (Integer)table.get(key);
        if(value == null)
            table.put(value = key, value);
        
        return value;
    }

    public static void clear() {
        synchronized(table) {
            table = new Hashtable();
        }
    }
    
}
