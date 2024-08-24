package rsystems.slashCommands.tickets;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.TicketObject;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class Ticket extends SlashCommand {

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        final Member author = event.getMember();

        if (author != null) {
            int activeTicketCount = 0;

            Map<Long, TicketObject> ticketMap = null;
            try {
                ticketMap = HiveBot.database.getTickets(author.getIdLong());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (ticketMap != null) {
                for (Map.Entry<Long, TicketObject> entry : ticketMap.entrySet()) {
                    if (entry.getValue().isOpenStatus()) {
                        activeTicketCount++;
                    }
                }

                if (activeTicketCount > 0) {
                    reply(event, "You already have an active ticket.  Please use that channel before creating a new ticket.");
                    return;
                }
            }

            //User does not have an open ticket at this time
            final UUID ticketID = UUID.randomUUID();

            TextInput ticketBody = TextInput.create("ticket-body", "Issue/Problem statement", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Please type a summary of your issue here.")
                    .setMinLength(1)
                    .setMaxLength(2000)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("ticket-" + ticketID, "Ticket Submission")
                    .addComponents(ActionRow.of(ticketBody))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public String getDescription() {
        return "Contact the staff of this server in a private channel";
    }
}
