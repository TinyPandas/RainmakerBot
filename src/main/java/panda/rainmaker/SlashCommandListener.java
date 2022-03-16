package panda.rainmaker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import panda.rainmaker.util.ChannelReactionCache;
import panda.rainmaker.util.RoleGiverCache;

import java.util.List;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("Event triggered.");
        if (event.getGuild() == null) {
            return;
        }

        switch(event.getName()) {
            case "enable-reactions":
                TextChannel enableChannel = event.getOption("channel").getAsTextChannel();
                String enableReaction = event.getOption("reaction").getAsString();
                enableReaction(event, enableChannel, enableReaction);
                break;
            case "disable-reactions":
                TextChannel disableChannel = event.getOption("channel").getAsTextChannel();
                OptionMapping disableReactionOption = event.getOption("reaction");
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

        String reactionId = null;
        if (reaction.contains(":")) {
            int index = reaction.indexOf(":", reaction.indexOf(":") + 1);
            reactionId = reaction.substring(index + 1, reaction.length() - 1);
        }

        Emote emoteToAdd = null;

        if (reactionId != null) {
            emoteToAdd = event.getGuild().getEmoteById(reactionId);
        } else {
            List<Emote> emotes = event.getGuild().getEmotesByName(reaction, true);
            if (emotes.size() == 0) {
                hook.sendMessage("There were no emotes associated with this name.").queue();
                return;
            }

            if (emotes.size() > 1) {
                hook.sendMessage("There are too many emotes associated with this name.").queue();
                return;
            }
            emoteToAdd = emotes.get(0);
        }


        ChannelReactionCache.addReactionToChannel(channel.getId(), emoteToAdd.getId());
        hook.sendMessage("Successfully enabled " + emoteToAdd.getAsMention() + " in " + channel.getAsMention()).queue();
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

        if (reaction != null) {
            String reactionId = null;
            if (reaction.contains(":")) {
                int index = reaction.indexOf(":", reaction.indexOf(":") + 1);
                reactionId = reaction.substring(index + 1, reaction.length() - 1);
            }

            Emote emoteToRemove = null;

            if (reactionId != null) {
                emoteToRemove = event.getGuild().getEmoteById(reactionId);
            } else {
                List<Emote> emotes = event.getGuild().getEmotesByName(reaction, true);
                if (emotes.size() == 0) {
                    hook.sendMessage("There were no emotes associated with this name.").queue();
                    return;
                }
                emoteToRemove = emotes.get(0);
            }

            ChannelReactionCache.removeReactionFromChannel(channelId, emoteToRemove.getId());
            hook.sendMessage("Successfully disabled " + emoteToRemove.getAsMention() + " in " + channel.getAsMention()).queue();
        } else {
            ChannelReactionCache.removeReactionsInChannel(channelId);
            hook.sendMessage("Successfully disabled all reactions in " + channel.getAsMention()).queue();
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

        RoleGiverCache.setRoleChannelId(channel.getId());
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

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.MANAGE_ROLES)) {
            hook.sendMessage("I do not have permission to manage roles.").queue();
            return;
        }

        String reactionId = null;
        if (reaction.contains(":")) {
            int index = reaction.indexOf(":", reaction.indexOf(":") + 1);
            reactionId = reaction.substring(index + 1, reaction.length() - 1);
        }

        Emote emoteToAdd = null;

        if (reactionId != null) {
            emoteToAdd = event.getGuild().getEmoteById(reactionId);
        } else {
            List<Emote> emotes = event.getGuild().getEmotesByName(reaction, true);
            if (emotes.size() == 0) {
                hook.sendMessage("There were no emotes associated with this name.").queue();
                return;
            }

            if (emotes.size() > 1) {
                hook.sendMessage("There are too many emotes associated with this name.").queue();
                return;
            }
            emoteToAdd = emotes.get(0);
        }

        if (emoteToAdd == null) {
            hook.sendMessage("Are you sure that emote (" + reaction + ") is a part of this server?").queue();
            return;
        }

        RoleGiverCache.addRoleToList(event.getGuild(), listName, role.getId(), emoteToAdd.getId());
        hook.sendMessage("Successfully added " + role.getAsMention() + " to " + listName + ".").queue();
    }
}
