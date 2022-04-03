package panda.rainmaker.command;

import panda.rainmaker.command.commands.DisableReactionsCommand;
import panda.rainmaker.command.commands.EnableReactionsCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    
    private static final HashMap<String, CommandObject> loadedCommands = new HashMap<>();
    
    static {
        System.out.println("Loading commands.");

        addCommandObject(new EnableReactionsCommand());
        addCommandObject(new DisableReactionsCommand());
    }

    private static void addCommandObject(CommandObject commandObject) {
        System.out.println("Loading " + commandObject.getName());
        loadedCommands.put(commandObject.getName(), commandObject);
    }
    
    public static List<CommandObject> getCommands() {
        return new ArrayList<>(loadedCommands.values());
    }

    public static CommandObject getCommand(String name) {
        return loadedCommands.get(name);
    }
}
