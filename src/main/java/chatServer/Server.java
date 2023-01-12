package chatServer;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.net.*;
import chatProtocol.Message;
import chatProtocol.Protocol;
import chatProtocol.User;
import chatProtocol.IService;
import javax.sound.midi.Soundbank;
import javax.swing.*;
public class Server {
    ServerSocket serverSocket;
    List<Worker> workers;
    // Un socket funciona como un intermediario entre la aplicación y el flujo de datos hacia la red.
    // Envían y reciben conjuntos de bytes, sin ninguna interpretación.
    // Poseen: canal de salida (OutputStream) y canal de entrada (InputStream).
    // Tipo de hilo, en cual espera solicitudes del cliente.

    public Server() {
        try {
            serverSocket = new ServerSocket(Protocol.PORT);
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Server started...");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
        }
    }

    private User login(ObjectInputStream in, ObjectOutputStream out, IService service) throws IOException, ClassNotFoundException, Exception {
        int method = in.readInt();
        if (method != Protocol.LOGIN) throw new Exception("Should login first...");
        User user = (User)in.readObject();
        user = service.login(user);
        out.writeInt(Protocol.ERROR_NO_ERROR);
        out.writeObject(user);
        out.flush();
        return user;
    }

    public void run() {
        IService service = new Service();
        boolean proceed = true;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;
        int method = -1;
        while (proceed) {
            try {
                socket = serverSocket.accept();
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream() );
                System.out.println("Connection established...");
                method = in.readInt();
                switch (method) {
                    case Protocol.LOGIN: {
                        try {
                            User user = service.login((User) in.readObject());
                            if (user == null) throw new Exception("User do not exists in database...");
                            out.writeInt(Protocol.ERROR_NO_ERROR);
                            out.writeObject(user);
                            out.flush();
                            Worker worker = new Worker(this, in, out, user, service);
                            workers.add(worker);
                            worker.start();
                        } catch (Exception ex) {
                            out.writeInt(Protocol.ERROR_LOGIN);
                            out.flush();
                            socket.close();
                            System.out.println("Connection closed...");
                        }
                    } break;
                    case Protocol.REGISTER: {
                        try {
                            User user = service.register((User) in.readObject());
                            out.writeInt(Protocol.ERROR_NO_ERROR);
                            out.writeObject(user);
                            out.flush();
                            Worker worker = new Worker(this, in, out, user, service);
                            workers.add(worker);
                            worker.start();
                        } catch (Exception ex) {
                            out.writeInt(Protocol.REGISTER_ERROR);
                            out.flush();
                            socket.close();
                            System.out.println("Connection closed...");
                        }
                    } break;
                    default: {
                        out.writeInt(Protocol.ERROR_LOGIN);
                        out.flush();
                        socket.close();
                        System.out.println("Connection closed...");
                    }
                }
            }
            catch (IOException ex) {}
        }
    }

    public void deliver(Message message) {
        for (Worker worker : workers) {
            // Llama al worker del usuario destino cuando estos coinciden.
            if (message.getReceiver().equals(worker.user)) {
                worker.deliver(message);
                break;
            }
        }
    }

    public void notifyContacts(User u) {
        for (Worker worker : workers) {
            // Notifica a todos los contactos.
            worker.notifyContact(u);
        }
    }

    public void notifyContactsOff(User u) {
        for (Worker worker : workers) {
            worker.notifyContactOff(u);
        }
    }

    public void remove(User u){
        for (Worker worker:workers) {
            if (worker.user.equals(u)) {
                workers.remove(worker);
                break;
            }
        }
        System.out.println("Remain: " + workers.size());
    }
}
