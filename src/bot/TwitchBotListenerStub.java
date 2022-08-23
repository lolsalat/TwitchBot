package bot;

public class TwitchBotListenerStub implements TwitchBotListener {

    @Override
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message) {
        // empty
    }

    @Override
    public void onPart(TwitchChatBot bot, TwitchMessage msg, String channel) {
        // empty
    }

    @Override
    public void onUnknownCommand(TwitchChatBot bot, TwitchMessage msg, String user, String command, String message) {
        // empty
    }

    @Override
    public void onClearChat(TwitchChatBot bot, TwitchMessage msg, String channel, String user) {
        // empty
    }

    @Override
    public void onClearMessage(TwitchChatBot bot, TwitchMessage msg, String channel, String message) {
        // empty
    }

    @Override
    public void onGlobalUserState(TwitchChatBot bot, TwitchMessage msg) {
        // empty
    }

    @Override
    public void onHostTarget(TwitchChatBot bot, TwitchMessage msg, String hostingChannel, String channel, int viewers) {
        // empty
    }

    @Override
    public void onNotice(TwitchChatBot bot, TwitchMessage msg, String notice) {
        // empty
    }

    @Override
    public void onReconnect(TwitchChatBot bot, TwitchMessage msg) {
        // empty
    }

    @Override
    public void onRoomState(TwitchChatBot bot, TwitchMessage msg, String channel) {
        // empty
    }

    @Override
    public void onUserNotice(TwitchChatBot bot, TwitchMessage msg, String channel, String message) {
        // empty
    }

    @Override
    public void onUserState(TwitchChatBot bot, TwitchMessage msg, String channel) {
        // empty
    }

    @Override
    public void onWhisper(TwitchChatBot bot, TwitchMessage msg, String user, String message) {
        // empty
    }

    @Override
    public void onCap(TwitchChatBot bot, TwitchMessage msg) {
        // empty
    }

    @Override
    public void onJoined(TwitchChatBot bot, TwitchMessage msg, String user, String channel) {
        // empty
    }
    
}
