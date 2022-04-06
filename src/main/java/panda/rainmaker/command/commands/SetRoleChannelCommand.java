package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.RoleGiverCache;

import static panda.rainmaker.util.PandaUtil.*;

public class SetRoleChannelCommand extends CommandObject {

    public SetRoleChannelCommand() {
        super("set-role-channel", "Set the channel for all role associated messages");
        addOptionData(OptionDataDefs.CHANNEL.asOptionData().setChannelTypes(ChannelType.TEXT));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Member member = getMemberFromSlashCommandEvent(event);
            memberHasPermission(member, Permission.MANAGE_CHANNEL);
            TextChannel channel = getTextChannelFromOption(event.getOption("channel"));
            RoleGiverCache.setRoleChannelId(guildSettings, channel.getId());
            passEvent(event, "Successfully set the role channel to " + channel.getAsMention());
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
