package panda.rainmaker.util;

import java.util.*;

public class ChannelReactionCache {
    private static final Map<String, Set<String>> reactionsInChannels = new HashMap<>();

    public static Set<String> getReactionsForChannel(final String channelId) {
        return reactionsInChannels.getOrDefault(channelId, new HashSet<>());
    }

    public static void addReactionToChannel(final String channelId, final String reaction) {
        System.out.printf("Adding %s to %s%n", reaction, channelId);
        Set<String> reactionList = getReactionsForChannel(channelId);
        reactionList.add(reaction);
        if (reactionsInChannels.containsKey(channelId)) {
            reactionsInChannels.replace(channelId, reactionList);
        } else {
            reactionsInChannels.put(channelId, reactionList);
        }
    }

    public static void removeReactionsInChannel(final String channelId) {
        reactionsInChannels.remove(channelId);
    }

    public static void removeReactionFromChannel(final String channelId, final String reaction) {
        Set<String> reactionList = getReactionsForChannel(channelId);
        reactionList.remove(reaction);
        if (reactionList.size() == 0) {
            reactionsInChannels.remove(channelId);
        } else {
            if (reactionsInChannels.containsKey(channelId)) {
                reactionsInChannels.replace(channelId, reactionList);
            } else {
                reactionsInChannels.put(channelId, reactionList);
            }
        }
    }
}
