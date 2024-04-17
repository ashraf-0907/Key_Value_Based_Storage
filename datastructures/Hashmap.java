package datastructures;
import java.util.HashMap;
public class Hashmap{
    private static HashMap<String, String> hMap = new HashMap<>();

    private static String getValue(String key)
    {
        String value = hMap.get(key);
        if(value==null)return null;
        else
        return value;
    }
}