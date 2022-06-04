package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.RoleGiverCache;

import static panda.rainmaker.util.PandaUtil.getGuildFromSlashCommandEvent;
import static panda.rainmaker.util.PandaUtil.getStringFromOption;
import static panda.rainmaker.util.RoleGiverCache.updateRoleList;

public class UpdateRoleList extends CommandObject {

    public UpdateRoleList() {
        super("update-role-list", "Updates a role list, removing invalid roles.");
        addOptionData(OptionDataDefs.LIST.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            String listName = getStringFromOption("List name", event.getOption("list"));
            RoleGiverCache.validateList(guildSettings, guild, listName);
            String updateRoleListResult = updateRoleList(guildSettings, guild, listName);
            passEvent(event, updateRoleListResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
