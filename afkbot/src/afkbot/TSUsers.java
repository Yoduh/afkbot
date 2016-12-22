package afkbot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

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
        api.setNickname("AFKBot");
        //api.sendChannelMessage("AFKBot is online and ready to kick ass!");
        List<Client> clients = api.getClients();
        ArrayList<Player> users = new ArrayList<Player>();
        for(Client client : clients) {
            Player user = new Player(client.getUniqueIdentifier(), client.getIp(), client.getNickname());
            users.add(user);
        }
        for(Player user: users) {
            System.out.println(user);
        }
        
        /*
        int x = 0;
        try {
            while (x < 3) {
                api.sendChannelMessage("test " + x);
                Thread.sleep(5 * 1000);
                x++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        query.exit();
    }
    
    public static Properties getProp(Properties prop, InputStream input) {
        try {
            String filename = "config.properties";
            input = TSUsers.class.getClassLoader().getResourceAsStream(filename);
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