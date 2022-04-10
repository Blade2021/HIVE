package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Activity extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();
        subCommands.add(new SubcommandData("add","Add an activity to the list").addOption(OptionType.STRING,"activity","The string to be displayed",true));
        subCommands.add(new SubcommandData("remove","Remove an activity from the list").addOption(OptionType.STRING,"id","The ID of the activity string to remove",true));
        subCommands.add(new SubcommandData("modify","Modify an activity on the list").addOption(OptionType.STRING,"id","The ID of the activity to modify",true).addOption(OptionType.STRING,"activity","The new string for the activity"));
        subCommands.add(new SubcommandData("list","List all activities that will be cycled through"));

        return commandData.addSubcommands(subCommands);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(isEphemeral()).queue();

        if(event.getSubcommandName().equalsIgnoreCase("add")){
            String activity = event.getOption("activity").getAsString();

            if(activity != null && !activity.isEmpty()){

                if(activity.length() <= 20) {

                    try {
                        Integer id = HiveBot.database.insertActivity(activity);
                        reply(event, String.format("`%s` has been inserted with ID: %d", activity, id), isEphemeral());

                    } catch (SQLException e) {
                        //e.printStackTrace();
                        reply(event, "An error occurred when attempting to add the activity", isEphemeral());

                    }
                } else {
                    reply(event,"That message is too long");
                }

            } else {
                reply(event,"I cannot add an empty activity",isEphemeral());
            }
        } else if(event.getSubcommandName().equalsIgnoreCase("remove")){

            Integer id = Integer.parseInt(event.getOption("id").getAsString());
            Integer result = null;

            try {
                TreeMap<Integer, String> treeMap = HiveBot.database.getActivityMap();

                if(treeMap.get(id) != null) {
                    result = HiveBot.database.deleteRow("HIVE_ActivityList","ID",id);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(result != null){
                reply(event,String.format("The activity with ID:`%d` has been deleted",id));
            } else {
                reply(event,String.format("An error occurred when removing the activity with ID: `%d`",id));
            }
        } else if(event.getSubcommandName().equalsIgnoreCase("modify")){

            Integer id = Integer.parseInt(event.getOption("id").getAsString());
            String activity = event.getOption("activity").getAsString();

            try {
                TreeMap<Integer, String> treeMap = HiveBot.database.getActivityMap();

                if(treeMap.get(id) != null) {
                    // ID does exist in the activity list

                    if(activity != null && !activity.isEmpty()){

                        if(HiveBot.database.putValue("HIVE_ActivityList","ActivityString",activity,"ID",id) >= 1){
                            reply(event,String.format("Activity ID: %d has been updated",id));
                        }
                    } else {
                        reply(event,"I cannot add an empty activity",isEphemeral());
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                reply(event,"An error occurred");
            }
        } else if(event.getSubcommandName().equalsIgnoreCase("list")){

            try {
                TreeMap<Integer, String> treeMap = HiveBot.database.getActivityMap();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("HIVE Activity List");
                builder.setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());

                StringBuilder idString = new StringBuilder();
                StringBuilder activityString = new StringBuilder();

                for(Map.Entry<Integer,String> entry:treeMap.entrySet()){
                    idString.append(entry.getKey()).append("\n");
                    activityString.append(entry.getValue()).append("\n");
                }

                builder.addField("ID",idString.toString(),true);
                builder.addField("Activity",activityString.toString(),true);

                reply(event,builder.build());

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public String getDescription() {
        return "Add, Modify, or Remove strings that will be shown on the Activity list";
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
