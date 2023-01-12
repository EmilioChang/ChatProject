package chatClient.logic;
import chatProtocol.User;
import jakarta.xml.bind.*;
import java.io.*;


public class XmlPersister {
    private static XmlPersister instancia;
    private String path;
    private XmlPersister() {}

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static XmlPersister getInstacia() {
        if (instancia == null) instancia = new XmlPersister();
        return instancia;
    }

    public Data cargar () throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        FileInputStream istream = new FileInputStream(this.path);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Data result = (Data) unmarshaller.unmarshal(istream);
        istream.close();
        // System.out.println(result.toString());
        return result;
    }

    public void guardar (Data data) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        FileOutputStream ostream = new FileOutputStream(this.path);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(data, ostream);
        ostream.flush();
        ostream.close();
    }
}
