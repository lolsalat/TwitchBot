package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.StringJoiner;
import java.awt.Color;
import java.util.Map.Entry;

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

        // request additional information
        this.sendLine("CAP REQ :twitch.tv/commands twitch.tv/tags twitch.tv/membership");

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
        this.sendMessage(Map.of(), message);
    }

    public void sendMessage(Map<String,String> tags, String message){

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

    public void sendCommand(String command, String... args){
        StringJoiner argStr = new StringJoiner(" ");
        for(String arg : args){
            argStr.add(arg);
        }
        this.sendLinef("PRIVMSG #%s :/%s%s", this.username, command, args.length != 0 ? " " + argStr.toString() : "");
    }

    public void banUser(String user){
        sendCommand("ban", user);
    }

    public void banUser(String user, String reason){
        sendCommand("ban", user, reason);
    }

    public void clear(){
        sendCommand("clear");
    }

    public void color(String color){
        sendCommand("color", color);
    }

    public void color(Color color){
        sendCommand("color", String.format("#%06X", color.getRGB() & 0xFFFFFF));
    }

    public void commercial(int time){
        sendCommand("commercial", Integer.toString(time));
    }

    public void delete(TwitchMessage message){
        this.delete(message.tags.get(TwitchMessage.TAG_ID));
    }

    public void delete(String messageId){
        sendCommand("delete", messageId);
    }

    public void disconnect(){
        sendCommand("disconnect");
    }

    public void emoteOnly(boolean enabled){
        sendCommand(enabled ? "emoteonly" : "emoteonlyoff");
    }

    public void followersOnly(boolean enabled){
        sendCommand(enabled ? "followers" : "follwersoff");
    }

    public void help(){
        sendCommand("help");
    }  

    public void help(String command){
        sendCommand("help", command);
    }

    public void host(String channel){
        sendCommand("host", channel);
    }

    public void unhost(String channel){
        sendCommand("unhost", channel);
    }

    public void marker(){
        sendCommand("marker");
    }

    public void marker(String description){
        sendCommand("marker", description);
    }

    public void me(String text){
        sendCommand("me", text);
    }

    public void mod(String user){
        sendCommand("mod", user);
    }

    public void unmod(String user){
        sendCommand("unmod", user);
    }

    public void mods(){
        // TODO: return things ;)
        sendCommand("mods");
    }

    public void raid(String channel){
        sendCommand("raid", channel);
    }

    public void unraid(){
        sendCommand("unraid");
    }

    public void slow(boolean enabled){
        sendCommand(enabled ? "slow" : "slowoff");
    }

    public void subscribers(boolean enabled){
        sendCommand(enabled ? "subscribers" : "subscribersoff");
    }

    public void timeout(String user){
        sendCommand("timeout", user);
    }

    public void timeout(String user, int timeout){
        sendCommand("timeout", user, Integer.toString(timeout));
    }

    public void untimeout(String user){
        sendCommand("untimeout", user);
    }

    public void uniquechat(boolean enabled){
        sendCommand(enabled ? "uniquechat" : "uniquechatoff");
    }

    public void vip(String user){
        sendCommand("vip", user);
    }

    public void unvip(String user){
        sendCommand("unvip", user);
    }

    public void vips(){
        // TODO: return something
        sendCommand("vips");
    }

    @Deprecated
    public void w(String user, String message){
        sendCommand("w", user, message);
    }

    public void sendLine(String line)  {
        if(this.debug)
            System.out.printf(">>> %s\n", line);
        out.println(line);
    }

}