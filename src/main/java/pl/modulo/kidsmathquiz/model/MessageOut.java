package pl.modulo.kidsmathquiz.model;

import java.util.Date;

public class MessageOut {

    private String content;
    private Date sentTimestamp;

    public MessageOut() {
        this.sentTimestamp = new Date();
    }

    public MessageOut(String content) {
        this();
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(Date sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }
}
