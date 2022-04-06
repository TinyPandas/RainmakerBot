package panda.rainmaker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
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

    public static void memberHasPermission(Member member, Permission permission) throws Exception{
        if (!member.hasPermission(permission)) throw new Exception("Missing permission: " + permission);
    }

    public static Member getMemberFromSlashCommandEvent(SlashCommandInteractionEvent event) throws Exception {
        if (event.getMember() == null) throw new Exception("Failed to fetch member.");
        return event.getMember();
    }

    public static Member getSelfMemberFromGuild(Guild guild) {
        return guild.getSelfMember();
    }

    public static Guild getGuildFromSlashCommandEvent(SlashCommandInteractionEvent event) throws Exception {
        if (event.getGuild() == null) throw new Exception("Failed to fetch Guild.");
        return event.getGuild();
    }

    public static TextChannel getTextChannelFromOption(OptionMapping optionMapping) throws Exception {
        if (optionMapping == null) throw new Exception("Channel option was not provided.");
        return optionMapping.getAsTextChannel();
    }

    public static String getStringFromOption(String optionName, OptionMapping optionMapping) throws Exception {
        if (optionMapping == null) throw new Exception(optionName + " was not provided");
        return optionMapping.getAsString();
    }

    public static String getDisplayResultForRemoveReactionFromReactionObject(Guild guild, ReactionObject reactionObject) {
        if (reactionObject == null) return "Disabled all reactions in %s.";
        return String.format("Disabled %s in", reactionObject.getDisplay(guild)) + "%s.";
    }

    public static Role getRoleFromOption(OptionMapping optionMapping) throws Exception {
        if (optionMapping == null) throw new Exception("Role was not provided.");
        return optionMapping.getAsRole();
    }
}
