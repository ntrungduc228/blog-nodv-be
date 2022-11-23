package nodv.payload.response;

import org.apache.logging.log4j.message.Message;

public class MessageResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageResponse (){

    }

    public MessageResponse(String message) {
        this.message = message;
    }
}