package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.PermissionMap;

import java.util.stream.Collectors;

import static panda.rainmaker.util.PandaUtil.*;

public class SetConfigValueCommand extends CommandObject {

    public SetConfigValueCommand() {
        super("set-config-value", "Updates a config option", true);
        addOptionData(OptionDataDefs.CONFIG_FIELD.asOptionData()
                .addChoices(
                        GuildSettings.FIELDS.stream()
                                .map(field -> new Command.Choice(field, field))
                                .collect(Collectors.toList())
                ));
        addOptionData(OptionDataDefs.CONFIG_VALUE.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Member actor = getMemberFromSlashCommandEvent(event);
            PermissionMap permissionCommandPermissions = guildSettings.getPermissionsForCommand(this.getName());
            boolean hasPermission = memberHasPermission(actor, Permission.MANAGE_SERVER, permissionCommandPermissions);

            if (!hasPermission) throw new Exception("Missing permission(s).");
            String field = getStringFromOption("Config field", event.getOption("field"));
            String value = getStringFromOption("Config value", event.getOption("value"));

            if (!GuildSettings.FIELDS.contains(field)) throw new Exception(field + " is not a valid config field.");
            passEvent(event, guildSettings.updateFieldWithValue(field, value));
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
