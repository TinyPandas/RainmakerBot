package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.util.OptionDataDefs;

import java.util.stream.Collectors;

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

        EventData eventData = super.validate(event);
        String field = (String) eventData.getOption("field").getValue();
        String value = (String) eventData.getOption("value").getValue();

        if (!GuildSettings.FIELDS.contains(field))
            failEvent(event, String.format("%s is not a valid field.", field));

        passEvent(event, guildSettings.updateFieldWithValue(field, value));
    }
}
