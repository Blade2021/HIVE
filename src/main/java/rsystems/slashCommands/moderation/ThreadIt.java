package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;



public class ThreadIt extends SlashCommand {
    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription())
                .addOption(OptionType.USER,"user","The user to invite to the thread",true)
                .addOption(OptionType.STRING,"title","The title of the thread",false);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.getInteraction().deferReply().queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
        builder.setTitle("New Thread Created!");
        builder.setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());

        String title = "Thread Started - " + event.getOption("user").getAsUser().getName();
        if(event.getOption("title") != null && !event.getOption("title").getAsString().isEmpty()){
            title = event.getOption("title").getAsString();
        }

        if(channel.getType().isThread()){
            final TextChannel parentChannel = event.getChannel().asThreadChannel().getParentChannel().asTextChannel();
            parentChannel.createThreadChannel(title).setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_3_DAYS).queue(success -> {
                success.join().queue();
                success.sendMessage(String.format("Hello %s,\n%s has started this thread for you.\n\nThanks,\n-HIVE",event.getOption("user").getAsUser().getAsMention(),sender.getAsMention())).queue();

                builder.setDescription(String.format("%s,\nA thread has been created for you here: %s.\n\nPlease check it when you get a moment.",event.getOption("user").getAsUser().getAsMention(),success.getAsMention()));
                builder.build();


                event.getInteraction().getHook().editOriginalEmbeds(builder.build()).queue();
            });
        } else {
            event.getInteraction().getChannel().asTextChannel().createThreadChannel(title, false).setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_3_DAYS).queue(success -> {
                success.join().queue();
                success.sendMessage(String.format("Hello %s,\n%s has started this thread for you.\n\nThanks,\n-HIVE", event.getOption("user").getAsUser().getAsMention(), sender.getAsMention())).queue();

                builder.setDescription(String.format("%s,\nA thread has been created for you here: %s.\n\nPlease check it when you get a moment.", event.getOption("user").getAsUser().getAsMention(), success.getAsMention()));

                event.getInteraction().getHook().editOriginalEmbeds(builder.build()).queue();
            });
        }
    }

    @Override
    public String getDescription() {
        return "Start a thread";
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.MANAGE_THREADS;
    }
}
