package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.LED;

import java.awt.*;
import java.sql.SQLException;

public class Led extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) {

        String[] args = content.split("\\s+");

        LED led = null;
        try{
            led = HiveBot.database.getLED(args[0]);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(led != null){

            boolean includeLedQty = (args.length > 1) && (Integer.valueOf(args[1]) > 0);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(String.format("%s - %d Volts",led.getLedName().toUpperCase(),led.getLedVoltage()));
            embedBuilder.setColor(Color.decode("#FF6145"));
            embedBuilder.setThumbnail("https://cdn.discordapp.com/icons/473448917040758787/a_476e5f6e550a1ce2bb43e3e094af6ab6.gif");


            float amperageTested = led.getWattagePerPixel_Tested()/ led.getLedVoltage();
            float amperageTheoretical = led.getWattagePerPixel_Theoretical()/ led.getLedVoltage();

            String whiteChannel = "No";
            if(led.isWhiteIncluded()){
                whiteChannel = "Yes";
            }

            embedBuilder.setDescription(String.format("**Dedicated White Channel:**  `%s`\n" +
                            "**Theoretical Wattage (Per Pixel):**  `%.3f`\n**Theoretical Amperage (Per Pixel):**  `%.3f`\n\n" +
                            "**Tested Wattage (Per Pixel):**  `%.3f`\n**Tested Amperage (Per Pixel):**  `%.3f`",whiteChannel,led.getWattagePerPixel_Theoretical(),
                    amperageTheoretical,led.getWattagePerPixel_Tested(),amperageTested));

            if((args.length > 1) && (Integer.parseInt(args[1]) > 0)){
                //embedBuilder.addBlankField(false);

                int qty = Integer.parseInt(args[1]);

                embedBuilder.addField("Pixel Count",String.format("`%d`",qty),true);
                embedBuilder.addBlankField(true);
                embedBuilder.addBlankField(true);
                embedBuilder.addField("Max Amperage (Tested)",String.format("`%.3f` Amps",amperageTested*qty),true);
                embedBuilder.addField("Max Wattage (Tested)",String.format("`%.3f` Watts",led.getWattagePerPixel_Tested()*qty),true);
                embedBuilder.addBlankField(true);

                float averageAmperage = (float) ((amperageTested * qty) * .6);
                embedBuilder.addField("Average Amperage (Tested)",String.format("`%.3f` Amps",averageAmperage),true);

                float averageWattage = (float) ((led.getWattagePerPixel_Tested() * qty) * .6);
                embedBuilder.addField("Average Wattage (Tested)",String.format("`%.3f` Watts",averageWattage),true);

                embedBuilder.addBlankField(true);
            }

            reply(event,embedBuilder.build());
            embedBuilder.clear();
        } else {
            MessageEmbed helpEmbed = Help.handleEvent(event,this);
            reply(event,helpEmbed);
        }
    }

    @Override
    public String getHelp() {

        String returnString ="`{prefix}{command} (Sub-Command) [args]`\n\n" +
                "**{prefix}{command} (LED Name)**\n`{prefix}{command} ws2815`\nGet some information about an LED.\n\n"+
                "**{prefix}{command} (LED Name) [QTY]**\n`{prefix}{command} ws2815 1200`\nGet calculated wattage/amperage for an LED";
        return returnString;


    }

    public void updateCommand(){
        this.updateCommand();
    }

}
