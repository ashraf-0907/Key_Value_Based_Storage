package DataStructure;

import java.util.concurrent.ConcurrentHashMap;

public class DBMemoryMap {
    private static final ConcurrentHashMap<String, String> dbMap = new ConcurrentHashMap<>();

    public static String setValue(String key, String value) {
        dbMap.put(key, value);
        return "OK";
    }

    public static String getValue(String key) {
        return dbMap.getOrDefault(key, "null");
    }

    public static String delValue(String key) {
        return dbMap.remove(key) != null ? "OK" : "null";
    }
}

