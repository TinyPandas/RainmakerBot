package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;

import static panda.rainmaker.util.PandaUtil.getGuildFromSlashCommandEvent;
import static panda.rainmaker.util.PandaUtil.getMemberFromSlashCommandEvent;

public class ViewConfigCommand extends CommandObject {

    public ViewConfigCommand() {
        super("view-config", "View the current config for this guild.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            Member actor = getMemberFromSlashCommandEvent(event);

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Config for " + guild.getName())
                    .setAuthor(actor.getEffectiveName());

            GuildSettings.FIELDS.forEach(field -> {
                String value = guildSettings.getValueForField(field);
                if (value == null) {
                    value = "\u200b";
                }

                builder.addField(field, value, true);
            });

            passEvent(event, "Displaying config.", builder.build());
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
