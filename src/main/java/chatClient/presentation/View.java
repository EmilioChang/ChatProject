package chatClient.presentation;

import chatClient.Application;
import chatProtocol.Message;
import chatProtocol.User;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class View implements Observer {
    private JPanel panel;
    private JPanel loginPanel;
    private JPanel bodyPanel;
    private JLabel idLabel;
    private JTextField idTextField;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordTextField;
    private JButton loginButton;
    private JButton exitButton;
    private JButton postButton;
    private JTextPane messagePane;
    private JButton logoutButton;
    private JTextField messageTextField;
    private JButton registerButton;
    private JScrollPane contactPane;
    private JTable contactTable;
    private JTextField contactTextField;
    private JButton contactButton;
    private JButton searchButton;
    private JTextField searchFdl;
    private JLabel textoFdl;
    private JLabel notificacionFdl;
    private JPanel evenAnotherPanel;

    Model model;
    Controller controller;

    public JTable getContactTable() {
        return contactTable;
    }

    public void setContactTable(JTable contactTable) {
        this.contactTable = contactTable;
    }

    public View() {
        loginPanel.setVisible(true);
        Application.window.getRootPane().setDefaultButton(loginButton);
        bodyPanel.setVisible(false);
        DefaultCaret caret = (DefaultCaret) messagePane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passwd = new String(passwordTextField.getPassword());
                User u = new User(idTextField.getText(), passwd, "Undefined");
                idTextField.setBackground(Color.white);
                passwordTextField.setBackground(Color.white);
                try {
                    if (idTextField.equals("") || passwd.equals("")) throw new Exception();
                    controller.login(u);
                    idTextField.setText("");
                    passwordTextField.setText("");
                } catch (Exception ex) {
                    idTextField.setBackground(Color.orange);
                    passwordTextField.setBackground(Color.orange);
                }
                if (contactTable.getRowCount() != 0) contactTable.setRowSelectionInterval(0,0);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User u = new User(idTextField.getText(), new String(passwordTextField.getPassword()), "");
                idTextField.setBackground(Color.white);
                passwordTextField.setBackground(Color.white);
                try {
                    if (idTextField.equals("") || passwordTextField.getText().equals("")) throw new Exception();
                    controller.register(u);
                    idTextField.setText("");
                    passwordTextField.setText("");
                } catch (Exception ex) {
                    idTextField.setBackground(Color.orange);
                    passwordTextField.setBackground(Color.orange);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.logout();
                    notificacionFdl.setText("");
                    textoFdl.setText("");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Selecciona el usuario de la tabla y se lo envía al post.
                int row = contactTable.getSelectedRow();
                String text = messageTextField.getText();
                controller.post(text, row);
            }
        });

        contactButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    controller.addContact();
                    contactTextField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Usuario no encontrado", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        contactPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                model.commit(Model.CHAT);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.searchContact(searchFdl.getText());
                     model.commit(Model.CHAT);
                     searchFdl.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Usuario no encontrado", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

       contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = contactTable.getSelectedRow();
                model.commit(model.CHAT);
                if (row != -1) {
                    contactTable.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    public void setModel(Model model) {
        this.model = model;
        model.addObserver(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void update(java.util.Observable updatedModel, Object properties) {
        int row = -1;
        if (contactTable != null) row = contactTable.getSelectedRow();
        int[] cols = {
                TableModel.ID,
                TableModel.CONNECTED
        };
        if (model.getContactsOnlyModel() == null) contactTable.setModel(new TableModel(cols, new ArrayList<User>()));
        else {
            contactTable.setModel(new TableModel(cols, model.getContactsOnlyModel()));
            contactTable.setRowHeight(30);
        }
        int prop = (int) properties;
        if (model.getCurrentUser() == null) {
            Application.window.setTitle("CHAT");
            loginPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(loginButton);
            bodyPanel.setVisible(false);
        } else {
            Application.window.setTitle(model.getCurrentUser().getName().toUpperCase());
            loginPanel.setVisible(false);
            bodyPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(postButton);
            if ((prop & Model.CHAT) == Model.CHAT) {
                this.messagePane.setText("");
                String text = "";
                if (row == -1) {
                    List<String> newMessages = new ArrayList<>();
                    String textNotification= "Nuevos mensajes de: ";
                    for (Message m : model.getMessages()) {
                        if (m.getReceiver().equals(model.getCurrentUser()) && !m.isSeen() && !newMessages.contains(m.getSender().getId())) {
                            newMessages.add(m.getSender().getId());
                        }
                    }
                    for (int i = 0; i < newMessages.size(); i++) {
                        textNotification += (newMessages.get(i)) + ", ";
                    }
                    notificacionFdl.setText(textNotification);
                }
                else if (model.getContacts() != null) {
                    User u = model.getContacts().get(row);
                    contactTable.setRowSelectionInterval(row, row);
                    textoFdl.setText("ESTAS CHATEANDO CON: " + u.getName());
                    for (Message m : model.getMessages()) {
                        if (m.getSender().equals(model.getCurrentUser()) && m.getReceiver().equals(u)) {
                            text += ("Me: " + m.getMessage() + "\n");
                        } else if (m.getSender().equals(u) && m.getReceiver().equals(model.getCurrentUser())) {
                            text += (u.getName() + ": " + m.getMessage() + "\n");
                        }
                    }
                }
                this.messagePane.setText(text);
            }
        }
        panel.validate();
    }

    public JTextField getMessageTextField() {
        return messageTextField;
    }

    public void setMessageTextField(JTextField messageTextField) {
        this.messageTextField = messageTextField;
    }

    public JTextField getContactTextField() {
        return contactTextField;
    }

    public void setSearchTextField(JTextField searchTextField) {
        this.contactTextField = searchTextField;
    }

    public JButton getContactButton() {
        return contactButton;
    }

    public void setContactButton(JButton searchButton) {
        this.contactButton = searchButton;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public void setLoginPanel(JPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }

    public void setBodyPanel(JPanel bodyPanel) {
        this.bodyPanel = bodyPanel;
    }

    public JLabel getIdLabel() {
        return idLabel;
    }

    public void setIdLabel(JLabel idLabel) {
        this.idLabel = idLabel;
    }

    public JTextField getIdTextField() {
        return idTextField;
    }

    public void setIdTextField(JTextField idTextField) {
        this.idTextField = idTextField;
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(JLabel nameLabel) {
        this.nameLabel = nameLabel;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public void setNameTextField(JTextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    public JLabel getPasswordLabel() {
        return passwordLabel;
    }

    public void setPasswordLabel(JLabel passwordLabel) {
        this.passwordLabel = passwordLabel;
    }

    public JTextField getPasswordTextField() {
        return passwordTextField;
    }

    public void setPasswordTextField(JPasswordField passwordTextField) {
        this.passwordTextField = passwordTextField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(JButton loginButton) {
        this.loginButton = loginButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public void setExitButton(JButton exitButton) {
        this.exitButton = exitButton;
    }

    public JButton getPostButton() {
        return postButton;
    }

    public void setPostButton(JButton postButton) {
        this.postButton = postButton;
    }

    public JTextPane getMessagePane() {
        return messagePane;
    }

    public void setMessagePane(JTextPane messagePane) {
        this.messagePane = messagePane;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public void setLogoutButton(JButton logoutButton) {
        this.logoutButton = logoutButton;
    }

}
