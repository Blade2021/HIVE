package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Reference;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;

public class ReferenceTrigger extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Look for reference triggers
        referenceCommand(event.getMessage());


        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Update references
        if (args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(31).getCommand()))) {
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(31).getRank()) {
                    LOGGER.info(HiveBot.commands.get(31).getCommand() + " called by " + event.getAuthor().getAsTag());
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
        referenceCommand(event.getMessage());

        //ReferenceList command
        if (HiveBot.commands.get(37).checkCommand(event.getMessage().getContentRaw())) {
            referenceListCommand(event.getMessage());
        }
    }


    private ArrayList<String> categories(ArrayList<Reference> refList) {
        ArrayList<String> cats = new ArrayList<>();

        for (Reference r : refList) {
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
        for (Reference r : HiveBot.references) {
            switch (index) {
                case 0:
                    output1.append(r.getRefCode()).append("\n");
                    break;
                case 1:
                    output2.append(r.getRefCode()).append("\n");
                    break;
                case 2:
                    output3.append(r.getRefCode()).append("\n");
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
            info.setDescription("https://github.com/Blade2021/HIVE-RefData");
            info.setColor(Color.CYAN);
            info.addField("", output1.toString(), true);
            info.addField("", output2.toString(), true);
            info.addField("", output3.toString(), true);
            info.setFooter("Called by " + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());
            message.getChannel().sendMessage(info.build()).queue();
            info.clear();
        } catch (PermissionException e) {
            message.getChannel().sendMessage("Missing permission exception: " + e.getPermission()).queue();
        }
    }

    private void referenceCommand(Message message) {

        boolean show = false;
        String[] args = message.getContentRaw().split("\\s+");

        //Parse through all references
        for (Reference r : HiveBot.references) {

            ArrayList<String> refCheck = new ArrayList<>();
            refCheck.add(r.getRefCode());

            try {
                if (r.getAlias().size() > 0) {
                    refCheck.addAll(r.getAlias());
                }
            } catch (NullPointerException e) {
            }


            // Look for reference in the datafile
            for (String s : refCheck) {
                if ((args[0].equalsIgnoreCase(HiveBot.prefix + s))) {
                    // A reference was found
                    LOGGER.info("REF " + s + " : " + r.getRefCode() + " called by " + message.getAuthor().getAsTag());

                    for (String checkForShow : args) {
                        if (checkForShow.equalsIgnoreCase("-show")) {
                            if (RoleCheck.getRank(message.getGuild(), message.getAuthor().getId()) >= 1) {
                                show = true;
                            }
                        }
                    }

                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("install"))) {
                        sendReference(message,r.getInstallString(),show);
                        return;
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
                            return;
                        }
                    }

                    if ((args.length >= 2) && (args[1].equalsIgnoreCase("alias"))) {
                        StringBuilder output = new StringBuilder();
                        try {
                            if (r.getAlias().size() > 0) {
                                output.append(r.getRefCode()).append(",");
                                r.getAlias().forEach(alias -> {
                                    output.append(alias).append(", ");
                                });

                                sendReference(message,output.toString(),show);
                                return;
                            } else {
                                sendReference(message,"There are no aliases assigned to: " + r.getRefCode(),show);
                                return;
                            }
                        } catch (NullPointerException e) {
                        }
                    }

                    EmbedBuilder info = new EmbedBuilder();

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

                    info.setTitle(r.getRefCode());
                    info.setColor(Color.CYAN);
                    info.setDescription(r.getDescription());
                    info.addField("Links", links.toString(), false);
                    info.addField("Installation", r.getInstallString(), false);
                    info.addField("Category", cats.toString(), false);

                    if(!info.isEmpty()) {
                        //!IMPORTANT - Send the MessageEmbed and not the EmbedBuilder
                        sendReference(message, info.build(), show);
                    }
                    info.clear();
                }
            }
        }
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
