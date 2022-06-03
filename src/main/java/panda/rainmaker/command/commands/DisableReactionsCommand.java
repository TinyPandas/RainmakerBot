package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.util.ChannelReactionCache;

import static panda.rainmaker.util.PandaUtil.*;
import static panda.rainmaker.util.RoleGiverCache.getReactionCacheValue;

public class DisableReactionsCommand extends CommandObject {

    public DisableReactionsCommand() {
        super("disable-reactions", "Disable a reaction for each message in a channel.");
        addOptionData(new OptionData(OptionType.CHANNEL, "channel",
                "The channel to disable on.")
                .setRequired(true)
                .setChannelTypes(ChannelType.TEXT));
        addOptionData(new OptionData(OptionType.STRING, "emote",
                "The reaction to remove from the channel."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            Member selfMember = getSelfMemberFromGuild(guild);
            checkMemberPermission(selfMember, Permission.MESSAGE_ADD_REACTION);

            TextChannel channel = getTextChannelFromOption(event.getOption("channel"));
            ReactionObject reactionObject = getReactionCacheValue(
                    guild,
                    getStringFromOption("Emote", event.getOption("emote"))
            );
            ChannelReactionCache.removeReactionFromChannel(channel.getId(), reactionObject);
            passEvent(event, String.format(getDisplayResultForRemoveReactionFromReactionObject(guild, reactionObject),
                    channel.getAsMention()));
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
