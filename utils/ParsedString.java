package utils;

public class ParsedString {
    public static String[] parsedString(String requestString) {
        String trimmedRequestMessage = requestString.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]+", "").trim();
        return trimmedRequestMessage.split("\\s+");
    }
}

