package chatServer;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.net.*;
import chatProtocol.Message;
import chatProtocol.User;
import chatProtocol.IService;
import chatServer.Data.*;

public class Service implements IService {
    private UserDao ud;
    private MessageDao md;

    public Service() {
        ud = new UserDao();
        md = new MessageDao();
    }

    public void post(Message message) {
        if(ud.getSpecificUser(message.getReceiver().getId()).isConnected()) {
            message.setSeen(true);
        }
        else {
            message.setSeen(false);
        }
        if (!ud.getSpecificUser(message.getReceiver().getId()).isConnected()) md.addMessage(message);
    }

    public User checkContact(String id) throws  Exception {
        return ud.getSpecificUser(id);
    }

    public User login(User user) throws Exception {
        User userDatabase = ud.getSpecificUser(user.getId());
        if (userDatabase != null) {
            user.setName(userDatabase.getName());
            user.setConnected(true);
            for (int i = 0; i < user.getContacts().size(); i++) {
                if (ud.getSpecificUser(user.getContacts().get(i).getId()).isConnected()) {
                    user.getContacts().get(i).setConnected(true);
                } else {
                    user.getContacts().get(i).setConnected(false);
                }
            }
            List<Message> messageSender = md.searchMessageSender(user.getId());
            List<Message> messageReceiver = md.searchMessageReceiver(user.getId());
            messageSender.addAll(messageReceiver);
            user.setMessages((ArrayList<Message>) messageSender);
            ud.updateUser(user); // Se actualiza el usuario en la base de datos.
            md.deleteMessage(user);
            return user; // Se devuelve el usuario actualizado.
        }
        return null;
    }

    public User register(User user) throws Exception {
        user.setConnected(true);
        ud.addUser(user);
        return user;
    }

    public User notifyContacts(User user) throws Exception {
        return user;
    }

    public User notifyContactsOff(User user) throws Exception {
        return user;
    }

    public void logout(User user) throws Exception {
        List<Message> messageReceiver = md.searchMessageReceiver(user.getId());
        List<Integer> messageReceiverId = md.getAllMessageId(user.getId());
        for (int i = 0; i < messageReceiver.size(); i++) {
            Message messageAux = messageReceiver.get(i);
            messageAux.setSeen(true);
            md.updateMessage(messageAux, messageReceiverId.get(i));
        }
        user.setConnected(false);
        ud.updateUser(user);
    }
}
