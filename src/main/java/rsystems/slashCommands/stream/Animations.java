package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.StreamAnimation;

import java.sql.SQLException;
import java.util.ArrayList;

public class Animations extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.KICK_MEMBERS;
    }

    @Override
    public Integer getPermissionIndex() {
        return 512;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public SlashCommandData getCommandData() {

        SlashCommandData slashCommandData = Commands.slash(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();
        subCommands.add(new SubcommandData("animation-killswitch","Disable animation requests").addOption(OptionType.BOOLEAN,"allowance","True = Enable / False = Disable",false));
        subCommands.add(new SubcommandData("register","Register a new animation"));
        subCommands.add(new SubcommandData("modify","Modify an existing animation").addOption(OptionType.INTEGER,"animation-id","The ID of the animation to be modified",true));

        return slashCommandData.addSubcommands(subCommands);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        //Satisfy the initial call
        //event.deferReply(this.isEphemeral()).queue();

        if (event.getSubcommandName().equalsIgnoreCase("animation-killswitch")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Stream Animations");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));

            if (event.getOption("allowance") == null) {

                builder.setDescription(String.format("Animations are currently set to: `%s`", String.valueOf(HiveBot.streamHandler.allowAnimations()).toUpperCase()));

                reply(event, builder.build());
                builder.clear();
                return;
            }

            boolean result = event.getOption("allowance").getAsBoolean();

            if (HiveBot.streamHandler.allowAnimations() == result) {
                reply(event, "That setting is already set to " + result);
                return;
            }

            HiveBot.streamHandler.setAllowAnimations(result);

            if (result) {

                reply(event, "Enabling Animations for users\n\nStand by for notification to stream channel");

                builder.setDescription("Animations are now enabled!\n\nPlease be kind and use in moderation");

            } else {
                reply(event, "Disabling Animations for users\n\nStand by for notification to stream channel");

                builder.setDescription("Animations are temporarily disabled!\n\nPlease still enjoy the show!");
            }

            HiveBot.streamHandler.getLiveStreamChatChannel().sendMessageEmbeds(builder.build()).queue();
            builder.clear();
        } else if(event.getSubcommandName().equalsIgnoreCase("register")){
            TextInput scene = TextInput.create("scene","Scene", TextInputStyle.SHORT)
                    .setPlaceholder("Please enter the Scene that the source is on.  Case Sensitive!")
                    .setMaxLength(30)
                    .build();

            TextInput source = TextInput.create("source","Source",TextInputStyle.SHORT)
                    .setPlaceholder("Please enter the Source to be registered.  Case Sensitive")
                    .setMaxLength(30)
                    .build();

            Modal modal = Modal.create("obsanimationreg", "OBS Animation Registration")
                    .addActionRows(ActionRow.of(scene), ActionRow.of(source))
                    .build();

            event.replyModal(modal).queue();
        } else if(event.getSubcommandName().equalsIgnoreCase("modify")){
            if(event.getOption("animation-id") != null){
                Integer id = event.getOption("animation-id").getAsInt();

                try {
                    StreamAnimation animation = HiveBot.database.getAnimation(id);

                    if(animation != null){
                        TextInput animationID = TextInput.create("animationID","Animation ID", TextInputStyle.SHORT)
                                .setPlaceholder("Animation ID")
                                .setMaxLength(30)
                                .setValue(String.valueOf(animation.getId()))
                                .build();


                        TextInput scene = TextInput.create("scene","Scene", TextInputStyle.SHORT)
                                .setPlaceholder("Please enter the Scene that the source is on.  Case Sensitive!")
                                .setMaxLength(30)
                                .setValue(String.valueOf(animation.getSceneName()))
                                .build();

                        TextInput source = TextInput.create("source","Source",TextInputStyle.SHORT)
                                .setPlaceholder("Please enter the Source to be registered.  Case Sensitive")
                                .setMaxLength(30)
                                .setValue(String.valueOf(animation.getSourceName()))
                                .build();

                        TextInput cost = TextInput.create("cost","Cost",TextInputStyle.SHORT)
                                .setPlaceholder("Please enter the Cost for the animation")
                                .setMaxLength(30)
                                .setValue(String.valueOf(animation.getCost()))
                                .build();

                        TextInput cooldown = TextInput.create("cooldown","Cooldown",TextInputStyle.SHORT)
                                .setPlaceholder("Please enter the Cooldown for the animation in minutes")
                                .setMaxLength(30)
                                .setValue(String.valueOf(animation.getCooldown()))
                                .build();

                        Modal modal = Modal.create("animation-mod", "OBS Animation Modification")
                                .addActionRows(ActionRow.of(animationID), ActionRow.of(scene), ActionRow.of(source), ActionRow.of(cost), ActionRow.of(cooldown))
                                .build();

                        event.replyModal(modal).queue();
                    } else {
                        reply(event,"uh.. That's not a valid ID. \uD83E\uDD54");
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Enable / Disable the dispatching of Animations via users";
    }
}
