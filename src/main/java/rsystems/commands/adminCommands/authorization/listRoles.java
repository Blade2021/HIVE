package rsystems.commands.adminCommands.authorization;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.util.Map;

public class listRoles extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 16384;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,handleEvent(message));
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        reply(event,handleEvent(message));
    }

    private Message handleEvent(Message message){
        Message output = null;

        Map<Long,Integer> authRoleMap = HiveBot.sqlHandler.getAuthRoles();

        StringBuilder roleIDString = new StringBuilder();
        StringBuilder roleValueString = new StringBuilder();
        StringBuilder roleNameString = new StringBuilder();

        for(Map.Entry<Long,Integer> entry:authRoleMap.entrySet()){
            roleIDString.append(entry.getKey()).append("\n");
            roleValueString.append(entry.getValue()).append("\n");

            if(HiveBot.mainGuild().getRoleById(entry.getKey()) != null){
                roleNameString.append(HiveBot.mainGuild().getRoleById(entry.getKey()).getName()).append("\n");
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Auth Role Table:")
                .setColor(Color.RED)
                .addField("Role Name",roleNameString.toString(),true)
                .addField("RoleID",roleIDString.toString(),true)
                .addField("Role Value",roleValueString.toString(),true);

        MessageBuilder messageBuilder = new MessageBuilder();
        output = messageBuilder.setEmbed(embedBuilder.build()).build();

        embedBuilder.clear();
        return output;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getName(){
        return "listAuthRoles";
    }
}
