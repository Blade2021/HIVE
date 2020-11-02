package rsystems.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import rsystems.HiveBot;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

import static rsystems.HiveBot.*;


public class SuggestionHandler extends SQLHandler {

    private String suggestionsPullChannel;
    private String suggestionsReviewChannel;
    private String suggestionsPostChannel;
    private String suggestionsArchiveChannel;


    public SuggestionHandler(String DatabaseURL, String DatabaseUser, String DatabaseUserPass, String pullChannel, String reviewChannel, String postChannel) {
        super(DatabaseURL,DatabaseUser,DatabaseUserPass);

        suggestionsPullChannel = pullChannel;
        suggestionsReviewChannel = reviewChannel;
        suggestionsPostChannel = postChannel;

        connect();
    }

    public int createSuggestion(String authorID, String suggestion){
        int output = 0;

        try {
            // Connect to the DB if connection was lost
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }
            Statement st = connection.createStatement();

            st.execute("INSERT INTO suggestionPool (requesterID, suggestion, submissionDate) VALUES (" + authorID + ", (\"" + suggestion + "\"), CURRENT_DATE)");

            output = getSuggestionID(authorID);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public int getSuggestionID(String authorID){
        int output = 0;

        try {
            // Connect to the DB if connection was lost
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT ID FROM suggestionPool WHERE requesterID = " + authorID);
            while (rs.next()) {
                output = rs.getInt("ID");
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public void updateRowInt(int ID, String column, int value){
        try {
            // Connect to the DB if connection was lost
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }
            Statement st = connection.createStatement();

            st.executeUpdate("UPDATE suggestionPool SET " + column + " = " + value + " WHERE ID = " + ID);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public void updateRowString(int ID, String column, String value){
        try {
            // Connect to the DB if connection was lost
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }
            Statement st = connection.createStatement();

            st.executeUpdate("UPDATE suggestionPool SET " + column + " = \"" + value + "\" WHERE ID = " + ID);

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }

    public int getFieldInt(int ID,String column){
            int output = 0;

            try {
                // Connect to the DB if connection was lost
                if ((connection == null) || (connection.isClosed())) {
                    connect();
                }
                Statement st = connection.createStatement();

                ResultSet rs = st.executeQuery("SELECT " + column + " FROM suggestionPool WHERE ID = " + ID);
                while(rs.next()){
                    output = rs.getInt(column);
                }

            } catch (SQLException throwables) {
                System.out.println(throwables.getMessage());
            }

            return output;

    }

    public String getFieldString(int ID,String column){
        String output = null;

        try {
            // Connect to the DB if connection was lost
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("SELECT " + column + " FROM suggestionPool WHERE ID = " + ID);
            while(rs.next()){
                output = rs.getString(column);
            }

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public boolean checkSuggestionPool(String messageID){
        int rowsFound = 0;

        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT pollMessageID FROM suggestionPool WHERE pollMessageID = " + messageID);

            while (rs.next()) {
                rowsFound++;
            }

        }catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return rowsFound > 0;
    }

    public Map<Integer,String> openSuggestionMap(int searchValue){
        Map<Integer,String> output = new HashMap<>();

        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID, pollMessageID FROM suggestionPool WHERE status == " + searchValue);
            while (rs.next()) {
                output.put(rs.getInt("ID"),rs.getString("pollMessageID"));
            }

        }catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    public ArrayList<Integer> openSuggestions(){
        ArrayList<Integer> output = new ArrayList<>();

        try {
            if ((connection == null) || (connection.isClosed())) {
                connect();
            }

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ID FROM suggestionPool WHERE status == 0");
            while (rs.next()) {
                output.add(rs.getInt("ID"));
            }

        }catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }

        return output;
    }

    /*
    POST SUGGESTION TO REVIEW CHANNEL FOR APPROVAL BEFORE POSTING PUBLICLY
     */
    public void postSuggestionReview(int suggestionID, Guild guild){
        //Send request notification to appropriate channels
        TextChannel suggestionReviewChannel = guild.getTextChannelById(suggestionsReviewChannel);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            embedBuilder.setTitle("Suggestion needs approval: " + guild.getMemberById(suggestionHandler.getFieldString(suggestionID, "requesterID")).getEffectiveName());
        } catch (NullPointerException e) {
            System.out.println((suggestionHandler.getFieldString(suggestionID, "requesterID")));
        }
        embedBuilder.setDescription(suggestionHandler.getFieldString(suggestionID, "suggestion"));
        embedBuilder.appendDescription("\n\nTo approve:");
        embedBuilder.appendDescription("```\n" + HiveBot.prefix + "suggestionapprove " + suggestionID + "\n```");
        embedBuilder.appendDescription("\nTo reject:");
        embedBuilder.appendDescription("```\n" + HiveBot.prefix + "suggestionreject " + suggestionID + "\n```");
        embedBuilder.appendDescription("\nOr click one of of the reactions below.");

        suggestionReviewChannel.sendMessage(embedBuilder.build()).queue(success -> {
            suggestionHandler.updateRowString(suggestionID, "pollMessageID", success.getId());
            success.addReaction("✅").queue();
            success.addReaction("❌").queue();
        });
        embedBuilder.clear();
    }

    /*
    POST SUGGESTION
    This method will post the suggestion to the output channel after being reviewed.
     */
    public void postSuggestion(int suggestionID, Guild guild){
        TextChannel suggestionOutputChannel = guild.getTextChannelById(suggestionsPostChannel);

        EmbedBuilder suggestionEmbedBuilder = new EmbedBuilder();

        // Set title
        try {
            suggestionEmbedBuilder.setTitle("Suggestion by: " + guild.getMemberById(getFieldString(suggestionID, "requesterID")).getEffectiveName());
        } catch (NullPointerException e) {
            suggestionEmbedBuilder.setTitle("Suggestion by: anonymous");
        }

        //Set description (Suggestion body)
        suggestionEmbedBuilder.setDescription(getFieldString(suggestionID, "suggestion"));

        //Set Status field
        suggestionEmbedBuilder.addField("Suggestion Status","Under Review",false);

        //Set Color
        suggestionEmbedBuilder.setColor(Color.YELLOW);

        //Set thumbnail
        try {
            suggestionEmbedBuilder.setThumbnail(guild.getMemberById(getFieldString(suggestionID, "requesterID")).getUser().getEffectiveAvatarUrl());
        } catch (NullPointerException e) {
            suggestionEmbedBuilder.setThumbnail(guild.getIconUrl());
        }

        //Set Footer
        suggestionEmbedBuilder.setFooter("Suggestion ID: " + suggestionID);

        // Attempt to post the embed to the channel
        try {
            suggestionOutputChannel.sendMessage(suggestionEmbedBuilder.build()).queue(success -> {
                updateRowString(suggestionID, "pollMessageID", success.getId());
                updateRowInt(suggestionID, "status", 1);

                //Add voting emotes to message
                //success.addReaction("\uD83D\uDD3C").queue();
                success.addReaction("✅").queue();

                //success.addReaction("\uD83D\uDD3D").queue();
                success.addReaction("❌").queue();

                //success.pin().queue();
            });

        } catch(NullPointerException e){
            LOGGER.severe("Could not post suggestion embed to channel");
        }
        suggestionEmbedBuilder.clear();
    }


    /*
    HANDLE SUGGESTION
    This method will approve/reject the message.
     */
    public void handleSuggestion(int suggestionID, Guild guild, Boolean acceptance, String statusMessage){
        TextChannel suggestionOutputChannel = guild.getTextChannelById(suggestionsPostChannel);

        //Get the pollMessageID from the DB
        getFieldString(suggestionID,"pollMessageID");
        try {
            //Grab the message
            Message message = grabSuggestionMessage(suggestionOutputChannel, getFieldString(suggestionID, "pollMessageID"));

            //Create a new temporary embed to hold the data
            EmbedBuilder tempEmbed = new EmbedBuilder();

            //Move previous entries into the new temporary embed
            List<MessageEmbed> embeds = message.getEmbeds();
            for(MessageEmbed me:embeds){
                tempEmbed.setTitle(me.getTitle());
                tempEmbed.setThumbnail(me.getThumbnail().getUrl());
                tempEmbed.setDescription(me.getDescription());
                tempEmbed.setFooter(me.getFooter().getText());
            }
            if(acceptance) {
                //Set color
                tempEmbed.setColor(Color.green);
            } else {
                //Set color
                tempEmbed.setColor(Color.red);
            }

            tempEmbed.addField("Suggestion Status",statusMessage,false);

            //Replace current embed with temporary one
            message.editMessage(tempEmbed.build()).override(true).queue();
            //message.unpin().queue();
            tempEmbed.clear();

        } catch (NullPointerException e){
            System.out.println("Could not find message");
        }
    }

    public void setStatusMessage(int suggestionID, Guild guild, String statusMessage){
        TextChannel suggestionOutputChannel = guild.getTextChannelById(suggestionsPostChannel);

        //Get the pollMessageID from the DB
        getFieldString(suggestionID,"pollMessageID");
        try {
            //Grab the message
            Message message = grabSuggestionMessage(suggestionOutputChannel, getFieldString(suggestionID, "pollMessageID"));

            //Create a new temporary embed to hold the data
            EmbedBuilder tempEmbed = new EmbedBuilder();

            //Move previous entries into the new temporary embed
            List<MessageEmbed> embeds = message.getEmbeds();
            for(MessageEmbed me:embeds){
                tempEmbed.setTitle(me.getTitle());
                tempEmbed.setThumbnail(me.getThumbnail().getUrl());
                tempEmbed.setColor(me.getColor());
                tempEmbed.setDescription(me.getDescription());

                if(!me.getFooter().getText().isEmpty()){
                    tempEmbed.setFooter(me.getFooter().getText());
                }
            }

            //Set the status field to input
            tempEmbed.addField("Suggestion Status",statusMessage,false);

            //Replace current embed with temporary one
            message.editMessage(tempEmbed.build()).override(true).queue();
            tempEmbed.clear();

            updateRowString(suggestionID,"statusMessage",statusMessage);

        } catch (NullPointerException e){
            System.out.println("Could not find message");
        }
    }

    public void setColor(int suggestionID, Guild guild, Color color){
        TextChannel suggestionOutputChannel = guild.getTextChannelById(suggestionsPostChannel);

        //Get the pollMessageID from the DB
        getFieldString(suggestionID,"pollMessageID");
        try {
            //Grab the message
            Message message = grabSuggestionMessage(suggestionOutputChannel, getFieldString(suggestionID, "pollMessageID"));

            //Create a new temporary embed to hold the data
            EmbedBuilder tempEmbed = new EmbedBuilder();

            //Move previous entries into the new temporary embed
            List<MessageEmbed> embeds = message.getEmbeds();
            for(MessageEmbed me:embeds){
                tempEmbed.setTitle(me.getTitle());
                tempEmbed.setThumbnail(me.getThumbnail().getUrl());
                tempEmbed.setColor(color);
                tempEmbed.setDescription(me.getDescription());
                me.getFields().forEach(field -> {
                    tempEmbed.addField(field.getName(),field.getValue(),false);
                });

            }

            //Replace current embed with temporary one
            message.editMessage(tempEmbed.build()).override(true).queue();
            tempEmbed.clear();

        } catch (NullPointerException e){
            System.out.println("Could not find message");
        }
    }

    public Message grabSuggestionMessage(TextChannel channel, String messageID) {
        try {
            List<Message> messages = channel.getHistory().retrievePast(100).complete();

            //Remove the approval request
            for (Message tempMessage : messages) {
                if (tempMessage.getId().equalsIgnoreCase(messageID)) {
                    return tempMessage;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("couldn't find message");
        }
        return null;
    }
}
