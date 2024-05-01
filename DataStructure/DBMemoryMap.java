package DataStructure;

import java.util.HashMap;

public class DBMemoryMap {
    private static HashMap<String, String> hMap;
    public DBMemoryMap()
    {
        hMap = new HashMap<>();
    }
    public String getValue(String key) {
        String value = hMap.get(key);
        if (value == null)
            return "Key not present, cannot get";
        else
            return value;
    }

    public String setValue(String key, String value) {
        if (hMap.containsKey(key))
            return "Key already present, cannot set value!!";
        else {
            hMap.put(key, value);
            return "SET operation successful";
        }
    }

    public String delValue(String key) {
        if (!hMap.containsKey(key))
            return "Key not present, cannot delete the value!!";
        else {
            hMap.remove(key);
            return "DEL operation successful";
        }
    }
}
