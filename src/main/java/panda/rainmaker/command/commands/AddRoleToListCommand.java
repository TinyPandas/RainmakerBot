package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.util.OptionDataDefs;

import static panda.rainmaker.util.RoleGiverCache.*;

public class AddRoleToListCommand extends CommandObject {

    public AddRoleToListCommand() {
        super("add-role-to-list", "Add a role to the specified list.", true);
        addOptionData(OptionDataDefs.LIST.asOptionData());
        addOptionData(OptionDataDefs.ROLE.asOptionData());
        addOptionData(OptionDataDefs.EMOTE.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        EventData eventData = super.validate(event);
        Guild guild = eventData.getGuild();
        String listName = (String) eventData.getOption("list").getValue();
        String reaction = (String) eventData.getOption("emote").getValue();
        Role role = (Role) eventData.getOption("role").getValue();
        ReactionObject reactionObject = getReactionCacheValue(guild, reaction);

        if (isInvalidList(guildSettings, guild, listName)) {
            failEvent(event, String.format("List with name `%s` does not exist.", listName));
            return;
        }

        String addResult = addRoleToList(guildSettings, guild, listName, role, reactionObject);
        passEvent(event, addResult);
    }
}
