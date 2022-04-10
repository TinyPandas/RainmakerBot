package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.PermissionMap;

import static panda.rainmaker.util.PandaUtil.*;
import static panda.rainmaker.util.RoleGiverCache.deleteRoleList;

public class DeleteRoleListCommand extends CommandObject {

    public DeleteRoleListCommand() {
        super("delete-role-list", "Deletes a role list.", true);
        addOptionData(OptionDataDefs.LIST.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            Member member = getMemberFromSlashCommandEvent(event);
            PermissionMap permissionCommandPermissions = guildSettings.getPermissionsForCommand(this.getName());
            boolean hasPermission = memberHasPermission(member, Permission.MANAGE_ROLES, permissionCommandPermissions);

            if (!hasPermission) throw new Exception("Missing permission(s).");
            String listName = getStringFromOption("List name", event.getOption("list"));
            String deleteResult = deleteRoleList(guildSettings, guild, listName);
            passEvent(event, deleteResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
