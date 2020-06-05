package rsystems.handlers;

import rsystems.adapters.KarmaUserInfo;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class KarmaSQLHandler extends SQLHandler {

    public KarmaSQLHandler(String databaseURL) {
        super(databaseURL);
        connect();
    }

    // Get date of last seen using ID
    @Override
    public String getDate(String id) {
        String date = "";

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

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

    public void addKarmaPoints(String id, String date) {
        try {
            Statement st = connection.createStatement();

            st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS + 1 WHERE ID = " + id);
            st.executeUpdate("UPDATE KARMA SET DATE = \"" + date + "\" WHERE ID = " + id);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public int getKarma(String id) {

        int value = 0;

        try {

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

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

    public boolean updateKarma(String sender, String receiver, Boolean direction) {
        //direction (True = Positive karma | False = Negative karma)
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();

            int availableKarma = 0;

            ResultSet rs = st.executeQuery("SELECT AV_POINTS FROM KARMA WHERE ID = " + sender);
            while (rs.next()) {
                availableKarma = rs.getInt("AV_POINTS");
            }

            if (availableKarma >= 1) {

                if (direction) {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_POS = KSEND_POS + 1 WHERE ID = " + sender);
                    st.executeUpdate("UPDATE KARMA SET USER_KARMA = USER_KARMA + 1 WHERE ID = " + receiver);
                } else {
                    st.executeUpdate("UPDATE KARMA SET AV_POINTS = AV_POINTS - 1, KSEND_NEG = KSEND_NEG + 1 WHERE ID = " + sender);
                    st.executeUpdate("UPDATE KARMA SET USER_KARMA = USER_KARMA - 1 WHERE ID = " + receiver);
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean overrideKarma(String id, int value) {
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean masterOverrideKarma(String value){
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE KARMA SET USER_KARMA = " + value + " WHERE USER_KARMA <> " + value);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public boolean masterOverridePoints(String value){
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

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
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

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

            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

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

    public boolean deleteUser(String id){
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM KARMA WHERE ID = " + id);
            return true;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
        return false;
    }

    public Map<String, Integer> getTopTen(){
        Map<String, Integer> topRank = new LinkedHashMap<>();
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT NAME, USER_KARMA FROM KARMA ORDER BY USER_KARMA DESC LIMIT 10");
            while(rs.next()){
                topRank.put(rs.getString("NAME"),rs.getInt("USER_KARMA"));
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return topRank;
    }

    public KarmaUserInfo userInfo(String id){
        KarmaUserInfo userInfoObject = new KarmaUserInfo();
        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID, NAME, USER_KARMA, AV_POINTS, KSEND_POS, KSEND_NEG FROM KARMA WHERE ID = " + id);
            while(rs.next()){
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
        if(userInfoObject.getId() > 0) {
            return userInfoObject;
        }
        return null;
    }
}
