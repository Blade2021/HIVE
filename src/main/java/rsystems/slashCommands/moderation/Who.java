package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.KarmaUserInfo;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class Who extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(),this.getDescription())
                .addOption(OptionType.USER,"member","The user to lookup")
                .addOption(OptionType.STRING,"userid","The user id to lookup");
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        if (event.getOption("member") != null) {

            handleEvent(event, event.getOption("member").getAsMember());

        } else
            if (event.getOption("userid") != null) {

            event.getGuild().retrieveMemberById(event.getOption("userid").getAsString()).queue(member -> {
                handleEvent(event, member);
            });

        } else {

            handleEvent(event,event.getMember());

            }
    }

    @Override
    public String getDescription() {
        return "Get information about a member";
    }

    private void handleEvent(final SlashCommandInteractionEvent event, final Member member) {

        KarmaUserInfo karmaUserInfo = null;

        try {
            karmaUserInfo = HiveBot.karmaSQLHandler.getKarmaUserInfo(member.getIdLong());


            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("User Info");

            builder.setThumbnail(member.getEffectiveAvatarUrl());

            builder.setColor(HiveBot.getColor(HiveBot.colorType.NOVA));
            builder.addField("\uD83D\uDC65 User Tag: ", member.getEffectiveName(), true);

            if(!member.getUser().isBot()) {
                builder.addField("✨ Current Karma: ", String.format("\uD83D\uDD39 %d", karmaUserInfo.getKarma()), true);
                builder.addField("\uD83D\uDC51 Current Rank: ", String.valueOf(HiveBot.karmaSQLHandler.getRank(member.getId()) + 1), true);
                builder.addField("\uD83D\uDC4D Positive Karma Sent: ", String.format("```diff\n+%d```", karmaUserInfo.getKsent_pos()), true);
                builder.addField("\uD83D\uDC4E Negative Karma Sent: ", String.format("```diff\n-%d```", karmaUserInfo.getKsent_neg()), true);
                builder.addBlankField(true);
            } else {
                builder.addField("Karma","BOTs don't get Karma",false);
            }
            builder.addField("Joined Server",member.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE),true);
            builder.addField("Joined Discord",member.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE),true);

            builder.setFooter("ID:" + member.getIdLong());

            reply(event,builder.build());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}
