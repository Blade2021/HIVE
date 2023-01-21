package rsystems.handlers;

import net.dv8tion.jda.api.entities.User;
import rsystems.Config;
import rsystems.HiveBot;

public class ExceptionHandler {

    public static void notifyException(final Exception e, final String callingClass){

        if(Config.get("Enable-Exception-Notifications").equalsIgnoreCase("1")) {

            final User notifyUser = HiveBot.jda.getUserById(Config.get("OWNER_ID"));

            if(notifyUser != null) {
                HiveBot.jda.openPrivateChannelById(notifyUser.getIdLong()).queue(privateChannel -> {

                    StringBuilder eString = new StringBuilder();
                    eString.append("**EXCEPTION:** ").append(e.getCause()).append("\n");
                    eString.append("**Class: **").append(callingClass).append("\n");
                    eString.append("**").append(e.getMessage()).append("**\n```\n");

                    for(StackTraceElement element: e.getStackTrace()){

                        if(eString.length()+5+element.toString().length() > 1900){
                            eString.append("```");
                            privateChannel.sendMessage(eString.toString()).queue();
                            eString.setLength(0);
                            eString.append("```\n");
                            eString.append(element).append("\n");
                        } else {
                            eString.append(element).append("\n");
                        }
                    }

                    if(eString.length() > 3) {
                        eString.append("```");
                        privateChannel.sendMessage(eString.toString()).queue();
                    }
                });
            }
        }
    }

}
