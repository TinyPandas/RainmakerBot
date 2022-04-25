package panda.rainmaker.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Generates an EventData object for an Event. This will
     * populate the elements required by all commands. If the
     * event's options are less than a commands OptionDataList
     * it will return null.
     * @param event - The SlashCommandInteractionEvent in effect.
     * @return Populated EventData containing validated fields.
     */
    public EventData validate(SlashCommandInteractionEvent event) {
        List<OptionMapping> options = event.getOptions();
        if (options.size() < optionsDataList.size()) {
            event.getHook().editOriginal(String.format("Mismatched arguments provided. Expected %s, Got %s.",
                    optionsDataList.size(), options.size())).queue();
            return null;
        }

        HashMap<String, OptionMapping> optionMapping = new HashMap<>();
        for (OptionMapping option : options) {
            optionMapping.put(option.getName(), option);
        }

        Guild guild = event.getGuild();
        TextChannel activeChannel = event.getTextChannel();
        Member actor = event.getMember();
        Member bot = null;
        if (guild != null) bot = guild.getSelfMember();

        return new EventData.Builder()
                .setActor(actor)
                .setBot(bot)
                .setGuild(guild)
                .setActiveChannel(activeChannel)
                .setOptions(optionMapping)
                .build();
    }
}
