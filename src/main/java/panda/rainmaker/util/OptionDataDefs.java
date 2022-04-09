package panda.rainmaker.util;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;

public enum OptionDataDefs {
    ROLE (OptionType.STRING, "role", "The role to interact with.", true),
    EMOTE (OptionType.STRING, "emote", "The emote to interact with.", true),
    LIST (OptionType.STRING, "list", "The list to interact with.", true),
    CHANNEL (OptionType.CHANNEL, "channel", "The channel to interact with.", true),
    USER (OptionType.USER, "user", "The user to interact with.", true),
    REASON (OptionType.STRING, "reason", "The reason for this command.", true);

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean isRequired;

    private final HashMap<String, OptionData> instances = new HashMap<>();

    OptionDataDefs(OptionType type, String name, String description, boolean isRequired) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
    }

    public OptionData asOptionData() {
        OptionData data = instances.get(this.name);
        if (data == null) {
            data = new OptionData(this.type, this.name, this.description, this.isRequired);
            instances.put(this.name, data);
        }
        return data;
    }
}
