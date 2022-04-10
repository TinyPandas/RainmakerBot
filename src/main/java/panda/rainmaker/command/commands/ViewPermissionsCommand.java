package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.PermissionMap;

import java.util.stream.Collectors;

import static panda.rainmaker.util.PandaUtil.*;

public class ViewPermissionsCommand extends CommandObject {

    public ViewPermissionsCommand() {
        super("view-permissions", "View the current assigned permissions.", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            Member actor = getMemberFromSlashCommandEvent(event);
            PermissionMap permissionCommandPermissions = guildSettings.getPermissionsForCommand(this.getName());
            boolean hasPermission = memberHasPermission(actor, Permission.MANAGE_SERVER, permissionCommandPermissions);

            if (!hasPermission) throw new Exception("Missing permission(s).");
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Permissions for " + guild.getName())
                    .setAuthor(actor.getEffectiveName());

            guildSettings.getCommandPermissions().forEach((cmdName, permMap) -> {
                String allowedRoleList = permMap.getAllowedRoles().stream()
                        .map(roleId -> String.format("<@&%s>", roleId))
                        .collect(Collectors.joining("\n"));

                String allowedMemberList = permMap.getAllowedUsers().stream()
                        .map(memId -> String.format("<@%s>", memId))
                        .collect(Collectors.joining("\n"));

                String disallowedRoleList = permMap.getDisallowedRoles().stream()
                        .map(roleId -> String.format("<@&%s>", roleId))
                        .collect(Collectors.joining("\n"));

                String disallowedMemberList = permMap.getDisallowedUsers().stream()
                        .map(memId -> String.format("<@%s>", memId))
                        .collect(Collectors.joining("\n"));

                int count = 3;

                if (allowedRoleList.length() > 0 || allowedMemberList.length() > 0) {
                    String allowedList = String.format("%s%n%s", allowedRoleList, allowedMemberList);
                    builder.addField(
                            String.format("Allowed to use %s", cmdName),
                            allowedList,
                            true
                    );
                    count--;
                }

                if (disallowedRoleList.length() > 0 || disallowedMemberList.length() > 0) {
                    String allowedList = String.format("%s%n%s", disallowedRoleList, disallowedMemberList);
                    builder.addField(
                            String.format("Allowed to use %s", cmdName),
                            allowedList,
                            true
                    );
                    count--;
                }

                if (count != 3) {
                    for (int i = 0; i < count; i++) {
                        builder.addBlankField(true);
                    }
                }
            });

            passEvent(event, "Displaying permissions.", builder.build());
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
