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
import panda.rainmaker.entity.EventData;
import panda.rainmaker.entity.ReactionObject;

import static panda.rainmaker.util.ChannelReactionCache.addReactionToChannel;
import static panda.rainmaker.util.PandaUtil.memberHasPermission;
import static panda.rainmaker.util.RoleGiverCache.getReactionCacheValue;

public class EnableReactionsCommand extends CommandObject {

    public EnableReactionsCommand() {
        super("enable-reactions", "Enable a reaction for each message in a channel.", true);
        addOptionData(new OptionData(OptionType.CHANNEL, "channel",
                "The channel to enable on.")
                .setRequired(true)
                .setChannelTypes(ChannelType.TEXT));
        addOptionData(new OptionData(OptionType.STRING, "emote",
                "The reaction to add to messages.")
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        EventData eventData = super.validate(event);
        Guild guild = eventData.getGuild();
        Member bot = eventData.getBot();
        if (!memberHasPermission(bot, Permission.MESSAGE_ADD_REACTION))
            failEvent(event, "Bot missing permission: " + Permission.MESSAGE_ADD_REACTION);

        TextChannel channel = (TextChannel) eventData.getOption("channel").getValue();
        String emote = (String) eventData.getOption("emote").getValue();
        ReactionObject reactionObject = getReactionCacheValue(guild, emote);
        boolean result = addReactionToChannel(channel.getId(), reactionObject);
        if (result) {
            passEvent(event, String.format("Successfully enabled %s in %s.",
                    reactionObject.getDisplay(guild), channel.getAsMention()));
        } else {
            passEvent(event, String.format("Failed to enable %s in %s.",
                    emote, channel.getAsMention()));
        }
    }
}
