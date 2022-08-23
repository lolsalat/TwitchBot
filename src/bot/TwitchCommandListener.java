package bot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchCommandListener extends TwitchBotListenerStub {
    
    @FunctionalInterface
    public static interface CommandFunction {
        public void execute(TwitchChatBot bot, String command, String... args);
    }

    public static class CommandInfo {
        public CommandFunction command;
        public String name;
        public String usage;
        public String help;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Command {
        public String name();
        public String help();
        public String usage() default "";
    }

    public String prefix = "!";
    public Map<String, CommandInfo> commandsRegistry = new HashMap<>();

    public void parseCommands(Object obj){
        for(Method m : obj.getClass().getMethods()){
            if(m.isAnnotationPresent(Command.class)){
                Command annotation = m.getAnnotation(Command.class);

                CommandInfo info = new CommandInfo();
                info.command = (bot, command, args) -> {
                    try {
                        m.invoke(obj, bot, command, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };
                info.name = annotation.name();
                info.help = annotation.help();
                info.usage = annotation.usage();
                if(info.usage.isEmpty()){
                    info.usage = "!" + info.command;
                }
                System.out.printf("Adding command %s\n", info.name);
                this.commandsRegistry.put(info.name, info);
            }
        }
    }

    public void unknownCommand(TwitchChatBot bot, String command, String... args){
        bot.chatMessages.sendTextMessage(
            String.format("Unbekannter Befehl: %s", command));
    }

    public void runCommand(TwitchChatBot bot, String command, String... args){
        CommandInfo cmd = commandsRegistry.get(command);
        if(cmd != null) {
            cmd.command.execute(bot, command, args);
        } else {
            this.unknownCommand(bot, command, args);
        }
    }

    @Override
    public void onMessageSent(TwitchChatBot bot, TwitchMessage msg, String channel, String sender, String message) {
        if(!message.startsWith(this.prefix))
            return;

        if(message.contains(" ")){
            String command = message.substring(prefix.length(), message.indexOf(" "));
            
            List<String> arguments = new ArrayList<>();
            StringBuilder curArg = new StringBuilder();
            boolean quote = false;

            for(int i = prefix.length() + command.length() + 1; i < message.length(); i++){
                char c = message.charAt(i);
                switch(c){

                    case '"':
                        if(quote){
                            arguments.add(curArg.toString());
                            curArg = new StringBuilder();
                            quote = false;
                        } else {
                            quote = true;
                        }
                        break;

                    case ' ':
                        if(quote){
                            curArg.append(c);
                        } else {
                            arguments.add(curArg.toString());
                            curArg = new StringBuilder();
                        }
                        break;

                    default:
                        curArg.append(c);
                        break;
                }
            }
            if(!curArg.isEmpty()){
                arguments.add(curArg.toString());
            }
            this.runCommand(bot, command, arguments.toArray(String[]::new));
        } else {
            this.runCommand(bot, message.substring(prefix.length()));
        }
        
    }

}
