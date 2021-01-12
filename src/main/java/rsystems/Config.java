package rsystems;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    public static Dotenv dotenv = Dotenv.load();

    public static void reload(){
        dotenv = Dotenv.load();
    }

    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }

}
