package rsystems.commands.adminCommands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmojiWhitelist extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 512;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");
        if((args != null) && (args.length >= 1)){

            if(args[0].equalsIgnoreCase("add")){
                if(subCommand_AddEmoji(content.substring(args[0].length()+1))){
                    reply(event,"Added Emoji");
                } else
                    reply(event,"Did not add Emoji");

                return;
            }

            if(args[0].equalsIgnoreCase("remove")){
                Long associatedRole = Long.valueOf(args[1]);

                for(String emoji:EmojiParser.extractEmojis(content)){
                    HiveBot.sqlHandler.removeEmojiFromWhitelist(associatedRole,EmojiParser.parseToAliases(emoji));
                }
                message.addReaction("✅ ").queue();
            }

            if(args[0].equalsIgnoreCase("list")){
                EmbedBuilder embedBuilder = new EmbedBuilder();

                for(Map.Entry<Long, ArrayList<String>> entry:HiveBot.emojiPerkMap.entrySet()){
                    embedBuilder.appendDescription("**Role:**  "+entry.getKey()+"\n");

                    StringBuilder emojiString = new StringBuilder();

                    for(String emoji:entry.getValue()){
                        emojiString.append(EmojiParser.parseToUnicode(emoji)).append("......").append("`").append(emoji).append("`\n");
                    }

                    embedBuilder.appendDescription(emojiString.toString());

                }

                reply(event,embedBuilder.build());
                embedBuilder.clear();
            }

        }
    }

    @Override
    public String getHelp() {
        return null;
    }

    private boolean subCommand_AddEmoji(String content){
        boolean output = false;

        String[] args = content.split("\\s+");
        if(args.length >= 2){
            Long associatedRole = Long.valueOf(args[0]);
            if(HiveBot.drZzzGuild().getRoleById(associatedRole) != null) {

                List<String> emojiList = EmojiParser.extractEmojis(content);

                for (String emoji : emojiList) {
                    if(HiveBot.sqlHandler.addEmojiToWhitelist(associatedRole,EmojiParser.parseToAliases(emoji))){
                        if(HiveBot.emojiPerkMap.get(associatedRole) != null){
                            HiveBot.emojiPerkMap.get(associatedRole).add(emoji);
                        } else {
                            HiveBot.emojiPerkMap.put(associatedRole,new ArrayList<String>());
                            HiveBot.emojiPerkMap.get(associatedRole).add(emoji);
                        }
                        output = true;
                    }
                }
            }
        }
        return output;
    }

    @Override
    public String getName() {
        return "nickEmoji";
    }
}
