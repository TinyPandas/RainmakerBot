package panda.rainmaker.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.*;

public class PermissionMap {

    private Set<String> allowedRoles;
    private Set<String> disallowedRoles;
    private Set<String> allowedUsers;
    private Set<String> disallowedUsers;

    public void setAllowedRoles(Set<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public void setDisallowedRoles(Set<String> disallowedRoles) {
        this.disallowedRoles = disallowedRoles;
    }

    public void setAllowedUsers(Set<String> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public void setDisallowedUsers(Set<String> disallowedUsers) {
        this.disallowedUsers = disallowedUsers;
    }

    private void check() {
        if (allowedRoles == null) allowedRoles = new HashSet<>();
        if (disallowedRoles == null) disallowedRoles = new HashSet<>();
        if (allowedUsers == null) allowedUsers = new HashSet<>();
        if (disallowedUsers == null) disallowedUsers = new HashSet<>();
    }

    public Set<String> getAllowedRoles() {
        check();
        return allowedRoles;
    }

    public Set<String> getDisallowedRoles() {
        check();
        return disallowedRoles;
    }

    public Set<String> getAllowedUsers() {
        check();
        return allowedUsers;
    }

    public Set<String> getDisallowedUsers() {
        check();
        return disallowedUsers;
    }

    public void updateRoleAccess(String roleId, boolean canAccess) {
        if (canAccess) {
            disallowedRoles.remove(roleId);
            allowedRoles.add(roleId);
        } else {
            disallowedRoles.add(roleId);
            allowedRoles.remove(roleId);
        }
    }

    public void updateUserAccess(String userId, boolean canAccess) {
        if (canAccess) {
            disallowedUsers.remove(userId);
            allowedUsers.add(userId);
        } else {
            disallowedUsers.add(userId);
            allowedUsers.remove(userId);
        }
    }

    public void updatePermission(Member member, Role role, boolean toAdd) {
        check();
        if (toAdd) {
            if (member != null) updateUserAccess(member.getId(), true);
            if (role != null) updateRoleAccess(role.getId(), true);
        } else {
            if (member != null) updateUserAccess(member.getId(), false);
            if (role != null) updateRoleAccess(role.getId(), false);
        }
    }

    public boolean fullPermissionCheckForMember(Member member) {
        check();
        String memberId = member.getId();
        if (disallowedUsers.contains(memberId)) return false;
        if (allowedUsers.contains(memberId)) return true;

        boolean roleState = false;
        int highestRole = -1;

        for (Role r : member.getRoles()) {
            String roleId = r.getId();
            int position = r.getPosition();
            if (getAllowedRoles().contains(roleId) && position > highestRole) {
                roleState = true;
                highestRole = position;
            }
            if (getDisallowedRoles().contains(roleId) && position > highestRole) {
                roleState = false;
                highestRole = position;
            }
        }

        return roleState;
    }

    public List<CommandPrivilege> generatePrivileges() {
        check();
        List<CommandPrivilege> generatedPrivileges = new ArrayList<>();
        for (String allowedRoleId : getAllowedRoles()) generatedPrivileges.add(CommandPrivilege.enableRole(allowedRoleId));
        for (String disallowedRoleId : getDisallowedRoles()) generatedPrivileges.add(CommandPrivilege.enableRole(disallowedRoleId));
        for (String allowedUserId : getAllowedUsers()) generatedPrivileges.add(CommandPrivilege.enableRole(allowedUserId));
        for (String disallowedUserId : getDisallowedUsers()) generatedPrivileges.add(CommandPrivilege.enableRole(disallowedUserId));

        return generatedPrivileges;
    }
}
