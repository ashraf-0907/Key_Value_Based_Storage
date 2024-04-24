package utils;

public class ParsedString {
    public static String [] parsedString(String requestString){
        String trimrequestMessage = requestString.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]+", "").trim();
        String[] stringParts = trimrequestMessage.split("\\s+");
        return stringParts;
    }
}
