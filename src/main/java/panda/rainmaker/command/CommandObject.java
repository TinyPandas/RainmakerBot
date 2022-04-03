package panda.rainmaker.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import panda.rainmaker.database.models.GuildSettings;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandObject {
    private final String name;
    private final String description;
    private final List<OptionData> optionsDataList;

    public CommandObject(String name, String description) {
        this(name, description, new ArrayList<>());
    }

    public CommandObject(String name, String description, List<OptionData> optionsDataList) {
        this.name = name;
        this.description = description;
        this.optionsDataList = optionsDataList;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getOptionsDataList() {
        return optionsDataList;
    }

    public void addOptionData(OptionData optionData) {
        optionsDataList.add(optionData);
    }

    public SlashCommandData getSlashImplementation() {
        return Commands.slash(getName(), getDescription())
                .addOptions(optionsDataList);
    }

    public abstract void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings);

    public boolean failEvent(InteractionHook hook, String reason) {
        hook.editOriginal(String.format("[Failed to execute: %s]%n%s", getName(), reason)).queue();
        return false;
    }

    public boolean passEvent(InteractionHook hook, String reason) {
        hook.editOriginal(reason).queue();
        return true;
    }
}
