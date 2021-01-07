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
            //HiveBot.extendedReferences.add(tempExtendedReference);
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

            //Add Reference Object into the references array
            //HiveBot.references.add(tempReference);
            referenceMap.putIfAbsent(keyStr.toString(), tempReference);

        });


        System.out.println(String.format("Loaded %d references\nLoaded %d extended references",referenceMap.size(),extendedReferenceMap.size()));
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }

        String content = event.getMessage().getContentRaw().replaceFirst(HiveBot.prefix,"");

        if(!event.getMessage().getMentionedMembers().isEmpty()){
            for(Member m:event.getMessage().getMentionedMembers()){
                content = content.replaceAll(m.getAsMention().toLowerCase(),"");
            }
        }

        content = content.trim();

        System.out.println(content);

        // CHECK REFERENCE MAP
        for(Map.Entry<String,Reference> entry:referenceMap.entrySet()){

            if(content.equalsIgnoreCase(entry.getKey())){
                replyToGuild(event,entry.getValue());
                return;
            }

            for(String alias:entry.getValue().getAliases()){
                if(content.equalsIgnoreCase(alias)){
                    replyToGuild(event,entry.getValue());
                    return;
                }
            }
        }

        // CHECK EXTENDED REFERENCE MAP
        for(Map.Entry<String,ExtendedReference> entry:extendedReferenceMap.entrySet()){
            if(content.equalsIgnoreCase(entry.getKey())){
                replyToGuild(event,entry.getValue());
                return;
            }

            if(entry.getValue().getAliases() != null) {
                for (String alias : entry.getValue().getAliases()) {
                    if (content.equalsIgnoreCase(alias)) {
                        replyToGuild(event, entry.getValue());
                        return;
                    }
                }
            }
        }

    }

    private void replyToGuild(GuildMessageReceivedEvent event, Reference reference){
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
