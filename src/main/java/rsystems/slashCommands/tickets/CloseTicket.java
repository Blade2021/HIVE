package rsystems.slashCommands.tickets;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.TicketObject;

import java.sql.SQLException;

public class CloseTicket extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        // Hold the reply
        event.deferReply().queue();

        final Long channelID = event.getChannel().getIdLong();
        TicketObject ticket = null;

        try {
            ticket = HiveBot.database.getTicketViaChannelID(channelID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if(ticket != null){
            if(ticket.isOpenStatus()){

                reply(event,"Closing ticket...\n\nPlease open a new ticket if you do not feel your issue is resolved.\n" +
                        "\n" +
                        "```ansi\n" +
                        "\u001B[2;31mThis chat will be automatically deleted after 30 days.\u001B[0m\n" +
                        "```");

                //close the ticket

                Integer dataUpdateCount = null;
                try {
                    dataUpdateCount = HiveBot.database.closeTicket(ticket.getTicketID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if(dataUpdateCount != null){

                    final TicketObject finalTicket = ticket;
                    final String closedTicketCategoryID = Config.get("CLOSED_TICKET_CATEGORY_ID");
                    final Category closedTicketCategory = event.getGuild().getCategoryById(closedTicketCategoryID);

                    event.getChannel().asTextChannel().getManager().setParent(closedTicketCategory).queue(Success -> {

                        final Member author = event.getGuild().getMemberById(finalTicket.getAuthorID());
                        event.getChannel().asTextChannel().upsertPermissionOverride(author).deny(Permission.MESSAGE_SEND).queue();

                        StringBuilder sb = new StringBuilder();
                        sb.append("**Ticket ID:** " + finalTicket.getTicketID().toString() + "\n\n");
                        sb.append("**Member:** " + event.getGuild().getMemberById(finalTicket.getAuthorID()).getAsMention() + "\n");
                        sb.append("**Member ID:** " + finalTicket.getAuthorID() + "\n");
                        sb.append("**Closing Admin:** " + event.getMember().getAsMention() + "\n");
                        sb.append("**Closing Admin ID:** " + event.getMember().getIdLong());



                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle("Closed Ticket - " + event.getChannel().getIdLong());
                        builder.setColor(HiveBot.getColor(HiveBot.colorType.NOTIFICATION));
                        builder.setDescription(sb.toString());
                        event.getGuild().getTextChannelById(Config.get("LOGCHANNEL")).sendMessageEmbeds(builder.build()).queue();
                    });
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "Close a ticket";
    }
}
