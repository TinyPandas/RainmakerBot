package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;

import static panda.rainmaker.util.PandaUtil.getGuildFromSlashCommandEvent;
import static panda.rainmaker.util.PandaUtil.getStringFromOption;
import static panda.rainmaker.util.RoleGiverCache.deleteRoleList;

public class DeleteRoleListCommand extends CommandObject {

    public DeleteRoleListCommand() {
        super("delete-role-list", "Deletes a role list.");
        addOptionData(OptionDataDefs.LIST.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            String listName = getStringFromOption("List name", event.getOption("list"));
            String deleteResult = deleteRoleList(guildSettings, guild, listName);
            passEvent(event, deleteResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
