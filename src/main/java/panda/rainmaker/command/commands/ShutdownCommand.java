package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;

public class ShutdownCommand extends CommandObject {

    public ShutdownCommand() {
        super("shutdown", "Safely shuts down the bot.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        event.getHook().editOriginal("Shutting down").queue(s -> event.getJDA().shutdownNow());
    }
}
