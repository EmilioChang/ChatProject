package chatServer.Data;

import chatProtocol.Message;

import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Date;
import chatProtocol.User;

public class MessageDao {
    private Database db;

    private boolean toBoolean(int val) {
        return val == 1;
    }
    private int toInt(boolean val) {
        if (val) return 1;
        return 0;
    }

    public MessageDao() {
        this.db = Database.instance();
    }

    public Message fromMessage(ResultSet resultSet, String alias) {
        try {
            Message msg = new Message();
            String dump = resultSet.getString(alias + ".id");
            msg.setReceiver(new User(resultSet.getString(alias + ".receiver"), "", resultSet.getString(alias + ".receiver")));
            msg.setSender(new User(resultSet.getString(alias + ".sender"), "", resultSet.getString(alias + ".sender")));
            msg.setMessage(resultSet.getString(alias + ".message"));
            msg.setSeen(toBoolean(resultSet.getInt(alias + ".seen")));
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    public void addMessage(Message message) {
        String sqlStatement = "insert into " +
                "Message " +
                "(id, sender, receiver, message, seen) " +
                "values(?,?,?,?,?)";
        try {
            Date currentDateTime = new Date();
            int seconds = (int) (currentDateTime.getTime() / 1000);
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setInt(1, seconds);
            statement.setString(2, message.getSender().getId());
            statement.setString(3, message.getReceiver().getId());
            statement.setString(4, message.getMessage());
            statement.setInt(5, toInt( message.isSeen()));
            db.executeUpdate(statement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error in data entry, or key is repeated.");
        }
    }

    public List<Message> searchMessageSender(String filter) {
        List<Message> result = new ArrayList<Message>();
        String sqlStatement = "select * " +
                "from " +
                "Message m " +
                "where m.sender=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, filter);
            ResultSet resultSet = db.executeQuery(statement);
            Message m;
            while (resultSet.next()) {
                m = fromMessage(resultSet, "m");
                result.add(m);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
        }
        return result;
    }

    public List<Message> searchMessageReceiver(String filter) {
        List<Message> result = new ArrayList<Message>();
        String sqlStatement = "select * " +
                "from " +
                "Message m " +
                "where m.receiver=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, filter);
            ResultSet resultSet = db.executeQuery(statement);
            Message m;
            while (resultSet.next()) {
                m = fromMessage(resultSet, "m");
                result.add(m);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
        }
        return result;
    }

    public int fromId(ResultSet resultSet, String alias) {
        try {
            int id = resultSet.getInt(alias + ".id");;
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Integer> getAllMessageId(String filter) {
        List<Integer> result = new ArrayList<Integer>();
        String sqlStatement = "select id " +
                "from " +
                "Message m " +
                "where m.receiver=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, filter);
            ResultSet resultSet = db.executeQuery(statement);
            Integer m;
            while (resultSet.next()) {
                m = fromId(resultSet, "m");
                result.add(m);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
        }
        return result;
    }

    public void updateMessage(Message message, int id) {
        String sqlStatement = "update " +
                "Message " +
                "set id=?, sender=?, receiver=?, message=?, seen=? " +
                "where id=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setInt(1, id);
            statement.setString(2, message.getSender().getId());
            statement.setString(3, message.getReceiver().getId());
            statement.setString(4, message.getMessage());
            statement.setInt(5, toInt(message.isSeen()));
            statement.setInt(6, id);
            int count = db.executeUpdate(statement);
            if (count == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
        }
    }

    public void deleteMessage(User user) throws Exception {
        try {
            String sqlStatement = "delete " +
                    "from Message " +
                    "where receiver =?";
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, user.getId());
            int count = db.executeUpdate(statement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Message does not exist.");
        }
    }

}
