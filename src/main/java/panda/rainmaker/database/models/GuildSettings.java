package panda.rainmaker.database.models;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GuildSettings {

    private ObjectId _id;
    private String guildId;
    private String roleChannelId;

    // roleId -> reactionId
    private Map<String, String> roleToReactionMap;

    // reactionId -> roleId
    private Map<String, String> reactionToRoleMap;

    // listName -> roleIdSet
    private Map<String, Set<String>> listToRoleMap;

    // messageId -> listName
    private Map<String, String> messageToListMap;

    // listName -> messageId
    private Map<String, String> listToMessageMap;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getRoleChannelId() {
        return roleChannelId;
    }

    public void setRoleChannelId(String roleChannelId) {
        this.roleChannelId = roleChannelId;
    }

    public Map<String, String> getRoleToReactionMap() {
        if (roleToReactionMap == null) {
            roleToReactionMap = new HashMap<>();
        }
        return roleToReactionMap;
    }

    public void setRoleToReactionMap(Map<String, String> roleToReactionMap) {
        this.roleToReactionMap = roleToReactionMap;
    }

    public Map<String, String> getReactionToRoleMap() {
        if (reactionToRoleMap == null) {
            reactionToRoleMap = new HashMap<>();
        }
        return reactionToRoleMap;
    }

    public void setReactionToRoleMap(Map<String, String> reactionToRoleMap) {
        this.reactionToRoleMap = reactionToRoleMap;
    }

    public Map<String, Set<String>> getListToRoleMap() {
        if (listToRoleMap == null) {
            listToRoleMap = new HashMap<>();
        }
        return listToRoleMap;
    }

    public void setListToRoleMap(Map<String, Set<String>> listToRoleMap) {
        this.listToRoleMap = listToRoleMap;
    }

    public Map<String, String> getMessageToListMap() {
        if (messageToListMap == null) {
            messageToListMap = new HashMap<>();
        }
        return messageToListMap;
    }

    public void setMessageToListMap(Map<String, String> messageToListMap) {
        this.messageToListMap = messageToListMap;
    }

    public Map<String, String> getListToMessageMap() {
        if (listToMessageMap == null) {
            listToMessageMap = new HashMap<>();
        }
        return listToMessageMap;
    }

    public void setListToMessageMap(Map<String, String> listToMessageMap) {
        this.listToMessageMap = listToMessageMap;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "_id=" + _id +
                ", guildId='" + guildId + '\'' +
                ", roleChannelId='" + roleChannelId + '\'' +
                ", roleToReactionMap=" + roleToReactionMap +
                ", reactionToRoleMap=" + reactionToRoleMap +
                ", listToRoleMap=" + listToRoleMap +
                ", messageToListMap=" + messageToListMap +
                ", listToMessageMap=" + listToMessageMap +
                '}';
    }
}
