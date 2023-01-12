package chatServer.Data;

import chatProtocol.User;

import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.io.*;

public class UserDao {
    private Database db;

    private boolean toBoolean(int val) {
        return val == 1;
    }
    private int toInt(boolean val) {
        if (val) return 1;
        return 0;
    }

    public UserDao() {
        this.db = Database.instance();
    }

    public User fromUser(ResultSet resultSet, String alias) {
        try {
            User usr = new User();
            usr.setId(resultSet.getString(alias + ".id"));
            usr.setPassword(resultSet.getString(alias + ".password"));
            usr.setName(resultSet.getString(alias + ".name"));
            usr.setConnected(toBoolean(resultSet.getInt(alias + ".connected")));
            return usr;
        } catch (Exception e) {
            return null;
        }
    }

    public User getSpecificUser(String id) {
        String sql = "select " +
                "* " +
                "from User u " +
                "where u.id=?";
        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet rs = db.executeQuery(stm);
            User u;
            if (rs.next()) {
                u = fromUser(rs, "u");
                return u;
            } else {
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
            return null;
        }
    }

    public void addUser(User user) {
        String sqlStatement = "insert into " +
                "User " +
                "(id, password, name, connected) " +
                "values(?,?,?,?)";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, user.getId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setInt(4, toInt(user.isConnected()));
            db.executeUpdate(statement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error in data entry, or key is repeated.");
        }
    }

    public List<User> searchUsers(String filter) {
        List<User> result = new ArrayList<User>();
        String sqlStatement = "select * " +
                "from " +
                "User u " +
                "where u.name like ?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, "%" + filter + "%");
            ResultSet resultSet = db.executeQuery(statement);
            User user;
            while (resultSet.next()) {
                user = fromUser(resultSet, "u");
                result.add(user);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Unexpected error.");
        }
        return result;
    }

    public void deleteUser(User user) {
        String sqlStatement = "delete " +
                "from User " +
                "where id=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, user.getId());
            int count = db.executeUpdate(statement);
            if (count == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
        }
    }

    public void updateUser(User user) {
        String sqlStatement = "update " +
                "User " +
                "set id=?, password=?, name=?, connected=? " +
                "where id=?";
        try {
            PreparedStatement statement = db.prepareStatement(sqlStatement);
            statement.setString(1, user.getId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setInt(4, toInt(user.isConnected()));
            statement.setString(5, user.getId());
            int count = db.executeUpdate(statement);
            if (count == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(new JFrame(), "User does not exist.");
        }
    }
}
