package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.Config;
import rsystems.objects.Command;

public class PowerCal extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        if((args.length >= 3) && (args[0] != null)) {
            String subCommand = args[0];

            Double watts = null;
            Double amps = null;
            Double voltage = null;
            Double ohms = null;

            String voltageString = grabData("V:",content);
            if((voltageString != null) && (!voltageString.isEmpty())){
                voltage = Double.parseDouble(voltageString);
                System.out.println("Setting voltage to: " + voltage);
            }
            String wattageString = grabData("W:",content);
            if((wattageString != null) && (!wattageString.isEmpty())){
                watts = Double.parseDouble(wattageString);
                System.out.println("Setting watts to: " + watts);
            }
            String ohmsString = grabData("O:",content);
            if((ohmsString != null) && (!ohmsString.isEmpty())){
                ohms = Double.parseDouble(ohmsString);
                System.out.println("Setting ohms to: " + ohms);
            }
            String ampsString = grabData("A:",content);
            if((ampsString != null) && (!ampsString.isEmpty())){
                amps = Double.parseDouble(ampsString);
                System.out.println("Setting amps to: " + amps);
            }

            Double result = null;

            switch(subCommand){
                case "amps":
                    System.out.println("Debug: AMPS");
                    if((watts != null) && (ohms != null)){
                        result = Math.sqrt(watts/ohms);

                    } else if((watts != null) && (voltage != null)){
                        result = (watts/voltage);

                    } else if((voltage != null) && (ohms != null)){
                        result = voltage/ohms;

                    }
                    break;

                case "volts":
                    System.out.println("Debug: VOLTS");
                    if((ohms != null) && (amps != null)){
                        result = ohms * amps;

                    } else if((watts != null) && (amps != null)){
                        result = watts/amps;

                    } else if((watts != null) && (ohms != null)){
                        result = Math.sqrt((watts*ohms));
                    }
                    break;

                case "ohms":
                    System.out.println("Debug: OHMS");
                    if((voltage != null) && (amps != null)){
                        result = voltage / amps;

                    } else if((voltage != null) && (watts != null)){
                        result = (Math.pow(voltage,2))/watts;

                    } else if((watts != null) && (amps != null)){
                        result = watts/(Math.pow(amps,2));
                    }
                    break;
                case "watts":
                    System.out.println("Debug: WATTS");
                    if((voltage != null) && (ohms != null)){
                        result = (Math.pow(voltage,2))/ohms;

                    } else if((ohms != null) && (amps != null)){
                        result = ohms * (Math.pow(amps,2));

                    } else if((voltage != null) && (amps != null)){
                        result = voltage * amps;
                    }
                    break;
            }

            if(result != null){
                reply(event,String.format("%.2f %s",result, subCommand.toUpperCase()));
            }

        }
    }

    @Override
    public String getHelp() {
        String returnString = ("{prefix}{command} [Sub-Command] [args]\n" +
                "Calculate any of power stats below using any two known arguments.\n\n" +
                "Examples: \nVoltage: `V:120`    |  Amps: `A:10`\n"+
                "Ohms: `O:10.2`    |  Watts: `W:1300`\n\n" +
                "**volts**\n`{prefix}{command} volts [args]`\n" +
                "**amps**\n`{prefix}{command} amps [args]`\n" +
                "**watts**\n`{prefix}{command} watts [args]`\n" +
                "**ohms**\n`{prefix}{command} ohms [args]`\n\n" +
                "Full Example:\n{prefix}{command} watts V:120 A:10");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private String grabData(String identifier, String content) {

        String dataOutput = null;
        identifier = identifier.toLowerCase();

        if (content.toLowerCase().contains(identifier.toLowerCase())) {
            int localIndex = content.toLowerCase().indexOf(identifier) + identifier.length();
            int endingIndex = content.indexOf(" ", localIndex + 1);

            if (endingIndex >= 1) {
                dataOutput = content.substring(localIndex, endingIndex);
            } else {
                dataOutput = content.substring(localIndex);
            }

        }

        return dataOutput;
    }
}
