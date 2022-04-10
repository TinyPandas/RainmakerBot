package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.command.Commands;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.PermissionMap;

import java.util.stream.Collectors;

import static panda.rainmaker.util.PandaUtil.*;

public class PermissionCommand extends CommandObject {

    public PermissionCommand() {
        super("permissions", "Updates permissions on a command.", true);
        addOptionData(OptionDataDefs.PERM_TARGET.asOptionData()
                .addChoices(
                        Commands.getCommands().stream()
                                .filter(CommandObject::getIsPermissible)
                                .map(commandObject -> new Command.Choice(commandObject.getName(), commandObject.getName()))
                                .collect(Collectors.toList())
                )
                .addChoice("permissions", "permissions"));
        addOptionData(OptionDataDefs.PERM_ACTION.asOptionData()
                .addChoice("add", "add")
                .addChoice("remove", "remove"));
        addOptionData(OptionDataDefs.PERM_ENTITY.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Member actor = getMemberFromSlashCommandEvent(event);
            PermissionMap permissionCommandPermissions = guildSettings.getPermissionsForCommand(this.getName());
            boolean hasPermission = memberHasPermission(actor, Permission.MANAGE_SERVER, permissionCommandPermissions);

            if (!hasPermission) throw new Exception("Missing permission(s).");

            String targetCommand = getStringFromOption("Command target", event.getOption("target"));
            String action = getStringFromOption("Permission action", event.getOption("action"));

            boolean toAdd = action.equals("add");

            OptionMapping entityOption = event.getOption("entity");
            if (entityOption == null) throw new Exception("Entity was not provided.");

            PermissionMap targetMap = guildSettings.getPermissionsForCommand(targetCommand);
            Member member = null;
            Role role = null;

            try {
                member = entityOption.getAsMember();
                role = entityOption.getAsRole();
            } catch (IllegalStateException ignored) {}

            targetMap.updatePermission(member, role, toAdd);

            passEvent(event,guildSettings.updatePermissionsForCommand(targetCommand, targetMap));
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
