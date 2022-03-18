package panda.rainmaker.util;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

public class RoleGiverCache {

    private static final Map<String, String> roleToReactionMap = new HashMap<>();
    private static final Map<String, String> reactionToRoleMap = new HashMap<>();
    private static final Map<String, Set<String>> listToRoleMap = new HashMap<>();
    private static final Map<String, String> messageToListMap = new HashMap<>();
    private static final Map<String, String> listToMessageMap = new HashMap<>();
    private static final Map<String, String> guildToRoleChannelMap = new HashMap<>();

    private static Set<String> getRolesForList(String listName) {
        System.out.println("Getting roles for " + listName);
        return listToRoleMap.getOrDefault(listName, new HashSet<>());
    }

    public static void setRoleChannelId(Guild guild, String channelId) {
        String guildId = guild.getId();
        if (guildToRoleChannelMap.containsKey(guildId)) {
            guildToRoleChannelMap.replace(guildId, channelId);
        } else {
            guildToRoleChannelMap.put(guildId, channelId);
        }
        //TODO: invalidate all other data
    }

    public static boolean isValidList(Guild guild, String listName) {
        System.out.println("Validating: " + listName + " [" + getUID(guild, listName) + "]");
        return listToRoleMap.containsKey(getUID(guild, listName));
    }

    public static String createList(Guild guild, String listName) {
        if (!guildToRoleChannelMap.containsKey(guild.getId())) {
            return "No role channel has been set. Please use `/set-role-channel` and try again.";
        }

        String listUID = getUID(guild, listName);

        if (listToRoleMap.containsKey(listUID)) {
            return "A list with the name: " + listName + " already exists.";
        }

        listToRoleMap.put(listUID, getRolesForList(listUID));
        System.out.println("Created a role list named: " + listUID);

        String roleChannelId = guildToRoleChannelMap.get(guild.getId());

        // send a new message with list name
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel == null) {
            return "Unable to find a TextChannel with the associated id: " + roleChannelId;
        }

        channel.sendMessage("Self Assignable roles for the list: **" + listName + "**").queue(msg -> {
            messageToListMap.put(listUID, msg.getId());
            listToMessageMap.put(msg.getId(), listUID);
        });

        return "Successfully create a role list with the name: " + listName;
    }

    public static String addRoleToList(Guild guild, String listName, Role role, String reactionId) {
        String roleId = role.getId();
        String reactionUID = getUID(guild, getUID(listName, reactionId));
        String roleUID = getUID(guild, roleId);

        if (reactionToRoleMap.containsKey(reactionUID)) {
            return "Failed to add " + role.getAsMention() + ", emote/emoji already bound.";
        }

        if (roleToReactionMap.containsKey(roleUID)) {
            return "Failed to add " + role.getAsMention() + ", role already bound.";
        }

        reactionToRoleMap.put(reactionUID, roleId);
        roleToReactionMap.put(roleUID, reactionId);
        String listUID = getUID(guild, listName);
        Set<String> roleList = getRolesForList(listUID);
        roleList.add(roleId);
        if (listToRoleMap.containsKey(listUID)) {
            listToRoleMap.replace(listUID, roleList);
        } else {
            listToRoleMap.put(listUID, roleList);
        }

        updateMessage(guild, listName);
        return "Successfully added " + role.getAsMention() + " to " + listName;
    }

    public static void removeRoleFromList(Guild guild, String listName, String roleId) {
        String reactionId = roleToReactionMap.remove(getUID(guild, roleId));
        reactionToRoleMap.remove(getUID(guild, getUID(listName, reactionId)));
        String listUID = getUID(guild, listName);
        Set<String> roleList = getRolesForList(listUID);
        roleList.remove(roleId);

        if (listToRoleMap.containsKey(listUID)) {
            listToRoleMap.replace(listUID, roleList);
        } else {
            listToRoleMap.put(listUID, roleList);
        }

        updateMessage(guild, listName);
    }

    public static String deleteRoleList(Guild guild, String listName) {
        if (!guildToRoleChannelMap.containsKey(guild.getId())) {
            return "No role channel has been set. Please use `/set-role-channel` and try again.";
        }

        String listUID = getUID(guild, listName);

        if (!listToRoleMap.containsKey(listUID)) {
            return "A list with the name: " + listName + " does not exist.";
        }

        Set<String> roleList = getRolesForList(listUID);

        for (String roleId : roleList) {
            String reactionId = roleToReactionMap.remove(getUID(guild, roleId));
            reactionToRoleMap.remove(getUID(guild, getUID(listName, reactionId)));
        }

        deleteMessage(guild, listName);
        listToRoleMap.remove(listUID);

        String messageId = messageToListMap.remove(listUID);
        listToMessageMap.remove(messageId);

        return "Successfully deleted list `" + listName + "`.";
    }

    private static void updateMessage(Guild guild, String listName) {
        String listUID = getUID(guild, listName);
        String roleChannelId = guildToRoleChannelMap.get(guild.getId());
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel != null) {
            String messageId = messageToListMap.get(listUID);
            if (messageId != null) {
                channel.retrieveMessageById(messageId).queue(msg -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Self Assignable roles for the list **");
                    builder.append(listName);
                    builder.append("**:\n");

                    Set<String> roleList = getRolesForList(listUID);

                    for (String roleId : roleList) {
                        String reactionId = roleToReactionMap.get(getUID(guild, roleId));
                        System.out.println("RID: " + reactionId);
                        Role role = guild.getRoleById(roleId);
                        boolean isEmoji = EmojiManager.isEmoji(reactionId);

                        builder.append("**=>** React with ");

                        if (isEmoji) {
                            builder.append(EmojiParser.parseToUnicode(reactionId));

                            MessageReaction.ReactionEmote reaction = msg.getReactionByUnicode(reactionId);
                            if (reaction == null) {
                                msg.addReaction(reactionId).queue();
                            }
                        } else {
                            Emote emote = guild.getEmoteById(reactionId);

                            builder.append(emote.getAsMention());

                            MessageReaction.ReactionEmote reaction = msg.getReactionById(emote.getId());
                            if (reaction == null) {
                                msg.addReaction(emote).queue();
                            }
                        }

                        builder.append(" for the ");
                        builder.append(role.getAsMention());
                        builder.append(" role.\n");


                    }

                    msg.editMessage(builder.toString()).queue();
                });
            }
        }
    }

    private static void deleteMessage(Guild guild, String listName) {
        String listUID = getUID(guild, listName);
        String roleChannelId = guildToRoleChannelMap.get(guild.getId());
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel != null) {
            String messageId = messageToListMap.get(listUID);
            if (messageId != null) {
                channel.retrieveMessageById(messageId).queue(msg -> {
                   msg.delete().queue();
                });
            }
        }
    }

    public static String getRoleIdFromEmote(String messageId, String emoteId) {
        String listUID = listToMessageMap.get(messageId);

        if (listUID != null) {
            String reactionUID = getUID(listUID, emoteId);
            String roleId = reactionToRoleMap.get(reactionUID);

            if (roleId == null) {
                System.out.println("No role binding found for " + reactionUID);
            }

            return roleId;
        } else {
            System.out.println("No list found for " + messageId + " and " + emoteId);
        }

        return null;
    }

    private static String getUID(Guild guild, String objectId) {
        return getUID(guild.getId(), objectId);
    }

    private static String getUID(String hashId, String rangeId) {
        return String.format("%s||%s", hashId, rangeId);
    }
}
