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
        //invalidate all other data
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

        channel.sendMessage("Self Assignable roles for the list: " + listName).queue(msg -> {
            messageToListMap.put(listUID, msg.getId());
        });

        return "Successfully create a role list with the name: " + listName;
    }

    public static void addRoleToList(Guild guild, String listName, String roleId, String reactionId) {
        reactionToRoleMap.put(getUID(guild, reactionId), roleId);
        roleToReactionMap.put(getUID(guild, roleId), reactionId);
        String listUID = getUID(guild, listName);
        Set<String> roleList = getRolesForList(listUID);
        roleList.add(roleId);
        if (listToRoleMap.containsKey(listUID)) {
            listToRoleMap.replace(listUID, roleList);
        } else {
            listToRoleMap.put(listUID, roleList);
        }

        updateMessage(guild, listName);
    }

    public static void removeRoleFromList(Guild guild, String listName, String roleId) {

    }

    public static void deleteRoleList(Guild guild, String listName) {

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
                    builder.append("Self Assignable roles for the list: ");
                    builder.append(listName);
                    builder.append("\n\n");

                    Set<String> roleList = getRolesForList(listUID);

                    for (String roleId : roleList) {
                        String reactionId = roleToReactionMap.get(getUID(guild, roleId));
                        System.out.println("RID: " + reactionId);
                        Role role = guild.getRoleById(roleId);
                        boolean isEmoji = EmojiManager.isEmoji(reactionId);

                        builder.append("React with ");

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

    private static String getUID(Guild guild, String objectId) {
        return String.format("%s||%s", guild.getId(), objectId);
    }
}
