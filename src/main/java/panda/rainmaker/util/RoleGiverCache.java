package panda.rainmaker.util;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.*;
import panda.rainmaker.database.GuildDao;
import panda.rainmaker.database.models.GuildSettings;

import java.util.*;

public class RoleGiverCache {

    //private static final Map<String, String> roleToReactionMap = new HashMap<>();

    private static Set<String> getRolesForList(GuildSettings guildSettings, String listName) {
        System.out.println("Getting roles for " + listName);
        return guildSettings.getListToRoleMap().getOrDefault(listName, new HashSet<>());
    }

    public static void setRoleChannelId(GuildSettings guildSettings, String channelId) {
        guildSettings.setRoleChannelId(channelId);
        GuildDao.saveGuildSettings(guildSettings);
    }

    public static boolean isInvalidList(GuildSettings guildSettings, Guild guild, String listName) {
        System.out.println("Validating: " + listName + " [" + getUID(guild, listName) + "]");
        return !guildSettings.getListToRoleMap().containsKey(getUID(guild, listName));
    }

    public static String createList(GuildSettings guildSettings, Guild guild, String listName) {
        if (guildSettings.getRoleChannelId() == null) {
            return "No role channel has been set. Please use `/set-role-channel` and try again.";
        }

        String listUID = getUID(guild, listName);

        if (guildSettings.getListToRoleMap().containsKey(listUID)) {
            return "A list with the name: " + listName + " already exists.";
        }

        Map<String, Set<String>> listToRoleMap = guildSettings.getListToRoleMap();
        listToRoleMap.put(listUID, getRolesForList(guildSettings, listUID));
        guildSettings.setListToRoleMap(listToRoleMap);

        System.out.println("Created a role list named: " + listUID);

        String roleChannelId = guildSettings.getRoleChannelId();

        // send a new message with list name
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel == null) {
            return "Unable to find a TextChannel with the associated id: " + roleChannelId;
        }

        channel.sendMessage("Self Assignable roles for the list: **" + listName + "**").queue(msg -> {
            Map<String, String> messageToListMap = guildSettings.getMessageToListMap();
            messageToListMap.put(listUID, msg.getId());
            guildSettings.setMessageToListMap(messageToListMap);

            Map<String, String> listToMessageMap = guildSettings.getListToMessageMap();
            listToMessageMap.put(msg.getId(), listUID);
            guildSettings.setListToMessageMap(listToMessageMap);

            GuildDao.saveGuildSettings(guildSettings);
        });

        return "Successfully create a role list with the name: " + listName;
    }

    public static String addRoleToList(GuildSettings guildSettings, Guild guild, String listName, Role role, String reactionId) {
        String roleId = role.getId();
        String reactionUID = getUID(guild, getUID(listName, reactionId));
        String roleUID = getUID(guild, roleId);

        Map<String, String> reactionToRoleMap = guildSettings.getReactionToRoleMap();
        Map<String, String> roleToReactionMap = guildSettings.getRoleToReactionMap();

        if (reactionToRoleMap.containsKey(reactionUID)) {
            return "Failed to add " + role.getAsMention() + ", emote/emoji already bound.";
        }

        if (roleToReactionMap.containsKey(roleUID)) {
            return "Failed to add " + role.getAsMention() + ", role already bound.";
        }

        reactionToRoleMap.put(reactionUID, roleId);
        guildSettings.setReactionToRoleMap(reactionToRoleMap);

        roleToReactionMap.put(roleUID, reactionId);
        guildSettings.setRoleToReactionMap(roleToReactionMap);

        String listUID = getUID(guild, listName);
        Set<String> roleList = getRolesForList(guildSettings, listUID);
        roleList.add(roleId);
        if (guildSettings.getListToRoleMap().containsKey(listUID)) {
            guildSettings.getListToRoleMap().replace(listUID, roleList);
        } else {
            guildSettings.getListToRoleMap().put(listUID, roleList);
        }

        GuildDao.saveGuildSettings(guildSettings);
        updateMessage(guildSettings, guild, listName);
        return "Successfully added " + role.getAsMention() + " to " + listName;
    }

    public static void removeRoleFromList(GuildSettings guildSettings, Guild guild, String listName, String roleId) {
        Map<String, String> roleToReactionMap = guildSettings.getRoleToReactionMap();
        String reactionId = roleToReactionMap.remove(getUID(guild, roleId));
        guildSettings.setRoleToReactionMap(roleToReactionMap);

        Map<String, String> reactionToRoleMap = guildSettings.getReactionToRoleMap();
        reactionToRoleMap.remove(getUID(guild, getUID(listName, reactionId)));
        guildSettings.setReactionToRoleMap(reactionToRoleMap);

        String listUID = getUID(guild, listName);
        Set<String> roleList = getRolesForList(guildSettings, listUID);
        roleList.remove(roleId);

        if (guildSettings.getListToRoleMap().containsKey(listUID)) {
            guildSettings.getListToRoleMap().replace(listUID, roleList);
        } else {
            guildSettings.getListToRoleMap().put(listUID, roleList);
        }

        GuildDao.saveGuildSettings(guildSettings);
        updateMessage(guildSettings, guild, listName);
    }

    public static String deleteRoleList(GuildSettings guildSettings, Guild guild, String listName) {
        if (guildSettings.getRoleChannelId() == null) {
            return "No role channel has been set. Please use `/set-role-channel` and try again.";
        }

        String listUID = getUID(guild, listName);

        if (!guildSettings.getListToRoleMap().containsKey(listUID)) {
            return "A list with the name: " + listName + " does not exist.";
        }

        Set<String> roleList = getRolesForList(guildSettings, listUID);
        Map<String, String> reactionToRoleMap = guildSettings.getReactionToRoleMap();
        Map<String, String> roleToReactionMap = guildSettings.getRoleToReactionMap();

        for (String roleId : roleList) {
            String reactionId = roleToReactionMap.remove(getUID(guild, roleId));
            guildSettings.setRoleToReactionMap(roleToReactionMap);

            reactionToRoleMap.remove(getUID(guild, getUID(listName, reactionId)));
            guildSettings.setReactionToRoleMap(reactionToRoleMap);
        }

        deleteMessage(guildSettings, guild, listName);
        guildSettings.getListToRoleMap().remove(listUID);

        Map<String, String> messageToListMap = guildSettings.getMessageToListMap();
        String messageId = messageToListMap.remove(listUID);
        guildSettings.setMessageToListMap(messageToListMap);

        Map<String, String> listToMessageMap = guildSettings.getListToMessageMap();
        listToMessageMap.remove(messageId);
        guildSettings.setListToMessageMap(listToMessageMap);

        GuildDao.saveGuildSettings(guildSettings);
        return "Successfully deleted list `" + listName + "`.";
    }

    private static void updateMessage(GuildSettings guildSettings, Guild guild, String listName) {
        String listUID = getUID(guild, listName);
        String roleChannelId = guildSettings.getRoleChannelId();
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel != null) {
            String messageId = guildSettings.getMessageToListMap().get(listUID);
            if (messageId != null) {
                channel.retrieveMessageById(messageId).queue(msg -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Self Assignable roles for the list **");
                    builder.append(listName);
                    builder.append("**:\n");

                    Set<String> roleList = getRolesForList(guildSettings, listUID);

                    for (String roleId : roleList) {
                        String reactionId = guildSettings.getRoleToReactionMap().get(getUID(guild, roleId));
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
                            Emote emote = guild.getJDA().getEmoteById(reactionId);

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

    private static void deleteMessage(GuildSettings guildSettings, Guild guild, String listName) {
        String listUID = getUID(guild, listName);
        String roleChannelId = guildSettings.getRoleChannelId();
        TextChannel channel = guild.getTextChannelById(roleChannelId);
        if (channel != null) {
            String messageId = guildSettings.getMessageToListMap().get(listUID);
            if (messageId != null) {
                channel.retrieveMessageById(messageId).queue(msg -> msg.delete().queue());
            }
        }
    }

    public static String getRoleIdFromEmote(GuildSettings guildSettings, String messageId, String emoteId) {
        String listUID = guildSettings.getListToMessageMap().get(messageId);

        if (listUID != null) {
            String reactionUID = getUID(listUID, emoteId);
            String roleId = guildSettings.getReactionToRoleMap().get(reactionUID);

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
