package afkbot;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.*;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;

public class AFK{
    //my steam id: 76561197960442709
    
    public static void main(String[] args) throws SteamApiException {
        Properties prop = new Properties();
        InputStream input = null;
        prop = getProp(prop, input);
        String mykey = prop.getProperty("steamkey");
        String myid = prop.getProperty("mysteamid");
        
        SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder(mykey).build();
        GetFriendListRequest request = SteamWebApiRequestFactory.createGetFriendListRequest(myid);
        GetFriendList friendsList = client.<GetFriendList> processRequest(request);
        
        List steamIDs = new ArrayList();
        for( com.lukaspradel.steamapi.data.json.friendslist.Friend friend : friendsList.getFriendslist().getFriends() ) {
            System.out.println( friend.getSteamid() );
            steamIDs.add(friend.getSteamid());
        }
        
        int count = steamIDs.size();
        
        GetPlayerSummariesRequest r = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(steamIDs);
        GetPlayerSummaries sums = client.<GetPlayerSummaries> processRequest(r);
        for( com.lukaspradel.steamapi.data.json.playersummaries.Player player : sums.getResponse().getPlayers()) {
            System.out.println(player.getPersonaname() + ": " + player.getPersonastate());
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


