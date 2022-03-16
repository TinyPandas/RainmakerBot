package panda.rainmaker.util;

import net.dv8tion.jda.api.entities.*;

import java.util.*;

public class RoleGiverCache {

    private static final Map<String, String> roleToReactionMap = new HashMap<>();
    private static final Map<String, String> reactionToRoleMap = new HashMap<>();
    private static final Map<String, Set<String>> listToRoleMap = new HashMap<>();
    private static final Map<String, String> messageToListMap = new HashMap<>();
    private static String ROLE_CHANNEL_ID;

    private static Set<String> getRolesForList(String listName) {
        return listToRoleMap.getOrDefault(listName, new HashSet<>());
    }

    public static void setRoleChannelId(String channelId) {
        ROLE_CHANNEL_ID = channelId;
        // invalidate all other data.
    }

    public static String createList(Guild guild, String listName) {
        if (ROLE_CHANNEL_ID == null) {
            return "No role channel has been created.";
        }

        if (listToRoleMap.containsKey(listName)) {
            return "A list with the name: " + listName + " already exists.";
        }

        // send a new message with list name
        TextChannel channel = guild.getTextChannelById(ROLE_CHANNEL_ID);
        if (channel == null) {
            return "Unable to find a TextChannel with the associated id: " + ROLE_CHANNEL_ID;
        }

        channel.sendMessage("Self Assignable roles for the list: " + listName).queue(msg -> {
            messageToListMap.put(listName, msg.getId());
        });

        return "Successfully create a role list with the name: " + listName;
    }

    public static void addRoleToList(Guild guild, String listName, String roleId, String reactionId) {
        reactionToRoleMap.put(reactionId, roleId);
        roleToReactionMap.put(roleId, reactionId);
        Set<String> roleList = getRolesForList(listName);
        roleList.add(roleId);
        if (listToRoleMap.containsKey(listName)) {
            listToRoleMap.replace(listName, roleList);
        } else {
            listToRoleMap.put(listName, roleList);
        }

        updateMessage(guild, listName);
    }

    public static void removeRoleFromList(Guild guild, String listName, String roleId) {

    }

    private static void updateMessage(Guild guild, String listName) {
        TextChannel channel = guild.getTextChannelById(ROLE_CHANNEL_ID);
        if (channel != null) {
            String messageId = messageToListMap.get(listName);
            if (messageId != null) {
                channel.retrieveMessageById(messageId).queue(msg -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Self Assignable roles for the list: ");
                    builder.append(listName);
                    builder.append("\n\n");

                    Set<String> roleList = getRolesForList(listName);

                    for (String roleId : roleList) {
                        String reactionId = roleToReactionMap.get(roleId);
                        Emote emote = guild.getEmoteById(reactionId);
                        Role role = guild.getRoleById(roleId);

                        builder.append("React with ");
                        builder.append(emote.getAsMention());
                        builder.append(" for the ");
                        builder.append(role.getAsMention());
                        builder.append(" role.\n");

                        MessageReaction.ReactionEmote reaction = msg.getReactionById(emote.getId());
                        if (reaction == null) {
                            msg.addReaction(emote).queue();
                        }
                    }

                    msg.editMessage(builder.toString()).queue();
                });
            }
        }
    }
}
