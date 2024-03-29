package panda.rainmaker.listeners;

import com.vdurmont.emoji.EmojiManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import panda.rainmaker.util.ChannelReactionCache;

import java.util.Set;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        if (event.getChannel().getType().equals(ChannelType.TEXT)) {
            TextChannel channel = event.getTextChannel();
            Set<String> reactions = ChannelReactionCache.getReactionsForChannel(channel.getId());

            if (reactions.size() > 0) {
                for (String reaction : reactions) {
                    boolean isEmoji = EmojiManager.isEmoji(reaction);

                    if (isEmoji) {
                        message.addReaction(reaction).queue();
                    } else {
                        Emote emote = guild.getEmoteById(reaction);
                        if (emote != null) {
                            message.addReaction(emote).queue();
                        } else {
                            ChannelReactionCache.removeInvalidReactionFromChannel(channel.getId(), reaction);
                        }
                    }
                }
            }
        }
    }
}
