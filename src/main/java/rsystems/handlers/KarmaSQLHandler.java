package rsystems.handlers;

import net.dv8tion.jda.api.entities.User;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import rsystems.objects.KarmaUserInfo;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KarmaSQLHandler extends SQLHandler {

    public KarmaSQLHandler(MariaDbPoolDataSource pool) {
        super(pool);
    }

    // Get date of last seen using ID
    public Timestamp getTimestamp(String id) throws SQLException {
        Timestamp timestamp = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT LastPointIncrement FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                timestamp = rs.getTimestamp("LastPointIncrement");
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return timestamp;
    }

    public void addKarmaPoints(Long id, Timestamp datetime, boolean staff) throws SQLException {

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            /*
            ADD POINT TO USER FOR BEING ONLINE IF THEY AREN'T MAXED OUT
             */

            int availablePoints = 0;
            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + id);
            // ID was found, get available points
            while (rs.next()) {
                availablePoints = rs.getInt("AV_POINTS");
            }

            if (availablePoints < 10) {
                if (staff) {
                    System.out.println("Staff Found");
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = 5 WHERE ID = " + id);
                } else {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS + 1 WHERE ID = " + id);
                }
            }

            // SET DATE TO FINISH QUERY

            //todo fix this
            st.executeUpdate(String.format("UPDATE KARMA SET LastPointIncrement = '%s' WHERE ID = %d",datetime,id));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public Integer getKarma(String id) throws SQLException {

        Integer value = null;
        Connection connection = pool.getConnection();

        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT USER_KARMA FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt("USER_KARMA");
                //System.out.println(value);
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return value;
    }

    public String getUserTag(String id) throws SQLException {

        String tag = null;
        Connection connection = pool.getConnection();

        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT NAME FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                tag = rs.getString("NAME");
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return tag;
    }

    public KarmaUserInfo getKarmaUserInfo(Long userID) throws SQLException {

        KarmaUserInfo karmaUserInfo = null;

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT LastPointIncrement, AV_POINTS, USER_KARMA, KSEND_POS, KSEND_NEG FROM KARMA WHERE ID = " + userID);

            while (rs.next()) {
                karmaUserInfo = new KarmaUserInfo();
                karmaUserInfo.setKarma(rs.getInt("USER_KARMA"));
                karmaUserInfo.setAvailable_points(rs.getInt("AV_POINTS"));
                karmaUserInfo.setKsent_pos(rs.getInt("KSEND_POS"));
                karmaUserInfo.setKsent_neg(rs.getInt("KSEND_NEG"));
                karmaUserInfo.setLastKarmaPoint(rs.getTimestamp("LastPointIncrement").toInstant());
                break;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return karmaUserInfo;
    }

    public int updateKarma(final Long messageID, final User sender, final User receiver, final boolean direction) throws SQLException {
        System.out.printf("DEBUG:\nSender:%s\nReceiver:%s%n", sender, receiver);

        int output = 0;
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            int availableKarma = 0;

            if (getKarma(receiver.getId()) == null) {
                insertUser(receiver.getId(), receiver.getAsTag());
            }

            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + sender.getId());

            // ID was found, get available points
            while (rs.next()) {
                availableKarma = rs.getInt("AV_POINTS");
            }

            // User has enough points
            if (availableKarma >= 1) {


                if (direction) {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_POS = KSEND_POS + 1 WHERE ID = " + sender.getId());
                    st.execute("UPDATE KARMA SET USER_KARMA = USER_KARMA + 1, WeeklyKarma = WeeklyKarma + 1 WHERE ID = " + receiver.getId());
                    st.execute(String.format("INSERT INTO KARMA_TrackerTable (MessageID, ReceivingUser, SendingUser, Timestamp) VALUES (%d, %d, %d, current_timestamp)", messageID, receiver.getIdLong(), sender.getIdLong()));

                } else {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_NEG = KSEND_NEG + 1 WHERE ID = " + sender.getId());
                    st.executeUpdate("UPDATE KARMA SET USER_KARMA = USER_KARMA - 1 WHERE ID = " + receiver.getId());
                }

                output = 4;
            } else {
                // User does not have enough points
                output = 2;
            }


            connection.close();
        } catch (SQLException throwables) {
            System.out.println("Karma Update Handler Exception");
            throwables.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Could not find user");
        }
        // Return default
        return output;
    }

    public boolean overrideKarma(String id, int value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE ID = " + id);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public int checkKarmaRanking(String id) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            int indexesFound = 0;
            ResultSet rs = st.executeQuery(String.format("SELECT COUNT(ID) FROM karmaTracker WHERE (ID = %s AND DATE > (current_date() - '7 days'))", id));
            while (rs.next()) {
                indexesFound = rs.getInt(1);
            }
            connection.close();
            return indexesFound;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return 0;
    }

    public boolean masterOverrideKarma(String value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE USER_KARMA <> " + value);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public boolean masterOverridePoints(String value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET AV_POINTS = " + value + " WHERE AV_POINTS <> " + value);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public boolean overrideKarmaPoints(String id, int value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET AV_POINTS = " + value + " WHERE ID = " + id);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public int getAvailableKarmaPoints(String id) throws SQLException {

        int value = 0;

        Connection connection = pool.getConnection();
        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt("AV_POINTS");
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return value;
    }

    public boolean insertUser(String id, String name) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            //Initiate the formatter for formatting the date into a set format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            //Get the current date
            LocalDate currentDate = LocalDate.now();
            //Format the current date into a set format
            String formattedCurrentDate = formatter.format(currentDate);

            st.executeUpdate(String.format("INSERT INTO KARMA (ID,NAME,DATE) VALUES (\"%s\", \"%s\",\"%s\")", id, name, formattedCurrentDate));
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String id) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM KARMA WHERE ID = " + id);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public LinkedHashMap<Long, Integer> getTopTen() throws SQLException {
        LinkedHashMap<Long, Integer> topRank = new LinkedHashMap<>();
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID, USER_KARMA FROM KARMA ORDER BY USER_KARMA DESC LIMIT 10");
            while (rs.next()) {
                topRank.put(rs.getLong("ID"), rs.getInt("USER_KARMA"));
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return topRank;
    }

    public int getInt(String column, String id) throws SQLException {

        int value = 0;
        Connection connection = pool.getConnection();

        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt(column);
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return value;
    }

    public int getRank(String id) throws SQLException {
        int output = 0;
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT USER_KARMA FROM KARMA WHERE ID = " + id);

            int currentKarma = 0;
            while (rs.next()) {
                currentKarma = rs.getInt("USER_KARMA");
            }

            rs = st.executeQuery("SELECT COUNT(USER_KARMA) FROM KARMA WHERE USER_KARMA > " + currentKarma);
            while (rs.next()) {
                output = rs.getInt(1);
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;

    }

    public boolean setInt(String id, String column, int value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET " + column + " = " + value + " WHERE ID = " + id);

            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }


        return false;
    }

    public boolean setType(String id, int type) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            int currentType = 0;
            ResultSet rs = st.executeQuery("SELECT KTYPE FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                currentType = rs.getInt("KTYPE");
            }

            connection.close();

            if (currentType < 4) {
                st.executeUpdate("UPDATE KARMA SET KTYPE = " + type + " WHERE ID = " + id);
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public void clearTracking() throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM karmaTracker WHERE DATE < (SELECT TIMESTAMP('now', '-7 day'))");

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public ArrayList<String> getActive() throws SQLException {
        ArrayList<String> members = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID FROM KARMA WHERE KTYPE >= 1");
            while (rs.next()) {
                members.add(String.valueOf(rs.getLong("ID")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return members;
    }

    public List<String> getAllKarmaSymbols() throws SQLException {
        List<String> output = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT Symbol FROM KARMA_SymbolTable");
            while (rs.next()) {
                output.add(rs.getString("Symbol"));
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    public String getKarmaSymbol(String userID) throws SQLException {
        Integer karmaAmount = getKarma(userID);
        String output = null;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT Symbol FROM KARMA_SymbolTable WHERE %d >= Minimum ORDER BY Minimum DESC", karmaAmount));
            while (rs.next()) {
                output = rs.getString("Symbol");
                break;
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return output;
    }

    public Map<String, Integer> getActiveUsers() throws SQLException {
        Map<String, Integer> activeUsers = new HashMap<>();
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT ID FROM karmaTracker WHERE (DATE > (current_date - '-7 days'))");
            while (rs.next()) {
                Statement nestedSt = connection.createStatement();
                ResultSet nestedRs = nestedSt.executeQuery("SELECT ID, COUNT(ID) FROM karmaTracker WHERE (ID = " + rs.getLong("ID") + " AND DATE > (SELECT TIMESTAMP('now', '-7 day')))");
                while (nestedRs.next()) {
                    activeUsers.put(String.valueOf(rs.getLong("ID")), nestedRs.getInt(2));
                }
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return activeUsers;
    }

    public boolean insertStaging(Long channelID, Long messageID, Long memberID) throws SQLException {
        boolean output = false;
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            st.execute(String.format("INSERT INTO KARMA_StagingTable (ChannelID,MessageID,OwnerID) VALUES (%d,%d,%d)", channelID, messageID, memberID));

            if (st.getUpdateCount() >= 1)
                output = true;

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public boolean checkStaging(Long messageID, Long memberID) throws SQLException {

        boolean output = false;

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT ChannelID, OwnerID FROM KARMA_StagingTable WHERE MessageID = %d AND OwnerID = %d", messageID, memberID));
            while (rs.next()) {
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

    public boolean deleteFromStaging(Long messageID) throws SQLException {

        boolean output = false;
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            st.execute("DELETE FROM KARMA_StagingTable WHERE MessageID = " + messageID);
            if (st.getUpdateCount() >= 1)
                output = true;


            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return output;
    }

    public LinkedHashMap<Integer, Long> getTopThree() throws SQLException {

        Connection connection = pool.getConnection();
        LinkedHashMap<Integer, Long> karmaMap = new LinkedHashMap<>();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID, WeeklyKarma FROM KARMA ORDER BY WeeklyKarma DESC LIMIT 3");

            int index = 0;
            while (rs.next()) {

                    karmaMap.put(rs.getInt("WeeklyKarma"),rs.getLong("ID"));

                    index++;

            }

            st.execute("UPDATE KARMA SET WeeklyKarma = 0");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return karmaMap;
    }
}
