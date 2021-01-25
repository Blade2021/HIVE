package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.Map;
import java.util.TreeMap;

public class ActivityString extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 4096;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        if(args.length >= 1){

            if(args[0].equalsIgnoreCase("list")){

                EmbedBuilder embedBuilder = new EmbedBuilder();

                StringBuilder activityIDString = new StringBuilder();
                StringBuilder activityString = new StringBuilder();

                TreeMap<Integer, String> activityMap = HiveBot.sqlHandler.getActivityMap();
                for(Map.Entry entry:activityMap.entrySet()){
                    activityIDString.append(entry.getKey()).append("\n");
                    activityString.append(entry.getValue()).append("\n");
                }

                embedBuilder.setTitle("Activity Message List")
                        .addField("ID",activityIDString.toString(),true)
                        .addField("String",activityString.toString(),true);

                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.setEmbed(embedBuilder.build());

                channelReply(event,messageBuilder.build());

                embedBuilder.clear();
                messageBuilder.clear();
            }

            if(args[0].equalsIgnoreCase("add")){

                if(args.length >= 2){

                    //String activityMessage = content.substring(content.indexOf("add"+1));
                    String activityMessage = content.substring(args[0].length()+1);
                    if(activityMessage.length() > 32){
                        reply(event,"Your message is too long.  Shorten it and try again\nLimit: 32 characters");
                        return;
                    }

                    if(!activityMessage.isEmpty()){
                        Integer result = HiveBot.sqlHandler.insertActivity(activityMessage);

                        if(result >= 1){
                            reply(event,String.format("Activity Message put into queue.  ID: %d",result));
                            event.getMessage().addReaction("✅ ").queue();
                        }
                    }

                }

            }

            if((args[0].equalsIgnoreCase("delete")) || (args[0].equalsIgnoreCase("remove"))){

                if(args.length >= 2){

                    Integer id = Integer.parseInt(args[1]);
                    if(id != null){
                        if(HiveBot.sqlHandler.deleteValue("HIVE_ActivityList","ID",id) != null){
                            event.getMessage().addReaction("✅ ").queue();
                        }
                    }

                }

            }

        }
    }

    @Override
    public String getHelp() {

        String returnString = ("{prefix}{command} [Sub-Command] [args]\n\n" +
                "The activity message pool provides a constant rotation of messages that will show up as HIVE's activity.  These messages can only be 20 characters long.\n\n" +
                "**Add**\n`{prefix}{command} add [Message]`\nThis will add the message to the pool.\n\n" +
                "**Remove**\n`{prefix}{command} remove [ID]`\nThis will remove the corresponding message from the pool.\n\n" +
                "**List**\n`{prefix}{command} list`\nThis will list all the messages in the activity message pool.\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    @Override
    public String getName(){
        return "activity";
    }
}
