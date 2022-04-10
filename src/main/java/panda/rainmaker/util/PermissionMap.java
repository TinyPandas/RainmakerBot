package panda.rainmaker.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class PermissionMap {

    private Set<String> allowedRoleIds = new HashSet<>();
    private Set<String> allowedUserIds = new HashSet<>();

    private final HashMap<String, Boolean> roleAccess = new HashMap<>();
    private final HashMap<String, Boolean> userAccess = new HashMap<>();

    public Set<String> getAllowedRoles() {
        Set<String> allowedRoles = new HashSet<>();

        for (Map.Entry<String, Boolean> entry : roleAccess.entrySet()) {
            if (entry.getValue()) {
                allowedRoles.add(entry.getKey());
            }
        }

        return allowedRoles;
    }

    public Set<String> getDisallowedRoles() {
        Set<String> disallowedRoles = new HashSet<>();

        for (Map.Entry<String, Boolean> entry : roleAccess.entrySet()) {
            if (!entry.getValue()) {
                disallowedRoles.add(entry.getKey());
            }
        }

        return disallowedRoles;
    }

    public Set<String> getAllowedUsers() {
        Set<String> allowedUsers = new HashSet<>();

        for (Map.Entry<String, Boolean> entry : userAccess.entrySet()) {
            if (entry.getValue()) {
                allowedUsers.add(entry.getKey());
            }
        }

        return allowedUsers;
    }

    public Set<String> getDisallowedUsers() {
        Set<String> disallowedUsers = new HashSet<>();

        for (Map.Entry<String, Boolean> entry : userAccess.entrySet()) {
            if (!entry.getValue()) {
                disallowedUsers.add(entry.getKey());
            }
        }

        return disallowedUsers;
    }

    public void setRoleAccess(String roleId, boolean canAccess) {
        roleAccess.put(roleId, canAccess);
    }

    public void setUserAccess(String roleId, boolean canAccess) {
        userAccess.put(roleId, canAccess);
    }

    public Set<String> getAllowedRoleIds() {
        return allowedRoleIds;
    }

    public Set<String> getAllowedUserIds() {
        return allowedUserIds;
    }

    public void updatePermission(Member member, Role role, boolean toAdd) {
        if (toAdd) {
            if (member != null) setUserAccess(member.getId(), true);
            if (role != null) setRoleAccess(role.getId(), true);
        } else {
            if (member != null) setUserAccess(member.getId(), false);
            if (role != null) setRoleAccess(role.getId(), false);
        }
    }

    public boolean fullPermissionCheckForMember(Member member) {
        Boolean userState = userAccess.get(member.getId());
        if (userState != null && userState) return true;

        boolean roleState = false;
        int highestRole = -1;

        for (Role r : member.getRoles()) {
            if (roleAccess.containsKey(r.getId())) {
                if (r.getPosition() > highestRole) {
                    roleState = roleAccess.get(r.getId());
                    highestRole = r.getPosition();
                }
            }
        }

        return roleState;
    }
}
