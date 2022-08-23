package bot;

import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

/**
 * This class interfaces with chat messages. <br>
 * Right now, it can only send chat messages.
 */
public class TwitchChatMessages {
    
    private final TwitchChatBot bot;

    protected TwitchChatMessages(TwitchChatBot bot){
        this.bot = bot;
    }

    /**
     * Sends a text message
     * @param message Message to send
     */
    public void sendTextMessage(String message){
        this.sendTextMessage(Map.of(), message);
    }

    /**
     * Sends a text messages with tags.
     * @param tags Tags to apply
     * @param message Message to send
     */
    public void sendTextMessage(Map<String,String> tags, String message){

        String prefix = "";

        if(!tags.isEmpty()){
            prefix = "@";

            StringJoiner tagsList = new StringJoiner(";");

            for(Entry<String,String> tag : tags.entrySet()){
                tagsList.add(tag.getKey() + "=" + tag.getValue());
            }

            prefix += tagsList + " ";
        }

        bot.sendLinef("%sPRIVMSG #%s :%s", prefix, bot.username.toLowerCase(), message);
    }

}
