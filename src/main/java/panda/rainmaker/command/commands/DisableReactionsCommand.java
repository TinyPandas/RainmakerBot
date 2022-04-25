package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.util.ChannelReactionCache;

import static panda.rainmaker.util.PandaUtil.getDisplayResultForRemoveReactionFromReactionObject;
import static panda.rainmaker.util.RoleGiverCache.getReactionCacheValue;

public class DisableReactionsCommand extends CommandObject {

    public DisableReactionsCommand() {
        super("disable-reactions", "Disable a reaction for each message in a channel.", true);
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

        EventData eventData = super.validate(event);
        Guild guild = eventData.getGuild();
        Member bot = eventData.getBot();
        TextChannel channel = (TextChannel) eventData.getOption("channel").getValue();
        String emote = (String) eventData.getOption("emote").getValue();
        ReactionObject reactionObject = getReactionCacheValue(guild, emote);
        ChannelReactionCache.removeReactionFromChannel(channel.getId(), reactionObject);
        String removedReactionDisplay = getDisplayResultForRemoveReactionFromReactionObject(guild, reactionObject);

        passEvent(event, String.format(removedReactionDisplay, channel.getAsMention()));
    }
}
