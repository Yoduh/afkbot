package afkbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Player {
    private String uniqueID;
    private String clientNick;
    private String ip;
    private String steamID;
    
    public Player(String uniqueID, String ip, String name) {
        this.uniqueID = uniqueID;
        this.ip = ip;
        this.clientNick = name;
        
        Properties prop = new Properties();
        InputStream input = null;
        prop = getProp(prop, input);
        String friendIDsString = prop.getProperty("friendIDs");
        String[] friendIDs = friendIDsString.split(",");
        
        this.setSteamID(findSteamID(this.uniqueID, friendIDs));
    }
    
    public String getID() {
        return uniqueID;
    }
    
    public String getName() {
        return clientNick;
    }
    
    public String getIP() {
        return ip;
    }
    
    public String getSteamID() {
        return steamID;
    }
    
    public void setName(String name) {
        clientNick = name;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public void setSteamID(String id) {
        steamID = id;
    }
    
    public String toString() {
        return "unique ID: " + this.uniqueID + ", IP address: " + this.ip + ", Name: " + this.clientNick
                + ", Steam ID: " + this.steamID;
    }
    
    public String findSteamID(String uniqueID, String[] friendIDs) {
        if(uniqueID.equals("CSP14EBY21aA5oYf9BpXyDMNJW0=")) {   //Mightymouse
            return friendIDs[0];
        } else if (uniqueID.equals("QzyXJN1EwNyk6GODe/MfW2wIHZk=")) {   //Yoduh
            return friendIDs[1];
        } else if (uniqueID.equals("PdaU7tWJQU2ClSmUHuLqPVhvzps=")) {   //Commander
            return friendIDs[2];
        } else if (uniqueID.equals("DtylwbG/exGONmcPUH4zhW/qMBg=")) {   //Baconman
            return friendIDs[3];
        }/* else if (uniqueID.equals("")) {   //Boo4
            return friendIDs[4];
        } else if (uniqueID.equals("")) {   //Donwon
            return friendIDs[5];
        } else if (uniqueID.equals("")) {   //Lamperkat
            return friendIDs[6];
        }*/
        return null;
    }
    
    public static Properties getProp(Properties prop, InputStream input) {
        try {
            String filename = "config.properties";
            input = Player.class.getClassLoader().getResourceAsStream(filename);
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
