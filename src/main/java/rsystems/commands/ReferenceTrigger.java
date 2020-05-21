package rsystems.commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Reference;
import rsystems.adapters.RoleCheck;

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


        }



}
