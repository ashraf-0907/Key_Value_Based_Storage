//package event_loop;
//
//import utils.ResponseStructure;
//import utils.message;
//import utils.statusCode;
//import DataStructure.DBMemoryMap;
//
//public class RequestHandlers {
//
//    static ResponseStructure res = new ResponseStructure();
//
//    public static ResponseStructure requestHandlerFunction (String [] stringParts){
//        if("set".equals(stringParts[0])){
//            if(stringParts.length == 3) {
//                String dbData = setRequest(stringParts[1], stringParts[2]);
//                if (dbData != null && !dbData.isEmpty()) {
//                    res.setData(dbData);
//                    res.setMessage(String.valueOf(message.SUCCESS));
//                    res.setStatusCode(200);
//                }else{
//                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
//                    res.setStatusCode(500);
//                }
//            }
//            else{
//                res.setStatusCode(401);
//                res.setMessage(String.valueOf(message.REQUEST_ERROR));
//            }
//        }
//        else if("get".equals(stringParts[0])){ // Corrected string comparison
//            if(stringParts.length == 2){
//                String value = getRequest(stringParts[1]);
//                if(value != null && !value.isEmpty()){
//                    res.setData(value);
//                    res.setMessage(String.valueOf(message.SUCCESS));
//                    res.setStatusCode(200);
//                }else{
//                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
//                    res.setStatusCode(500);
//                }
//            }
//            else{
//                res.setStatusCode(Integer.parseInt(String.valueOf(statusCode.SYNTAX_ERROR)));
//                res.setMessage(String.valueOf(message.REQUEST_ERROR));
//            }
//        }
//        else if("del".equals(stringParts[0])){
//            if(stringParts.length == 2){
//                String delResponse  = delRequest(stringParts[1]);
//                if(delResponse != null && !delResponse.isEmpty()){
//                    res.setData(delResponse);
//                    res.setMessage(String.valueOf(message.SUCCESS));
//                    System.out.println("hehe from requesthandler");
//                    res.setStatusCode(200);
//                }else{
//                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
//                    res.setStatusCode(500);
//                }
//            }
//            else{
//                res.setStatusCode(400);
//                res.setMessage(String.valueOf(message.REQUEST_ERROR));
//            }
//        }
//        else{
//            res.setStatusCode(401);
//            res.setMessage(String.valueOf(message.REQUEST_ERROR));
//        }
//        return res;
//    }
//
//    private static String setRequest(String key,String value){
//        String keyValue = DBMemoryMap.setValue(key,value);
//        return keyValue;
//    }
//
//    private static String getRequest(String key){
//        String value = DBMemoryMap.getValue(key);
//        return value;
//    }
//
//    private static String delRequest(String key){
//        String delResponse = DBMemoryMap.delValue(key);
//        return delResponse;
//    }
//}



package event_loop;

import utils.ResponseStructure;
import utils.message;
import utils.statusCode;
import DataStructure.DBMemoryMap;

public class RequestHandlers {

    public static ResponseStructure requestHandlerFunction(String[] stringParts,DBMemoryMap myDB) {
        ResponseStructure res = new ResponseStructure(); // Create a new ResponseStructure for each request

        if ("set".equals(stringParts[0])) {
            if (stringParts.length == 3) {
                String dbData = setRequest(stringParts[1], stringParts[2],myDB);
                if (dbData != null && !dbData.isEmpty()) {
                    res.setData(dbData);
                    res.setMessage(String.valueOf(message.SUCCESS));
                    res.setStatusCode(200);
                } else {
                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
                    res.setStatusCode(500);
                }
            } else {
                res.setStatusCode(401);
                res.setMessage(String.valueOf(message.REQUEST_ERROR));
            }
        } else if ("get".equals(stringParts[0])) {
            if (stringParts.length == 2) {
                String value = getRequest(stringParts[1],myDB);
                if (value != null && !value.isEmpty()) {
                    res.setData(value);
                    res.setMessage(String.valueOf(message.SUCCESS));
                    res.setStatusCode(200);
                } else {
                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
                    res.setStatusCode(500);
                }
            } else {
                res.setStatusCode(Integer.parseInt(String.valueOf(statusCode.SYNTAX_ERROR)));
                res.setMessage(String.valueOf(message.REQUEST_ERROR));
            }
        } else if ("del".equals(stringParts[0])) {
            if (stringParts.length == 2) {
                String delResponse = delRequest(stringParts[1],myDB);
                if (delResponse != null && !delResponse.isEmpty()) {
                    res.setData(delResponse);
                    res.setMessage(String.valueOf(message.SUCCESS));
                    res.setStatusCode(200);
                } else {
                    res.setMessage(String.valueOf(message.RESPONSE_ERROR));
                    res.setStatusCode(500);
                }
            } else {
                res.setStatusCode(400);
                res.setMessage(String.valueOf(message.REQUEST_ERROR));
            }
        } else {
            res.setStatusCode(401);
            res.setMessage(String.valueOf(message.REQUEST_ERROR));
        }
        return res;
    }

    private static String setRequest(String key, String value,DBMemoryMap myDB) {
        String keyValue = myDB.setValue(key, value);
        return keyValue;
    }

    private static String getRequest(String key,DBMemoryMap myDB) {
        String value = myDB.getValue(key);
        return value;
    }

    private static String delRequest(String key,DBMemoryMap myDB) {
        String delResponse = myDB.delValue(key);
        return delResponse;
    }
}

