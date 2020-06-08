package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.ExtendedReference;
import rsystems.adapters.Reference;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.ArrayList;

import static rsystems.HiveBot.LOGGER;

public class ReferenceTrigger extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Look for reference triggers
        if(!checkExtendedReferences(event.getMessage())) {
            checkReferences(event.getMessage());
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Update references
        if (args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(31).getCommand()))) {
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(31))){
                    HiveBot.referenceLoader.updateData();
                    event.getMessage().addReaction("✅").queue();
                }
            } catch (NullPointerException e) {
            }
        }

        //ReferenceList command
        if (HiveBot.commands.get(37).checkCommand(event.getMessage().getContentRaw())) {
            referenceListCommand(event.getMessage());
        }

    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        //Check for references
        if(!checkExtendedReferences(event.getMessage())) {
            checkReferences(event.getMessage());
        }

        //ReferenceList command
        if (HiveBot.commands.get(37).checkCommand(event.getMessage().getContentRaw())) {
            referenceListCommand(event.getMessage());
        }
    }


    private ArrayList<String> categories(ArrayList<ExtendedReference> refList) {
        ArrayList<String> cats = new ArrayList<>();

        for (ExtendedReference r : refList) {
            ArrayList<String> tempCat = new ArrayList<>();
            tempCat.addAll(r.getCategory());
            for (String s : tempCat) {
                if (!(cats.contains(s))) {
                    cats.add(s);
                }
            }
        }
        return cats;
    }

    private void referenceListCommand(Message message) {
        LOGGER.info(HiveBot.commands.get(37).getCommand() + " called by " + message.getAuthor().getAsTag());

        //Initalize stringbuilder objects to hold the list items
        StringBuilder output1 = new StringBuilder();
        StringBuilder output2 = new StringBuilder();
        StringBuilder output3 = new StringBuilder();

        //Track index to add to each list evenly
        int index = 0;

        //Iterate through each Reference and grab the main ref code
        for (ExtendedReference r : HiveBot.extendedReferences) {
            switch (index) {
                case 0:
                    output1.append(r.getReferenceCommand()).append("\n");
                    break;
                case 1:
                    output2.append(r.getReferenceCommand()).append("\n");
                    break;
                case 2:
                    output3.append(r.getReferenceCommand()).append("\n");
                    break;
            }
            index++;
            if (index > 2) {
                index = 0;
            }
        }

        index = 0;
        StringBuilder output4 = new StringBuilder();
        StringBuilder output5 = new StringBuilder();
        StringBuilder output6 = new StringBuilder();

        //Iterate through each Reference and grab the main ref code
        for (Reference r : HiveBot.references) {
            switch (index) {
                case 0:
                    output4.append(r.getReferenceCommand()).append("\n");
                    break;
                case 1:
                    output5.append(r.getReferenceCommand()).append("\n");
                    break;
                case 2:
                    output6.append(r.getReferenceCommand()).append("\n");
                    break;
            }
            index++;
            if (index > 2) {
                index = 0;
            }
        }

        try {
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("HIVE Reference List");
            info.setDescription("[Public Repo](https://github.com/Blade2021/HIVE-RefData)\n\n**How do I use these?**\n" +
                    "Just type ~ then the reference you'd like to grab. \n" +
                    "HIVE will send you a DM (Direct Message) with the information if its an extended reference.\n" +
                    "```diff\nExample: ~mqtt\n```");
            info.setColor(Color.CYAN);
            info.addField("References",output4.toString(),true);
            info.addField("",output5.toString(),true);
            info.addField("",output6.toString(),true);
            info.addField("Extended References", output1.toString(), true);
            info.addField("", output2.toString(), true);
            info.addField("", output3.toString(), true);
            info.setFooter("Called by " + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());
            message.getChannel().sendMessage(info.build()).queue();
            info.clear();
        } catch (PermissionException e) {
            message.getChannel().sendMessage("Missing permission exception: " + e.getPermission()).queue();
        }
    }

    private boolean checkExtendedReferences(Message message) {

        boolean show = false;
        String[] args = message.getContentRaw().split("\\s+");

        //Parse through all extended references
        for (ExtendedReference r : HiveBot.extendedReferences) {

            ArrayList<String> refCheck = new ArrayList<>();
            refCheck.add(r.getReferenceCommand());

            try {
                if (r.getAliases().size() > 0) {
                    refCheck.addAll(r.getAliases());
                }
            } catch (NullPointerException e) {
            }


            // Look for reference in the datafile
            for (String s : refCheck) {
                if((message.getContentRaw().toLowerCase().startsWith(HiveBot.prefix + s.toLowerCase())) || (message.getContentRaw().toLowerCase().startsWith(HiveBot.altPrefix + s.toLowerCase()))){
                //if ((args[0].equalsIgnoreCase(HiveBot.prefix + s)) || (args[0].equalsIgnoreCase(HiveBot.altPrefix + s))) {
                    // A reference was found
                    LOGGER.info("REF " + s + " : " + r.getReferenceCommand() + " called by " + message.getAuthor().getAsTag());

                    for (String checkForShow : args) {
                        if (checkForShow.equalsIgnoreCase("-show")) {
                            if (RoleCheck.getRank(message.getGuild(), message.getAuthor().getId()) >= 1) {
                                show = true;
                            }
                        }
                    }

                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("install"))) {
                        sendReference(message,r.getInstallString(),show);
                        return true;
                    }
                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("links"))) {
                        StringBuilder output = new StringBuilder();
                        r.getLinks().forEach(link -> {
                            if (!(link.isEmpty())) {
                                output.append("<").append(link).append(">").append("\n");
                            }
                        });
                        if (output.length() >= 1) {

                            sendReference(message,output.toString(),show);
                            return true;
                        }
                    }

                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("alias"))) {
                        StringBuilder output = new StringBuilder();
                        try {
                            if (r.getAliases().size() > 0) {
                                output.append(r.getReferenceCommand()).append(",");
                                r.getAliases().forEach(alias -> {
                                    output.append(alias).append(", ");
                                });

                                sendReference(message,output.toString(),show);
                                return true;
                            } else {
                                sendReference(message,"There are no aliases assigned to: " + r.getReferenceCommand(),show);
                                return true;
                            }
                        } catch (NullPointerException e) {
                        }
                    }

                    EmbedBuilder info = new EmbedBuilder();

                    StringBuilder aliasList = new StringBuilder();
                    try {
                        r.getAliases().forEach(alias -> {
                            if (!(alias.isEmpty())) {
                                aliasList.append(alias).append(", ");
                            }
                        });
                    } catch(NullPointerException e){
                        aliasList.append("No aliases found");
                    }

                    StringBuilder links = new StringBuilder();
                    r.getLinks().forEach(link -> {
                        if (!(link.isEmpty())) {
                            links.append("<").append(link).append(">").append("\n");
                        }
                    });

                    StringBuilder cats = new StringBuilder();
                    if (r.getCategory().size() > 1) {
                        r.getCategory().forEach(cat -> {
                            if (!(cat.isEmpty())) {
                                cats.append(cat).append(", ");
                            }
                        });
                    } else {
                        cats.append(r.getCategory().get(0));
                    }

                    info.setTitle(r.getReferenceCommand());
                    info.setColor(Color.CYAN);
                    info.setDescription(r.getDescription());
                    info.addField("Links", links.toString(), false);
                    info.addField("Installation", r.getInstallString(), false);
                    info.addField("Category", cats.toString(), false);
                    info.addField("Aliases", aliasList.toString(),false);

                    if(!info.isEmpty()) {
                        //!IMPORTANT - Send the MessageEmbed and not the EmbedBuilder
                        sendReference(message, info.build(), show);
                    }
                    info.clear();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkReferences(Message message) {
        String[] args = message.getContentRaw().split("\\s+");

        //Parse through all extended references
        for (Reference r : HiveBot.references) {

            ArrayList<String> refCheck = new ArrayList<>();
            refCheck.add(r.getReferenceCommand());

            try {
                if (r.getAliases().size() > 0) {
                    refCheck.addAll(r.getAliases());
                }
            } catch (NullPointerException e) {
            }


            // Look for reference in the datafile
            for (String s : refCheck) {
                if((message.getContentRaw().toLowerCase().startsWith(HiveBot.prefix + s.toLowerCase())) || (message.getContentRaw().toLowerCase().startsWith(HiveBot.altPrefix + s.toLowerCase()))){
                //if ((args[0].equalsIgnoreCase(HiveBot.prefix + s)) || (args[0].equalsIgnoreCase(HiveBot.altPrefix + s))) {
                    try{
                        message.delete().queue();
                    } catch (PermissionException e){

                    }


                    // A reference was found
                    LOGGER.info("REF " + s + " : " + r.getReferenceCommand() + " called by " + message.getAuthor().getAsTag());

                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("alias"))) {
                        StringBuilder output = new StringBuilder();
                        try {
                            if (r.getAliases().size() > 0) {
                                output.append(r.getReferenceCommand()).append(",");
                                r.getAliases().forEach(alias -> {
                                    output.append(alias).append(", ");
                                });

                                sendReference(message,output.toString(),true);
                                return true;
                            } else {
                                sendReference(message,"There are no aliases assigned to: " + r.getReferenceCommand(),true);
                                return true;
                            }
                        } catch (NullPointerException e) {
                        }
                    }

                    EmbedBuilder info = new EmbedBuilder();

                    info.setColor(Color.CYAN);
                    info.setDescription(r.getDescription());
                    info.setFooter(r.getReferenceCommand() + " called by " + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());

                    if(!info.isEmpty()) {
                        //!IMPORTANT - Send the MessageEmbed and not the EmbedBuilder
                        sendReference(message, info.build(), true);
                    }
                    info.clear();
                    return true;
                }
            }
        }
        return false;
    }

    private void sendReference(Message message, MessageEmbed embedMessage, boolean show){
        if (show) {
            try {
                message.getChannel().sendMessage(embedMessage).queue();
            } catch (PermissionException e) {
                message.getChannel().sendMessage("Missing Permissions: " + e.getPermission()).queue();
            } catch (IllegalStateException e){
                System.out.println("Empty embed detected");
            }
        } else {
            try {
                message.getAuthor().openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage(embedMessage).queue(
                            success -> {
                                message.addReaction("✅").queue();
                            },
                            failure -> {
                                message.addReaction("⚠").queue();
                                //LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + message.getAuthor().getAsTag());
                                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                            });

                });
            } catch (NullPointerException e) {
            }catch (IllegalStateException e){
                System.out.println("Empty embed detected");
            }
        }
    }

    private void sendReference(Message message,String embedMessage, boolean show){
        if (show) {
            try {
                message.getChannel().sendMessage(embedMessage).queue();
            } catch (PermissionException e) {
                message.getChannel().sendMessage("Missing Permissions: " + e.getPermission()).queue();
            }catch (IllegalStateException e){
                System.out.println("Empty embed detected");
            }
        } else {
            try {
                message.getAuthor().openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage(embedMessage).queue(
                            success -> {
                                message.addReaction("✅").queue();
                            },
                            failure -> {
                                message.addReaction("⚠").queue();
                                //LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + message.getAuthor().getAsTag());
                                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                            });

                });
            } catch (NullPointerException e) {
            }catch (IllegalStateException e){
                System.out.println("Empty embed detected");
            }
        }
    }
}
