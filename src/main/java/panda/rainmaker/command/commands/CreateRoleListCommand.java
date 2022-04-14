package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.RoleGiverCache;

import static panda.rainmaker.util.PandaUtil.getGuildFromSlashCommandEvent;
import static panda.rainmaker.util.PandaUtil.getStringFromOption;

public class CreateRoleListCommand extends CommandObject {

    public CreateRoleListCommand() {
        super("create-role-list", "Create a new role list.", true);
        addOptionData(OptionDataDefs.LIST.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            String listName = getStringFromOption("List name", event.getOption("list"));
            String createResult = RoleGiverCache.createList(guildSettings,
                    getGuildFromSlashCommandEvent(event), listName);
            passEvent(event, createResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
