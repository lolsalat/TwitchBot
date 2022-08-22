package bot;

public interface TwitchBotListener {
    
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message);

    public void onPart(TwitchChatBot bot, TwitchMessage msg, String channel);

    public void onUnknownCommand(TwitchChatBot bot, TwitchMessage msg, String user, String command, String message);

    public void onClearChat(TwitchChatBot bot, TwitchMessage msg, String channel, String user);

    public void onClearMessage(TwitchChatBot bot, TwitchMessage msg, String channel, String message);

    public void onGlobalUserState(TwitchChatBot bot, TwitchMessage msg);

    public void onHostTarget(TwitchChatBot bot, TwitchMessage msg, String hostingChannel, String channel, int viewers);

    public void onNotice(TwitchChatBot bot, TwitchMessage msg, String notice);

    public void onReconnect(TwitchChatBot bot, TwitchMessage msg);

    public void onRoomState(TwitchChatBot bot, TwitchMessage msg, String channel);

    public void onUserNotice(TwitchChatBot bot, TwitchMessage msg, String channel, String message);

    public void onUserState(TwitchChatBot bot, TwitchMessage msg, String channel);

    public void onWhisper(TwitchChatBot bot, TwitchMessage msg, String user, String message);

    public void onCap(TwitchChatBot bot, TwitchMessage msg);

    public void onJoined(TwitchChatBot bot, TwitchMessage msg, String user, String channel);

}
