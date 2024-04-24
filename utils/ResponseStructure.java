package utils;

public class ResponseStructure {
    private int statusCode;
    private String message;
    private String data = "";

    public ResponseStructure(){

    }

    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return this.statusCode;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

}
