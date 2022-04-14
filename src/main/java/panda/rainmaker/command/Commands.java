package panda.rainmaker.command;

import panda.rainmaker.command.commands.*;
import panda.rainmaker.command.commands.ViewConfigCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    
    private static final HashMap<String, CommandObject> loadedCommands = new HashMap<>();
    
    static {
        System.out.println("Loading commands.");

        addCommandObject(new AddRoleToListCommand());
        addCommandObject(new ArticleCommand());
        // TODO addCommandObject(new CommandHistoryCommand());
        addCommandObject(new CreateRoleListCommand());
        addCommandObject(new DeleteRoleListCommand());
        addCommandObject(new DisableReactionsCommand());
        addCommandObject(new EnableReactionsCommand());
        addCommandObject(new ReportCommand());
        addCommandObject(new RemoveRoleFromListCommand());
        addCommandObject(new SetConfigValueCommand());
        addCommandObject(new ShutdownCommand());
        addCommandObject(new ViewConfigCommand());
        addCommandObject(new ViewPermissionsCommand());
        addCommandObject(new WikiCommand());

        // Loaded last so all commands exist in list.
        addCommandObject(new PermissionCommand());
    }

    private static void addCommandObject(CommandObject commandObject) {
        System.out.println("Loading " + commandObject.getName());
        loadedCommands.put(commandObject.getName(), commandObject);
    }

    public static List<String> getCommandNameList() {
        return new ArrayList<>(loadedCommands.keySet());
    }

    public static List<CommandObject> getCommands() {
        return new ArrayList<>(loadedCommands.values());
    }

    public static CommandObject getCommand(String name) {
        return loadedCommands.get(name);
    }
}
