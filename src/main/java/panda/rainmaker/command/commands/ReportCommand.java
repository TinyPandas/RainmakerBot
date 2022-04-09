package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.util.OptionDataDefs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static panda.rainmaker.util.PandaUtil.*;

public class ReportCommand extends CommandObject {

    public ReportCommand() {
        super("report", "Report a user to all online staff members.");
        addOptionData(OptionDataDefs.USER.asOptionData());
        addOptionData(OptionDataDefs.REASON.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        try {
            Guild guild = getGuildFromSlashCommandEvent(event);
            TextChannel channelOfIncident = getTextChannelFromEvent(event);
            Member reporter = getMemberFromSlashCommandEvent(event);
            Member reported = getMemberFromOption(event.getOption("user"));
            String reason = getStringFromOption("Reason", event.getOption("reason"));
            Role staffRole = getRoleFromGuildById(guild, guildSettings.getStaffRoleId());
            TextChannel reportChannel = getTextChannelFromGuildById(guild, guildSettings.getReportChannelId());
            List<Member> allStaff = guild.getMembersWithRoles(staffRole);
            List<Member> onlineStaff = new ArrayList<>(allStaff)
                    .stream()
                    .filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    .collect(Collectors.toList());
            String message = "";
            if (onlineStaff.size() == 0) {
                message += allStaff.stream().map(IMentionable::getAsMention).collect(Collectors.joining());
            } else {
                message += onlineStaff.stream().map(IMentionable::getAsMention).collect(Collectors.joining());
            }

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("Report Command")
                    .setTitle("User Report")
                    .addField("User being reported", reported.getAsMention(), false)
                    .addField("User issuing report", reporter.getAsMention(), false)
                    .addField("Reason for report", reason, false)
                    .addField("Channel of report", channelOfIncident.getAsMention(), false);

            reportChannel.sendMessage(message).setEmbeds(embedBuilder.build()).queue(success -> {
                passEvent(event, "Report posted successfully.");
            }, fail -> {
                failEvent(event, "Failed to submit report. Please try again");
            });
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
