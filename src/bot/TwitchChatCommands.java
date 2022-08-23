package bot;

import java.util.StringJoiner;
import java.awt.Color;

/**
 * The sole use of this class is to send chat commands.
 */
public class TwitchChatCommands {

    private final TwitchChatBot bot;

    protected TwitchChatCommands(TwitchChatBot bot){
        this.bot = bot;
    }

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
        bot.sendLinef("PRIVMSG #%s :/%s%s", bot.username, command, args.length != 0 ? " " + argStr.toString() : "");
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