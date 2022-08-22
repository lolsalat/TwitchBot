package main;

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
            Thread.sleep(1000);
        } else {
            System.err.println("Login failed");
        }
    }

}