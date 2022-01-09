package rsystems.commands.moderator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class ReferenceTest extends Command {

    private static final String[] ALIASES = new String[] {"refTest", "testRef"};

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Test Reference");
        builder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

        builder.setDescription(message.getContentRaw().replace("\\n","\n"));


        event.getChannel().sendMessageEmbeds(builder.build()).queue(success -> {
            event.getMessage().delete().queue();
        });

        builder.clear();
    }

    @Override
    public String getHelp() {
        return "Test a reference";
    }

    @Override
    public String[] getAliases() {
        return ALIASES;
    }
}
