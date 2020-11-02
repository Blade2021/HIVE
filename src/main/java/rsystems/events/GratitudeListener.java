package rsystems.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    public static ArrayList<String> coolDownChannels = new ArrayList<>();

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        String[] triggers = {"thanks","thank you","thnx","thx "};

        if(!coolDownChannels.contains(event.getChannel().getId())) {

            for (String s : triggers) {
                if (event.getMessage().getContentRaw().toLowerCase().contains(s)) {
                    event.getChannel().sendMessage("Don't forget to send karma! <:KU:717177145717424180> ~karma for more info!").queue(success -> {
                        success.delete().queueAfter(10, TimeUnit.MINUTES);
                    });

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                System.out.println("Adding " + event.getChannel().getName() + " to cooldown");
                                coolDownChannels.add(event.getChannel().getId());
                                //Sleep the thread for 10 minutes
                                Thread.sleep(600000);
                            } catch (InterruptedException ie) {
                            }
                            //Remove the entry from the HashMap
                            Iterator it = coolDownChannels.iterator();
                            while(it.hasNext()){
                                String checkId = (String) it.next();
                                if(checkId.equalsIgnoreCase(event.getChannel().getId())){
                                    it.remove();
                                }
                            }
                        }
                    }).start();

                }
            }
        }

    }

}
