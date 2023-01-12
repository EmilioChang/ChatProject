package chatProtocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import chatClient.logic.XmlPersister;
import jakarta.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements Serializable {
    @XmlTransient
    Date currentDateTime = new Date();

    @XmlID
    String id;
    String message;
    boolean seen;
    User receiver;
    User sender;

    public Message() {
        this.id = String.valueOf((int) (currentDateTime.getTime() / 1000));
        this.sender = null;
        this.message = "";
        this.receiver = null;
        this.seen = false;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Message(User sender, String message, User receiver) {
        receiver.setMessages(new ArrayList<>());
        sender.setMessages(new ArrayList<>());
        this.id = String.valueOf((int) (currentDateTime.getTime() / 1000));
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.seen = false;
    }

    public Message(User sender, String message, User receiver, boolean seen) {
        receiver.setMessages(new ArrayList<>());
        sender.setMessages(new ArrayList<>());
        this.id = String.valueOf((int) (currentDateTime.getTime() / 1000));
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.seen = seen;
    }
    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", message='" + message + '\'' +
                ", receiver=" + receiver +
                '}';
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
