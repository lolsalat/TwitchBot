package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.StringJoiner;
import java.awt.Color;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TwitchChatBot extends Thread {

    /*
     * Static Constants
     */

    /**
     * Default host for chat bot IRC
     */
    public static final String DEFAULT_HOST = "irc.chat.twitch.tv";

    /**
     * Default port for chat bot IRC (using SSL)
     */
    public static final int DEFAULT_PORT = 6697;


    /*
     * Fields
     */

    /**
     * Output (bot to Twitch chat IRC)
     */
    private final PrintStream out;

    /**
     * Input (Twitch chat IRC to bot)
     */
    private final BufferedReader in;

    /**
     * Username of the streamer
     */
    public final String username;

    /**
     * Parses and handles messages from the server
     */
    public final TwitchMessageHandler messageHandler;

    /**
     * Debug flag. <br>
     * Enable to dump all input and output to System.out.
     */
    public boolean debug = false;


    /*
     * Constructors
     */

    /**
     * All-args Constructor
     * @param username {@link #username}
     * @param host hostname of Twitch chat IRC
     * @param port port of Twitch chat IRC (with SSL)
     * @param messageHandlerConstructor Constructor to construct {@link #messageHandler} from bot instance
     * @throws IOException if something goes wrong while connecting to the IRC
     */
    public TwitchChatBot(String username, String host, int port, Function<TwitchChatBot, TwitchMessageHandler> messageHandlerConstructor) throws IOException {
        this.username = username;
        this.messageHandler = messageHandlerConstructor.apply(this);

        // 0. SSL setup
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();

        // 1. connect to server
        SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.startHandshake();
        
        // 2. get input and output streams
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintStream(socket.getOutputStream());

        // TODO: check SSL errors!
    }

    /**
     * Constructor
     * @param username {@link #username}
     * @throws IOException if something goes wrong while connecting to the IRC
     */
    public TwitchChatBot(String username) throws IOException {
        this(username, DEFAULT_HOST, DEFAULT_PORT, TwitchMessageHandler::new);
    }


    /*
     * Internal Methods
     */

    /**
     * Used internally to check for a response message sequence
     * @param types Expected types of responses
     * @return true if the sequence was found, false otherwise
     */
    private boolean isMessageSequence(String... types){
        for(String s : types){
            if(!this.readMessage().message.equals(s))
                return false;
        }
        return true;
    }

    @Override
    public void run(){
        System.out.println("Callback thread started!");
        while(true){
            TwitchMessage message = this.readMessage();

            if(message == null)
                break;

            this.messageHandler.handleMesssage(message);          
        }
    }

    /**
     * You likely do not need this! <br>
     * Reads a line from {@link #in}
     * @return The line read
     */
    public String readLine() {
        try {
            String line = in.readLine();
            if(debug){
                System.out.printf("<<< %s\n", line);
            }
            return line;
        } catch(IOException e){
            throw new RuntimeException("IOException", e);
        }
    }

    /**
     * You likely do not need this! <br>
     * Reads a line from {@link #in} and parses it into a {@link bot.TwitchMessage}
     * @return The parsed message
     */
    public TwitchMessage readMessage(){
        return TwitchMessage.parse(readLine());
    }

    /**
     * You likely do not need this! <br>
     * Sends a line to {@link #out} after formatting it.
     * @param line Format string to send
     * @param formatters Values to apply to format string
     */
    public void sendLinef(String line, Object... formatters) {
        this.sendLine(String.format(line, formatters));
    }

    /**
     * You likely do not need this! <br>
     * Sends a line to {@link #out}
     * @param line Line to send
     */
    public void sendLine(String line)  {
        if(this.debug)
            System.out.printf(">>> %s\n", line);
        out.println(line);
    }

    /**
     * You likely do not need this! <br>
     * @param message Message to send
     */
    public void sendMessage(TwitchMessage message){
        this.sendLine(message.toString());
    }


    /*
     * Starting the bot
     */

    /**
     * Starts the bot. <br>
     * This will start a callback thread which will deliver events to listeners.
     * @param token Access Token for Twitch Chat API
     * @return true on success, false otherwise
     */
    public boolean start(String token){
        this.sendLinef("PASS oauth:%s", token);
        this.sendLinef("NICK %s", this.username.toLowerCase());
        
        // TODO this may be wrong
        if(!this.isMessageSequence("001", "002", "003", "004", "375", "372", "376"))
            return false;
        
        this.sendLinef("JOIN #%s", this.username.toLowerCase()); 
        // response list for JOIN
        TwitchMessage msg;
        if(!this.isMessageSequence("JOIN"))
            return false;
        do{
            msg = this.readMessage();
        } while(msg.message.equals("353"));
        if(!msg.message.equals("366"))
            return false;

        // request additional information
        this.sendLine("CAP REQ :twitch.tv/commands twitch.tv/tags twitch.tv/membership");

        // start callback thread
        this.setName("Callback Thread");
        this.start();

        return true;
    }


    /*
     * Chat messages
     */

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

        this.sendLinef("%sPRIVMSG #%s :%s", prefix, this.username.toLowerCase(), message);
    }


    /*
     * Chat commands
     */

    /**
     * Send a chat command
     * @param command Command to send
     * @param args Arguments for the command
     */
    public void sendCommand(String command, String... args){
        StringJoiner argStr = new StringJoiner(" ");
        for(String arg : args){
            argStr.add(arg);
        }
        this.sendLinef("PRIVMSG #%s :/%s%s", this.username, command, args.length != 0 ? " " + argStr.toString() : "");
    }

    /**
     * Ban a user (/ban)
     * @param user User to ban
     */
    public void banUser(String user){
        this.sendCommand("ban", user);
    }

    /**
     * Ban a user (/ban)
     * @param user User to ban
     * @param reason Reason for banning the user
     */
    public void banUser(String user, String reason){
        this.sendCommand("ban", user, reason);
    }

    /**
     * Clear the chat (/clear)
     */
    public void clear(){
        this.sendCommand("clear");
    }

    /**
     * Change the chat bot color (/color)
     * @param color Color to set. Can either be a color ("red", "blue") or hexadecimal code ("#FF0000")
     */
    public void color(String color){
        this.sendCommand("color", color);
    }

    /**
     * Change the chat bot color (/color)
     * @param color Color to set
     */
    public void color(Color color){
        this.sendCommand("color", String.format("#%06X", color.getRGB() & 0xFFFFFF));
    }

    /**
     * Start a commercial (/commecial)
     * @param time Time in seconds. Must be 30, 60, 90, 120, 150, or 180
     */
    public void commercial(int time){
        this.sendCommand("commercial", Integer.toString(time));
    }

    /**
     * Start a 30 second commercial (/commercial)
     */
    public void commercial(){
        this.sendCommand("commercial");
    }
    
    /**
     * Delete a message
     * @param message Message to delete
     */
    public void delete(TwitchMessage message){
        this.delete(message.tags.get(TwitchMessage.TAG_ID));
    }

    /**
     * Delete a message
     * @param messageId Id of message to delete
     */
    public void delete(String messageId){
        this.sendCommand("delete", messageId);
    }

    /**
     * Disconnect the bot
     */
    public void disconnect(){
        this.sendCommand("disconnect");
    }

    /**
     * Enable / Disable emote only chat (/emoteonly /emoteonlyoff)
     * @param enabled True for enable, false for disable
     */
    public void emoteOnly(boolean enabled){
        this.sendCommand(enabled ? "emoteonly" : "emoteonlyoff");
    }


    /**
     * Enable / Disable followers only chat (/followers /followersoff)
     * @param enabled True for enable, false for disable
     */
    public void followersOnly(boolean enabled){
        this.sendCommand(enabled ? "followers" : "follwersoff");
    }

    /**
     * Send help command (/help)
     */
    public void help(){
        this.sendCommand("help");
    }  

    /**
     * Send help command for specific command (/help)
     */
    public void help(String command){
        this.sendCommand("help", command);
    }

    /**
     * Host another channel in this channel (/host)
     * @param channel Channel to host
     */
    public void host(String channel){
        this.sendCommand("host", channel);
    }

    /**
     * Stop hosting another channel in this channel (/unhost)
     * @param channel
     */
    public void unhost(String channel){
        this.sendCommand("unhost", channel);
    }

    /**
     * Set a marker (/marker)
     */
    public void marker(){
        this.sendCommand("marker");
    }

    /**
     * Set a marker (/marker)
     * @param description Description for the marker
     */
    public void marker(String description){
        this.sendCommand("marker", description);
    }

    /**
     * Send a status message (/me)
     * @param text Text of the status message
     */
    public void me(String text){
        this.sendCommand("me", text);
    }

    /**
     * Give mod privileges to a user (/mod)
     * @param user User to give mod to
     */
    public void mod(String user){
        this.sendCommand("mod", user);
    }

    /**
     * Remove mod privileges from a user (/unmod)
     * @param user User to remove mod from
     */
    public void unmod(String user){
        this.sendCommand("unmod", user);
    }

    /**
     * Get list of mods (/mods)
     */
    public void mods(){
        // TODO: return things ;)
        this.sendCommand("mods");
    }

    /**
     * Raid another channel (/raid)
     * @param channel Channel to raid
     */
    public void raid(String channel){
        this.sendCommand("raid", channel);
    }

    /**
     * Stop raiding another channel (/unraid)
     */
    public void unraid(){
        this.sendCommand("unraid");
    }

    /**
     * Enable / disable slow mode (/slow /slowoff)
     * @param enabled True to enable, false to disable
     */
    public void slow(boolean enabled){
        this.sendCommand(enabled ? "slow" : "slowoff");
    }

    /**
     * Enable / disable subscribers only mode (/subscribers /subscribersoff)
     * @param enabled True to enable, false to disable
     */
    public void subscribers(boolean enabled){
        this.sendCommand(enabled ? "subscribers" : "subscribersoff");
    }

    /**
     * Timeout a user (/timeout)
     * @param user User to timeout
     */
    public void timeout(String user){
        this.sendCommand("timeout", user);
    }

    /**
     * Timeout a user (/timeout)
     * @param user User to timeout
     * @param timeout Time until timeout automatically stops (probably in seconds)
     */
    public void timeout(String user, int timeout){
        this.sendCommand("timeout", user, Integer.toString(timeout));
    }

    /**
     * Remove timeout from user (/untimeout)
     * @param user User to remove timeout from
     */
    public void untimeout(String user){
        this.sendCommand("untimeout", user);
    }

    /**
     * Enable / disable unique chat (/uniquechat /uniquechatoff)
     * @param enabled True to enable, false to disable
     */
    public void uniquechat(boolean enabled){
        this.sendCommand(enabled ? "uniquechat" : "uniquechatoff");
    }

    /**
     * Grant user VIP status (/vip)
     * @param user User to grant VIP status to
     */
    public void vip(String user){
        this.sendCommand("vip", user);
    }

    /**
     * Revoke VIP status from user (/unvip)
     * @param user User to remove VIP status from
     */
    public void unvip(String user){
        this.sendCommand("unvip", user);
    }

    /**
     * Get VIPs (/vips)
     */
    public void vips(){
        // TODO: return something
        this.sendCommand("vips");
    }

    /**
     * Send Whisper message
     * @param user User to send message to
     * @param message Message to send
     */
    @Deprecated
    public void w(String user, String message){
        this.sendCommand("w", user, message);
    }

}