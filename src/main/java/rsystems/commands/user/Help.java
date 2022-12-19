package rsystems.commands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class Help extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        String[] args = content.split("\\s+");

        String commandName = args[0];

        for (Command c : HiveBot.dispatcher.getCommands()) {

            if (c.getName().equalsIgnoreCase(commandName)) {
                MessageEmbed embed = handleEvent(event, c);
                reply(event,embed);
                return;
            }

            for (final String alias : c.getAliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    MessageEmbed embed = handleEvent(event, c);
                    reply(event,embed);
                    return;
                }
            }
        }

        reply(event,"No command was found with that name");
    }

    public static MessageEmbed handleEvent(final MessageReceivedEvent event, final Command c) {
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

        return builder.build();
    }

    @Override
    public String getHelp() {
        return "If you need help calling the help command for help with a command.  Then you really do need help.\n-anon\n\n" +
                "" +
                "{prefix}{command} (command)";
    }
}
