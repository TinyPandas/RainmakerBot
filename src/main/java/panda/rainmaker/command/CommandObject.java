package panda.rainmaker.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
    private final boolean isPermissible;

    public CommandObject(String name, String description) {
        this(name, description, new ArrayList<>(), false);
    }

    public CommandObject(String name, String description, List<OptionData> optionsDataList) {
        this(name, description, optionsDataList, false);
    }

    public CommandObject(String name, String description, boolean isPermissible) {
        this(name, description, new ArrayList<>(), isPermissible);
    }

    public CommandObject(String name, String description, List<OptionData> optionsDataList, boolean isPermissible) {
        this.name = name;
        this.description = description;
        this.optionsDataList = optionsDataList;
        this.isPermissible = isPermissible;
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

    public boolean failEvent(SlashCommandInteractionEvent event, String reason) {
        event.getHook().editOriginal(String.format("[Failed to execute: %s]%n%s", getName(), reason)).queue();
        return false;
    }

    public boolean passEvent(SlashCommandInteractionEvent event, String reason, MessageEmbed... embeds) {
        event.getHook().editOriginal(reason).setEmbeds(embeds).queue();
        return true;
    }

    public boolean getIsPermissible() {
        return isPermissible;
    }
}
