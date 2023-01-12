package chatServer.Data;

import java.sql.*;
import java.util.*;
import java.io.*;

public class Database {
    private static Database instance;
    Connection connection;

    public Database() {
        getConnection();
    }

    public static Database instance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private static final String PROPERTIES_FILE_NAME = "/dataChatDatabase.properties";

    public void getConnection() {
        try {
            Properties prop = new Properties();
            prop.load(getClass().getResourceAsStream(PROPERTIES_FILE_NAME));
            String driver = prop.getProperty("database_driver");
            String server = prop.getProperty("database_server");
            String port = prop.getProperty("database_port");
            String user = prop.getProperty("database_user");
            String password = prop.getProperty("database_password");
            String database = prop.getProperty("database_name");

            String URL_conexion = "jdbc:mysql://" + server + ":" + port + "/" +
                    database + "?user=" + user + "&password=" + password + "&serverTimezone=UTC";
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(URL_conexion);
        } catch (Exception e) {
            System.err.println("DATABASE CONNECTION FAILED");
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public PreparedStatement prepareStatement(String statement) throws Exception {
        try {
            return connection.prepareStatement(statement);
        } catch (SQLException e) {
            throw new Exception("DATABASE ERROR");
        }
    }

    public int executeUpdate(PreparedStatement statement) throws Exception {
        try {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new Exception("DUPLICATE ENTRY");
        } catch (Exception ex) {
            throw new Exception("DATABASE ERROR");
        }
    }

    public ResultSet executeQuery(PreparedStatement statement) throws Exception {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new Exception("DATABASE ERROR");
        }
    }
}
