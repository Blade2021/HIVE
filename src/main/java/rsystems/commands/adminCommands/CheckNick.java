package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.events.NicknameListener;
import rsystems.objects.Command;

public class CheckNick extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 64;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        if((args == null) || (args[0].isEmpty())){

        } else {
            Member member = null;
            if(!message.getMentionedMembers().isEmpty()){
                member = message.getMentionedMembers().get(0);
            } else {
                member = HiveBot.mainGuild().getMemberById(Long.valueOf(args[0]));
            }


            if(member != null){
                NicknameListener.handleKarmaNickname(member.getIdLong());
            }

        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
