package rsystems.handlers;

import net.dv8tion.jda.api.entities.User;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsystems.objects.KarmaUserInfo;

import java.sql.*;
import java.util.LinkedHashMap;

public class KarmaSQLHandler extends SQLHandler {

    final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public KarmaSQLHandler(MariaDbPoolDataSource pool) {
        super(pool);
    }

    // Get date of last seen using UserID
    public Timestamp getTimestamp(Long userID) throws SQLException {
        Timestamp timestamp = null;

        Connection connection = pool.getConnection();

        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT LastPointIncrement FROM KarmaUserTable WHERE UserID = " + userID);
            while (rs.next()) {
                timestamp = rs.getTimestamp("LastPointIncrement");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return timestamp;
    }

    public void addKarmaPoints(Long UserID, Timestamp datetime, boolean staff) throws SQLException {

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            /*
            ADD POINT TO USER FOR BEING ONLINE IF THEY AREN'T MAXED OUT
             */

            int availablePoints = 0;
            ResultSet rs = st.executeQuery("SELECT AvailablePoints FROM KarmaUserTable WHERE UserID = " + UserID);
            // UserID was found, get available points
            while (rs.next()) {
                availablePoints = rs.getInt("AvailablePoints");
            }

            if (availablePoints < 10) {
                if (staff) {
                    System.out.println("Staff Found");
                    st.executeUpdate("UPDATE KarmaUserTable SET AvailablePoints = 5 WHERE UserID = " + UserID);
                } else {
                    st.executeUpdate("UPDATE KarmaUserTable SET AvailablePoints = AvailablePoints + 1 WHERE UserID = " + UserID);
                }
            }

            // SET DATE TO FINISH QUERY

            //todo fix this
            st.executeUpdate(String.format("UPDATE KarmaUserTable SET LastPointIncrement = '%s' WHERE UserID = %d", datetime, UserID));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public Integer getKarma(Long userID) throws SQLException {

        Integer value = null;
        Connection connection = pool.getConnection();

        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(String.format("SELECT COUNT(fk_UserID) AS COUNT FROM Karma_Trackers WHERE (fk_UserID = %d AND LogTS > (current_date() - '90 days'))",userID));
            while (rs.next()) {
                value = rs.getInt("COUNT");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return value;
    }

    public KarmaUserInfo getKarmaUserInfo(Long userID) throws SQLException {

        KarmaUserInfo karmaUserInfo = null;

        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT UserID, LastPointIncrement, AvailablePoints, KPosSent, KNegSent FROM KarmaUserTable WHERE UserID = " + userID);

            while (rs.next()) {
                karmaUserInfo = new KarmaUserInfo(rs.getLong("UserID"));
                karmaUserInfo.setAvailable_points(rs.getInt("AvailablePoints"));
                karmaUserInfo.setKsent_pos(rs.getInt("KPosSent"));
                karmaUserInfo.setLastKarmaPoint(rs.getTimestamp("LastPointIncrement").toInstant());
                break;
            }

            if (karmaUserInfo != null) {
                rs = st.executeQuery(String.format("SELECT COUNT(fk_UserID) AS COUNT FROM Karma_Trackers WHERE fk_UserID = %d AND LogTS >= CURDATE() - INTERVAL 1 MONTH and CURDATE()",userID));

                while(rs.next()){
                    karmaUserInfo.setKarma(rs.getInt("COUNT"));
                }


            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            ExceptionHandler.notifyException(throwables,this.getClass().getName());
        } finally {
            connection.close();
        }

        return karmaUserInfo;
    }

    /**
     * Send postive karma to a user
     *
     * @param messageID   The message that got the karma transaction
     * @param sender      The user sending the karma
     * @param receiver    The reciever of the karma
     * @param fromPackage If the karma transaction was a result of the package emoji
     * @return <p>200 = Process Completed OK<br>
     * 400 = Sender did not have enough points</p>
     * @throws SQLException
     */
    public Integer sendKarma(final Long messageID, final User sender, final User receiver, final boolean fromPackage) throws SQLException {

        this.logger.info(String.format("Sending Karma: %d -> %d | FromPkg: %b",sender.getIdLong(),receiver.getIdLong(),fromPackage));

        Integer returnValue = null;
        int fromPackageInteger = 0;

        if (fromPackage) {
            fromPackageInteger = 1;
        }

        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            int availablePoints = 0;

            if (getKarma(receiver.getIdLong()) == null) {
                createKarmaUser(receiver.getIdLong());
            }

            ResultSet rs = st.executeQuery("SELECT AvailablePoints FROM KarmaUserTable WHERE UserID = " + sender.getId());

            // UserID was found, get available points
            while (rs.next()) {
                availablePoints = rs.getInt("AvailablePoints");
            }

            // User has enough points
            if (availablePoints >= 1) {

                st.execute(String.format("INSERT INTO Karma_Trackers (fk_UserID, SendingUserID, MessageID, FromPackage) VALUES (%d, %d, %d, %d)", receiver.getIdLong(), sender.getIdLong(), messageID, fromPackageInteger));
                st.executeUpdate("UPDATE KarmaUserTable SET AvailablePoints = AvailablePoints - 1 WHERE UserID = " + sender.getId());

                returnValue = 200;
            } else {
                // User does not have enough points
                returnValue = 400;
            }


            connection.close();
        } catch (SQLException throwables) {
            System.out.println("Karma Update Handler Exception");
            throwables.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Could not find user");
        }
        // Return default
        return returnValue;
    }

    public int checkKarmaRanking(String UserID) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();

            int indexesFound = 0;
            ResultSet rs = st.executeQuery(String.format("SELECT COUNT(UserID) FROM karmaTracker WHERE (UserID = %s AND DATE > (current_date() - '7 days'))", UserID));
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

    public boolean overrideKarmaPoints(String UserID, int value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KarmaUserTable SET AvailablePoints = " + value + " WHERE UserID = " + UserID);
            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return false;
    }

    public Integer getAvailableKarmaPoints(Long userID) throws SQLException {

        Integer returnValue = null;

        Connection connection = pool.getConnection();
        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT AvailablePoints FROM KarmaUserTable WHERE UserID = " + userID);
            while (rs.next()) {
                returnValue = rs.getInt("AvailablePoints");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }
        return returnValue;
    }

    public Integer createKarmaUser(Long userID) throws SQLException {
        Connection connection = pool.getConnection();
        Integer returnValue = null;

        this.logger.info("CREATING KARMA USER: " + userID);

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(String.format("INSERT INTO KarmaUserTable (UserID) VALUE (%d)", userID));
            returnValue = st.getUpdateCount();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            connection.close();
        }
        return returnValue;
    }

    public boolean deleteUser(String UserID) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM KarmaUserTable WHERE UserID = " + UserID);
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
            ResultSet rs = st.executeQuery("SELECT distinct fk_UserID, count(fk_UserID) as karmaCount from Karma_Trackers left join honey.KarmaUserTable KUT on Karma_Trackers.fk_UserID = KUT.UserID group by fk_UserID order by karmaCount desc limit 10");
            while (rs.next()) {
                topRank.put(rs.getLong("fk_UserID"), rs.getInt("karmaCount"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }

        return topRank;
    }

    public int getInt(String column, String UserID) throws SQLException {

        int value = 0;
        Connection connection = pool.getConnection();

        try {


            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM KarmaUserTable WHERE UserID = " + UserID);
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

    public boolean setInt(String UserID, String column, int value) throws SQLException {
        Connection connection = pool.getConnection();
        try {

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KarmaUserTable SET " + column + " = " + value + " WHERE UserID = " + UserID);

            connection.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            connection.close();
        }


        return false;
    }

    public boolean insertStaging(Long channelID, Long messageID, Long memberID) throws SQLException {
        boolean output = false;
        Connection connection = pool.getConnection();

        try {

            Statement st = connection.createStatement();
            st.execute(String.format("INSERT INTO Karma_PackageStaging (MessageID,ChannelID,OwnerID) VALUES (%d,%d,%d)", messageID, channelID, memberID));

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
            ResultSet rs = st.executeQuery(String.format("SELECT ChannelID, OwnerID FROM Karma_PackageStaging WHERE MessageID = %d AND OwnerID = %d", messageID, memberID));
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

            st.execute("DELETE FROM Karma_PackageStaging WHERE MessageID = " + messageID);
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
}
