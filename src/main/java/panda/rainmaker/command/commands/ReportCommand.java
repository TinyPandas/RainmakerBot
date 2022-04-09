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

            List<Member> allStaff = guild.getMembersWithRoles(staffRole)
                    .stream()
                    .filter(member -> !member.getUser().isBot())
                    .collect(Collectors.toList());

            List<Member> onlineStaff = new ArrayList<>(allStaff)
                    .stream()
                    .filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE))
                    .collect(Collectors.toList());

            String message = staffRole.getAsMention();
            if (onlineStaff.size() != 0) {
                message = onlineStaff.stream().map(IMentionable::getAsMention).collect(Collectors.joining());
            }

            String lastMessageId = channelOfIncident.getLatestMessageId();
            MessageHistory channelHistory = channelOfIncident.getHistoryBefore(lastMessageId, 100).complete();
            List<Message> userHistory = channelHistory.getRetrievedHistory()
                    .stream()
                    .filter(historyMessage -> historyMessage.getAuthor().getId().equals(reported.getId()))
                    .collect(Collectors.toList());

            List<String> userLastMessages = userHistory
                    .subList(0, Math.min(userHistory.size(), 5))
                    .stream()
                    .map(Message::getContentRaw)
                    .collect(Collectors.toList());

            String userHistoryMessage = "";
            for (int i = userLastMessages.size() - 1; i >= 0; i--) {
                userHistoryMessage = String.format("%s%n%s", userHistoryMessage, userLastMessages.get(i));
            }

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("Report Command")
                    .setTitle("User Report")
                    .addField("User being reported", reported.getAsMention(), true)
                    .addField("User issuing report", reporter.getAsMention(), true)
                    .addField("Channel of report", channelOfIncident.getAsMention(), true)
                    .addField("Reason for report", reason, false)
                    .addField("User Last 5 Messages", String.format("```%n%s%n```", userHistoryMessage), false);

            reportChannel.sendMessage(message).setEmbeds(embedBuilder.build()).queue(
                    success -> passEvent(event, "Report posted successfully."),
                    fail -> failEvent(event, "Failed to submit report. Please try again")
            );
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }
    }
}
