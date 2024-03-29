package rsystems.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class ReferenceTester extends Command {

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

        builder.setDescription(content.replace("\\n","\n"));

        builder.setFooter("Requested by " + event.getMember().getEffectiveName(),event.getMember().getEffectiveAvatarUrl());

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.setEmbeds(builder.build());
        messageBuilder.addActionRow(Button.primary("source","Source"));

        event.getChannel().sendMessage(messageBuilder.build()).queue(success -> {
            event.getMessage().delete().queue();
        });

        builder.clear();
    }

    @Override
    public String getHelp() {
        return String.format("{prefix}%s (Reference Description ONLY)\n" +
                "\n" +
                "Test a reference",this.getName());
    }

    @Override
    public String[] getAliases() {
        return ALIASES;
    }
}
