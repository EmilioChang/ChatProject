package chatServer;

import chatProtocol.IService;
import chatProtocol.Message;
import chatProtocol.Protocol;
import chatProtocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// La clase worker es un hilo que se encarga de atender a un cliente.

public class Worker {
    Server server;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;
    boolean proceed;

    public Worker(Server server, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.server = server;
        this.in = in;
        this.out = out;
        this.user = user;
        this.service = service;
    }

    public void start() {
        try {
            System.out.println("Worker handling requests...");
            Thread t = new Thread(new Runnable() {
                public void run(){
                    listen();
                }
            });
            proceed = true;
            t.start();
        } catch (Exception ex) {}
    }

    public void stop() {
        proceed = false;
        System.out.println("Closed connection...");
    }

// El método listen es el que se encarga de atender algunas peticiones antes de ser enviadas al Server.

    public void listen(){
        int method;
        while (proceed) {
            try {
                method = in.readInt();
                System.out.println("Operation: " + method);
                switch(method) {
                    case Protocol.LOGOUT: {
                        try {
                            service.logout(user);
                            server.remove(user);
                        } catch (Exception ex) {}
                        stop();
                    } break;
                    case Protocol.POST: {
                        Message message = null;
                        try {
                            message = (Message) in.readObject();
                            message.setSender(user);
                            service.post(message);
                            server.deliver(message);
                        } catch (ClassNotFoundException ex) {}
                    } break;
                    case Protocol.CONTACT: {
                        try {
                            User u = service.checkContact((String) in.readObject());
                            checkContact(u);
                        } catch (Exception ex) {}
                    } break;
                    case Protocol.ONLINE: {
                        try {
                            User u = service.notifyContacts((User) in.readObject());
                            server.notifyContacts(u);
                        } catch (Exception ex) {}
                    } break;
                    case Protocol.OFFLINE: {
                        try {
                            User u = service.notifyContactsOff((User) in.readObject());
                            server.notifyContactsOff(u);
                        } catch (Exception ex) {}
                    } break;
                }
            } catch (IOException ex) {
                System.out.println(ex);
                proceed = false;
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void deliver(Message message) {
        try {
            out.writeInt(Protocol.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {}
    }

    public void notifyContact(User u) {
        try {
            out.writeInt(Protocol.ONLINE);
            out.writeObject(u);
            out.flush();
        } catch (IOException ex) {}
    }

    public void notifyContactOff(User u) {
        try {
            out.writeInt(Protocol.OFFLINE);
            out.writeObject(u);
            out.flush();
        } catch (IOException ex) {}
    }

    public void checkContact(User u) {
        try {
            out.writeInt(Protocol.CONTACT_RESPONSE);
            if (u == null) {
                out.writeInt(Protocol.ERRROR_CONTACT);
            } else {
                out.writeInt(Protocol.ERROR_NO_ERROR);
                out.writeObject(u);
            }
            out.flush();
        } catch (Exception ex) {}
    }
}
