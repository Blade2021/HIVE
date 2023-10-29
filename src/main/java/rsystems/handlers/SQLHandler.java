package rsystems.handlers;

import org.mariadb.jdbc.MariaDbPoolDataSource;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.*;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class SQLHandler {
    protected static MariaDbPoolDataSource pool = null;

    public SQLHandler(String URL, String user, String pass) {

        try {
            pool = new MariaDbPoolDataSource(URL);
            pool.setUser(user);
            pool.setPassword(pass);
            /*
            pool.setMaxPoolSize(10);
            pool.setMinPoolSize(2);



            pool.initialize();

             */
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

    public String getValue(String tableName, String valueColumn, String identifierColumn, Long identifier) throws SQLException {
        String output = null;
        Connection connection = pool.getConnection();

        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT %s FROM %s WHERE %s = %d", valueColumn, tableName, identifierColumn, identifier));
            while (rs.next()) {
                output = rs.getString(valueColumn);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;

    }

    public Integer getInteger(String tableName, String valueColumn, String identifierColumn, Long identifier) throws SQLException {
        Integer output = null;
        Connection connection = pool.getConnection();

        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT %s FROM %s WHERE %s = %d", valueColumn, tableName, identifierColumn, identifier));
            while (rs.next()) {
                output = rs.getInt(valueColumn);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    public boolean checkStreamBlacklist(Long userID) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT UserID FROM Stream_Blacklist WHERE UserID = %d", userID));
            while (rs.next()) {
                if (rs.getLong("UserID") == userID) {
                    output = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;

    }

    /**
     * Check the blacklist table for a userID, if found Ignore the user.
     *
     * @param userID The userid of the user to be checked.
     * @return Wether of not the user is blacklisted
     */
    public boolean checkBlacklist(Long userID) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT UserID FROM HIVE_Blacklist WHERE UserID = %d", userID));
            while (rs.next()) {
                if (rs.getLong("UserID") == userID) {
                    output = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;

    }

    // Get date of last seen using ID
    public String getDate(String id) throws SQLException {
        String date = "";

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT DATE FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return date;
    }

    public ArrayList<String> getList(String tableName, String columnName) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s", columnName, tableName));
            while (rs.next()) {
                list.add(rs.getString(columnName));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return list;
    }

    public ArrayList<String> getList(Long guildID, String tableName, String columnName) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s WHERE ChildGuildID = %d", columnName, tableName, guildID));
            while (rs.next()) {
                list.add(rs.getString(columnName));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return list;
    }

    public Map<Long, String> getMap(Long guildID, String tableName, String firstColumn, String secondColumn) throws SQLException {
        Map<Long, String> map = new HashMap<>();
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT %s, %s FROM %s WHERE ChildGuildID = %d", firstColumn, secondColumn, tableName, guildID));
            while (rs.next()) {

                map.putIfAbsent(rs.getLong(firstColumn), rs.getString(secondColumn));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return map;
    }

    public Integer putString(String tablename, String columnName, String newValue, String identifierColumn, Integer identifier) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", tablename, columnName, newValue, identifierColumn, identifier));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public Integer putKeyValue(String key, String value) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE Bot_Settings SET Value = '%s' WHERE `Key` = '%s'", value, key));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public String getKeyValue(String key) throws SQLException {
        String value = "";
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT Value FROM Bot_Settings WHERE `Key` = '%s'", key));
            while (rs.next()) {
                value = rs.getString("Value");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return value;
    }

    public Integer putInt(String tablename, String columnName, Integer newValue, String identifierColumn, String identifier) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE %s SET %s = %d WHERE %s = '%s'", tablename, columnName, newValue, identifierColumn, identifier));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    /**
     * @param tablename
     * @param columnName
     * @param newValue
     * @param identifierColumn
     * @param identifier
     * @return Returns the amount of rows changed
     * @throws SQLException
     */
    public Integer putInt(String tablename, String columnName, Integer newValue, String identifierColumn, Integer identifier) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE %s SET %s = %d WHERE %s = %d", tablename, columnName, newValue, identifierColumn, identifier));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public Timestamp getTimestamp(String tableName, String columnName, String identifierColumn, Long identifier) throws SQLException {

        Timestamp output = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s WHERE %s = %d", columnName, tableName, identifierColumn, identifier));
            while (rs.next()) {
                output = rs.getTimestamp(columnName);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public Integer putTimestamp(String tablename, String columnName, Timestamp value, String identifierColumn, Integer identifier) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'", tablename, columnName, value, identifierColumn, identifier));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }


    // Get date of last seen using ID
    public LED getLED(String ledName) throws SQLException {
        String date = "";

        Connection connection = pool.getConnection();

        LED led = new LED(ledName);
        boolean ledFound = false;

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT Voltage, WhitePixel, WattageTheory, WattageTested FROM LED_Table WHERE ledname = '%s'", ledName.toLowerCase()));

            while (rs.next()) {
                ledFound = true;
                led.setLedVoltage(rs.getInt("Voltage"));
                led.setWhiteIncluded(rs.getBoolean("WhitePixel"));
                led.setWattagePerPixel_Tested(rs.getFloat("WattageTested"));
                led.setWattagePerPixel_Theoretical(rs.getFloat("WattageTheory"));
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        if (ledFound)
            return led;
        else
            return null;
    }

    public ArrayList<String> getLEDList() throws SQLException {
        Connection connection = pool.getConnection();

        ArrayList<String> ledList = new ArrayList<>();
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ledName FROM LED_Table");

            while (rs.next()) {

                ledList.add(rs.getString("ledName"));
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return ledList;
    }

    /**
     * INSERT AN LED INTO THE SYSTEM  (This will be useful for you PIXELHEADS!)
     *
     * @param led - The LED with values set to be added.
     * @return <p>200 - Status OK</p>
     * @throws SQLException
     */
    public Integer insertLED(LED led) throws SQLException {
        Connection connection = pool.getConnection();

        Integer output = null;

        try {
            Statement st = connection.createStatement();

            // text  (String)
            // 121  (Integer)
            // 2312.221  (Float)
            // True/False  (Boolean)


            st.execute(String.format("INSERT INTO LED_Table (ledName, Voltage, WhitePixel, WattageTheory, WattageTested, Description) VALUES ('%s', %d, %s, %f, %f, '%s')",
                    led.getLedName(),
                    led.getLedVoltage(),
                    led.isWhiteIncluded(),
                    led.getWattagePerPixel_Theoretical(),
                    led.getWattagePerPixel_Tested(),
                    led.getDescription())
            );

            if (st.getUpdateCount() > 0) {
                output = 200;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return output;
    }

    public Map<String, LED> getLEDMap() throws SQLException {
        Connection connection = pool.getConnection();

        Map<String, LED> ledList = new HashMap();
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ledName, Voltage, WhitePixel, WattageTheory, WattageTested, Description FROM LED_Table");

            while (rs.next()) {

                LED led = new LED(rs.getString("ledName"));
                led.setLedVoltage(rs.getInt("Voltage"));
                led.setWhiteIncluded(rs.getBoolean("WhitePixel"));
                led.setWattagePerPixel_Tested(rs.getFloat("WattageTested"));
                led.setWattagePerPixel_Theoretical(rs.getFloat("WattageTheory"));
                led.setDescription(rs.getString("Description"));

                ledList.putIfAbsent(led.getLedName(), led);
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return ledList;
    }

    public boolean checkForLedUpsert() throws SQLException {
        Connection connection = pool.getConnection();

        boolean output = false;
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ledName FROM LED_Table WHERE Upsert = 1");

            while (rs.next()) {

                output = true;
                break;
            }

            st.execute("UPDATE LED_Table SET Upsert = 0 WHERE Upsert = 1");

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }


    public String getDate(String id, String table) throws SQLException {
        String date = "";

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM " + table + " WHERE ID = " + id);
            while (rs.next()) {
                date = rs.getString("DATE");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return date;
    }

    // Set the last seen date using id

    public int setDate(String table, String id, String date) throws SQLException {

        int updateCount = 0;
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            updateCount = st.executeUpdate("UPDATE " + table + " SET DATE = \"" + date + "\" WHERE ID = " + id);
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return updateCount;
    }

    // Get date of last seen using ID
    public String getName(String id) throws SQLException {
        String name = "";
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT NAME FROM LastSeenTable WHERE ID = " + id);
            while (rs.next()) {
                name = rs.getString("NAME");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return name;
    }

    // Set the name of the user using id
    public int setName(String id, String name) throws SQLException {
        int output = 0;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            output = (st.executeUpdate("UPDATE LastSeenTable SET NAME = \"" + name + "\" WHERE ID = " + id));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date) throws SQLException {
        boolean output = false;
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            output = st.execute("INSERT INTO LastSeenTable (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    // Insert NEW users into the DB
    public boolean insertUser(String id, String name, String date, String column) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            output = st.execute("INSERT INTO " + column + " (ID,NAME,DATE) VALUES(" + id + ",\"" + name + "\",\"" + date + "\");");
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    // Remove using from DB
    public boolean removeUser(String id) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            output = st.execute("DELETE FROM LastSeenTable WHERE ID = " + id);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    // Return an array list of all values in the DB
    public ArrayList<String> getAllUsers(String column) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM LastSeenTable");
            while (rs.next()) {
                values.add(rs.getString(column));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
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
    public void logCommandUsage(String commandName) throws SQLException {

        Connection connection = pool.getConnection();

        try {
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    /**
     * Add a role to the database as as a role with permissions.  This database table controls what roles have what permissions to the bot.
     *
     * @param roleID    The role to be given mod permissions.
     * @param roleName  The name of the role.
     * @param authLevel The permission index that the role will have.  This uses the binary system as before.
     * @return True - No errors | False - Errors occured on insert
     */
    public boolean addAuthRole(Long roleID, String roleName, Integer authLevel) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO HIVE_AuthRole (RoleID, RoleName, AuthLevel) VALUES (%d, \"%s\",%d)", roleID, roleName, authLevel));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Remove a role from the autheniation table.
     *
     * @param roleID The role to be removed from the authentication table.
     * @return True - No errors | False - Errors occured on removal
     */
    public boolean removeAuthRole(Long roleID) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            st.execute(String.format("DELETE FROM HIVE_AuthRole WHERE (RoleID = %d)", roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Update a role that is already in the auth table with a new name
     *
     * @param roleID   The role to be updated.
     * @param roleName The new name of the role.
     * @return True - No errors | False - Errors occured on insert
     */
    public boolean updateAuthRole(Long roleID, String roleName) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            st.execute(String.format("UPDATE HIVE_AuthRole SET (RoleName = \"%s\") WHERE (RoleID = %d)", roleName, roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Update a role that is already in the auth table with a new permissions
     *
     * @param roleID    The role to be updated.
     * @param authLevel The new permissions for the role.
     * @return True - No errors | False - Errors occured on insert
     */
    public boolean updateAuthRole(Long roleID, Integer authLevel) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            st.execute(String.format("UPDATE HIVE_AuthRole SET AuthLevel = %d WHERE RoleID = %d", authLevel, roleID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Get a map of the roleID and Permissions for all roles in the auth table.
     *
     * @return Map[RoleID, Role Permissions]
     */
    public Map<Long, Integer> getAuthRoles() throws SQLException {
        Map<Long, Integer> authMap = new HashMap<>();

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT RoleID, AuthLevel FROM HIVE_AuthRole");
            while (rs.next()) {
                authMap.putIfAbsent(rs.getLong("RoleID"), rs.getInt("AuthLevel"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return authMap;
    }

    /**
     * Add an emoji alias to the Emoji Whitelist for a role.
     *
     * @param roleID       The roleID that is receiving the emoji to its whitelist.
     * @param emojiUnicode The emoji in ALIAS form that will get stored as a varchar in the database.
     * @return True = Successful insertion | False = Database Error
     */
    public boolean addEmojiToWhitelist(Long roleID, String emojiUnicode) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();

        try {
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Remove an emoji from the whitelist.  This will affect the users nicknames.
     *
     * @param roleID The role to remove the emoji FROM
     * @param emoji  The emoji to be removed from the whitelist.
     * @return True - Emoji found and removed | False - No matches found
     */
    public boolean removeEmojiFromWhitelist(Long roleID, String emoji) throws SQLException {

        boolean output = false;

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            st.execute(String.format("DELETE FROM HIVE_PerksEmoji WHERE RoleID = %d AND EmojiUnicode = \"%s\"", roleID, emoji));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public void loadPerkEmojis() throws SQLException {

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            HiveBot.emojiPerkMap.clear();

            ResultSet rs = st.executeQuery("SELECT RoleID, EmojiUnicode FROM HIVE_PerksEmoji");
            while (rs.next()) {

                Long roleID = rs.getLong("RoleID");
                String emoji = rs.getString("EmojiUnicode");

                if (HiveBot.emojiPerkMap.containsKey(roleID)) {
                    HiveBot.emojiPerkMap.get(roleID).add(emoji);
                } else {
                    HiveBot.emojiPerkMap.putIfAbsent(roleID, new ArrayList<String>());
                    HiveBot.emojiPerkMap.get(roleID).add(emoji);
                }

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

    }

    /**
     * Grab the amount of times a command OR reference has been used.
     *
     * @param commandName The command name.
     * @return # of times the command/reference has been called.
     */
    public Integer checkUsage(String commandName) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT UsageCount, LastUsage FROM HIVE_CommandTracker WHERE Name = \"%s\"", commandName));
            while (rs.next()) {

                output = rs.getInt("UsageCount");

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }


    /**
     * Compare the assignable role table to see if it contains the provided role ID.
     *
     * @param roleID The roleID to compare to the database.
     * @return If role is found on the Assignable role table, then returns true.  Otherwise return false.
     */
    public boolean checkAssignableRole(Long roleID) throws SQLException {

        Boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT RoleID FROM HIVE_AssignRole WHERE RoleID = %d", roleID));
            while (rs.next()) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    /**
     * Get a list of assignable Roles
     *
     * @return List[RoleIDs] of assignable roles.
     */
    public List<Long> assignableRoleList() throws SQLException {

        List<Long> output = new ArrayList<>();

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT RoleID FROM HIVE_AssignRole");
            while (rs.next()) {
                output.add(rs.getLong("RoleID"));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }


    /**
     * Store the channel and message IDs into a table to allow pulling the message in the future for edits.
     *
     * @param channelID The channelID is used in the future to know what channel to pull from.
     * @param messageID The messageID, to allow grabbing the message in the future.
     * @return true = Successful insert into database | false = Database insertion error
     */
    public boolean storeEmbedData(Long channelID, Long messageID) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO HIVE_EmbedTable (ChannelID, MessageID) VALUES (%d, %d)", channelID, messageID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Get the channelID associated with a messageID from the database.
     *
     * @param messageID The ID of the message to lookup.
     * @return Returns channelID if messageID was found.  Otherwise returns null.
     */
    public Long getEmbedChannel(Long messageID) throws SQLException {
        Long output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT ChannelID FROM HIVE_EmbedTable WHERE MessageID = %d", messageID));
            while (rs.next()) {
                output = rs.getLong("ChannelID");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Get the channelID associated with a messageID from the database.
     *
     * @param tableName        The name of the table to update.
     * @param identifierColumn The column name that holds the identifier to use to identify what row to delete.
     * @param identifier       The identifier that will determine what row to delete.
     * @return Returns how many rows were deleted.
     */
    public Integer deleteValue(String tableName, String identifierColumn, Long identifier) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d", tableName, identifierColumn, identifier));
            output = st.getUpdateCount();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Delete a row(s) from the table with a identifier.
     *
     * @param tableName        The table referencing the row(s) to be deleted
     * @param identifierColumn The column name that holds the identifier
     * @param identifier       The identifier, this is used to query what rows to delete
     * @return The amount of rows deleted.
     */
    public Integer deleteValue(String tableName, String identifierColumn, Integer identifier) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d", tableName, identifierColumn, identifier));
            output = st.getUpdateCount();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public Integer deleteRow(String tableName, String identifierColumn, Long identifier) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d", tableName, identifierColumn, identifier));
            output = st.getUpdateCount();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public Integer deleteRow(String tableName, String identifierColumn, Integer identifier) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = %d", tableName, identifierColumn, identifier));
            output = st.getUpdateCount();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public Integer deleteRow(String tableName, String identifierColumn, String identifier) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM %s WHERE %s = '%s'", tableName, identifierColumn, identifier));
            output = st.getUpdateCount();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * This method will place a string in the rolling queue of the activity pool.
     *
     * @param activity What to add to the queue of messages that get displayed in the activity field.  Limit 32 characters
     * @return Returns the ID of the inserted row
     */
    public Integer insertActivity(String activity) throws SQLException {
        Integer output = 0;

        Connection connection = pool.getConnection();
        try {


            PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO HIVE_ActivityList (ActivityString) VALUES (\"%s\")", activity), new String[]{"ID"});
            st.execute();

            if (st.getUpdateCount() >= 1) {
                ResultSet rs = st.getGeneratedKeys();
                while (rs.next()) {
                    output = rs.getInt("ID");
                }
            }


        } catch (SQLException throwables) {
            //throwables.printStackTrace();
            throw throwables;
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Get a TreeMap of the activities that are in the database pool.
     *
     * @return TreeMap of entries
     */
    public TreeMap<Integer, String> getActivityMap() throws SQLException {
        TreeMap<Integer, String> activityMap = new TreeMap<>();

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, ActivityString FROM HIVE_ActivityList");
            while (rs.next()) {
                activityMap.putIfAbsent(rs.getInt("ID"), rs.getString("ActivityString"));
            }


        } catch (SQLException throwables) {
            //throwables.printStackTrace();

            throw throwables;
        } finally {
            connection.close();
        }

        return activityMap;

    }

    /**
     * This method pulls the next activity from the pool.  If the next activity is null, it returns back to the first row.
     *
     * @param currentIndex The current activities ID
     * @return The next activity found in the database.
     */
    public String nextActivity(Integer currentIndex) throws SQLException {
        String output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT ID, ActivityString FROM HIVE_ActivityList WHERE ID > %d LIMIT 1", currentIndex));
            while (rs.next()) {
                output = rs.getString("ActivityString");
                HiveBot.activityStatusIndex = rs.getInt("ID");
            }

            if (output == null) {
                rs = st.executeQuery("SELECT ActivityString FROM HIVE_ActivityList WHERE ID = 1");
                while (rs.next()) {
                    output = rs.getString("ActivityString");
                }

                HiveBot.activityStatusIndex = 1;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;

    }

    /**
     * This method will check the database for any user authorization overrides.  This is used to add permissions to a certain user rather then a role.  Allowing more control of permissions.
     *
     * @param userID The userid of the user to be checked
     * @return True - Authorized | False - Unauthorized
     */
    public Integer checkAuthOverride(Long userID) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT AuthLevel FROM HIVE_AuthUser WHERE UserID = %d LIMIT 1", userID));
            while (rs.next()) {
                output = rs.getInt("AuthLevel");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Add a user to the authentication override table.
     *
     * @param userID    The userID of the user to be added.
     * @param authLevel The authentication level of the user.
     * @param userTag   The user's tag for reference purposes only.
     * @return True - Insert transaction completed | False - Errors Occurred
     */
    public boolean insertAuthUser(Long userID, int authLevel, String userTag) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("INSERT INTO HIVE_AuthUser (UserID, AuthLevel, UserTag) VALUES (%d, %d, '%s')", userID, authLevel, userTag));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public boolean removeAuthUser(Long userID) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("DELETE FROM HIVE_AuthUser WHERE UserID = %d", userID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public boolean updateAuthUser(Long userID, Integer newAuthLevel) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE FROM HIVE_AuthUser SET AuthLevel = %d WHERE UserID = %d", newAuthLevel, userID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public boolean updateAuthUser(Long userID, String userTag) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE FROM HIVE_AuthUser SET UserTag = '%s' WHERE UserID = %d", userTag, userID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public Map<Long, Integer> getModRoles() throws SQLException {
        Map<Long, Integer> resultSet = new HashMap<>();

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ModRoleID,Permissions FROM ModRoleTable");
            while (rs.next()) {
                resultSet.put(rs.getLong("ModRoleID"), rs.getInt("Permissions"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return resultSet;
    }


    /**
     * This method will add a string to the database to be pulled when a user uses the Mini command.
     *
     * @param userID  The userid of the user requesting the insert
     * @param message The message to be added
     * @return True - Successful Transaction | False - Error
     */
    public boolean insertUserMini(Long userID, String message) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.execute("DELETE FROM HIVE_UserMessageTable WHERE UserID = " + userID);
            st.executeQuery(String.format("INSERT INTO HIVE_UserMessageTable (UserID, Message) VALUES (%d, '%s')", userID, message));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Update the user mini message
     *
     * @param userID  The user's ID to be updated
     * @param message The updated message to overwrite the current one.
     * @return True - Successful Transaction | False - Errors
     */
    public boolean updateUserMini(Long userID, String message) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE HIVE_UserMessageTable SET Message = \"%s\" WHERE UserID = %d", message, userID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Update the user mini message color
     *
     * @param userID    The user's ID to be updated
     * @param colorCode The updated color to overwrite the current one.
     * @return True - Successful Transaction | False - Errors
     */
    public boolean updateUserMessageColor(Long userID, String colorCode) throws SQLException {
        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.executeQuery(String.format("UPDATE HIVE_UserMessageTable SET Color = \"%s\" WHERE UserID = %d", colorCode, userID));
            if (st.getUpdateCount() >= 1) {
                output = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public String grabRandomGreeting() throws SQLException {
        String returnString = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT Message FROM HIVE_Greetings ORDER BY RAND() LIMIT 1");
            while (rs.next()) {
                returnString = rs.getString("Message");
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return returnString;
    }

    /**
     * @param userID
     * @return <p>200 = User Added or User Updated</p>
     * <p>201 = Database error </p>
     * <p>401 = User is not outside Here status window.  (Calling too many times)</p>
     * @throws SQLException
     */
    public Integer acceptHereStatus(Long userID, Integer incrementAmount) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT LastHereStatus, Points FROM HIVE_UserTable WHERE UserID = " + userID);

            Integer currentPoints = null;
            Instant previousTimestamp = null;

            if (incrementAmount == null) {
                incrementAmount = Integer.parseInt(Config.get("HERE_INCREMENT_AMOUNT"));
            }

            while (rs.next()) {
                currentPoints = rs.getInt("Points");
                previousTimestamp = rs.getTimestamp("LastHereStatus").toInstant();
                break;
            }

            if (currentPoints != null) {


                if (previousTimestamp.plus(12, ChronoUnit.HOURS).isBefore(Instant.now())) {

                    //if (previousTimestamp.isAfter(Instant.now().minus(12, ChronoUnit.HOURS))) {

                    st.execute(String.format("UPDATE HIVE_UserTable SET LastHereStatus = '%s', Points = %d WHERE UserID = %d", Timestamp.from(Instant.now()), currentPoints + incrementAmount, userID));
                    if (st.getUpdateCount() > 0) {
                        output = 200;
                    } else {
                        output = 201;
                    }

                } else {
                    output = 401;
                }
            } else {
                st.execute(String.format("INSERT INTO HIVE_UserTable (UserID, Points, LastHereStatus) VALUES (%d,%d,'%s')", userID, incrementAmount, Timestamp.from(Instant.now())));
                if (st.getUpdateCount() > 0) {
                    output = 200;
                } else {
                    output = 201;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Gets the amount of points a user has to spend on Animationisements, LED effects, and other stream luxuries.
     *
     * @param userID The DISCORD UserID of the user to be verified
     * @return UserStreamObject
     * @throws SQLException
     */
    public UserStreamObject getStreamPoints(Long userID) throws SQLException {
        UserStreamObject output = null;

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT Points, SpentPoints FROM HIVE_UserTable WHERE UserID = " + userID);

            while (rs.next()) {
                output = new UserStreamObject();
                output.setPoints(rs.getInt("Points"));
                output.setSpentPoints(rs.getInt("SpentPoints"));
                break;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Insert a message action into the database for processing at a later time.
     *
     * @param timestamp  The timestamp that the action will need to take place.  This should be any time in the future.
     * @param channelID  The channel ID that contains the message
     * @param messageID  The ID of the message that will be used for the action.
     * @param actionType <p>The type of action.  </p>
     *                   <p>1 = Unpinning a message</p>
     * @return If successful, return a 1 (Updated row count)
     * @throws SQLException
     */
    public Integer insertMessageAction(final Timestamp timestamp, final Long channelID, final Long messageID, final int actionType) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO MessageTable (ActionDate, MessageID, ChannelID, ActionType) VALUES ('%s',%d,%d,%d)", timestamp, messageID, channelID, actionType));
            output = st.getUpdateCount();

        } catch (SQLException throwables) {
            //throwables.printStackTrace();
            throw new SQLException(throwables);
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * This method is to retrieve the list of messages in the queue to be unpinned
     *
     * @param timestamp The timestamp to compare against.  (Should be current time)
     * @return Returns an arrayList of all expired messageActions for processing.
     * @throws SQLException
     */
    public ArrayList<MessageAction> getExpiredMessageActions(final Timestamp timestamp) throws SQLException {

        ArrayList<MessageAction> list = new ArrayList<>();

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT MessageID, ChannelID, ActionType FROM MessageTable WHERE ActionDate < '%s'", timestamp));

            while (rs.next()) {
                list.add(new MessageAction(rs.getLong("MessageID"), rs.getLong("ChannelID"), rs.getInt("ActionType")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return list;

    }

    public void insertCredential(Integer tokenID, String accessToken, String refreshToken) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO TokenTable (tokenID, access_Token, refresh_Token) VALUES (%d, '%s', '%s')",
                    tokenID, accessToken, refreshToken));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

    }

    public Credential getCredential(Integer tokenID) throws SQLException {
        Connection connection = pool.getConnection();

        Credential credential = null;

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT access_Token, refresh_Token FROM TokenTable WHERE tokenID = %d", tokenID));
            while (rs.next()) {

                String accessToken = rs.getString("access_Token");
                String refreshToken = rs.getString("refresh_Token");

                credential = new Credential(accessToken, refreshToken);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return credential;
    }

    public int registerOBSAnimation(String sceneName, String sourceName) throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO StreamAnimations (Scene, Source) VALUES ('%s','%s')", sceneName, sourceName), Statement.RETURN_GENERATED_KEYS);
            st.execute();

            ResultSet resultSet = st.getGeneratedKeys();
            if (resultSet.next()) {
                output = resultSet.getInt("ID");
            }

        } catch (SQLException throwables) {
            throw new SQLException(throwables);
        } finally {
            connection.close();
        }

        return output;
    }

    /**
     * Updates an animation with new information if it already exists.  This relies on the source name!
     *
     * @param scene
     * @param sourceName
     * @param sourceID
     * @return The amount of rows changed
     * @throws SQLException
     */
    public Integer updateAnimation(final String scene, final String sourceName, final Integer sourceID) throws SQLException {

        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT Source FROM StreamAnimations WHERE SourceID = %d AND Scene = '%s'", sourceID, scene));

            // Does a row already exist?
            if (rs.first()) {
                st.execute(String.format("UPDATE StreamAnimations SET sourceID=%d WHERE Source = '%s'", sourceID, sourceName));
                result = st.getUpdateCount();
            } else {
                // NO IT DOESN'T!  CREATE ONE!
                st.execute(String.format("INSERT INTO StreamAnimations (Scene, Source, SourceID) VALUES ('%s','%s',%d)", scene, sourceName, sourceID));
                result = st.getUpdateCount();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public Integer modifyAnimation(Integer animationID, StreamAnimation animation) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE StreamAnimations SET Scene = '%s', Source = '%s', Cost = %d, Cooldown = %d WHERE ID = %d",
                    animation.getSceneName(), animation.getSourceName(), animation.getCost(), animation.getCooldown(), animation.getId()));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public Map<Integer, StreamAnimation> getAnimations() throws SQLException {
        Map<Integer, StreamAnimation> AnimationMap = new TreeMap<>();

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, Scene, Source, SourceID, Runtime, Cost, Cooldown FROM StreamAnimations WHERE Enabled = 1");

            while (rs.next()) {

                int id = rs.getInt("ID");
                String scene = rs.getString("Scene");
                String source = rs.getString("Source");
                int sourceID = rs.getInt("SourceID");
                int runtime = rs.getInt("Runtime");
                int cost = rs.getInt("Cost");
                int cooldown = rs.getInt("Cooldown");

                AnimationMap.putIfAbsent(id, new StreamAnimation(id, scene, source, sourceID, runtime, cost, cooldown, true));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return AnimationMap;
    }

    public StreamAnimation getAnimation(Integer ID) throws SQLException {
        Connection connection = pool.getConnection();

        StreamAnimation Animation = null;

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT Scene, Source, SourceID, Cost, Cooldown, Runtime, Enabled FROM StreamAnimations WHERE ID = %d", ID));
            while (rs.next()) {

                Animation = new StreamAnimation(ID, rs.getString("Scene"), rs.getString("Source"), rs.getInt("SourceID"), rs.getInt("Runtime"), rs.getInt("Cost"), rs.getInt("Cooldown"), rs.getBoolean("Enabled"));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return Animation;
    }

    public Integer registerStream() throws SQLException {
        Integer output = null;

        Connection connection = pool.getConnection();
        try {

            PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO StreamArchive (Start) VALUES ('%s')", Timestamp.from(Instant.now())), Statement.RETURN_GENERATED_KEYS);
            st.execute();

            ResultSet resultSet = st.getGeneratedKeys();
            if (resultSet.next()) {
                output = resultSet.getInt("ID");
            }

        } catch (SQLException throwables) {
            throw new SQLException(throwables);
        } finally {
            connection.close();
        }

        return output;
    }

    public Integer consumePoints(Long userid, Integer amount) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE HIVE_UserTable SET Points = (Points - %d), SpentPoints = (SpentPoints + %d) WHERE UserID = %d", amount, amount, userid));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public Integer refundPoints(Long userid, Integer amount) throws SQLException {
        Integer result = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE HIVE_UserTable SET Points = (Points + %d), SpentPoints = (SpentPoints - %d) WHERE UserID = %d", amount, amount, userid));
            result = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return result;
    }

    public void recordAnimationLog(int streamID, DispatchRequest request) throws SQLException {

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("INSERT INTO StreamAnimationLog (StreamID, RequestingUserID, RequestedAnimationID) VALUES (%d, %d, %d)", streamID, request.getRequestingUserID(), request.getSelectedAnimation().getId()));

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }
    }

    public Map<Long, String> getPixelTubeList() throws SQLException {

        Connection connection = pool.getConnection();
        Map<Long, String> pixelMap = new HashMap<>();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT UserID, YouTubeLink FROM PixelheadYT"));
            while (rs.next()) {

                pixelMap.put(rs.getLong("UserID"), rs.getString("YouTubeLink"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return pixelMap;
    }

    public Integer putPixelTubeLink(Long userID, String link) throws SQLException {

        Connection connection = pool.getConnection();

        Integer returnValue = null;

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT UserID FROM PixelheadYT WHERE UserID = " + userID);
            Integer foundRows = 0;

            while (rs.next()) {
                foundRows++;
                break;
            }

            if (foundRows > 0) {
                st.execute(String.format("UPDATE PixelheadYT SET YouTubeLink='%s' WHERE UserID = %d", link, userID));
            } else {
                st.execute(String.format("INSERT INTO PixelheadYT (UserID, YouTubeLink) VALUES (%d, '%s')", userID, link));
            }
            returnValue = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    /**
     * Create a container in the database for the poll data
     *
     * @param starterID
     * @param optionCount
     * @return ID of the created Poll for processing
     * @throws SQLException
     */
    public Integer createPoll(final Long starterID, final int optionCount, final int multipleChoice, final int hideResponses) throws SQLException {
        Connection connection = pool.getConnection();

        Integer returnValue = null;

        try {

            PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO PollTracker (StarterID, OptionCount, MultipleChoice, HideResponses) VALUES (%d, %d, %d, %d)", starterID, optionCount, multipleChoice, hideResponses), Statement.RETURN_GENERATED_KEYS);
            st.execute();

            ResultSet resultSet = st.getGeneratedKeys();
            if (resultSet.next()) {
                returnValue = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public Integer putLong(final String tableName, final String valueColumnName, final Long value, final String identifierColumnName, final int identifier) throws SQLException {
        Connection connection = pool.getConnection();

        Integer returnValue = null;

        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE %s SET %s = %d WHERE %s = %d", tableName, valueColumnName, value, identifierColumnName, identifier));
            returnValue = st.getUpdateCount();

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    /**
     * @param userID
     * @param voteOption
     * @param messageID
     * @return
     * @throws SQLException
     */
    public Integer addVote(Long userID, int voteOption, Long messageID) throws SQLException {

        Connection connection = pool.getConnection();

        Integer returnValue = null;

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT id FROM PollTracker WHERE PollMessageID = " + messageID);
            Integer pollID = null;

            while (rs.next()) {
                pollID = rs.getInt("id");
                break;
            }

            if (pollID != null) {
                returnValue = addVote(pollID, userID, voteOption);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    /**
     * @param pollID
     * @param userID
     * @param voteOption
     * @return
     * @throws SQLException
     */
    public Integer addVote(Integer pollID, Long userID, int voteOption) throws SQLException {

        Connection connection = pool.getConnection();

        Integer returnValue = null;

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT MultipleChoice, AllowVoting FROM PollTracker WHERE id = " + pollID);

            Boolean allowMultipleChoice = false;
            Boolean allowVoting = false;

            while (rs.next()) {
                if (rs.getInt("MultipleChoice") == 1) {
                    allowMultipleChoice = true;
                }

                if (rs.getInt("AllowVoting") == 1) {
                    allowVoting = true;
                }
            }

            if (allowVoting) {

                rs = st.executeQuery(String.format("SELECT VoteOption FROM PollAudit WHERE fk_PollID = %d AND VoterID = %d", pollID, userID));
                int rowCnt = 0;
                Set<Integer> valueSet = new HashSet<>();

                while (rs.next()) {
                    rowCnt++;
                    valueSet.add(rs.getInt("VoteOption"));
                }

                if (rowCnt == 0) {

                    st.execute(String.format("INSERT INTO PollAudit (fk_PollID, VoterID, VoteOption) VALUES (%d, %d, %d)", pollID, userID, voteOption));
                    st.execute(String.format("UPDATE PollTracker SET Option%d = Option%d+1 WHERE id = %d", voteOption, voteOption, pollID));

                    // Vote was successful
                    returnValue = 200;

                } else {

                    if (valueSet.contains(voteOption)) {
                        // User already voted that option
                        returnValue = 401;
                    } else {
                        // User voted an option that was not found.  Need to check multiple choice

                        if (allowMultipleChoice) {
                            st.execute(String.format("INSERT INTO PollAudit (fk_PollID, VoterID, VoteOption) VALUES (%d, %d, %d)", pollID, userID, voteOption));
                            st.execute(String.format("UPDATE PollTracker SET Option%d = Option%d+1 WHERE id = %d", voteOption, voteOption, pollID));

                            returnValue = 200;
                        } else {
                            returnValue = 402;
                        }
                    }
                }

            } else {
                // Voting is no longer allowed
                returnValue = 404;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public Poll getPoll(Long messageID) throws SQLException {

        Connection connection = pool.getConnection();

        Poll returnValue = null;

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT id FROM PollTracker WHERE PollMessageID = " + messageID);
            Integer pollID = null;

            while (rs.next()) {
                pollID = rs.getInt("id");
                break;
            }

            if (pollID != null) {
                returnValue = getPoll(pollID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public Poll getPoll(final Integer pollID) throws SQLException {

        Connection connection = pool.getConnection();

        Poll returnValue = null;

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT id, StarterID, PollMessageID, ChannelID, OptionCount, AllowVoting, MultipleChoice, HideResponses, Option1, Option2, Option3, Option4 FROM PollTracker WHERE id = " + pollID);

            while (rs.next()) {

                returnValue = new Poll(rs.getInt("id"),
                        rs.getInt("AllowVoting"),
                        rs.getInt("MultipleChoice"),
                        rs.getInt("HideResponses"),
                        rs.getLong("StarterID"),
                        rs.getLong("PollMessageID"),
                        rs.getLong("ChannelID"),
                        rs.getInt("OptionCount"),
                        rs.getInt("Option1"),
                        rs.getInt("Option2"),
                        rs.getInt("Option3"),
                        rs.getInt("Option4"));
                break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public boolean checkForReference(String trigger) throws SQLException {

        Connection connection = pool.getConnection();
        boolean triggerFound = false;

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT ReferenceTrigger FROM HIVE_Library WHERE ReferenceTrigger = '%s'", trigger));

            while (rs.next()) {
                triggerFound = true;
                break;
            }

            if (!triggerFound) {
                rs = st.executeQuery(String.format("SELECT child_ReferenceTrigger FROM HIVE_Library_Aliases WHERE Aliases = '%s'", trigger));

                while (rs.next()) {
                    triggerFound = true;
                    break;
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }
        return triggerFound;
    }

    public Integer insertReference(final Reference ref) throws SQLException {
        Connection connection = pool.getConnection();

        Integer returnCount = null;

        try {

            Statement st = connection.createStatement();

            st.execute(String.format("INSERT INTO HIVE_Library (ReferenceTrigger, ReferenceBody) VALUES (\"%s\", \"%s\")", ref.getReferenceCommand(), ref.getDescription()));

            if (st.getUpdateCount() > 0) {
                returnCount = 200;
            }

            if (returnCount > 0 && ref.getTitle() != null && !ref.getTitle().isEmpty()) {
                st.execute(String.format("UPDATE HIVE_Library SET ReferenceTitle = \"%s\" WHERE ReferenceTrigger = \"%s\"", ref.getTitle(), ref.getReferenceCommand()));
            }

            if (returnCount > 0 && ref.getAliases() != null && !ref.getAliases().isEmpty()) {
                for (String s : ref.getAliases()) {
                    st.execute(String.format("INSERT INTO HIVE_Library_Aliases (child_ReferenceTrigger, Aliases) VALUES ('%s', \"%s\")", ref.getReferenceCommand(), s));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnCount;
    }

    public Integer modifyReference(final Reference ref) throws SQLException {
        Connection connection = pool.getConnection();

        Integer returnCount = null;

        try {

            Statement st = connection.createStatement();

            st.execute(String.format("UPDATE HIVE_Library SET ReferenceBody = \"%s\" WHERE ReferenceTrigger = \"%s\"", ref.getDescription(), ref.getReferenceCommand()));

            if (st.getUpdateCount() > 0) {
                returnCount = 200;
            }

            if (returnCount > 0 && ref.getTitle() != null && !ref.getTitle().isEmpty()) {
                st.execute(String.format("UPDATE HIVE_Library SET ReferenceTitle = \"%s\" WHERE ReferenceTrigger = \"%s\"", ref.getTitle(), ref.getReferenceCommand()));
            }

            if (returnCount > 0 && ref.getAliases() != null && !ref.getAliases().isEmpty()) {

                ArrayList<String> currentAliases = getReferenceAliases(ref.getReferenceCommand());

                if (currentAliases != null) {
                    for (String s : currentAliases) {
                        if (!ref.getAliases().contains(s)) {
                            st.execute(String.format("DELETE FROM HIVE_Library_Aliases WHERE child_ReferenceTrigger = \"%s\" and Aliases = \"%s\"", ref.getReferenceCommand(), s));
                        }
                    }
                }

                for (String s : ref.getAliases()) {
                    if ((currentAliases != null && !currentAliases.contains(s)) || currentAliases == null) {
                        st.execute(String.format("INSERT INTO HIVE_Library_Aliases (child_ReferenceTrigger, Aliases) VALUES ('%s', \"%s\")", ref.getReferenceCommand(), s));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnCount;
    }


    public void oneTimeInsertReference(final Reference reference) throws SQLException {
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            st.execute(String.format("INSERT INTO HIVE_Library (ReferenceTrigger, ReferenceTitle, ReferenceBody) VALUES (\"%s\",\"%s\",\"%s\")", reference.getReferenceCommand(), reference.getTitle(), reference.getDescription()));

            if (reference.getAliases() != null && !reference.getAliases().isEmpty()) {
                for (String alias : reference.getAliases()) {
                    st.execute(String.format("INSERT INTO HIVE_Library_Aliases (child_ReferenceTrigger,Aliases) VALUES (\"%s\", \"%s\")", reference.getReferenceCommand(), alias));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }
    }

    public ArrayList<String> getReferenceList() throws SQLException {

        Connection connection = pool.getConnection();
        ArrayList<String> returnArray = new ArrayList<>();

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ReferenceTrigger FROM HIVE_Library");

            while (rs.next()) {
                returnArray.add(rs.getString("ReferenceTrigger"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnArray;

    }

    public Reference getReference(final String lookup) throws SQLException {

        Connection connection = pool.getConnection();
        Reference returnReference = null;

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT ReferenceTrigger,ReferenceTitle, ReferenceBody FROM HIVE_Library WHERE ReferenceTrigger = '%s'", lookup));

            boolean foundReference = false;

            while (rs.next()) {
                foundReference = true;
                returnReference = new Reference(rs.getString("ReferenceTrigger"), rs.getString("ReferenceBody"));

                if (rs.getString("ReferenceTitle") != null && !rs.getString("ReferenceTitle").isEmpty()) {
                    returnReference.setTitle(rs.getString("ReferenceTitle"));
                }

                ArrayList<String> aliasList = getReferenceAliases(returnReference.getReferenceCommand());
                if (aliasList != null && aliasList.size() > 0) {
                    returnReference.setAliases(aliasList);
                }
            }

            if (!foundReference) {
                //Reference was not found in main library, check for aliases

                rs = st.executeQuery(String.format("SELECT child_ReferenceTrigger FROM HIVE_Library_Aliases WHERE Aliases = '%s'", lookup));

                while (rs.next()) {

                    rs = st.executeQuery(String.format("SELECT ReferenceTrigger,ReferenceTitle,ReferenceBody FROM HIVE_Library WHERE ReferenceTrigger = '%s'", rs.getString("child_ReferenceTrigger")));

                    while (rs.next()) {
                        foundReference = true;
                        returnReference = new Reference(rs.getString("ReferenceTrigger"), rs.getString("ReferenceBody"));

                        if (rs.getString("ReferenceTitle") != null && !rs.getString("ReferenceTitle").isEmpty()) {
                            returnReference.setTitle(rs.getString("ReferenceTitle"));
                        }

                        ArrayList<String> aliasList = getReferenceAliases(returnReference.getReferenceCommand());
                        if (aliasList != null && aliasList.size() > 0) {
                            returnReference.setAliases(aliasList);
                        }
                    }
                }
            }

            return returnReference;

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnReference;

    }

    public ArrayList<String> getReferenceAliases(String referenceTrigger) throws SQLException {

        ArrayList<String> returnArray = new ArrayList<>();

        Connection connection = pool.getConnection();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT Aliases FROM HIVE_Library_Aliases WHERE child_ReferenceTrigger = \"%s\"", referenceTrigger));

            while (rs.next()) {
                returnArray.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        if (returnArray.size() > 0) {
            return returnArray;
        } else {
            return null;
        }
    }

    public AutoResponse checkForAutoResponse(final String message) throws SQLException {

        Connection connection = pool.getConnection();
        String autoResponseName = null;
        AutoResponse response = null;
        int triggerCount = 0;
        boolean timeLimitSatisfied = false;

        try {
            Statement st = connection.createStatement();

            String[] args = message.split("\\s+");
            for (String s : args) {
                s = s.trim();
                if (autoResponseName == null) {
                    ResultSet rs = st.executeQuery(String.format("SELECT fk_Name FROM AutoResponse_Triggers WHERE Trig = '%s'", s));

                    // Find the AutoResponse related to the first trigger found
                    while (rs.next()) {
                        autoResponseName = rs.getString("fk_Name");
                        triggerCount = 1;

                        // Load the AutoResponse
                        rs = st.executeQuery(String.format("SELECT Name, Title, Response, MinTriggerCount, MinHours, MinMinutes FROM HIVE_AutoResponse WHERE Name = '%s'", autoResponseName));
                        while (rs.next()) {
                            response = new AutoResponse(
                                    rs.getString("Name"),
                                    rs.getString("Response"),
                                    rs.getInt("MinTriggerCount"),
                                    rs.getInt("MinHours"),
                                    rs.getInt("MinMinutes"),
                                    rs.getString("Title")
                            );
                        }

                        Instant lastTrigger = null;

                        rs = st.executeQuery(String.format("SELECT WhitelistChannelID, LastTrigger FROM AutoResponse_WhitelistTable WHERE fk_Name = '%s'", autoResponseName));
                        while (rs.next()) {
                            response.getWatchChannelList().add(rs.getLong("WhitelistChannelID"));
                            if(rs.getTimestamp("LastTrigger") != null) {
                                lastTrigger = rs.getTimestamp("LastTrigger").toInstant();
                            }
                        }

                        if((lastTrigger == null) || (Instant.now().isAfter(lastTrigger.plus(response.getMinHoursBetweenResponse(),ChronoUnit.HOURS).plus(response.getMinMinutesBetweenResponse(),ChronoUnit.MINUTES)))){
                            timeLimitSatisfied = true;
                        }

                        rs = st.executeQuery(String.format("SELECT Trig FROM AutoResponse_Triggers WHERE fk_Name = '%s'", autoResponseName));
                        while (rs.next()) {
                            response.getTriggerWords().add(rs.getString("Trig"));
                        }
                    }
                } else {
                    // AutoResponse is already defined, check arg for trigger words
                    for (String triggerWord : response.getTriggerWords()) {
                        if (triggerCount < response.getMinTriggerCount()) {
                            if (s.trim().toLowerCase().contains(triggerWord.toLowerCase())) {
                                triggerCount++;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        if (response != null && triggerCount >= response.getMinTriggerCount() && timeLimitSatisfied) {
            return response;
        } else {
            return null;
        }
    }

    public Integer autoResponse_setTimestamp(final String autoResponseName, final Long channelID) throws SQLException {

        Connection connection = pool.getConnection();
        Integer updateCount = null;

        try {
            Statement st = connection.createStatement();
            st.executeQuery(String.format("UPDATE AutoResponse_WhitelistTable SET LastTrigger = '%s' WHERE fk_Name = '%s' AND WhitelistChannelID = %d", Timestamp.from(Instant.now()), autoResponseName, channelID));

            updateCount = st.getUpdateCount();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return updateCount;
    }

    public Integer getCashews(final Long userID) throws SQLException {

        Integer returnValue = null;

        Connection connection = pool.getConnection();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT Points FROM HIVE_UserTable WHERE UserID = %d", userID));

            while (rs.next()) {
                returnValue = rs.getInt("Points");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public boolean deductCashews(final Long userID, final int amount) throws SQLException {

        boolean returnValue = false;

        Connection connection = pool.getConnection();
        try {
            Statement st = connection.createStatement();
            st.executeQuery(String.format("UPDATE HIVE_UserTable SET Points = Points - %d WHERE UserID = %d", amount, userID));

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    public int setOptStatus(final Long userID) throws SQLException {

        boolean currentStatus = checkOptStatus(userID);
        int returnValue = 0;


        int finalStatus = 1;
        if (currentStatus) {
            finalStatus = 0;
        }

        Connection connection = pool.getConnection();
        try {
            Statement st = connection.createStatement();
            st.execute(String.format("UPDATE HIVE_CoffeeShop SET OptStatus = %d WHERE UserID = %d", finalStatus, userID));

            if (st.getUpdateCount() == 0) {

                st.execute(String.format("INSERT INTO HIVE_CoffeeShop (UserID, OptStatus) VALUES (%d, %d)", userID, finalStatus));
                returnValue = finalStatus;

            } else {
                returnValue = finalStatus;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }

    /**
     * Check the opt-out status of a user
     *
     * @param userID
     * @return True = OptOut
     * False = Allowed to send messages
     * @throws SQLException
     */
    public boolean checkOptStatus(final Long userID) throws SQLException {

        boolean returnValue = false;
        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT OptStatus FROM HIVE_CoffeeShop WHERE UserID = %d", userID));

            while (rs.next()) {
                if (rs.getInt("OptStatus") == 1) {
                    returnValue = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        } finally {
            connection.close();
        }

        return returnValue;
    }


}
