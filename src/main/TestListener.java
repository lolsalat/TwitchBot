package main;

import bot.TwitchBotListener;
import bot.TwitchChatBot;
import bot.TwitchMessage;

public class TestListener  implements TwitchBotListener {

    public void runCommand(TwitchChatBot bot, String command, String arg){
        switch(command){

            case "github" : 
                bot.sendMessage("https://github.com/lolsalat/TwitchBot");
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

            this.runCommand(bot, command, arg);
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
    
}
