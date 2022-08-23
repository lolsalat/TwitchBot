package bot;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

/**
 * A message that can be sent to the Twitch chat IRC or was received from it. <br>
 * You usually don't have to worry about sending them.
 */
public class TwitchMessage {
    
    /**
     * Tag for message id
     */
    public static final String TAG_ID = "id";
    
    /**
     * Tag for user id
     */
    public static final String TAG_USER_ID = "user-id";

    // TODO: add more tag constants

    /**
     * Tags attached to this message. Empty if tags are not enabled
     */
    public Map<String,String> tags = new HashMap<>();


    /**
     * "Sender" of the message (not parsed, in a weird format)
     */
    public String sender;

    /**
     * Message type (e.g. JOIN or PRIVMSG)
     */
    public String message;

    /**
     * Message parameters
     */
    public String[] arguments;

    /**
     * Parses a TwitchMessage from a String
     * @param line String to parse
     * @return The parsed message
     */
    public static TwitchMessage parse(String line){
        if(line == null)
            return null;

        TwitchMessage message = new TwitchMessage();

        if(line.startsWith("@")){

            String[] tags = line.substring(1, line.indexOf(" ")).split(";");

            for(String tag : tags){
                String tagName = tag.substring(0, tag.indexOf("="));
                String tagValue = tag.substring(tag.indexOf("=") + 1);
                message.tags.put(tagName, tagValue);
            }

            line = line.substring(line.indexOf(" ") + 1);
        }

        String[] split = line.split(" ");


        if(split.length < 2)
            throw new RuntimeException(String.format("line '%s' is not a valid message", line));

        message.sender = split[0];
        message.message = split[1];

        // args = Anzahl der Argumente
        int argsCount;
        for(argsCount = 0; argsCount < split.length - 2; argsCount ++){
            if(split[1 + argsCount].startsWith(":")){
                break;
            }
        }

        // argumente parsen
        String[] args = new String[argsCount];
        System.arraycopy(split, 2, args, 0, argsCount);
        if(argsCount > 0){
            if(args[argsCount - 1].startsWith(":")){
                StringJoiner joiner = new StringJoiner(" ");

                // den : wegschmeißen
                joiner.add(args[argsCount - 1].substring(1));

                for(int i = argsCount + 2; i < split.length; i++){
                    joiner.add(split[i]);
                }

                args[argsCount - 1] = joiner.toString();
            }
        }
        message.arguments = args;

        return message;
    }

    /**
     * un-parses the message into a format that can be sent as IRC message.
     */
    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(this.sender);
        joiner.add(this.message);

        for(String s : this.arguments){
            if(s.contains(" "))
                s = ":" + s;
            joiner.add(s);
        }

        String tags = "";

        if(this.tags != null && !this.tags.isEmpty()){
            tags = "@";
            StringJoiner tagJoiner = new StringJoiner(";");
            for(Entry<String,String> tag : this.tags.entrySet()){
                tagJoiner.add(tag.getKey() + "=" + tag.getValue());
            }
        }

        return tags + joiner.toString();
    }

}
