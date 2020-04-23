package rsystems.Handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import net.dv8tion.jda.api.entities.User;
import rsystems.UserModel;

import java.io.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class Jackson {

    //private static ObjectMapper objectMapper = new ObjectMapper();

    /*

    private static ObjectMapper getDefaultObjectMapper(){
        ObjectMapper defaultObjectMapper = new ObjectMapper();

        return defaultObjectMapper;
    }

    public static JsonNode parse(String src) throws IOException {
        return objectMapper.readTree(src);
    }

    */

    public static void readData() {

        String path = "data.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = new FileInputStream(new File(path));
            TypeReference<List<UserModel>> typeReference = new TypeReference<List<UserModel>>() {};
            List<UserModel> users = objectMapper.readValue(inputStream,typeReference);
            for(UserModel x:users){
                System.out.println(x);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
