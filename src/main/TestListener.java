package main;


import bot.TwitchChatBot;
import bot.TwitchCommandListener;
import bot.TwitchMessage;

/**
 * Example Bot that does some basic things
 */
public class TestListener  extends TwitchCommandListener {

    public TestListener(){
        super();
        this.parseCommands(this);
   }

    @Command(name="github", help="gibt den Github link aus")
    public void githubCommand(TwitchChatBot bot, String command, String... arguments){
        bot.chatMessages.sendTextMessage("https://github.com/lolsalat/");
    }

    @Command(name = "help", usage="!help [command]", help="gibt diese hilfe aus")
    public void helpCommand(TwitchChatBot bot, String command, String... arguments){
        if(arguments.length == 0){
            bot.chatMessages.sendTextMessage("Kommandos:");
            for(CommandInfo cmd : this.commandsRegistry.values()){
                bot.chatMessages.sendTextMessage("-> " + cmd.usage + ": " + cmd.help);
            }
        } else {
            for(String cmd : arguments){
                CommandInfo info = this.commandsRegistry.get(cmd);
                if(info != null){
                    bot.chatMessages.sendTextMessage("-> " + info.usage + ": " + info.help);
                } else {
                    bot.chatMessages.sendTextMessage("der Befehl '" + cmd + "' existiert nicht!");
                }
            }
        }
    }

    @Command(name="echo", usage="!echo <text>", help="der Bot antwortet mit deiner Nachricht")
    public void echoCommand(TwitchChatBot bot, String command, String... arguments){
        if(arguments.length == 0){
            bot.chatMessages.sendTextMessage("ich kann keine Leere Nachricht zurückgeben!");
        } else {
           bot.chatMessages.sendTextMessage("->" + arguments[0]);
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
    public void onJoined(TwitchChatBot bot, TwitchMessage msg, String user, String channel) {
        System.out.printf("User %s joined the chat!\n", user);
        // bot.chatMessages.sendTextMessage(String.format("Willkommen im Stream %s!", user));
    }
    
}
