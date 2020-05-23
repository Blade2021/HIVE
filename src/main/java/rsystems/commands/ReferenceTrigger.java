package rsystems.commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Reference;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.ArrayList;

import static rsystems.HiveBot.LOGGER;

public class ReferenceTrigger extends ListenerAdapter{

        public void onGuildMessageReceived(GuildMessageReceivedEvent event){

            String[] args = event.getMessage().getContentRaw().split("\\s+");

            //Parse through all references
            for(Reference r:HiveBot.references){

                ArrayList<String> refCheck = new ArrayList<>();
                refCheck.add(r.getRefCode());

                try {
                    if (r.getAlias().size() > 0) {
                        refCheck.addAll(r.getAlias());
                    }
                }catch(NullPointerException e){
                }


                // Look for reference in the datafile
                for(String s:refCheck) {
                    if ((args[0].equalsIgnoreCase(HiveBot.prefix + s))) {
                        // A reference was found
                        LOGGER.info("REF:" + r.getRefCode() + " called by " + event.getAuthor().getAsTag());

                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("install")) {
                                event.getChannel().sendMessage(r.getInstallString()).queue();
                            }
                            if (args[1].equalsIgnoreCase("links")) {
                                StringBuilder output = new StringBuilder();
                                r.getLinks().forEach(link -> {
                                    if (!(link.isEmpty())) {
                                        output.append("<").append(link).append(">").append("\n");
                                    }
                                });
                                if (output.length() >= 1) {
                                    event.getChannel().sendMessage(output).queue();
                                }
                            }

                            if (args[1].equalsIgnoreCase("alias")){
                                StringBuilder output = new StringBuilder();
                                try {
                                    if (r.getAlias().size() > 0){
                                        output.append(r.getRefCode()).append(",");
                                        r.getAlias().forEach(alias -> {
                                            output.append(alias).append(",");
                                        });

                                        event.getChannel().sendMessage(output).queue();

                                    }
                                } catch(NullPointerException e){}
                            }

                        } else {
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

                            event.getChannel().sendMessage(info.build()).queue();
                            info.clear();
                        }
                    }
                }
            }

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

            if(args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(37).getCommand())){
                // Check user access
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(37).getRank()) {
                    LOGGER.info(HiveBot.commands.get(37).getCommand() + " called by " + event.getAuthor().getAsTag());

                    //Initalize stringbuilder objects to hold the list items
                    StringBuilder output1 = new StringBuilder();
                    StringBuilder output2 = new StringBuilder();
                    StringBuilder output3 = new StringBuilder();

                    //Track index to add to each list evenly
                    int index = 0;

                    //Iterate through each Reference and grab the main ref code
                    for(Reference r:HiveBot.references){
                        switch (index){
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
                        if(index > 2){
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
                        info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                        event.getChannel().sendMessage(info.build()).queue();
                        info.clear();
                    } catch (PermissionException e){
                        event.getChannel().sendMessage("Missing permission exception: " + e.getPermission()).queue();
                    }
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            }
        }

        private ArrayList<String> categories(ArrayList<Reference> refList){
            ArrayList<String> cats = new ArrayList<>();

            for(Reference r:refList){
                ArrayList<String> tempCat = new ArrayList<>();
                tempCat.addAll(r.getCategory());
                for(String s:tempCat){
                    if(!(cats.contains(s))){
                        cats.add(s);
                    }
                }
            }
            return cats;
        }

}
