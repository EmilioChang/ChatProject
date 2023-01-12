package chatClient.presentation;

import chatClient.logic.Data;
import chatClient.logic.ServiceProxy;
import chatClient.logic.XmlPersister;
import chatProtocol.Message;
import chatProtocol.User;
import chatServer.Data.UserDao;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Controller {
    View view;
    Model model;
    ServiceProxy localService;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy) ServiceProxy.instance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
    }

    public void register(User u) throws Exception {
        JTextField name = new JTextField("");
        Object[] fields = {"Name:", name};
        int option = JOptionPane.showConfirmDialog(view.getPanel(), fields, view.getIdTextField().getText(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try {
                u.setName(name.getText());
                User logged = ServiceProxy.instance().register(u);
                model.setCurrentUser(logged);
                model.commit(Model.USER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getPanel(), ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            refreshContacts();
        }
    }

    public void login(User u) throws Exception {
        u.load();
        User logged = ServiceProxy.instance().login(u);
        logged.getMessages().addAll(u.loadMessages());
        if (logged == null) {
            register(u);
        } else {
            if (logged.getPassword().equals(u.getPassword())) {
                model.setMessages(logged.getMessages());
                model.sortMessages();
                model.setCurrentUser(logged);
                model.commit(Model.USER);
            } else {
                JOptionPane.showMessageDialog(view.getPanel(), "Check password.", "ERROR", JOptionPane.ERROR_MESSAGE);
                view.getPasswordTextField().setBackground(Color.orange);
            }
        }
        refreshContacts();
        model.sortMessages();
        model.commit(Model.USER + Model.CHAT);
    }

    public void post(String text, int row) {
        Message message = new Message();
        message.setMessage(text);
        message.setSender(model.getCurrentUser());
        if (row == -1) JOptionPane.showMessageDialog(view.getPanel(), "Seleccione un destinario", "ERROR", JOptionPane.ERROR_MESSAGE);
        else{
            message.setReceiver(model.getContacts().get(row));
            model.getMessages().add(message);
            ServiceProxy.instance().post(message);
            model.commit(Model.CHAT);
            if (row != -1) view.getContactTable().setRowSelectionInterval(row, row);
        }
        view.getMessageTextField().setText("");
    }

    public void searchContact(String id) {
        List<User> rows = model.getCurrentUser().getContacts().stream().filter(u -> u.getId().contains(id)).collect(java.util.stream.Collectors.toList());
        model.setContacts(rows);
    }

    public void addContact() throws Exception { // Parte 1 del addContact().
        String id = view.getContactTextField().getText();
        if (id.equals(model.getCurrentUser().getId())) return;
        User contact = localService.checkContact(id);
    }

    public void connectContacts(User u) throws Exception {
        for (int i = 0; i < model.getCurrentUser().getContacts().size(); i++) {
            if (model.getCurrentUser().getContacts().get(i).getId().equals(u.getId())) return;
        }
        model.getCurrentUser().getContacts().add(u);
        refreshContacts();
        model.commit(Model.CHAT);
    }

    public void notifyContact(User u, boolean state) {
        if (model.getCurrentUser() == null) return;
        if (model.getCurrentUser().getContacts() == null || model.getCurrentUser().getContacts().isEmpty()) return;
        for (int i = 0; i < model.getCurrentUser().getContacts().size(); i++) {
            if (model.getCurrentUser().getContacts().get(i).equals(u)) {
                model.getCurrentUser().getContacts().get(i).setConnected(state);
            }
        }
        model.commit(Model.CHAT);
    }

    public void refreshContacts() throws Exception {
        ServiceProxy.instance().notifyContacts(model.getCurrentUser());
    }
    public void refreshContactsOff() throws Exception {
        ServiceProxy.instance().notifyContactsOff(model.getCurrentUser());
    }

    public void logout() throws Exception {
        try {
            Data data = new Data();
            data.setContacts(model.getCurrentUser().getContacts());
            data.setMessages(model.getMessages());
            XmlPersister.getInstacia().guardar(data);
            refreshContactsOff();
            Thread.sleep(100);
            ServiceProxy.instance().logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.setContacts(new ArrayList<>());
            model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        model.commit(Model.USER + Model.CHAT);
    }
    public void deliver(Message message) {
        model.messages.add(message);
        model.commit(Model.CHAT);
    }
}

