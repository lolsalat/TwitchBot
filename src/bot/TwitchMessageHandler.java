package bot;

import java.util.HashSet;
import java.util.Set;

public class TwitchMessageHandler {

    private final Set<TwitchBotListener> listeners;
    private final TwitchChatBot bot;

    public TwitchMessageHandler(TwitchChatBot bot){
        this.listeners = new HashSet<>();
        this.bot = bot;
    }

    public void add(TwitchBotListener listener){
        this.listeners.add(listener);
    }

    public void remove(TwitchBotListener listener){
        this.listeners.remove(listener);
    }

    public void handleMesssage(TwitchMessage message){
        String sender = message.sender;
        if(message.sender.contains("!")){
            sender = message.sender.substring(1, message.sender.indexOf("!"));
        }

        switch(message.message){

            case "421":
                listeners.forEach(x -> x.onUnknownCommand(bot, message, message.arguments[0], message.arguments[1], message.arguments[2]));
                break;

            case "PART":
                System.out.println(message);
                listeners.forEach(x -> x.onPart(bot, message, message.arguments[0]));
                break;

            case "PING":
                bot.sendLinef("PONG :%s", message.arguments[0]);
                break;

            case "PRIVMSG": {
                final String sender_ = sender;
                listeners.forEach(x -> x.onMessageSent(this.bot, message, message.arguments[0], sender_, message.arguments[1]));
                break;
            }

            default:
                System.out.printf("unknown message type '%s' for message '%s'\n", message.message, message);
                break;
        }
    }

}