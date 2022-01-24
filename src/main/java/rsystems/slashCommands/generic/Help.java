package rsystems.slashCommands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.SlashCommand;

public class Help extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(), this.getDescription());
        commandData.addOption(OptionType.STRING, "command", "The name of the command to gather information about",true);

        return commandData;
    }


    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {

        event.deferReply(isEphemeral()).queue();

        String commandName = event.getOption("command").getAsString();

        for (Command c : HiveBot.dispatcher.getCommands()) {

            if (c.getName().equalsIgnoreCase(commandName)) {
                handleEvent(event, c);
                return;
            }

            for (final String alias : c.getAliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    handleEvent(event, c);
                    return;
                }
            }
        }

        event.getHook().editOriginal("No command was found with that name").queue();
    }

    private void handleEvent(SlashCommandEvent event, final Command c) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Help | " + c.getName());
        builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));

        String description = c.getHelp();
        description = description.replace("{prefix}",HiveBot.prefix);
        description = description.replace("{command}",c.getName());
        builder.setDescription(description);

        if((c.getAliases() != null) && (c.getAliases().length > 0)){
            StringBuilder aliasString = new StringBuilder();

            for(String alias:c.getAliases()){
                aliasString.append(alias).append(",");
            }

            builder.appendDescription("\n\n" + "**Aliases**: "+ aliasString);

            //builder.addField("Aliases",aliasString.toString(),false);
        }

        if(c.getPermissionIndex() != null){
            builder.addField("Permission Index:",String.valueOf(c.getPermissionIndex()),true);
        }

        if(c.getDiscordPermission() != null){
            builder.addField("Discord Permission:",c.getDiscordPermission().getName(),true);
        }

        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }

    @Override
    public String getDescription() {
        return "Get Help for a Command";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
