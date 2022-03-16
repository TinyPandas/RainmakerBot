package panda.rainmaker;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import panda.rainmaker.util.ChannelReactionCache;

import java.util.Set;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Message event.");
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        TextChannel channel = event.getTextChannel();
        Set<String> reactions = ChannelReactionCache.getReactionsForChannel(channel.getId());

        if (reactions.size() > 0) {
            for (String reaction : reactions) {
                Emote emote = guild.getEmoteById(reaction);
                if (emote != null) {
                    message.addReaction(emote).queue();
                } else {
                    System.out.println("Invalid emote");
                }
            }
        } else {
            System.out.printf("No reactions for channel %s%n", channel.getId());
        }
    }
}
