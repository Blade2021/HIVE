package rsystems.commands.debug;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.EncryptionHandler;
import rsystems.objects.Reference;

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
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Test extends Command {

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        try {
            SecretKey key = EncryptionHandler.getKeyFromPassword(Config.get("ENCRYPTION_KEY"),Config.get("SALT"));
            IvParameterSpec spec = EncryptionHandler.generateIv();
            System.out.println(spec);

            String newSTring = EncryptionHandler.encryptPasswordBased(content,key,spec);
            HiveBot.database.insertToken(newSTring,spec);

            reply(event,newSTring);

            String decryptSTring = EncryptionHandler.decryptPasswordBased(newSTring,key,spec);
            System.out.println(decryptSTring);

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
        }

    }

    @Override
    public String getHelp() {
        return "Testing command";
    }
}
