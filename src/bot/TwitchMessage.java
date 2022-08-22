package bot;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class TwitchMessage {
    
    public static final String TAG_ID = "id", TAG_USER_ID = "user-id";

    public Map<String,String> tags;
    public String sender;
    public String message;
    public String[] arguments;

    public static TwitchMessage parse(String line){
        if(line == null)
            return null;

        TwitchMessage message = new TwitchMessage();
        message.tags = new HashMap<>();

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

        return joiner.toString();
    }

}
