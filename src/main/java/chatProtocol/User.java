package chatProtocol;

import java.util.*;
import java.io.*;

import chatClient.logic.Data;
import chatClient.logic.XmlPersister;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {
    @XmlID
    private String id;
    @XmlTransient
    private String password;
    private String name;
    private boolean connected;
    @XmlTransient
    private List<User> contacts;
    @XmlTransient
    private List<Message> messages;

    public User() {
        this.id = "";
        this.password = "";
        this.name = "";
        this.contacts = new ArrayList<>();
        this.connected = false;
        this.messages = new ArrayList<>();
    }

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.contacts = new ArrayList<>();
        this.connected = false;
        this.messages = new ArrayList<>();
    }

    public User(String id, String password, String name, ArrayList<User> contacts) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.contacts = contacts;
        this.connected = false;
        this.messages = new ArrayList<>();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<User> contacts) {
        this.contacts = contacts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (contacts != null) {
            return "User{" +
                    "id='" + id + '\'' +
                    ", password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    ", connected=" + connected +
                    ", contacts=" + contacts +
                    '}';
        }
        if (messages != null) {
            return "User{" +
                    "id='" + id + '\'' +
                    ", password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    ", connected=" + connected +
                    ", messages=" + messages +
                    '}';
        }
        else {
            return "User{" +
                    "id='" + id + '\'' +
                    ", password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    ", connected=" + connected +
                    '}';
        }
    }

    public void load() throws Exception {
        XmlPersister.getInstacia().setPath(this.getId() + ".xml");
        try {
            Data data = XmlPersister.getInstacia().cargar();
            List<User> contactsAux = data.getContacts();
            if (contactsAux != null && !contactsAux.isEmpty()) {
                this.setContacts((ArrayList<User>) contactsAux);
            }
        } catch (IOException ex) {}
        System.out.println(this.toString());
    }

    public ArrayList<Message> loadMessages() throws Exception {
        XmlPersister.getInstacia().setPath(this.getId() + ".xml");
        List<Message> messagesAux = new ArrayList<>();
        try {
            Data data = XmlPersister.getInstacia().cargar();
            messagesAux = data.getMessages();
            if (messagesAux != null && !messagesAux.isEmpty()) {
                this.setMessages((ArrayList<Message>) messagesAux);
            }
        } catch (IOException ex) {}
        return (ArrayList<Message>) messagesAux;
    }
}
