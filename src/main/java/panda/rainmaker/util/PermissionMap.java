package panda.rainmaker.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashSet;
import java.util.Set;

public class PermissionMap {

    private Set<String> allowedRoleIds = new HashSet<>();
    private Set<String> allowedUserIds = new HashSet<>();

    public void setAllowedRoleIds(Set<String> allowedRoleIds) {
        this.allowedRoleIds = allowedRoleIds;
    }

    public void addAllowedRoleId(String roleId) {
        allowedRoleIds.add(roleId);
    }

    public void removeAllowedRoleId(String roleId) {
        allowedRoleIds.remove(roleId);
    }

    public Set<String> getAllowedRoleIds() {
        return allowedRoleIds;
    }

    public void setAllowedUserIds(Set<String> allowedUserIds) {
        this.allowedUserIds = allowedUserIds;
    }

    public void addAllowedUserId(String userId) {
        allowedUserIds.add(userId);
    }

    public void removeAllowedUserId(String userId) {
        allowedUserIds.remove(userId);
    }

    public Set<String> getAllowedUserIds() {
        return allowedUserIds;
    }

    public void updatePermission(Member member, Role role, boolean toAdd) {
        if (toAdd) {
            if (member != null) addAllowedUserId(member.getId());
            if (role != null) addAllowedRoleId(role.getId());
        } else {
            if (member != null) removeAllowedUserId(member.getId());
            if (role != null) removeAllowedRoleId(role.getId());
        }
    }
}
