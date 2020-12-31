package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import rsystems.objects.Command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ChannelAnalyze extends Command {
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
		return;
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        //Create date formatter to output date to a universal pattern
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY");
		//Get the current date
		LocalDate currentDate = LocalDate.now();
		
		//Create the embed builder
        EmbedBuilder embedBuilder = new EmbedBuilder();
		
		final List<Category> categories = event.getGuild().getCategories();
		final List<TextChannel> textChannels = event.getGuild().getTextChannels();
		
		for(Category cat:categories){
			
			Map<String,String> channelMap = new LinkedHashMap<>();
			
			for(TextChannel t:textChannels){
				if(t.getParent().getIdLong() == cat.getIdLong()){
					String date = "N/A";
					
					try{

						if(t.hasLatestMessage()){
							List<Message> messages = new ArrayList<>();
							event.getChannel().getIterableHistory()
									.limit(1)
									.cache(false)
									.forEachAsync(messages::add);

							date = formatter.format(messages.get(0).getTimeCreated());
						}
					}catch(Exception e){}
					
					channelMap.putIfAbsent(t.getName(),date);
				}
			}
			
			StringBuilder channelString = new StringBuilder();
            StringBuilder datesString = new StringBuilder();
            StringBuilder daysPassed = new StringBuilder();
			
			for(Map.Entry<String,String> entry:channelMap.entrySet()){
				channelString.append(entry.getKey()).append("\n");
				datesString.append(entry.getValue()).append("\n");
			}

			embedBuilder.addField(cat.getName(),channelString.toString(),true);
			embedBuilder.addField("",datesString.toString(),true);
			//embedBuilder.addField("",daysPassed.toString(),true);

		}
		
    }

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
