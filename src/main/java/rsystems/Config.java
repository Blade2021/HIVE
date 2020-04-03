package rsystems;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    public static final Dotenv dotenv = Dotenv.load();

    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }

}
