package panda.rainmaker.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import panda.rainmaker.command.Commands;
import panda.rainmaker.database.GuildDao;
import panda.rainmaker.database.models.GuildSettings;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        String guildId = event.getGuild().getId();
        GuildSettings guildSettings = GuildDao.fetchGuildSettings(guildId);
        if (guildSettings == null) {
            guildSettings = GuildDao.loadDefaults(guildId);
        }

        Commands.getCommand(event.getName()).execute(event, guildSettings);
    }
}
