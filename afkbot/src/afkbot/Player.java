package afkbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Player {
    private String uniqueID;
    private String clientNick;
    private String ip;
    private String steamID;
    private String game;
    private int chan;
    private int clientID;
    private int AFKstatus;
    
    public Player(String uniqueID, String ip, String name, int channel, int id) {
        this.uniqueID = uniqueID;
        this.ip = ip;
        this.clientNick = name;
        this.chan = channel;
        this.clientID = id;
        
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
    
    public String getGame() {
    	return game;
    }
    
    public int getChannel() {
    	return chan;
    }
    
    public int getClientID() {
    	return clientID;
    }
    
    public int getAFKstatus() {
    	return AFKstatus;
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
    
    public void setChannel(int chan) {
    	this.chan = chan;
    }
    
    public void setClientID(int id) {
    	this.clientID = id;
    }
    
    public void setAFKstatus(int id) {
    	this.AFKstatus = id;
    }
    
    public void setGame(String game) {
    	this.game = game;
    }
    
    public String toString() {
        return "unique ID: " + this.uniqueID + ", IP address: " + this.ip + ", Name: " + this.clientNick
                + ", Steam ID: " + this.steamID + ", in channel: " + this.chan + ", client ID: " + this.clientID
                + ", game: " + this.game + ", AFK status: " + this.AFKstatus;
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
        } else if (uniqueID.equals("57iTVDUxiF4MDIYPSh/WG0FYXxM=")) {   //Boo4
            return friendIDs[4];
        } /*else if (uniqueID.equals("")) {   //Donwon
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
