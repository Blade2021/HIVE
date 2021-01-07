package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Commands extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {



    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        List<String> genericCommands = new ArrayList<>();
        List<String> authorizedCommands = new ArrayList<>();

        Map<String, Integer> commandMap = HiveBot.dispatcher.getCommandMap();

        final Member member = event.getMember();
        if (member != null) {
            for (Map.Entry<String, Integer> entry : commandMap.entrySet()) {

                if ((entry.getValue() != null) && (entry.getValue() >= 1)) {

                    if (HiveBot.dispatcher.checkAuthorized(member, entry.getValue())) {
                        authorizedCommands.add(entry.getKey());
                    } else {
                        continue;
                    }
                } else {
                    genericCommands.add(entry.getKey());
                }

            }

            Collections.sort(authorizedCommands);
            Collections.sort(genericCommands);

            StringBuilder genericCommandString = new StringBuilder();
            for(String command:genericCommands){
            	genericCommandString.append("`" + command + "`").append(", ");
			}

            StringBuilder authCommandString = new StringBuilder();
            for(String authCommmand:authorizedCommands){
            	authCommandString.append("`" + authCommmand + "`").append(", ");
			}

			reply(event,String.format("Generic Commands:\n%s\n\nMod Commands:\n%s",genericCommandString.toString(),authCommandString.toString()));
			System.out.println(authorizedCommands);
			System.out.println(genericCommands);
        }

    }

    @Override
    public String getHelp() {
        return "Just a test";
    }
}
