package chatClient.logic;

import chatClient.presentation.Controller;
import chatProtocol.IService;
import chatProtocol.Message;
import chatProtocol.Protocol;
import chatProtocol.User;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServiceProxy implements IService {

    private static IService theInstance;
    ObjectInputStream in; // Objeto que recibe los datos del servidor (input).
    ObjectOutputStream out; // Objeto que envía los datos al servidor (output).
    Socket socket; // Socket que habilita los canales de comunicación.
    Controller controller;

    public ServiceProxy() {}

    public static IService instance(){
        if (theInstance == null) {
            theInstance = new ServiceProxy();
        }
        return theInstance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void connect() throws Exception {
        socket = new Socket(Protocol.SERVER, Protocol.PORT);
        out = new ObjectOutputStream(socket.getOutputStream() );
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void disconnect() throws Exception { // Se cierra la conexión del servidor.
        socket.shutdownOutput();
        socket.close();
    }

    public User login(User u) throws Exception {
        connect(); // Se conecta al servidor.
        try {
            out.writeInt(Protocol.LOGIN); // Se "escribe" la intención de login por el stream.
            out.writeObject(u); // Se serializa el objeto y se manda al stream.
            out.flush(); // Se limpia.
            int response = in.readInt(); // Se espera la respuesta.
            if (response == Protocol.ERROR_NO_ERROR){ // Según la respuesta se actúa.
                User u1 = (User) in.readObject();
                this.start();
                return u1;
            }
            else {
                disconnect();
                return null;
            }
        } catch (IOException | ClassNotFoundException ex) { return null; }
    }

    public User register(User u) throws Exception {
        connect();
        try {
            out.writeInt(Protocol.REGISTER);
            out.writeObject(u);
            out.flush();
            int response = in.readInt();
            if (response == Protocol.ERROR_NO_ERROR){
                User u1 = (User) in.readObject();
                this.start();
                return u1;
            }
            else {
                disconnect();
                throw new Exception("No remote user...");
            }
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    public void logout(User u) throws Exception { // Cierra la conexión con el servidor en el logout.
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(u);
        out.flush();
        this.stop(); // Detiene el hilo.
        this.disconnect();
    }

    public User checkContact(String id) throws  Exception{
        try{
            out.writeInt(Protocol.CONTACT);
            out.writeObject(id);
            out.flush();
            return null;
            /*
            * Explicación:
            * Se escribe la intención (protocol) que leerá un case en la clase Worker.
            * El case en la clase Worker utilizará el service para comunicarse con la base de datos.
            * El Worker procesa lo que el service envía y lo lleva al Server.
            * El Server lo comunica al resto de usuarios.
            * */
        } catch (Exception ex){
            return null;
        }
    }

    public void post(Message message) {
        try {
            out.writeInt(Protocol.POST);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {}
    }

    public User notifyContacts(User contact) {
        try{
            out.writeInt(Protocol.ONLINE);
            out.writeObject(contact);
            out.flush();
            return null;
        } catch (Exception ex){}
        return null;
    }

    public User notifyContactsOff(User contact) {
        try{
            out.writeInt(Protocol.OFFLINE);
            out.writeObject(contact);
            out.flush();
            return null;
        } catch (Exception ex){}
        return null;
    }

    // LISTENING FUNCTIONS

    boolean continuar = true; // Variable para saber si el hilo sigue corriendo.
    public void start() { // Inicia el hilo de ejecución.
        System.out.println("Client worker attending to requests...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            } // Se llama al método listen para que esté escuchando constantemente.
        });
        continuar = true;
        t.start();
    }

    public void stop(){
        continuar = false;
    }

    private void deliver( final Message message ) { // Metodo para entregar el mensaje al controlador.
        SwingUtilities.invokeLater(new Runnable() { // Crea un hilo.
            public void run() {
                controller.deliver(message);
            } // Llama al metodo deliver del controlador para entregar el mensaje.
        }
        );
    }
      // invokelater se encarga de ejecutar los hilos en orden que se desarrollan.

    public void connectContacts(User u) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    controller.connectContacts(u);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        );
    }

    public void notifyContact(User u) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    controller.notifyContact(u, true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        );
    }

    public void notifyContactOff(User u) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    controller.notifyContact(u, false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        );
    }

    public void listen() { // Método para escuchar constantemente los mensajes que llegan del servidor y entregarlos al controlador.
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operation: " + method);
                switch(method){
                    case Protocol.DELIVER: {
                        try {
                            Message message=(Message)in.readObject();
                            deliver(message);
                        } catch (ClassNotFoundException ex) {}
                        break;
                    }
                    case Protocol.CONTACT_RESPONSE: {
                        int response = in.readInt();
                        if (response == Protocol.ERROR_NO_ERROR) {
                            try {
                                User user = (User) in.readObject();
                                connectContacts(user);
                            } catch (ClassNotFoundException ex) {}
                        }
                        else {
                            System.out.println("Error in contact!");
                        }
                        break;
                    }
                    case Protocol.ONLINE: {
                        try {
                            User userToNotify = (User) in.readObject();
                            notifyContact(userToNotify);
                        } catch (ClassNotFoundException ex) {}
                        }break;
                    case Protocol.OFFLINE: {
                        try {
                            User userToNotify = (User) in.readObject();
                            notifyContactOff(userToNotify);
                        } catch (ClassNotFoundException ex) {}
                    }break;
                }
                out.flush(); // Limpia el búffer.
            } catch (IOException  ex) {
                continuar = false;
            }
        }
    }
}
