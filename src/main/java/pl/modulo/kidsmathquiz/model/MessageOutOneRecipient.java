package pl.modulo.kidsmathquiz.model;

public class MessageOutOneRecipient extends MessageOut {

    private String recipient;

    public MessageOutOneRecipient(String content, String recipient) {
        super(content);
        this.recipient = recipient;
    }
}
