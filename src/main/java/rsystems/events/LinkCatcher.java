package rsystems.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;


import javax.annotation.Nonnull;
import java.util.List;

public class LinkCatcher extends ListenerAdapter {

    private static Long pullChannelID = Long.valueOf(Config.get("link_pullChannel"));
	public static Long pushChannelID = Long.valueOf(Config.get("link_pushChannel"));;

    public void updateChannels(Long pullChannel, Long pushChannel){
        if(HiveBot.drZzzGuild().getTextChannelById(pullChannel) != null){
        	LinkCatcher.pullChannelID = pullChannel;
		}
		
		if(HiveBot.drZzzGuild().getTextChannelById(pushChannel) != null){
			LinkCatcher.pushChannelID = pushChannel;
		}
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
		if(event.getChannel().getIdLong() == pullChannelID){

			if ((event.getMessage().getContentRaw().contains("http://")) || (event.getMessage().getContentRaw().contains("https://")) || (event.getMessage().getContentRaw().contains("www."))) {
				if(HiveBot.getStreamMode()) {

					final String messageraw = event.getMessage().getContentRaw();
					final TextChannel pushChannel = HiveBot.drZzzGuild().getTextChannelById(pushChannelID);

					// Call method to get link
					String link = getLink(messageraw);

					if (link.length() <= 5) {
						//Link was not long enough to verify
						return;
					}

					// Call method to get author
					String author = getAuthor(event, messageraw);
					if (pushChannel != null) {

						List<Message> messages = pushChannel.getHistory().retrievePast(20).complete();

						for (Message m : messages) {
							if (m.getContentRaw().contains(link)) {
								event.getMessage().addReaction("⚠").queue();
								return;
							}
						}
						//If current link was not found in messages
						System.out.println("Sending link to channel: " + link);
						pushChannel.sendMessage(author + link).queue();
						event.getMessage().addReaction("\uD83D\uDCE8").queue();

					}
				} else {
					event.getMessage().addReaction("\uD83D\uDEE1").queue();
				}
			}
		}
    }

	private String getLink(String message){
		String link = "";

		if(message.contains("http")){
			int linkStart = message.indexOf("http");
			try{
				// Space was found after link
				link = message.substring(linkStart,message.indexOf(" ",linkStart+1));
			}
			catch(StringIndexOutOfBoundsException e){
				// No space was found
				link = message.substring(linkStart);
			}
		} else {
			int linkStart = message.indexOf("www");
			try {
				// Space was found after link
				link = message.substring(linkStart, message.indexOf(" ", linkStart + 1));
			}
			catch(StringIndexOutOfBoundsException e){
				// No space was found
				link = message.substring(linkStart);
			}
		}
		return link;
	}

	private String getAuthor(GuildMessageReceivedEvent event, String message){
		// Initialize author
		String author = "";
		// Does message contain brackets?
		if((message.contains("[")) && (message.contains("]"))){
			try{
				// Get locations of brackets
				int openBracketLocation = message.indexOf("[");
				int closeBracketLocation = message.indexOf("]");
				// Grab author, and strip youtube and twitch from author
				author = message.substring(openBracketLocation+1,closeBracketLocation).replaceFirst("YouTube:","").replaceFirst("Twitch:","");
				author = author + " : ";
			}
			catch (StringIndexOutOfBoundsException e){
				System.out.println("Could not find author");
			}
		} else {
			author = event.getMessage().getAuthor().getName();
			author = author + " : ";
		}

		return author;
	}
}
