package bot;

public interface TwitchBotListener {
    
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message);

    public void onPart(TwitchChatBot bot, TwitchMessage msg, String channel);

    public void onUnknownCommand(TwitchChatBot bot, TwitchMessage msg, String user, String command, String message);

}
