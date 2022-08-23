package bot;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for handling IRC messages received on the Twitch chat IRC. <br>
 * You likely don't have to ever use any of this directly.
 */
public class TwitchMessageHandler {

    /**
     * Listeners for parsed events
     */
    private final Set<TwitchBotListener> listeners;

    /**
     * Bot instance
     */
    private final TwitchChatBot bot;

    /**
     * Constructor
     * @param bot {@link #bot}
     */
    public TwitchMessageHandler(TwitchChatBot bot){
        this.listeners = new HashSet<>();
        this.bot = bot;
    }

    /**
     * Add a listener to {@link #listeners}
     * @param listener Listener to add
     */
    protected void add(TwitchBotListener listener){
        this.listeners.add(listener);
    }

    /**
     * Remove a listener from {@link #listeners}
     * @param listener Listener to remove
     */
    protected void remove(TwitchBotListener listener){
        this.listeners.remove(listener);
    }

    /**
     * Handle a TwitchMessage and call listeners if necessary
     * @param message Message to handle
     */
    protected void handleMesssage(TwitchMessage message){
        if(message.sender.equals("PING")){
            bot.sendLinef("PONG :%s", message.message);
            return;
        }

        String sender = message.sender;
        if(message.sender.contains("!")){
            sender = message.sender.substring(1, message.sender.indexOf("!"));
        }

        switch(message.message){

            case "WHISPER":
                listeners.forEach(x -> x.onWhisper(bot, message, message.arguments[0], message.arguments[1]));
                break;

            case "USERSTATE":
                listeners.forEach(x -> x.onUserState(bot, message, message.arguments[0]));
                break;

            case "USERNOTICE":
                String text = message.arguments.length == 1 ? "" : message.arguments[1];
                listeners.forEach(x -> x.onUserNotice(bot, message, message.arguments[0], text));
                break;

            case "ROOMSTATE":
                listeners.forEach(x -> x.onRoomState(bot, message, message.arguments[0]));
                break;  
                
            case "RECONNECT":
                listeners.forEach(x -> x.onReconnect(bot, message));
                break;                

            case "NOTICE":
                listeners.forEach(x -> x.onNotice(bot, message, message.arguments[0]));
                break;

            case "HOSTTARGET":
                listeners.forEach(x -> x.onHostTarget(bot, message, message.arguments[0], message.arguments[1], Integer.parseInt(message.arguments[2])));
                break;

            case "GLOBALUSERSTATE":
                listeners.forEach(x -> x.onGlobalUserState(bot, message));
                break;

            case "CLEARMSG":
                listeners.forEach(x -> x.onClearMessage(bot, message, message.arguments[0], message.arguments[1]));
                break;

            case "CLEARCHAT":
                listeners.forEach(x -> x.onClearChat(bot, message, message.arguments[0], message.arguments[1]));
                break;

            case "CAP":
                listeners.forEach(x -> x.onCap(bot, message));
                break;

            case "421":
                listeners.forEach(x -> x.onUnknownCommand(bot, message, message.arguments[0], message.arguments[1], message.arguments[2]));
                break;

            case "PART":
                System.out.println(message);
                listeners.forEach(x -> x.onPart(bot, message, message.arguments[0]));
                break;

            case "PRIVMSG": {
                final String sender_ = sender;
                listeners.forEach(x -> x.onMessageSent(this.bot, message, message.arguments[0], sender_, message.arguments[1]));
                break;
            }

            case "JOIN": {
                final String sender_ = sender;
                listeners.forEach(x -> x.onJoined(this.bot, message, sender_, message.arguments[0]));
                break;
            }

            default:
                System.out.printf("unknown message type '%s' for message '%s'\n", message.message, message);
                break;
        }
    }

}