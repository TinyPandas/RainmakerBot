package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.RoleGiverCache;

import static panda.rainmaker.util.PandaUtil.*;
import static panda.rainmaker.util.RoleGiverCache.addRoleToList;
import static panda.rainmaker.util.RoleGiverCache.getReactionCacheValue;

public class AddRoleToListCommand extends CommandObject {

    public AddRoleToListCommand() {
        super("add-role-to-list", "Add a role to the specified list.");
        addOptionData(OptionDataDefs.LIST.asOptionData());
        addOptionData(OptionDataDefs.ROLE.asOptionData());
        addOptionData(OptionDataDefs.EMOTE.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            String listName = getStringFromOption("List name", event.getOption("list"));
            RoleGiverCache.validateList(guildSettings, guild, listName);
            String reaction = getStringFromOption("Emote", event.getOption("emote"));
            ReactionObject reactionObject = getReactionCacheValue(guild, reaction);
            Role role = getRoleFromOption(event.getOption("role"));
            String addRoleResult = addRoleToList(guildSettings, guild, listName, role, reactionObject);
            passEvent(event, addRoleResult);
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
