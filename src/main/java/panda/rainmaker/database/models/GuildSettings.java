package panda.rainmaker.database.models;

import org.bson.types.ObjectId;
import panda.rainmaker.database.GuildDao;
import panda.rainmaker.util.PermissionMap;

import java.util.*;

public class GuildSettings {

    public static final List<String> FIELDS = Arrays.asList("roleChannelId", "staffRoleId", "reportChannelId");

    private ObjectId _id;
    private String guildId;
    private String roleChannelId;
    private String staffRoleId;
    private String reportChannelId;

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

    // commandName -> PermissionMap
    private Map<String, PermissionMap> commandPermissions;

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

    public String getStaffRoleId() {
        return staffRoleId;
    }

    public void setStaffRoleId(String staffRoleId) {
        this.staffRoleId = staffRoleId;
    }

    public String getReportChannelId() {
        return reportChannelId;
    }

    public void setReportChannelId(String reportChannelId) {
        this.reportChannelId = reportChannelId;
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
                ", staffRoleId='" + staffRoleId + '\'' +
                ", reportChannelId='" + reportChannelId + '\'' +
                '}';
    }

    public String getValueForField(String field) {
        switch(field) {
            case "roleChannelId":
                return getRoleChannelId();
            case "staffRoleId":
                return getStaffRoleId();
            case "reportChannelId":
                return getReportChannelId();
        }

        return null;
    }

    private void setValueForField(String field, String value) {
        switch(field) {
            case "roleChannelId":
                setRoleChannelId(value);
                break;
            case "staffRoleId":
                setStaffRoleId(value);
                break;
            case "reportChannelId":
                setReportChannelId(value);
                break;
        }
    }

    public String updateFieldWithValue(String field, String value) {
        System.out.println("Updating " + field + " to " + value);
        String currentValue = getValueForField(field);
        setValueForField(field, value);
        boolean updated = GuildDao.saveGuildSettings(this);

        if (updated) {
            return String.format("Successfully updated %s to %s. [Old value: %s]", field, value, currentValue);
        }

        return String.format("Failed to update %s to %s. [Current value: %s]", field, value, currentValue);
    }

    public Map<String, PermissionMap> getCommandPermissions() {
        return commandPermissions;
    }

    public void setCommandPermissions(Map<String, PermissionMap> commandPermissions) {
        this.commandPermissions = commandPermissions;
    }

    public PermissionMap getPermissionsForCommand(String commandName) {
        if (commandPermissions == null) commandPermissions = new HashMap<>();

        PermissionMap map = commandPermissions.get(commandName);
        if (map == null) {
            map = new PermissionMap();
            commandPermissions.put(commandName, map);
        }

        return map;
    }

    public String updatePermissionsForCommand(String commandName, PermissionMap permissionMap) {
        commandPermissions.put(commandName, permissionMap);
        boolean updated = GuildDao.saveGuildSettings(this);

        return (updated ? "Successfully" : "Failed to") + " update permissions for " + commandName;
    }
}
