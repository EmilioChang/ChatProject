package chatProtocol;

public class Protocol {
    // Establecimiento de las "reglas" que se seguirán para determinar códigos en la comunicación con el servidor.
    public static final String SERVER = "localhost";
    /*
    * En cmd: ipconfig
    * Cambiar SERVER = "XXX.XXX.XXX.XX"
    * XXX.XXX.XXX.XX = ipV4 de Ethernet Adapter o localhost.
    * */
    public static final int PORT = 1234;

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int POST = 3;
    public static final int REGISTER = 4;
    public static final int CONTACT = 5;
    public static final int ONLINE = 7;
    public static final int OFFLINE = 8;

    public static final int CONTACT_RESPONSE = 6;

    public static final int DELIVER = 10;

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_LOGIN = 1;
    public static final int ERROR_LOGOUT = 2;
    public static final int ERROR_POST = 3;
    public static final int REGISTER_ERROR = 4;
    public static final int ERRROR_CONTACT = 5;
    public static final int ERROR_ONLINE_OFFLINE = 6;
}
