package rsystems.slashCommands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class SendACoffee extends SlashCommand {

    int cost = 5;

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription()).addOption(OptionType.USER, "user", "The user you would like to send a coffee too", true).addOption(OptionType.STRING,"body","An optional message to send to the user");
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public void dispatch(final User sender, final MessageChannel channel, final String content, final SlashCommandInteractionEvent event) {

        //Defer the Reply to allow proper handling of the message
        event.deferReply().setEphemeral(this.isEphemeral()).queue();

        try {

            if(HiveBot.database.checkOptStatus(sender.getIdLong())){
                event.getHook().editOriginal("Sorry, You are opted out of receiving coffees so you are unable to send any.").queue();
                return;
            }

            if(HiveBot.database.getCashews(sender.getIdLong()) >= cost){
                // User has enough cashews to cover the cost
                final Member recipient = event.getOption("user").getAsMember();

                String msg = null;
                if(event.getOption("body") != null){
                    msg = event.getOption("body").getAsString();
                }

                final String finalMsg = msg;


                if(recipient != null){

                    if(HiveBot.database.checkOptStatus(recipient.getIdLong())){
                        event.getHook().editOriginal("❌ `TRANSACTION CANCELLED`\n\nSorry, that user has opt'd to not receive messages from me.").queue();
                        return;
                    }

                    // Attempt to open a Direct Message with user
                    recipient.getUser().openPrivateChannel().queue(privateChannel -> {

                        if(finalMsg != null && !finalMsg.isEmpty()){
                            privateChannel.sendMessageEmbeds(createEmbed(finalMsg)).queue();
                            event.getHook().editOriginal("✅ `TRANSACTION SUCCESS`\n\nYour message has been sent!\n\nOriginal Message:\n" + finalMsg).queue();
                        } else {
                            privateChannel.sendMessageEmbeds(createEmbed()).queue();
                            event.getHook().editOriginal("✅ `TRANSACTION SUCCESS`\n\nA nice cup of coffee has been sent to that user.  Thank you!").queue();
                        }

                        try {
                            HiveBot.database.deductCashews(sender.getIdLong(),cost);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }, failure -> {

                        event.getHook().editOriginal("❌ `TRANSACTION CANCELLED`\n\nSorry, That user doesn't allow messages from me..").queue();

                    });



                }


            } else {

                // User does not have enough cashews
                event.getHook().editOriginal("❌ `TRANSACTION CANCELLED`\n\nSorry,\nLooks like you don't have enough cashews to do that right now").queue();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "Send a user a coffee as a show of gratitude.  This command will cost you 5 cashews.";
    }

    private MessageEmbed createEmbed(){
        return createEmbed("A special someone told me to give you this coffee. ☕\n\n" +
                "Hope you have a great day!");
    }

    private MessageEmbed createEmbed(final String message){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("You have received a coffee!")
                .setColor(HiveBot.getColor(HiveBot.colorType.USER));

        builder.setDescription(message);

        builder.setFooter(String.format("P.S. If you would like to 'OPT-OUT' of these messages, please just use the command %soptout",HiveBot.prefix));
                //.setDescription(String.format("%sP.S. If you would like to 'OPT-OUT' of these messages, please just use the command %soptout",message,HiveBot.prefix));

        return builder.build();
    }
}
