package afkbot;

import java.util.*;
import java.util.logging.Level;
import java.io.IOException;
import java.io.InputStream;

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

	static public void main(String[] args) throws SteamApiException {
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
		        if(!users.equals("[]")) {	//no valid users if "[]"
		        	users = steamUsers(prop, users);
		        }
		        if(!users.equals("[]")) {	//no valid users if "[]"
		        	moveUsers(users, api);
		        }
		        Thread.sleep(300 * 1000);	//sleep 5 minutes then do it all again
	        } 
        } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
        query.exit();
	}
	
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
        for( com.lukaspradel.steamapi.data.json.playersummaries.Player player : sums.getResponse().getPlayers()) {
        	if(player.getPersonastate() != 2 && player.getPersonastate() != 3 && player.getPersonastate() != 0) {
        		for(Player u: users) {
        			if(u.getSteamID() != null && u.getSteamID().equals(player.getSteamid())) {
        				users.remove(u);
        				break;
        			}
        		}
        	}
        }
        
        return users;
    }
    
    public static void moveUsers(ArrayList<Player> users, TS3Api api) {
    	Channel AFKchan = api.getChannelByNameExact("AFK", false);
    	for(Player u: users) {
    		Date d = new Date();
    		api.moveClient(u.getClientID(), AFKchan.getId());
    		api.sendPrivateMessage(u.getClientID(), "ALERT: You were moved to the AFK channel after you were detected as idle on Steam. " + 
    		"If you manually set yourself to \"Away\" on purpose, you'll need to undo that to not get moved again.");
    	}
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