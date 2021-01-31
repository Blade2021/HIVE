package rsystems.handlers;

import java.sql.*;
import java.util.*;

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

    /**
     * Initialize the class using a pool that is already created.
     *
     * @param pool The PoolDataSource
     */
    public SQLHandler(MariaDbPoolDataSource pool) {
        SQLHandler.pool = pool;
    }

    public String getValue(String tableName, String valueColumn, String identifierColumn, Long identifier){
        String output = null;
        try {
            Connection connection = pool.getConnection();

            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT %s FROM %s WHERE %s = %d", valueColumn,tableName,identifierColumn,identifier));
            while (rs.next()) {
                output = rs.getString(valueColumn);
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;

    }

    /**
     * Check the blacklist table for a userID, if found Ignore the user.
     *
     * @param userID The userid of the user to be checked.
     * @return Wether of not the user is blacklisted
     */
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

    /**
     * Record the amount of times a command/reference has been used.  If the command/reference does not exist in the database table yet.  It will add it.
     *
     * @param commandName The command/reference to be logged.
     */
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

    /**
     * Add a role to the database as as a role with permissions.  This database table controls what roles have what permissions to the bot.
     *
     * @param roleID The role to be given mod permissions.
     * @param roleName The name of the role.
     * @param authLevel The permission index that the role will have.  This uses the binary system as before.
     * @return True - No errors | False - Errors occured on insert
     */
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

    /**
     * Remove a role from the autheniation table.
     *
     * @param roleID The role to be removed from the authentication table.
     * @return True - No errors | False - Errors occured on removal
     */
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

    /**
     * Update a role that is already in the auth table with a new name
     *
     * @param roleID The role to be updated.
     * @param roleName The new name of the role.
     * @return True - No errors | False - Errors occured on insert
     */
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

    /**
     * Update a role that is already in the auth table with a new permissions
     *
     * @param roleID The role to be updated.
     * @param authLevel The new permissions for the role.
     * @return True - No errors | False - Errors occured on insert
     */
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

    /**
     * Get a map of the roleID and Permissions for all roles in the auth table.
     *
     * @return Map[RoleID,Role Permissions]
     */
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

    /**
     * Add an emoji alias to the Emoji Whitelist for a role.
     *
     * @param roleID The roleID that is receiving the emoji to its whitelist.
     * @param emojiUnicode The emoji in ALIAS form that will get stored as a varchar in the database.
     * @return True = Successful insertion | False = Database Error
     */
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

    /**
     * Grab the amount of times a command OR reference has been used.
     *
     * @param commandName The command name.
     * @return # of times the command/reference has been called.
     */
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


    /**
     * Compare the assignable role table to see if it contains the provided role ID.
     *
     * @param roleID The roleID to compare to the database.
     * @return If role is found on the Assignable role table, then returns true.  Otherwise return false.
     */
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

    /**
     * Get a list of assignable Roles
     *
     * @return List[RoleIDs] of assignable roles.
     */
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


    /**
     * Store the channel and message IDs into a table to allow pulling the message in the future for edits.
     *
     *
     * @param channelID The channelID is used in the future to know what channel to pull from.
     * @param messageID The messageID, to allow grabbing the message in the future.
     * @return true = Successful insert into database | false = Database insertion error
     */
    public boolean storeEmbedData(Long channelID, Long messageID){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO HIVE_EmbedTable (ChannelID, MessageID) VALUES (%d, %d)",channelID,messageID));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    /**
     * Get the channelID associated with a messageID from the database.
     *
     *
     * @param messageID The ID of the message to lookup.
     * @return Returns channelID if messageID was found.  Otherwise returns null.
     */
    public Long getEmbedChannel(Long messageID){
        Long output = null;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT ChannelID FROM HIVE_EmbedTable WHERE MessageID = %d",messageID));
            while(rs.next()){
                output = rs.getLong("ChannelID");
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    /**
     * Get the channelID associated with a messageID from the database.
     *
     *
     * @param tableName The name of the table to update.
     * @param identifierColumn The column name that holds the identifier to use to identify what row to delete.
     * @param identifier The identifier that will determine what row to delete.
     * @return Returns how many rows were deleted.
     */
    public Integer deleteValue(String tableName, String identifierColumn, Long identifier){
        Integer output = null;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d",tableName,identifierColumn,identifier));
            output = st.getUpdateCount();

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public Integer deleteValue(String tableName, String identifierColumn, Integer identifier){
        Integer output = null;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d",tableName,identifierColumn,identifier));
            output = st.getUpdateCount();

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public Integer insertActivity(String activity){
        Integer output = 0;

        try{
            Connection connection = pool.getConnection();

            PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO HIVE_ActivityList (ActivityString) VALUES (\"%s\")",activity),new String[] {"ID"});
            st.execute();

            if(st.getUpdateCount() >= 1){
                ResultSet rs = st.getGeneratedKeys();
                while(rs.next()) {
                    output = rs.getInt("ID");
                }
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public TreeMap<Integer, String> getActivityMap(){
        TreeMap<Integer, String> activityMap = new TreeMap<>();

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, ActivityString FROM HIVE_ActivityList");
            while(rs.next()){
                activityMap.putIfAbsent(rs.getInt("ID"),rs.getString("ActivityString"));
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return activityMap;

    }

    public String nextActivity(Integer currentIndex){
        String output = null;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT ID, ActivityString FROM HIVE_ActivityList WHERE ID > %d LIMIT 1",currentIndex));
            while(rs.next()){
                output = rs.getString("ActivityString");
                HiveBot.activityStatusIndex = rs.getInt("ID");
            }

            if(output == null){
                rs = st.executeQuery(String.format("SELECT ActivityString FROM HIVE_ActivityList WHERE ID = 1"));
                while(rs.next()){
                    output = rs.getString("ActivityString");
                }

                HiveBot.activityStatusIndex = 1;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;

    }

    public Integer checkAuthOverride(Long userID){
        Integer output = null;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT AuthLevel FROM HIVE_AuthUser WHERE UserID = %d LIMIT 1",userID));
            while(rs.next()){
                output = rs.getInt("AuthLevel");
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean insertAuthUser(Long userID, int authLevel, String userTag){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("INSERT INTO HIVE_AuthUser (UserID, AuthLevel, UserTag) VALUES (%d, %d, '%s')",userID,authLevel,userTag));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean removeAuthUser(Long userID){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM HIVE_AuthUser WHERE UserID = %d",userID));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean updateAuthUser(Long userID, Integer newAuthLevel){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE FROM HIVE_AuthUser SET AuthLevel = %d WHERE UserID = %d",newAuthLevel, userID));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean updateAuthUser(Long userID, String userTag){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE FROM HIVE_AuthUser SET UserTag = '%s' WHERE UserID = %d",userTag, userID));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean insertUserMessage(Long userID, String message){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("INSERT INTO HIVE_UserMessageTable (UserID, Message) VALUES (%d, '%s')",userID,message));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }

    public boolean updateUserMessage(Long userID, String message){
        boolean output = false;

        try{
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE HIVE_UserMessageTable SET Message = \"%s\" WHERE UserID = %d",message,userID));
            if(st.getUpdateCount() >= 1){
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }


}
