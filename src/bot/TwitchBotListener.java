package bot;

public interface TwitchBotListener {
    
    /**
     * Called whenever a chat message is sent
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel the message was sent in
     * @param sender Sender of the message
     * @param message Content of the message
     */
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message);

    /**
     * Called whenever a user leaves the channel (I think)
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel channel that was left
     */
    public void onPart(TwitchChatBot bot, TwitchMessage msg, String channel);

    /**
     * Called whenever an Unknown Command message is received
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param user User that sent the unknown command
     * @param command Command that was sent
     * @param message Additional message ("Unknown Command")
     */
    public void onUnknownCommand(TwitchChatBot bot, TwitchMessage msg, String user, String command, String message);

    /**
     * Called whenever the chat is cleared
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel that is cleared 
     * @param user User to clear messages from
     */
    public void onClearChat(TwitchChatBot bot, TwitchMessage msg, String channel, String user);

    /**
     * Called whenever a message is removed
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel the message is in 
     * @param message Content of the message
     */
    public void onClearMessage(TwitchChatBot bot, TwitchMessage msg, String channel, String message);

    /**
     * Appearently called after Bot login
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     */
    public void onGlobalUserState(TwitchChatBot bot, TwitchMessage msg);

    /**
     * Called when another channel is hosted in the channel
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param hostingChannel Channel that hosts another channel
     * @param channel Channel that is hostet
     * @param viewers Amount of viewers
     */
    public void onHostTarget(TwitchChatBot bot, TwitchMessage msg, String hostingChannel, String channel, int viewers);

    /**
     * Called whenever a notice is received
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param notice Content of the notice
     */
    public void onNotice(TwitchChatBot bot, TwitchMessage msg, String notice);

    /**
     * Called whenever the Twitch API goes down for maintenance and is about to disconnect the bot
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     */
    public void onReconnect(TwitchChatBot bot, TwitchMessage msg);

    /**
     * I don't know when exactly this happens
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel this event occured in
     */
    public void onRoomState(TwitchChatBot bot, TwitchMessage msg, String channel);

    /**
     * Called whenever a notice is sent to a user 
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel the user is in
     * @param message Content of the notice
     */
    public void onUserNotice(TwitchChatBot bot, TwitchMessage msg, String channel, String message);

    /**
     * I don't know when exactly this happens
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param channel Channel this event occured in
     */
    public void onUserState(TwitchChatBot bot, TwitchMessage msg, String channel);

    /**
     * Called whenever someone whispers to the bot
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param user User that sent the whisper message
     * @param message Content of the whisper message
     */
    public void onWhisper(TwitchChatBot bot, TwitchMessage msg, String user, String message);

    /**
     * Called whenever capabilities are updated (usually no need to worry about this)
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     */
    public void onCap(TwitchChatBot bot, TwitchMessage msg);

    /**
     * Called whenever someone joins the channel. <br>
     * Sent with a bit of delay and on restart, events for already in-channel users are received (but I don't know whether for all or just some of them)
     * @param bot Instance of the {@link bot.TwitchChatBot}
     * @param msg Received {@link bot.TwitchMessage} that triggered this event
     * @param user User that joined
     * @param channel Channel the user joined
     */
    public void onJoined(TwitchChatBot bot, TwitchMessage msg, String user, String channel);

}
