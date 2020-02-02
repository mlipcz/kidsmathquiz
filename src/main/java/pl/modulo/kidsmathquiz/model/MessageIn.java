package pl.modulo.kidsmathquiz.model;

import java.util.Date;

public class MessageIn {
    private String senderName, message;
    private Date sentTime;

    public MessageIn() {
    }

    public MessageIn(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }
}
