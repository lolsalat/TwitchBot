package main;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import bot.TwitchChatBot;

/**
 * Small example of what we can do with the bot
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // read bot token
        String token = Files.readString(Paths.get("token.txt"));
        // username (you need to change this ;) )
        String username = "lolsalat2";

        // create a bot instance
        TwitchChatBot bot = new TwitchChatBot(username);

        // add listener for events that does all the interesting stuff
        bot.addListener(new TestListener());

        // try to login
        if(bot.start(token)) {
            /* login ok */
            // change bot color to pink
            bot.chatCommands.color(new Color(0xFF, 0, 0xFF));
            // send a message in the chat
            // bot.chatMessages.sendTextMessage("Hallo, ich bin der Chat bot :)");
        } else {
            /* login failed */
            System.err.println("Login failed");
        }
    }

}