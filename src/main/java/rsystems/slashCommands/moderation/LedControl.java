package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.LED;
import rsystems.objects.SlashCommand;
import rsystems.slashCommands.generic.Led;

import java.sql.SQLException;
import java.util.ArrayList;

public class LedControl extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();

        /**
         * add - Add an LED to the system matrix
         * remove - Remove an LED from the system
         * modify - Modify a current LED stats
         */

        // ADD LED COMMAND
        subCommands.add(new SubcommandData("add","Add a LED to the system")
                .addOption(OptionType.STRING,"led-name","The name/type of the LED to add to the system",true)
                .addOption(OptionType.NUMBER,"voltage","The Voltage of the LED",true)
                .addOption(OptionType.NUMBER,"wattage-tested","The TESTED wattage PER PIXEL",true)
                .addOption(OptionType.NUMBER,"wattage-theory","The THEORETICAL wattage PER PIXEL",true)
                .addOption(OptionType.BOOLEAN,"white-included","Does the LED type include a white pixel?",true));

        // REMOVE LED COMMAND
        subCommands.add(new SubcommandData("remove","Remove an LED from the system")
                .addOption(OptionType.STRING,"led-name","The name/type of the LED to add to the system",true));


        // MODIFY LED COMMAND
        subCommands.add(new SubcommandData("modify","Modify a current LED with new stats")
                .addOption(OptionType.STRING, "led-name", "The name/type of the LED to be edited",true)
                .addOption(OptionType.NUMBER,"voltage","The NEW voltage setting for the LED",false)
                .addOption(OptionType.NUMBER,"wattage-tested","The NEW TESTED wattage PER PIXEL",false)
                .addOption(OptionType.NUMBER,"wattage-theory","The NEW THEORETICAL wattage PER PIXEL",false)
                .addOption(OptionType.BOOLEAN,"white-included","The NEW White Included Setting",false)
        );

        commandData.addSubcommands(subCommands);

        return commandData;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(isEphemeral()).queue();

        if((event.getSubcommandName() == null) || (event.getSubcommandName().isEmpty())){
            reply(event,"Don't do that",isEphemeral());
            return;
        }

        // ADD LED SUB COMMAND
        if(event.getSubcommandName().equalsIgnoreCase("add")){
            try {
                if(HiveBot.database.getLED(event.getOption("led-name").getAsString()) == null){
                    // LED was not found.  continue;

                    Integer voltage = Integer.parseInt(event.getOption("voltage").getAsString());
                    Float testedWattage = Float.parseFloat(event.getOption("wattage-tested").getAsString());
                    Float theoreticalWattage = Float.parseFloat(event.getOption("wattage-theory").getAsString());
                    Boolean whitePixel = event.getOption("white-included").getAsBoolean();
                    if(whitePixel == null){
                        whitePixel = false;
                    }

                    LED newLED = new LED(event.getOption("led-name").getAsString(),voltage,whitePixel,theoreticalWattage,testedWattage);

                    Integer insertStatusCode = HiveBot.database.insertLED(newLED);
                    if(insertStatusCode == 200){

                        MessageBuilder messageBuilder = new MessageBuilder();
                        messageBuilder.setContent("The new LED has been created and put into the database");
                        messageBuilder.setEmbeds(Led.createLEDEmbed(newLED,100));

                        reply(event,messageBuilder.build(),isEphemeral());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // REMOVE LED SUB COMMAND
        else if(event.getSubcommandName().equalsIgnoreCase("remove")){
            String ledName = event.getOption("led-name").getAsString();

            try {
                if(HiveBot.database.deleteRow("LED_Table","ledName",ledName) > 0){
                    reply(event,String.format("%s was removed from the database",ledName),isEphemeral());
                } else {
                    reply(event,"Didn't find any LEDs in the database with that name",isEphemeral());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        else if(event.getSubcommandName().equalsIgnoreCase("modify")){
            String ledName = event.getOption("led-name").getAsString();

            try {
                LED led = HiveBot.database.getLED(ledName);

                if(led != null){

                    // Remove the old data from the database
                    HiveBot.database.deleteRow("LED_Table","ledName",ledName);


                    if(event.getOption("voltage") != null){
                        led.setLedVoltage(Integer.parseInt(event.getOption("voltage").getAsString()));
                    }

                    if(event.getOption("wattage-tested") != null){
                        led.setWattagePerPixel_Tested(Float.parseFloat(event.getOption("wattage-tested").getAsString()));
                    }

                    if(event.getOption("wattage-theory") != null){
                        led.setWattagePerPixel_Theoretical(Float.parseFloat(event.getOption("wattage-theory").getAsString()));
                    }

                    if(event.getOption("white-included") != null){
                        led.setWhiteIncluded(event.getOption("white-included").getAsBoolean());
                    }

                    Integer insertStatusCode = HiveBot.database.insertLED(led);
                    if(insertStatusCode == 200){

                        MessageBuilder messageBuilder = new MessageBuilder();
                        messageBuilder.setContent("The new LED has been created and put into the database");
                        messageBuilder.setEmbeds(Led.createLEDEmbed(led,100));

                        reply(event,messageBuilder.build(),isEphemeral());
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public String getDescription() {
        return "Control of the LED database";
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}
