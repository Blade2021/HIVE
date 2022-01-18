package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.EncryptionHandler;
import rsystems.objects.SlashCommand;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class SubmitToken extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        commandData.addOption(OptionType.STRING,"broadcasterid","The broadcaster ID to store with the key",true);
        commandData.addOption(OptionType.STRING,"accesstoken","The access token to store",true);
        commandData.addOption(OptionType.STRING,"refreshtoken","The refresh token to store",true);

        return commandData;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {

        event.deferReply(isEphemeral()).queue();

        Integer broadcasterID = Integer.parseInt(event.getOption("broadcasterid").getAsString());
        String accessToken = event.getOption("accesstoken").getAsString();
        String refreshToken = event.getOption("refreshtoken").getAsString();

        try {
            SecretKey key = EncryptionHandler.getKeyFromPassword(Config.get("ENCRYPTION_KEY"),Config.get("SALT"));
            IvParameterSpec accessTokenKey = EncryptionHandler.generateIv();

            String encryptedAccessToken = EncryptionHandler.encryptPasswordBased(accessToken,key,accessTokenKey);

            IvParameterSpec refreshTokenKey = EncryptionHandler.generateIv();
            String encryptedRefreshToken = EncryptionHandler.encryptPasswordBased(refreshToken,key,refreshTokenKey);

            HiveBot.database.insertCredential(broadcasterID,encryptedAccessToken,accessTokenKey,encryptedRefreshToken,refreshTokenKey);

            reply(event,"Success");
            return;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        reply(event,"Failure");

    }

    @Override
    public String getDescription() {
        return "Testing functions";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
