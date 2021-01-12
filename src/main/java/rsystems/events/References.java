package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.handlers.JSONFileHandler;
import rsystems.objects.ExtendedReference;
import rsystems.objects.Reference;


import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class References extends ListenerAdapter {

    public static Map<String, Reference> referenceMap = new HashMap<>();
    public static Map<String, ExtendedReference> extendedReferenceMap = new HashMap<>();


    public static void loadReferences(){
		
		// Clear out old references
        referenceMap.clear();
        extendedReferenceMap.clear();

        JSONFileHandler extendedReferenceFile = new JSONFileHandler("extendedReferenceData.json");
        Object extendedReferenceCommands = extendedReferenceFile.getDatafileData();
        JSONObject extendedReferenceData = (JSONObject) extendedReferenceCommands;

        // Load extended references
        extendedReferenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = extendedReferenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            ExtendedReference tempExtendedReference = new ExtendedReference(
                    keyStr.toString(),
                    parsedValue.get("description").toString().replace("{prefix}",HiveBot.prefix),
                    parsedValue.get("installation").toString()
            );

            try{
                tempExtendedReference.setAliases(getArrayList(parsedValue,"alias"));
            } catch(NullPointerException e){
            }

            tempExtendedReference.setLinks(getArrayList(parsedValue,"links"));
            tempExtendedReference.setCategory(getArrayList(parsedValue,"category"));

            //Add Reference Object into the references array
            extendedReferenceMap.putIfAbsent(keyStr.toString(),tempExtendedReference);

        });


        JSONFileHandler referenceFile = new JSONFileHandler("referenceData.json");
        Object referenceCommands = referenceFile.getDatafileData();
        JSONObject referenceData = (JSONObject) referenceCommands;

        // Load Simple references
        referenceData.keySet().forEach(keyStr -> {
			
            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            Reference tempReference = new Reference(
                    keyStr.toString(),
                    parsedValue.get("description").toString()
            );

            try{
                tempReference.setAliases(getArrayList(parsedValue,"alias"));
				
            } catch(NullPointerException e){
				
            }
            tempReference.setCategory(getArrayList(parsedValue,"category"));

            if(parsedValue.get("title") != null){
                tempReference.setTitle(parsedValue.get("title").toString());
            }

            //Add Reference Object into the references array
            referenceMap.putIfAbsent(keyStr.toString(), tempReference);

        });


        System.out.println(String.format("Loaded %d references\nLoaded %d extended references",referenceMap.size(),extendedReferenceMap.size()));
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }

		//Replace the first occurance of the bot's prefix with null
        String content = event.getMessage().getContentDisplay().replaceFirst(HiveBot.prefix,"");

		//Remove all mentions for checking the message for reference
        if(!event.getMessage().getMentionedMembers().isEmpty()){
            for(Member m:event.getMessage().getMentionedMembers()){
                content = content.replaceAll("@" + m.getEffectiveName(),"");
            }
        }

		//Trim the whitespace on the end
        content = content.trim();

        // CHECK REFERENCE MAP
        for(Map.Entry<String,Reference> entry:referenceMap.entrySet()){
			
			//Initiate a variable to determine wether the reference was found on this index.
			boolean referenceFound = false;

			//Message equals root Reference Name
            if(content.equalsIgnoreCase(entry.getKey())){
                referenceFound = true;
            } else {
				//Check if the reference has aliases
				if(entry.getValue().getAliases() != null){
					
					//Parse through each alias to see if message equals alias
					for(String alias:entry.getValue().getAliases()){
						if(content.equalsIgnoreCase(alias)){
							referenceFound = true;
							break;
						}
					}
				}
			}
			
			//Reference was found!
			if(referenceFound){
				//Reply to the original request with the data.
				replyToGuild(event,entry.getValue());
				//Log the command was used to the database.
				HiveBot.sqlHandler.logCommandUsage(entry.getKey());
			}
        }

        // CHECK EXTENDED REFERENCE MAP
        for(Map.Entry<String,ExtendedReference> entry:extendedReferenceMap.entrySet()){
			
			boolean referenceFound = false;
			
            if(content.equalsIgnoreCase(entry.getKey())){
                referenceFound = true;
            } else {
				if(entry.getValue().getAliases() != null) {
					for (String alias : entry.getValue().getAliases()) {
						if (content.equalsIgnoreCase(alias)) {
							referenceFound = true;
							break;
						}
					}
				}
			}
			
			if(referenceFound){
				replyToGuild(event,entry.getValue());
				HiveBot.sqlHandler.logCommandUsage(entry.getKey());
				return;
			}
        }

    }

    private void replyToGuild(GuildMessageReceivedEvent event, Reference reference){
        MessageBuilder messageBuilder = new MessageBuilder();
        for(Member m:event.getMessage().getMentionedMembers()){
            messageBuilder.append(m.getAsMention() + " ");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN)
                .setFooter(String.format("%s called by %s",reference.getReferenceCommand(),event.getMember().getEffectiveName()))
                .setDescription(reference.getDescription());

        if(reference.getTitle() != null){
            embedBuilder.setTitle(reference.getTitle());
        }

        messageBuilder.setEmbed(embedBuilder.build());
        event.getMessage().reply(messageBuilder.build()).queue();
        embedBuilder.clear();
    }

    private void replyExtendedReference(GuildMessageReceivedEvent event, ExtendedReference reference){
        MessageBuilder messageBuilder = new MessageBuilder();
        for(Member m:event.getMessage().getMentionedMembers()){
            messageBuilder.append(m.getAsMention());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN)
                .setFooter(String.format("%s called by %s",reference.getReferenceCommand(),event.getMember().getEffectiveName()))
                .setDescription(reference.getDescription());

        messageBuilder.setEmbed(embedBuilder.build());
        event.getMessage().reply(messageBuilder.build()).queue();
        embedBuilder.clear();
    }

    private static ArrayList<String> getArrayList(JSONObject parsedValue, String key){
        //Get links from datafile
        JSONArray jsonArray = (JSONArray) parsedValue.get(key);
        ArrayList<String> arrayList = new ArrayList<>();

        for(Object linkObject:jsonArray){
            arrayList.add(linkObject.toString());
        }
        return arrayList;
    }
}
