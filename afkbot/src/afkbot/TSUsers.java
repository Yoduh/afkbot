package afkbot;

import java.util.Properties;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

public class TSUsers {
    static public void main(String[] args) {
        Properties prop = new Properties();
        InputStream input = null;
        prop = getProp(prop, input);
        String serverip = prop.getProperty("serverip");
        String admin = prop.getProperty("login");
        String pw = prop.getProperty("password");
        
        final TS3Config config = new TS3Config();
        config.setHost(serverip);
        config.setDebugLevel(Level.ALL);

        final TS3Query query = new TS3Query(config);
        query.connect();

        final TS3Api api = query.getApi();
        api.login(admin, pw);
        api.selectVirtualServerById(1);
        api.setNickname("PutPutBot");
        api.sendChannelMessage("PutPutBot is online!");
    }
    
    public static Properties getProp(Properties prop, InputStream input) {
        try {
            String filename = "config.properties";
            input = AFK.class.getClassLoader().getResourceAsStream(filename);
            if(input == null){
                System.out.println("Sorry, unable to find " + filename);
                return null;
            }

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}