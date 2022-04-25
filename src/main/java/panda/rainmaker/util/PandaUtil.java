package panda.rainmaker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;
import panda.rainmaker.database.GlobalDao;
import panda.rainmaker.database.models.GlobalSettings;
import panda.rainmaker.entity.ReactionObject;

public class PandaUtil {
    public static final ObjectMapper objectMapper;

    public static GlobalSettings globalSettings;

    static {
        objectMapper = new ObjectMapper();
        globalSettings = GlobalDao.retrieveGlobalSettings();
        if (globalSettings == null) {
            globalSettings = GlobalDao.loadDefaults();
        }
    }

    public static boolean memberHasPermission(Member member, Permission permission) {
        //if (!member.hasPermission(permission)) throw new Exception("Missing permission: " + permission);
        return memberHasPermission(member, permission, new PermissionMap());
    }

    public static boolean memberHasPermission(Member member, Permission permission, PermissionMap permissionMap) {
        boolean corePermission = member.hasPermission(permission);
        if (corePermission) return true;

        return permissionMap.fullPermissionCheckForMember(member);
    }

    public static String getDisplayResultForRemoveReactionFromReactionObject(Guild guild, ReactionObject reactionObject) {
        if (reactionObject == null) return "Disabled all reactions in %s.";
        return String.format("Disabled %s in", reactionObject.getDisplay(guild)) + "%s.";
    }

    @Nullable
    public static Role getRoleFromGuildById(Guild guild, String roleId){
        if (roleId == null) return null;
        return guild.getRoleById(roleId);
    }

    @Nullable
    public static TextChannel getTextChannelFromGuildById(Guild guild, String channelId) {
        if (channelId == null) return null;
        return guild.getTextChannelById(channelId);
    }
}
