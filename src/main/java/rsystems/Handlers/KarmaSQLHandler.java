package rsystems.handlers;

import org.mariadb.jdbc.MariaDbPoolDataSource;
import rsystems.objects.KarmaUserInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class KarmaSQLHandler extends SQLHandler {

    public KarmaSQLHandler(MariaDbPoolDataSource pool) {
        super(pool);
    }

    // Get date of last seen using ID
    @Override
    public String getDate(String id) {
        String date = "";

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT DATE FROM KARMA WHERE ID = " + id);
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

    public void addKarmaPoints(String id, String date, boolean staff) {
        try {
            Connection connection = pool.getConnection();
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
            st.executeUpdate("UPDATE KARMA SET DATE = \"" + date + "\" WHERE ID = " + id);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public int getKarma(String id) {

        int value = 0;

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT USER_KARMA FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt("USER_KARMA");
                System.out.println(value);
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return value;
    }

    public String getUserTag(String id) {

        String tag = null;

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT NAME FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                tag = rs.getString("NAME");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return tag;
    }

    public int updateKarma(Long messageID, String sender, String receiver, Boolean direction) {
        System.out.println(String.format("DEBUG:\nSender:%s\nReceiver:%s", sender, receiver));

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            int availableKarma = 0;

            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + sender);

            // ID was found, get available points
            while (rs.next()) {
                availableKarma = rs.getInt("AV_POINTS");
            }


            // User has enough points
            if (availableKarma >= 1) {


                if (direction) {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_POS = KSEND_POS + 1 WHERE ID = " + sender);
                    st.execute("UPDATE KARMA SET USER_KARMA = USER_KARMA + 1 WHERE ID = " + receiver);
                    st.execute(String.format("INSERT INTO KARMA_TrackerTable (MessageID, ReceivingUser, SendingUser, Timestamp) VALUES (%d, %d, %d, current_timestamp)",messageID,Long.valueOf(receiver),Long.valueOf(sender)));

                } else {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_NEG = KSEND_NEG + 1 WHERE ID = " + sender);
                    st.executeUpdate("UPDATE KARMA SET USER_KARMA = USER_KARMA - 1 WHERE ID = " + receiver);
                }


                return 4;
            } else {
                // User does not have enough points
                return 2;
            }
        } catch (SQLException throwables) {
            System.out.println("Karma Update Handler Exception");
            throwables.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Could not find user");
        }
        // Return default
        return 0;
    }

    public boolean overrideKarma(String id, int value) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public int checkKarmaRanking(String id) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            int indexesFound = 0;
            ResultSet rs = st.executeQuery(String.format("SELECT COUNT(ID) FROM karmaTracker WHERE (ID = %s AND DATE > (current_date() - '7 days'))", id));
            while (rs.next()) {
                indexesFound = rs.getInt(1);
            }

            return indexesFound;

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return 0;
    }

    public boolean masterOverrideKarma(String value) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE USER_KARMA <> " + value);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean masterOverridePoints(String value) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET AV_POINTS = " + value + " WHERE AV_POINTS <> " + value);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean overrideKarmaPoints(String id, int value) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET AV_POINTS = " + value + " WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public int getAvailableKarmaPoints(String id) {

        int value = 0;

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt("AV_POINTS");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return value;
    }

    public boolean deleteUser(String id) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM KARMA WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public Map<String, Integer> getTopTen() {
        Map<String, Integer> topRank = new LinkedHashMap<>();
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT NAME, USER_KARMA FROM KARMA ORDER BY USER_KARMA DESC LIMIT 10");
            while (rs.next()) {
                topRank.put(rs.getString("NAME"), rs.getInt("USER_KARMA"));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return topRank;
    }

    public KarmaUserInfo userInfo(String id) {
        KarmaUserInfo userInfoObject = new KarmaUserInfo();
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID, NAME, USER_KARMA, AV_POINTS, KSEND_POS, KSEND_NEG FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                userInfoObject.setId(rs.getLong("ID"));
                userInfoObject.setName(rs.getString("NAME"));
                userInfoObject.setKarma(rs.getInt("USER_KARMA"));
                userInfoObject.setAvailable_points(rs.getInt("AV_POINTS"));
                userInfoObject.setKsent_pos(rs.getInt("KSEND_POS"));
                userInfoObject.setKsent_neg(rs.getInt("KSEND_NEG"));

            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        if (userInfoObject.getId() != null) {
            return userInfoObject;
        }
        return null;
    }

    public int getInt(String column, String id) {

        int value = 0;

        try {

            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                value = rs.getInt(column);
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return value;
    }

    public int getRank(String id){
        int output = 0;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT USER_KARMA FROM KARMA WHERE ID = " + id);

            int currentKarma = 0;
            while (rs.next()) {
                currentKarma = rs.getInt("USER_KARMA");
            }

            rs = st.executeQuery("SELECT COUNT(USER_KARMA) FROM KARMA WHERE USER_KARMA > " + currentKarma);
            while(rs.next()){
                output = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return output;

    }

    public boolean setInt(String id, String column, int value) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET " + column + " = " + value + " WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean setType(String id, int type) {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            int currentType = 0;
            ResultSet rs = st.executeQuery("SELECT KTYPE FROM KARMA WHERE ID = " + id);
            while (rs.next()) {
                currentType = rs.getInt("KTYPE");
            }

            if (currentType < 4) {
                st.executeUpdate("UPDATE KARMA SET KTYPE = " + type + " WHERE ID = " + id);
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public void clearTracking() {
        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM karmaTracker WHERE DATE < (SELECT DATETIME('now', '-7 day'))");
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public ArrayList<String> getActive() {
        ArrayList<String> members = new ArrayList<>();

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID FROM KARMA WHERE KTYPE >= 1");
            while (rs.next()) {
                members.add(String.valueOf(rs.getLong("ID")));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return members;
    }

    public Map<String, Integer> getActiveUsers() {
        Map<String, Integer> activeUsers = new HashMap<>();

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT ID FROM karmaTracker WHERE (DATE > (current_date - '-7 days'))");
            while (rs.next()) {
                Statement nestedSt = connection.createStatement();
                ResultSet nestedRs = nestedSt.executeQuery("SELECT ID, COUNT(ID) FROM karmaTracker WHERE (ID = " + rs.getLong("ID") + " AND DATE > (SELECT DATETIME('now', '-7 day')))");
                while (nestedRs.next()) {
                    activeUsers.put(String.valueOf(rs.getLong("ID")), nestedRs.getInt(2));
                }
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return activeUsers;
    }

    public boolean insertStaging(Long channelID, Long messageID, Long memberID) {
        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            st.execute(String.format("INSERT INTO KARMA_StagingTable (ChannelID,MessageID,OwnerID) VALUES (%d,%d,%d)", channelID, messageID, memberID));

            if (st.getUpdateCount() >= 1)
                output = true;

            connection.close();

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public boolean checkStaging(Long messageID, Long memberID) {

        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(String.format("SELECT ChannelID, OwnerID FROM KARMA_StagingTable WHERE MessageID = %d AND OwnerID = %d", messageID, memberID));
            while (rs.next()) {
                output = true;
            }

            connection.close();
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public boolean deleteFromStaging(Long messageID) {

        boolean output = false;

        try {
            Connection connection = pool.getConnection();
            Statement st = connection.createStatement();

            st.execute("DELETE FROM KARMA_StagingTable WHERE MessageID = " + messageID);
            if (st.getUpdateCount() >= 1)
                output = true;


            connection.close();
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }
}
