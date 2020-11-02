package rsystems.handlers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLHandler {

    protected static Connection connection = null;

    protected final String DatabaseURL;
    protected final String DatabaseUser;
    protected final String DatabaseUserPass;


    public SQLHandler(String DatabaseURL, String DatabaseUser, String DatabaseUserPass) {
        this.DatabaseURL = DatabaseURL;
        this.DatabaseUser = DatabaseUser;
        this.DatabaseUserPass = DatabaseUserPass;
        connect();
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(DatabaseURL, DatabaseUser, DatabaseUserPass);

            if (connection.isValid(30)) {
                System.out.println("Database connected");
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }


    }

    // Get date of last seen using ID
    public String getDate(String id) {
        String date = "";

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return date;
    }

    public String getDate(String id, String table) {
        String date = "";

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM " + table + " WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return date;
    }

    // Set the last seen date using id
    public int setDate(String table, String id, String date) {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            return (st.executeUpdate("UPDATE " + table + " SET DATE = \"" + date + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return 0;
    }

    // Get date of last seen using ID
    public String getName(String id) {
        String name = "";

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT NAME FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                name = rs.getString("NAME");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return name;
    }

    // Set the name of the user using id
    public int setName(String id, String name) {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            return (st.executeUpdate("UPDATE LastSeenTable SET NAME = \"" + name + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return 0;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date) {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            return st.execute("INSERT INTO LastSeenTable (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return false;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date, String column) {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            return st.execute("INSERT INTO " + column + " (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return false;
    }

    // Remove using from DB
    public boolean removeUser(String id) {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            return st.execute("DELETE FROM LastSeenTable WHERE ID = " + id);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return false;
    }

    // Close Connection to DB
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Return an array list of all values in the DB
    public ArrayList<String> getAllUsers(String column) {
        ArrayList<String> values = new ArrayList<>();

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM LastSeenTable");
            while (rs.next()) {
                values.add(rs.getString(column));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return values;
    }

    // Return an array list of all values in the DB
    public HashMap<String, String> getAllUsers() {
        HashMap<String, String> idMap = new HashMap<>();

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, NAME FROM LastSeenTable");
            while (rs.next()) {
                idMap.put(rs.getString("ID"), rs.getString("NAME"));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return idMap;
    }

    // Return an array list of all values in the DB
    public HashMap<String, String> getAllUserDates() {
        HashMap<String, String> idMap = new HashMap<>();

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, DATE FROM LastSeenTable");
            while (rs.next()) {
                idMap.put(rs.getString("ID"), rs.getString("DATE"));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }

        return idMap;
    }

    public int getDBSize() {
        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            int rowCount = 0;
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM KARMA");
            while(rs.next()){
                rowCount = rs.getInt(1);
            }

            return rowCount;


        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            //closeConnection();
        }
        return 0;
    }

}
