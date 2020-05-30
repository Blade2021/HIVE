package rsystems.handlers;

import java.sql.*;
import java.util.ArrayList;

public class SQLHandler {

    private static Connection connection = null;

    public void connect(){
        //Connection connection = null;
        try{
            String url = "jdbc:sqlite:honeycomb.db";
            connection = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public String getDate(String id){
        String date = "";

        try{

            if((connection == null) || (connection.isClosed())){
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM LastSeenTable WHERE ID = " + id);
            while(rs.next()){
                date = rs.getString("DATE");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }

        return date;
    }

    public int setDate(String id, String date){
        try{

            if((connection == null) || (connection.isClosed())){
                connect();
            }

            Statement st = connection.createStatement();
            return (st.executeUpdate("UPDATE LastSeenTable SET DATE = \"" + date + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }
        return 0;
    }

    public int setName(String id, String name){
        try{

            if((connection == null) || (connection.isClosed())){
                connect();
            }

            Statement st = connection.createStatement();
            return (st.executeUpdate("UPDATE LastSeenTable SET NAME = \"" + name + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }
        return 0;
    }

    public boolean insertUser(String id, String name, String date){
        try{

            if((connection == null) || (connection.isClosed())){
                connect();
            }

            Statement st = connection.createStatement();

            return st.execute("INSERT INTO LastSeenTable (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    public boolean removeUser(String id){
        try{

            if((connection == null) || (connection.isClosed())){
                connect();
            }

            Statement st = connection.createStatement();

            return st.execute("DELETE FROM LastSeenTable WHERE ID = " + id);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    private void closeConnection(){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    public ArrayList<String> getAllUsers(String column) {
        ArrayList<String> users = new ArrayList<>();

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM LastSeenTable");
            while (rs.next()) {
                users.add(rs.getString(column));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } finally {
            closeConnection();
        }

        return users;
    }

}
