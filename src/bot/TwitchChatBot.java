package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.Function;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Most important class to do chat bot things. <br>
 * Most things are accessed through an instance of this class.
 */
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
    private final TwitchMessageHandler messageHandler;

    /**
     * Sends chat commands
     */
    public final TwitchChatCommands chatCommands;

    /*
     * Sends chat messages
     */
    public final TwitchChatMessages chatMessages;

    /**
     * Debug flag. <br>
     * Enable to dump all input and output to System.out.
     */
    public boolean debug;


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
        this.chatCommands = new TwitchChatCommands(this);
        this.chatMessages = new TwitchChatMessages(this);

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
    protected String readLine() {
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
    protected TwitchMessage readMessage(){
        return TwitchMessage.parse(readLine());
    }

    /**
     * You likely do not need this! <br>
     * Sends a line to {@link #out} after formatting it.
     * @param line Format string to send
     * @param formatters Values to apply to format string
     */
    protected void sendLinef(String line, Object... formatters) {
        this.sendLine(String.format(line, formatters));
    }

    /**
     * You likely do not need this! <br>
     * Sends a line to {@link #out}
     * @param line Line to send
     */
    protected void sendLine(String line)  {
        if(this.debug)
            System.out.printf(">>> %s\n", line);
        out.println(line);
    }


    /*
     * exposed methods
     */

    /**
     * You likely do not need this! <br>
     * @param message Message to send
     */
    public void sendMessage(TwitchMessage message){
        this.sendLine(message.toString());
    }

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

    /**
     * Adds a listener to the {@link #messageHandler}
     * @param listener Listener to add
     */
    public void addListener(TwitchBotListener listener){
        this.messageHandler.add(listener);
    }

    /**
     * Removes a listener from the {@link #messageHandler}
     * @param listener Listener to remove
     */
    public void removeListener(TwitchBotListener listener){
        this.messageHandler.remove(listener);
    }
}