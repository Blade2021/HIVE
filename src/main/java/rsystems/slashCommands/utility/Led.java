package rsystems.slashCommands.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.HiveBot;
import rsystems.objects.LED;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.util.Map;

public class Led extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(this.getName().toLowerCase(),this.getDescription().toLowerCase());

        try {
            Map<String, LED> ledMap = HiveBot.database.getLEDMap();

            for(Map.Entry<String, LED> entry:ledMap.entrySet()){
                commandData.addSubcommands(generateSubCommand(entry.getKey().toLowerCase(),entry.getValue().getDescription()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*commandData.addOption(OptionType.STRING,"type","The type of LED to use",true);
        commandData.addOption(OptionType.NUMBER,"qty","How many LEDs to calculate",false);

         */

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply(isEphemeral()).queue();

        LED led = null;
        try{

            led = HiveBot.database.getLED(event.getSubcommandName());
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
        embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
        embedBuilder.setThumbnail("https://kno.wled.ge/assets/images/ui/akemi/001_cheerful.png");


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

    public SubcommandData generateSubCommand(String ledType, String ledDescription){

        SubcommandData subcommandData = new SubcommandData(ledType,ledDescription);
        //subcommandData.addOption(OptionType.STRING,"type","The type of LED to use",true);
        subcommandData.addOption(OptionType.STRING,"qty","How many LEDs to calculate",false);

        return subcommandData;
    }
}
