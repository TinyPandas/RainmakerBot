package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.util.OptionDataDefs;

import static panda.rainmaker.util.RoleGiverCache.isInvalidList;
import static panda.rainmaker.util.RoleGiverCache.removeRoleFromList;

public class RemoveRoleFromListCommand extends CommandObject {

    public RemoveRoleFromListCommand() {
        super("remove-role-from-list", "Remove a role from the specific list.", true);
        addOptionData(OptionDataDefs.LIST.asOptionData());
        addOptionData(OptionDataDefs.ROLE.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        EventData eventData = super.validate(event);
        Guild guild = eventData.getGuild();
        String listName = (String) eventData.getOption("list").getValue();
        Role role = (Role) eventData.getOption("role").getValue();

        if (isInvalidList(guildSettings, guild, listName)) {
            failEvent(event, String.format("List with name `%s` does not exist.", listName));
            return;
        }

        String removeResult = removeRoleFromList(guildSettings, guild, listName, role);
        passEvent(event, removeResult);
    }
}
