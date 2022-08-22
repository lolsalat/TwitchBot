package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TwitchChatBot extends Thread {

    public static final String DEFAULT_HOST = "irc.chat.twitch.tv";
    public static final int DEFAULT_PORT = 6697;

    private final PrintStream out;
    private final BufferedReader in;
    public boolean debug = true;
    public final String username;
    public TwitchMessageHandler messageHandler;

    public TwitchChatBot(String username, String host, int port) throws IOException {
        this.username = username;
        this.messageHandler = new TwitchMessageHandler(this);

        // 0. SSL setup
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();

        // 1. connect to server
        SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
        socket.startHandshake();
        
        // 2. get input and output streams
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintStream(socket.getOutputStream());
    }

    public TwitchChatBot(String username) throws IOException {
        this(username, DEFAULT_HOST, DEFAULT_PORT);
    }

    private boolean isMessageSequence(String... types){
        for(String s : types){
            if(!this.readMessage().message.equals(s))
                return false;
        }
        return true;
    }

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

        // start callback thread
        this.setName("Callback Thread");
        this.start();

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

    public void sendMessage(String message){
        this.sendLinef("PRIVMSG #%s :%s", this.username.toLowerCase(), message);
    }

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

    public TwitchMessage readMessage(){
        return TwitchMessage.parse(readLine());
    }

    public void sendLinef(String line, Object... formatters) {
        this.sendLine(String.format(line, formatters));
    }

    public void sendLine(String line)  {
        if(this.debug)
            System.out.printf(">>> %s\n", line);
        out.println(line);
    }

}