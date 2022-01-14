package rsystems.slashCommands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.HiveBot;
import rsystems.objects.LED;
import rsystems.objects.SlashCommand;

import java.awt.*;
import java.sql.SQLException;

public class Led extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());
        commandData.addOption(OptionType.STRING,"type","The type of LED to use",true);
        commandData.addOption(OptionType.NUMBER,"qty","How many LEDs to calculate",false);

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {

        event.deferReply(isEphemeral()).queue();

        LED led = null;
        try{
            led = HiveBot.database.getLED(event.getOption("type").getAsString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(led != null){

            Integer qty =  null;

            if(event.getOption("qty") != null){
                qty = Integer.parseInt(event.getOption("qty").getAsString());
            }

            reply(event,createLEDEmbed(led,qty),isEphemeral());

        } else {
            reply(event,String.format("I did not find an LED with that type.\nTry using `%sLEDList` to get a list of supported LED Types.",HiveBot.prefix),isEphemeral());
        }
    }

    @Override
    public String getDescription() {
        return "Get information about an LED";
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    public static MessageEmbed createLEDEmbed(LED led, Integer qty){
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


        if(qty != null){

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

        return embedBuilder.build();
    }
}
