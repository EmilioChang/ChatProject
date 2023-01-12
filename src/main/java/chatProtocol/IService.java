package chatProtocol;

public interface IService {
    public User login(User user) throws Exception;
    public void logout(User user) throws Exception;
    public User register(User user) throws Exception;
    public void post(Message message);
    public User checkContact(String id) throws Exception;
    public User notifyContacts(User user) throws Exception;
    public User notifyContactsOff(User user) throws Exception;
}
