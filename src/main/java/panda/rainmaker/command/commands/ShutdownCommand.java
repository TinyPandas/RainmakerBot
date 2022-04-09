package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;

import static panda.rainmaker.util.PandaUtil.getMemberFromSlashCommandEvent;

public class ShutdownCommand extends CommandObject {

    public ShutdownCommand() {
        super("shutdown", "Safely shuts down the bot.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();
        try {
            Member actor = getMemberFromSlashCommandEvent(event);
            if (!actor.getId().equals("169208961533345792")) throw new Exception("You do not have permission to do this.");

            event.getHook().editOriginal("Shutting down").queue(s -> event.getJDA().shutdownNow());
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
