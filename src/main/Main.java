package main;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import bot.TwitchChatBot;

public class Main {

    public static void main(String[] args) throws Exception {
        String token = Files.readString(Paths.get("token.txt"));
        String username = "lolsalat2";

        TwitchChatBot bot = new TwitchChatBot(username);

        bot.debug = false;
        bot.messageHandler.add(new TestListener());

        // login
        if(bot.start(token)) {
            bot.color(new Color(0xFF, 0, 0xFF));
            Thread.sleep(1000);
        } else {
            System.err.println("Login failed");
        }
    }

}