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
    private int awayTime;
    private long TSidleTime;
    
    public Player(String uniqueID, String ip, String name, int channel, int id, long idle) {
        this.uniqueID = uniqueID;
        this.ip = ip;
        this.clientNick = name;
        this.chan = channel;
        this.clientID = id;
        awayTime = 0;
        this.TSidleTime = idle / 1000;
        
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
    
    public int getAwayTime() {
    	return awayTime;
    }
    
    public long getTSidleTime() {
    	return TSidleTime;
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
    	AFKstatus = id;
    	if(AFKstatus != 3 && AFKstatus != 4) {
    		awayTime = 0;
    	} else {
    		awayTime++;
    	}
    }
    
    public void setGame(String game) {
    	this.game = game;
    }
    
    public void setIdleTime(long time) {
    	this.TSidleTime = time / 1000;
    }
    
    public String toString() {
        return "Name: " + this.clientNick + ", unique ID: " + this.uniqueID + ", IP address: " + this.ip
                + ", Steam ID: " + this.steamID + ", in channel: " + this.chan + ", client ID: " + this.clientID
                + ", game: " + this.game + ", TS idle time: " + this.TSidleTime + ", AFK status: " + this.AFKstatus 
                + ", AFK timer: " + this.awayTime + "m";
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
        } else if (uniqueID.equals("oRst0MWRnV08gZUnyA3hjxSTShc=")) {   //Donwon
            return friendIDs[5];
        } else if (uniqueID.equals("LVdaLG2JY5vmXpW8a0LDwJ2I13Y=")) {   //Lamperkat
            return friendIDs[6];
        } else if (uniqueID.equals("hkbwUSBfVKer2Cxg8gGNWQ+anig=")) {   //Kingmonkey
            return friendIDs[7];
        }
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
