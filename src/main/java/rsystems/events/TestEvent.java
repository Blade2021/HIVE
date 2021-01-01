package rsystems.events;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;



public class TestEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!EmojiParser.extractEmojis(event.getMessage().getContentDisplay()).isEmpty()){
            for(String s: EmojiParser.extractEmojis(event.getMessage().getContentDisplay())){
                System.out.println(EmojiParser.parseToAliases(s));
            }
        }
    }
}
