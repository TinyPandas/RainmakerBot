package panda.rainmaker.util;

import panda.rainmaker.entity.ReactionObject;

import java.util.*;

public class ChannelReactionCache {
    private static final Map<String, Set<String>> reactionsInChannels = new HashMap<>();

    public static Set<String> getReactionsForChannel(final String channelId) {
        return reactionsInChannels.getOrDefault(channelId, new HashSet<>());
    }

    public static void addReactionToChannel(final String channelId, final ReactionObject reaction) throws Exception {
        if (reaction == null) throw new Exception("No ReactionObject provided.");
        System.out.printf("Adding %s to %s%n", reaction, channelId);
        Set<String> reactionList = getReactionsForChannel(channelId);
        reactionList.add(reaction.getValue());
        if (reactionsInChannels.containsKey(channelId)) {
            reactionsInChannels.replace(channelId, reactionList);
        } else {
            reactionsInChannels.put(channelId, reactionList);
        }
    }

    public static void removeReactionFromChannel(final String channelId, final ReactionObject reaction) {
        if (reaction == null) {
            reactionsInChannels.remove(channelId);
        } else {
            Set<String> reactionList = getReactionsForChannel(channelId);
            reactionList.remove(reaction.getValue());

            if (reactionsInChannels.containsKey(channelId)) {
                reactionsInChannels.replace(channelId, reactionList);
            } else {
                reactionsInChannels.put(channelId, reactionList);
            }
        }
    }

    public static void removeInvalidReactionFromChannel(String channelId, String reaction) {
        Set<String> reactionList = getReactionsForChannel(channelId);
        reactionList.remove(reaction);

        if (reactionsInChannels.containsKey(channelId)) {
            reactionsInChannels.replace(channelId, reactionList);
        } else {
            reactionsInChannels.put(channelId, reactionList);
        }
    }
}
