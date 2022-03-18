package panda.rainmaker.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;
import panda.rainmaker.util.RoleGiverCache;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        Member member = event.getMember();
        if (member == null) return;
        if (member.getUser().isBot()) return;

        String messageId = event.getMessageId();
        MessageReaction reaction = event.getReaction();
        MessageReaction.ReactionEmote emote = reaction.getReactionEmote();
        String emoteId = null;

        if (emote.isEmoji()) {
            emoteId = emote.getEmoji();
        } else {
            emoteId = emote.getEmote().getId();
        }

        String roleId = RoleGiverCache.getRoleIdFromEmote(messageId, emoteId);
        if (roleId != null) {
            Guild guild = event.getGuild();
            Role role = guild.getRoleById(roleId);

            if (role != null) {
                System.out.println("Adding " + role.getAsMention() + " to " + member.getAsMention());
                guild.addRoleToMember(member, role).queue();
            } else {
                System.out.print("No role for " + roleId);
                TextChannel channel = event.getTextChannel();
                channel.retrieveMessageById(messageId).queue(msg -> {
                    if (emote.isEmote()) {
                        msg.removeReaction(emote.getEmote(), member.getUser()).queue();
                    } else {
                        msg.removeReaction(emote.getEmoji(), member.getUser()).queue();
                    }
                });
            }
        } else {
            System.out.println("No roleId");
            TextChannel channel = event.getTextChannel();
            channel.retrieveMessageById(messageId).queue(msg -> {
                if (emote.isEmote()) {
                    msg.removeReaction(emote.getEmote(), member.getUser()).queue();
                } else {
                    msg.removeReaction(emote.getEmoji(), member.getUser()).queue();
                }
            });
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);
        Member member = event.getMember();
        if (member == null) return;
        if (member.getUser().isBot()) return;

        String messageId = event.getMessageId();
        MessageReaction reaction = event.getReaction();
        MessageReaction.ReactionEmote emote = reaction.getReactionEmote();
        String emoteId = null;

        if (emote.isEmoji()) {
            emoteId = emote.getEmoji();
        } else {
            emoteId = emote.getEmote().getId();
        }

        String roleId = RoleGiverCache.getRoleIdFromEmote(messageId, emoteId);
        if (roleId != null) {
            Guild guild = event.getGuild();
            Role role = guild.getRoleById(roleId);

            if (role != null) {
                System.out.println("Removing " + role.getAsMention() + " from " + member.getAsMention());
                guild.removeRoleFromMember(member, role).queue();
            }
        }
    }
}
