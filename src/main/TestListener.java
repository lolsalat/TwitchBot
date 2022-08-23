package main;

import java.util.Map;

import bot.TwitchBotListener;
import bot.TwitchChatBot;
import bot.TwitchMessage;

public class TestListener  implements TwitchBotListener {

    public void runCommand(TwitchChatBot bot, TwitchMessage msg, String command, String arg){
        switch(command){

            case "github" : 
                bot.chatMessages.sendTextMessage("https://github.com/lolsalat/TwitchBot");
                break;

            case "answerme":
                bot.chatMessages.sendTextMessage(Map.of("reply-parent-msg-id", msg.tags.get("id"), "target-user-id", msg.tags.get("user-id")), "I answered you!");
                break;
            
        }
    }

    @Override
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message) {
        System.out.printf("[onMessageSent][%s][%s]: %s\n", channel, sender, message);
    
        if(message.startsWith("!")){

            String command = message.substring(1);
            String arg = "";
            
            if(command.contains(" ")) {
                arg = command.substring(command.indexOf(" ") + 1);
                command = command.substring(0, command.indexOf(" "));
            }

            this.runCommand(bot, msg, command, arg);
        }

    }

    @Override
    public void onPart(TwitchChatBot bot, TwitchMessage msg, String channel) {
        System.out.printf("[onPart]: %s\n", channel);

    }

    @Override
    public void onUnknownCommand(TwitchChatBot bot, TwitchMessage msg, String user, String command, String message) {
        System.out.printf("[onUnknownCommand][%s][%s]: %s\n", user, command, message);
    }

    @Override
    public void onClearChat(TwitchChatBot bot, TwitchMessage msg, String channel, String user) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onClearMessage(TwitchChatBot bot, TwitchMessage msg, String channel, String message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGlobalUserState(TwitchChatBot bot, TwitchMessage msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onHostTarget(TwitchChatBot bot, TwitchMessage msg, String hostingChannel, String channel, int viewers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNotice(TwitchChatBot bot, TwitchMessage msg, String notice) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onReconnect(TwitchChatBot bot, TwitchMessage msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRoomState(TwitchChatBot bot, TwitchMessage msg, String channel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserNotice(TwitchChatBot bot, TwitchMessage msg, String channel, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserState(TwitchChatBot bot, TwitchMessage msg, String channel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onWhisper(TwitchChatBot bot, TwitchMessage msg, String user, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCap(TwitchChatBot bot, TwitchMessage msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onJoined(TwitchChatBot bot, TwitchMessage msg, String user, String channel) {
        System.out.printf("User %s joined the chat!\n", user);
        bot.chatMessages.sendTextMessage(String.format("Willkommen im Stream %s!", user));
    }
    
}
