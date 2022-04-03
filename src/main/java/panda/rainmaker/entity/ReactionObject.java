package panda.rainmaker.entity;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import panda.rainmaker.util.ChannelReactionCache;

public class ReactionObject {

    private boolean isEmoji = false;
    private String value = "";

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
