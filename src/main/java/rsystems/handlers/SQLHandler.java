package rsystems.handlers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariadb.jdbc.MariaDbPoolDataSource;
import rsystems.HiveBot;

public class SQLHandler {
    protected static MariaDbPoolDataSource pool = null;

    public SQLHandler(String URL, String user, String pass) {

        try {
            pool = new MariaDbPoolDataSource(URL);
            pool.setUser(user);
            pool.setPassword(pass);
            pool.setMaxPoolSize(10);
            pool.setMinPoolSize(2);

            pool.initialize();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public SQLHandler(MariaDbPoolDataSource pool) {
        SQLHandler.pool = pool;
    }

    public boolean checkBlacklist(Long userID) {
        boolean output = false;
        try {
            Connection connection = pool.getConnection();

            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT UserID FROM HIVE_Blacklist WHERE UserID = %d", userID));
            while (rs.next()) {
                if (rs.getLong("UserID") == userID) {
                    output = true;
                }
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;

    }

    // Get date of last seen using ID
    public String getDate(String id) {
        String date = "";

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT DATE FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return date;
    }

    public String getDate(String id, String table) {
        String date = "";

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM " + table + " WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return date;
    }

    // Set the last seen date using id
    public int setDate(String table, String id, String date) {

        int updateCount = 0;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            updateCount = st.executeUpdate("UPDATE " + table + " SET DATE = \"" + date + "\" WHERE ID = " + id);
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return updateCount;
    }

    // Get date of last seen using ID
    public String getName(String id) {
        String name = "";

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT NAME FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                name = rs.getString("NAME");
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return name;
    }

    // Set the name of the user using id
    public int setName(String id, String name) {
        int output = 0;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            output = (st.executeUpdate("UPDATE LastSeenTable SET NAME = \"" + name + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            output = st.execute("INSERT INTO LastSeenTable (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date, String column) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            output = st.execute("INSERT INTO " + column + " (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    // Remove using from DB
    public boolean removeUser(String id) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            output = st.execute("DELETE FROM LastSeenTable WHERE ID = " + id);
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    // Return an array list of all values in the DB
    public ArrayList<String> getAllUsers(String column) {
        ArrayList<String> values = new ArrayList<>();

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM LastSeenTable");
            while (rs.next()) {
                values.add(rs.getString(column));
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return values;
    }

    // Return an array list of all values in the DB
    public HashMap<String, String> getAllUsers() {
        HashMap<String, String> idMap = new HashMap<>();
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, NAME FROM LastSeenTable");
            while (rs.next()) {
                idMap.put(rs.getString("ID"), rs.getString("NAME"));
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idMap;
    }

    // Return an array list of all values in the DB
    public HashMap<String, String> getAllUserDates() {
        HashMap<String, String> idMap = new HashMap<>();

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, DATE FROM LastSeenTable");
            while (rs.next()) {
                idMap.put(rs.getString("ID"), rs.getString("DATE"));
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return idMap;
    }

    public int getDBSize() {
        int output = 0;
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            int rowCount = 0;
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM KARMA");
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }

            output = rowCount;
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    public void logCommandUsage(String commandName) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT UsageCount FROM HIVE_CommandTracker WHERE Name = \"%s\"", commandName));

            boolean commandFound = false;
            while (rs.next()) {
                commandFound = true;
                int newCount = rs.getInt(1) + 1;

                st.execute(String.format("UPDATE HIVE_CommandTracker SET UsageCount = %d, LastUsage = current_timestamp WHERE Name = \"%s\"", newCount, commandName));
            }
            if (!commandFound) {
                st.execute(String.format("INSERT INTO HIVE_CommandTracker (Name, UsageCount) VALUES (\"%s\", 1)", commandName));
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean addAuthRole(Long roleID, String roleName, Integer authLevel) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO HIVE_AuthRole (RoleID, RoleName, AuthLevel) VALUES (%d, \"%s\",%d)", roleID, roleName, authLevel));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean removeAuthRole(Long roleID) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("DELETE FROM HIVE_AuthRole WHERE (RoleID = %d)", roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean updateAuthRole(Long roleID, String roleName) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("UPDATE HIVE_AuthRole SET (RoleName = \"%s\") WHERE (RoleID = %d)", roleName, roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean updateAuthRole(Long roleID, Integer authLevel) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("UPDATE HIVE_AuthRole SET AuthLevel = %d WHERE RoleID = %d", authLevel, roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public Map<Long,Integer> getAuthRoles(){
        Map<Long, Integer> authMap = new HashMap<>();

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT RoleID, AuthLevel FROM HIVE_AuthRole");
            while(rs.next()){
                authMap.putIfAbsent(rs.getLong("RoleID"),rs.getInt("AuthLevel"));
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return authMap;
    }

    public boolean addEmojiToWhitelist(Long roleID, String emojiUnicode) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            boolean emojiFound = false;

            ResultSet rs = st.executeQuery(String.format("SELECT RoleID FROM HIVE_PerksEmoji WHERE (RoleID = %d) AND (EmojiUnicode = \"%s\")", roleID, emojiUnicode));
            while (rs.next()) {
                emojiFound = true;
            }

            if (!emojiFound) {
                st.execute(String.format("INSERT INTO HIVE_PerksEmoji (RoleID, EmojiUnicode) VALUES (%d, \"%s\")", roleID, emojiUnicode));
                if (st.getUpdateCount() >= 1) {
                    output = true;
                }
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean removeEmojiFromWhitelist(Long roleID, String emoji) {

        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("DELETE FROM HIVE_PerksEmoji WHERE RoleID = %d AND EmojiUnicode = \"%s\"", roleID, emoji));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public void loadPerkEmojis(){

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            HiveBot.emojiPerkMap.clear();

            ResultSet rs = st.executeQuery("SELECT RoleID, EmojiUnicode FROM HIVE_PerksEmoji");
            while(rs.next()){

                Long roleID = rs.getLong("RoleID");
                String emoji = rs.getString("EmojiUnicode");

                if(HiveBot.emojiPerkMap.containsKey(roleID)){
                    HiveBot.emojiPerkMap.get(roleID).add(emoji);
                } else {
                    HiveBot.emojiPerkMap.putIfAbsent(roleID,new ArrayList<String>());
                    HiveBot.emojiPerkMap.get(roleID).add(emoji);
                }

            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public Integer checkUsage(String commandName){
        Integer output = null;
        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT UsageCount, LastUsage FROM HIVE_CommandTracker WHERE Name = \"%s\"", commandName));
            while(rs.next()){

                output = rs.getInt("UsageCount");

            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    public boolean checkAssignableRole(Long roleID){

        Boolean output = false;
        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT RoleID FROM HIVE_AssignRole WHERE RoleID = %d", roleID));
            while(rs.next()){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    public List<Long> assignableRoleList(){

        List<Long> output = new ArrayList<>();
        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT RoleID FROM HIVE_AssignRole"));
            while(rs.next()){
                output.add(rs.getLong("RoleID"));
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

}
