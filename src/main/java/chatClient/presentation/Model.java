package chatClient.presentation;

import chatClient.logic.XmlPersister;
import chatProtocol.Message;
import chatProtocol.User;

import java.io.IOException;
import java.util.*;

public class Model extends java.util.Observable {
    User currentUser;
    User receiver;
    List<Message> messages;
    private List<User> contacts;
    public static int USER = 1;
    public static int CHAT = 2;

    public Model() {
        this.currentUser = null;
        this.receiver = null;
        this.messages= new ArrayList<>();
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser != null) contacts = currentUser.getContacts();
        else contacts = new ArrayList<User>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<User> getContacts() {
        if(currentUser != null) contacts = currentUser.getContacts();
        return contacts;
    }
    public List<User> getContactsOnlyModel() {
      return this.contacts;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addObserver(java.util.Observer o) {
        super.addObserver(o);
        this.commit(Model.USER+Model.CHAT);
    }

    public void commit(int properties){
        this.setChanged();
        this.notifyObservers(properties);
    }

    public void sortMessages() {
        if (messages == null || messages.isEmpty()) return;
        int size = messages.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (Integer.parseInt(messages.get(j).getId()) <= Integer.parseInt(messages.get(i).getId())) {
                    Message aux = messages.get(i);
                    messages.set(i, messages.get(j));
                    messages.set(j, aux);
                }
            }
        }

    }

}
