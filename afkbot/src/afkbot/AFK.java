package afkbot;

import java.util.*;
import java.util.logging.Level;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.*;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;

public class AFK {
	
	/**
	 * Main method. Sets up connection info for TS3 and runs all the things.
	 * @param args command line arguments (not used)
	 * @throws SteamApiException if Steam API messes up
	 * @throws IOException if logging functionality is enabled and messes up
	 */
	static public void main(String[] args) throws SteamApiException, IOException {
		//grab config.properties values
		Properties prop = new Properties();
        InputStream input = null;
        prop = getProp(prop, input);
        String serverip = prop.getProperty("serverip");
        String admin = prop.getProperty("login");
        String pw = prop.getProperty("password");
        
        //create TS3 bot and connect to TS3 with it
        final TS3Config config = new TS3Config();
        config.setHost(serverip);
        config.setDebugLevel(Level.ALL);
        final TS3Query query = new TS3Query(config);
        query.connect();
        final TS3Api api = query.getApi();
        api.login(admin, pw);
        api.selectVirtualServerById(1);
        api.setNickname("AFK Police");
        
        //do the dew
        try {
	        while(true) {
		        ArrayList<Player> users = new ArrayList<Player>();
		        users = TSUsers(users, api);
		        if(users.size() > 0) {
		        	users = steamUsers(prop, users);
		        }
		        System.out.println(users);
		        if(users.size() > 0) {
		        	moveUsers(users, api);
		        }
		        Thread.sleep(300 * 1000);	//sleep 5 minutes then do it all again
	        } 
        } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
        query.exit();
	}
	
	/**
	 * Gets a list of everyone connected to the TS3 server that isn't already in the AFK channel.
	 * @param users Initial blank list of users logged on the TS3 server
	 * @param api TS3 API object
	 * @return List of users logged on TS3
	 */
    static public ArrayList<Player> TSUsers(ArrayList<Player> users, TS3Api api) {
        List<Client> clients = api.getClients();
        Channel AFKchan = api.getChannelByNameExact("AFK", false);
        for(Client client : clients) {
        	if(client.getIp() != "" && client.getChannelId() != AFKchan.getId()) {	//don't care about users with no IP address (bot) or are already in AFK channel
        		Player user = new Player(client.getUniqueIdentifier(), client.getIp(), client.getNickname(), client.getChannelId(), client.getId());
        		users.add(user);
        	}
        }

        return users;
    }
	
    /**
     * Gets a list of all friends from my steam friend list that are also on TS3 and uses their current steam status to determine if they are AFK or not.
     * @param prop Property object for getting sensitive information from config.properties that I don't want published online
     * @param users List of everyone logged on the TS3 server
     * @return Same list of users logged on TS3 minus the same users on steam that are NOT away or snoozing
     * @throws SteamApiException if Steam API messes up
     */
    public static ArrayList<Player> steamUsers(Properties prop, ArrayList<Player> users) throws SteamApiException {
        String mykey = prop.getProperty("steamkey");
        String myid = prop.getProperty("mysteamid");
        
        SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder(mykey).build();
        GetFriendListRequest request = SteamWebApiRequestFactory.createGetFriendListRequest(myid);
        GetFriendList friendsList = client.<GetFriendList> processRequest(request);
        
        List<String> steamIDs = new ArrayList<String>();
        int x = 0;
        for(com.lukaspradel.steamapi.data.json.friendslist.Friend friend : friendsList.getFriendslist().getFriends() ) {
            for(Player user: users) {
            	if(user.getSteamID() != null && user.getSteamID().equals(friend.getSteamid())) {
            		steamIDs.add(friend.getSteamid());
            		x++;
            		break;
            	}
            }
            if(x >= users.size()) {
        		break;
        	}
        }
        
        //add myself to to steamIDs since I don't show up as a friend on my own friend list. Still make sure I'm on TS though
        for(Player user: users) {
        	if(x < users.size() && user.getID().equals((String) prop.getProperty("myTSid"))) {
        		steamIDs.add(myid);
        		break;
        	}
        }
        
        GetPlayerSummariesRequest r = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(steamIDs);
        GetPlayerSummaries sums = client.<GetPlayerSummaries> processRequest(r);
        System.out.println("status of all players on TS not in AFK channel:");
        for( com.lukaspradel.steamapi.data.json.playersummaries.Player player : sums.getResponse().getPlayers()) {
        		for(Player u: users) {
        			if(u.getSteamID() != null && u.getSteamID().equals(player.getSteamid())) {
        				u.setAFKstatus(player.getPersonastate());
        				u.setGame((String) player.getAdditionalProperties().get("gameextrainfo"));
        				System.out.println(u);
        				if((u.getAFKstatus() != 3 || (u.getGame() != null && u.getGame().equals("Rocket League"))) && u.getAFKstatus() != 4) {	//steam statuses that require moving to AFK channel
	        				users.remove(u);																		//Rocket League requires special rule (might go AFK with controller when you're not)
	        				break;
        				}
        			}
        		}
        }
        
        return users;
    }
    
    /**
     * Takes list of users to be moved and moves them to AFK channel. Logging functionality and private messaging
     *  on move can be commented or not to enable/disable that functionality.
     * @param users Users to be moved to AFK channel
     * @param api TS3 API object
     * @throws IOException if logging functionality can't find log.txt
     */
    public static void moveUsers(ArrayList<Player> users, TS3Api api) throws IOException {
    	//FileWriter output = new FileWriter(new File("log.txt"), true);
    	//Date d = new Date();
    	//String line = System.getProperty("line.separator");
    	Channel AFKchan = api.getChannelByNameExact("AFK", false);
    	//output.write(line + line + "Begin log: <" + d + ">" + line);
		//output.write("Users being moved:" + line);
    	for(Player u: users) {
    		//output.write(u + "" + line);
    		api.moveClient(u.getClientID(), AFKchan.getId());
    		//api.sendPrivateMessage(u.getClientID(), "ALERT: You were moved to the AFK channel after you were detected as idle on Steam. " + 
    		//"If you manually set yourself to \"Away\" on purpose, you'll need to undo that to not get moved again.");
    	}
    	//output.close();
    }
    
    /**
     * In charge of returning sensitive information from config.properties file
     * @param prop Properties object
     * @param input Initially blank input object for reading from config.properties
     * @return Whatever the requested property from config.properites was
     */
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