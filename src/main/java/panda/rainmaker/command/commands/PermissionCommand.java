package panda.rainmaker.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.command.Commands;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.util.OptionDataDefs;
import panda.rainmaker.util.PermissionMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionCommand extends CommandObject {

    public PermissionCommand() {
        super("permissions", "Updates permissions on a command.", true);
        addOptionData(OptionDataDefs.PERM_TARGET.asOptionData()
                .addChoices(
                        Commands.getCommands().stream()
                                .filter(CommandObject::getIsPermissible)
                                .map(commandObject -> new Command.Choice(commandObject.getName(), commandObject.getName()))
                                .collect(Collectors.toList())
                )
                .addChoice("permissions", "permissions"));
        addOptionData(OptionDataDefs.PERM_ACTION.asOptionData()
                .addChoice("add", "add")
                .addChoice("remove", "remove"));
        addOptionData(OptionDataDefs.PERM_ENTITY.asOptionData());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        event.deferReply(true).queue();

        EventData eventData = super.validate(event);
        if (eventData == null) return;
        Guild guild = eventData.getGuild();
        String targetCommand = (String) eventData.getOption("target").getValue();
        String action = (String) eventData.getOption("action").getValue();
        boolean toAdd = action.equals("add");

        PermissionMap targetMap = guildSettings.getPermissionsForCommand(targetCommand);
        Member member = null;
        Role role = null;

        try {
            member = (Member) eventData.getOption("entity").getValue();
        } catch (ClassCastException ignored) {}

        try {
            role = (Role) eventData.getOption("entity").getValue();
        } catch(ClassCastException ignored) {}

        targetMap.updatePermission(member, role, toAdd);

        guild.retrieveCommands().queue(commands -> {
            for (Command command : commands) {
                if (command.getName().equals(targetCommand)) {
                    List<CommandPrivilege> generatedPrivileges = new ArrayList<>();
                    generatedPrivileges.add(CommandPrivilege.enableUser(guild.getOwnerId()));
                    generatedPrivileges.addAll(targetMap.generatePrivileges());
                    command.updatePrivileges(guild, generatedPrivileges).queue();
                }
            }
        });

        String updateResult = guildSettings.updatePermissionsForCommand(targetCommand, targetMap);
        passEvent(event, updateResult);
    }
}
