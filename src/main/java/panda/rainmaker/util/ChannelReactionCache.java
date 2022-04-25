package panda.rainmaker.util;

import panda.rainmaker.entity.ReactionObject;

import java.util.*;

public class ChannelReactionCache {
    private static final Map<String, Set<String>> reactionsInChannels = new HashMap<>();

    public static Set<String> getReactionsForChannel(final String channelId) {
        return reactionsInChannels.getOrDefault(channelId, new HashSet<>());
    }

    public static boolean addReactionToChannel(final String channelId, final ReactionObject reaction) {
        if (reaction == null) return false;
        Set<String> reactionList = getReactionsForChannel(channelId);
        reactionList.add(reaction.getValue());
        if (reactionsInChannels.containsKey(channelId)) {
            reactionsInChannels.replace(channelId, reactionList);
        } else {
            reactionsInChannels.put(channelId, reactionList);
        }
        return true;
    }

    public static void removeReactionsInChannel(final String channelId) {
        reactionsInChannels.remove(channelId);
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
