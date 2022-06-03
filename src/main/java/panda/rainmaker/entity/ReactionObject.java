package panda.rainmaker.entity;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class ReactionObject {

    private final boolean isEmoji;
    private final String value;

    public ReactionObject(boolean isEmoji, String value) {
        this.isEmoji = isEmoji;
        this.value = value;
    }

    public boolean isEmoji() {
        return isEmoji;
    }

    public String getValue() {
        return value;
    }

    public String getDisplay(Guild guild) {
        if (isEmoji()) {
            return EmojiParser.parseToUnicode(value);
        } else {
            Emote emote = guild.getJDA().getEmoteById(value);

            if (emote != null) {
                return emote.getAsMention();
            } else {
                return null;
            }
        }
    }
}
