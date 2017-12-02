package spring.web.socket.chat.dto;

/**
 * ChatMessage dto class to be used in MVC pattern
 *
 * @author Yasitha Thilakaratne
 */
public class ChatMessage {

    private String from;
    private String text;
    private String recipient;

    public ChatMessage(String from, String text, String recipient) {
        this.from = from;
        this.text = text;
        this.recipient = recipient;
    }

    public ChatMessage() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
