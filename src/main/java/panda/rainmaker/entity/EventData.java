package panda.rainmaker.entity;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import panda.rainmaker.util.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventData {

    private final Guild guild;
    private final Member actor;
    private final Member bot;
    private final TextChannel activeChannel;
    private final HashMap<String, OptionMapping> optionMapping;

    private EventData(Guild guild, Member actor, Member bot,
                      TextChannel activeChannel, HashMap<String, OptionMapping> optionMapping) {
        this.guild = guild;
        this.actor = actor;
        this.bot = bot;
        this.activeChannel = activeChannel;
        this.optionMapping = optionMapping;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getActor() {
        return actor;
    }

    public Member getBot() {
        return bot;
    }

    public TextChannel getActiveChannel() {
        return activeChannel;
    }

    public List<OptionMapping> getOptions() {
        return new ArrayList<>(optionMapping.values());
    }

    public Option<?> getOption(String optionName) {
        OptionMapping option = optionMapping.get(optionName);

        switch(option.getType().getKey()) {
            case 1:
            case 2:
                throw new UnsupportedOperationException("SUB_COMMAND and SUB_COMMAND_GROUP are not supported.");
            case 3:
                return new Option<>(option.getAsString());
            case 4:
                return new Option<>(option.getAsInt());
            case 5:
                return new Option<>(option.getAsBoolean());
            case 6:
                return new Option<>(option.getAsUser());
            case 7:
                return new Option<>(option.getAsGuildChannel());
            case 8:
                return new Option<>(option.getAsRole());
            case 9:
                return new Option<>(option.getAsMentionable());
            case 10:
                return new Option<>(option.getAsDouble());
            case 11:
                return new Option<>(option.getAsAttachment());
            default:
                return null;
        }
    }

    public static class Builder {

        private Guild guild;
        private Member actor;
        private Member bot;
        private TextChannel activeChannel;
        private HashMap<String, OptionMapping> options;

        public Builder setGuild(Guild guild) {
            this.guild = guild;
            return this;
        }

        public Builder setActor(Member actor) {
            this.actor = actor;
            return this;
        }

        public Builder setBot(Member bot) {
            this.bot = bot;
            return this;
        }

        public Builder setActiveChannel(TextChannel activeChannel) {
            this.activeChannel = activeChannel;
            return this;
        }

        public Builder setOptions(HashMap<String, OptionMapping> options) {
            this.options = options;
            return this;
        }

        public EventData build() {
            return new EventData(guild, actor, bot, activeChannel, options);
        }
    }
}
