package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.RoleGiverCache;

import static panda.rainmaker.util.PandaUtil.*;
import static panda.rainmaker.util.RoleGiverCache.removeRoleFromList;

public class RemoveRoleFromListCommand extends CommandObject {

    public RemoveRoleFromListCommand() {
        super("remove-role-from-list", "Remove a role from the specific list.");
        addOptionData(OptionDataDefs.LIST.asOptionData());
        addOptionData(OptionDataDefs.ROLE.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            String listName = getStringFromOption("List name", event.getOption("list"));
            RoleGiverCache.validateList(guildSettings, guild, listName);
            Role role = getRoleFromOption(event.getOption("role"));
            String removeRoleResult = removeRoleFromList(guildSettings, guild, listName, role);
            passEvent(event, removeRoleResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
