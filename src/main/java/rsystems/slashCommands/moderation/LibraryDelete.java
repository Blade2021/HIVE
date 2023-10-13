package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.objects.SlashCommand;

public class LibraryDelete extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription())
                .addOption(OptionType.STRING, "reference", "The reference to modify",true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

    }

    @Override
    public String getDescription() {
        return null;
    }
}
