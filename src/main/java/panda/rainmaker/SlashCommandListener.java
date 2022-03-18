package panda.rainmaker;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.util.ChannelReactionCache;
import panda.rainmaker.util.RoleGiverCache;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        switch(event.getName()) {
            case "enable-reactions":
                TextChannel enableChannel = event.getOption("channel").getAsTextChannel();
                String enableReaction = event.getOption("emote").getAsString();
                enableReaction(event, enableChannel, enableReaction);
                break;
            case "disable-reactions":
                TextChannel disableChannel = event.getOption("channel").getAsTextChannel();
                OptionMapping disableReactionOption = event.getOption("emote");
                String disableReaction = null;
                if (disableReactionOption != null) {
                    disableReaction = disableReactionOption.getAsString();
                }
                disableReaction(event, disableChannel, disableReaction);
                break;
            case "set-role-channel":
                TextChannel channel = event.getOption("channel").getAsTextChannel();
                setRoleChannel(event, channel);
                break;
            case "create-role-list":
                String newListName = event.getOption("list-name").getAsString();
                createRoleList(event, newListName);
                break;
            case "add-role-to-list":
                String addToListName = event.getOption("list-name").getAsString();
                Role roleToAdd = event.getOption("role").getAsRole();
                String emote = event.getOption("emote").getAsString();
                addRoleToList(event, addToListName, roleToAdd, emote);
                break;
            case "remove-role-from-list":
                String removeFromListName = event.getOption("list-name").getAsString();
                Role roleToRemove = event.getOption("role").getAsRole();
                removeRoleFromList(event, removeFromListName, roleToRemove);
                break;
            case "delete-role-list":
                String deleteListName = event.getOption("list-name").getAsString();
                deleteRoleList(event, deleteListName);
                break;
            default:
                System.out.println("No event for this " + event.getName());
                break;
        }
    }

    private void enableReaction(SlashCommandInteractionEvent event, TextChannel channel, String reaction) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) {
            hook.sendMessage("I do not have permission to send reactions.").queue();
            return;
        }

        String channelId = channel.getId();
        String channelMention = channel.getAsMention();

        Guild guild = event.getGuild();
        ReactionObject reactionObject = getReactionCacheValue(guild, reaction);
        if (reactionObject == null) {
            hook.sendMessage("Failed to parse emoji/emote. Please ensure input value is correct and try again.").queue();
            return;
        }

        String reactionValue = reactionObject.getValue();
        ChannelReactionCache.addReactionToChannel(channelId, reactionValue);
        if (reactionObject.isEmoji()) {
            hook.sendMessage("Successfully enabled " + EmojiParser.parseToUnicode(reactionValue) + " in " + channelMention).queue();
        } else {
            Emote emote = guild.getEmoteById(reactionValue);

            if (emote != null) {
                hook.sendMessage("Successfully enabled " + emote.getAsMention() + " in " + channelMention).queue();
            } else {
                ChannelReactionCache.removeReactionFromChannel(channelId, reactionValue);
                hook.sendMessage("Failed to enable" + reactionValue + " in " + channelMention + ". [Missing Emote?]").queue();
            }
        }
    }

    private void disableReaction(SlashCommandInteractionEvent event, TextChannel channel, String reaction) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) {
            hook.sendMessage("I do not have permission to send reactions.").queue();
            return;
        }

        String channelId = channel.getId();
        String channelMention = channel.getAsMention();

        Guild guild = event.getGuild();
        ReactionObject reactionObject = getReactionCacheValue(guild, reaction);
        if (reactionObject == null) {
            ChannelReactionCache.removeReactionsInChannel(channelId);
            hook.sendMessage("Successfully disabled all reaction events in " + channelMention + ".").queue();
            return;
        }

        String reactionValue = reactionObject.getValue();
        ChannelReactionCache.removeReactionFromChannel(channelId, reactionValue);
        if (reactionObject.isEmoji()) {
            hook.sendMessage("Successfully disabled " + EmojiParser.parseToUnicode(reactionValue) + " in " + channelMention).queue();
        } else {
            Emote emote = guild.getEmoteById(reactionValue);

            if (emote != null) {
                hook.sendMessage("Successfully disabled " + emote.getAsMention() + " in " + channelMention).queue();
            } else {
                hook.sendMessage("Forcefully disabled " + reactionValue + " in " + channelMention + ". [Missing Emote.]").queue();
            }
        }
    }

    public void setRoleChannel(SlashCommandInteractionEvent event, TextChannel channel) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        RoleGiverCache.setRoleChannelId(channel.getGuild(), channel.getId());
        hook.sendMessage("Successfully set the role channel to " + channel.getAsMention()).queue();
    }

    public void createRoleList(SlashCommandInteractionEvent event, String listName) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        String createResult = RoleGiverCache.createList(event.getGuild(), listName);
        hook.sendMessage(createResult).queue();
    }

    public void addRoleToList(SlashCommandInteractionEvent event, String listName, Role role, String reaction) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Guild guild = event.getGuild();
        if (!RoleGiverCache.isValidList(guild, listName)) {
            hook.sendMessage("`" + listName + "` does not exist.").queue();
            return;
        }

        ReactionObject reactionObject = getReactionCacheValue(guild, reaction);
        if (reactionObject == null) {
            hook.sendMessage("Failed to parse emoji/emote. Please ensure input value is correct and try again.").queue();
            return;
        }

        String reactionValue = reactionObject.getValue();
        String addRoleResult = RoleGiverCache.addRoleToList(guild, listName, role, reactionValue);
        hook.sendMessage(addRoleResult).queue();
    }

    public void removeRoleFromList(SlashCommandInteractionEvent event, String listName, Role role) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Guild guild = event.getGuild();
        if (!RoleGiverCache.isValidList(guild, listName)) {
            hook.sendMessage("`" + listName + "` does not exist.").queue();
            return;
        }

        RoleGiverCache.removeRoleFromList(guild, listName, role.getId());
        hook.sendMessage("Successfully removed " + role.getAsMention() + " from " + listName).queue();
    }

    public void deleteRoleList(SlashCommandInteractionEvent event, String listName) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        String deleteResult = RoleGiverCache.deleteRoleList(event.getGuild(), listName);
        hook.sendMessage(deleteResult).queue();
    }

    private ReactionObject getReactionCacheValue(Guild guild, String reaction) {
        List<String> unicodeEmojis = EmojiParser.extractEmojis(reaction);

        if (unicodeEmojis.size() > 0) {
            return new ReactionObject(true, unicodeEmojis.get(0));
        }

        if (reaction.contains(":")) {
            int index = reaction.indexOf(":", reaction.indexOf(":") + 1);
            String reactionId = reaction.substring(index + 1, reaction.length() - 1);
            return new ReactionObject(false, reactionId);
        }

        List<Emote> emotes = guild.getEmotesByName(reaction, true);
        if (emotes.size() == 0) {
            return null;
        }

        return new ReactionObject(false, emotes.get(0).getId());
    }
}
