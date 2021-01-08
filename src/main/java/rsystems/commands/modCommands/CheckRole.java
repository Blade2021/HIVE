package rsystems.commands.modCommands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.ArrayList;
import java.util.List;

public class CheckRole extends Command {
	@Override
	public Integer getPermissionIndex() {
		return 2;
	}

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        String[] args = content.split("\\s+");
		if(args.length >= 1){
			
			Role role = null;
			
			try{
				Long roleID = Long.valueOf(args[0]);
				
				if(HiveBot.jda.getRoleById(roleID) != null){
					//The role exists
					
					role = event.getGuild().getRoleById(roleID);
					
				}
			} catch (NumberFormatException e){
				
				String roleName = content;
                roleName = roleName.replaceAll(" true","");

				if(HiveBot.debug)
					System.out.println("Lookup Name:"+roleName);

				for(Role r:event.getGuild().getRoles()){
					if(r.getName().equalsIgnoreCase(roleName)){
						role = r;
						break;
					}
				}
			}
			
			boolean getMembers = false;
			if(role != null){
				event.getMessage().addReaction("✅").queue();
				
				if((args.length >= 2) && (args[args.length-1].toLowerCase().equals("true"))){
					getMembers = true;
				}
			
			
				List<String> memberList = new ArrayList();
				int count = 0;
				
				for(Member m:event.getGuild().getMembersWithRoles(role)){
					count++;
					if((getMembers) && (count <= 200))
						memberList.add(m.getUser().getAsTag());
				}

				reply(event,String.format("Role:`%s` has %d users.\nID: %d",role.getName(),count,role.getIdLong()));
				if(getMembers){
					Role finalRole = role;
					event.getAuthor().openPrivateChannel().queue(privateChannel -> {
						privateChannel.sendMessage(String.format("`%s`:\n\n%s", finalRole.getName(),memberList.toString())).queue();
						memberList.clear();
					});
				}
				
			
			} else {
				event.getMessage().addReaction("⚠").queue();
				reply(event,"Could not find role");
			}
			
		}
		
    }

    @Override
    public String getHelp() {
        return "Just a test";
    }

    @Override
    public String getName() {
        return "Role";
    }

}
