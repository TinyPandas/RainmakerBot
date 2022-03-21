package panda.rainmaker.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import panda.rainmaker.Bot;
import panda.rainmaker.entity.ReactionObject;
import panda.rainmaker.rda_article.ArticleResponse;
import panda.rainmaker.rda_article.ArticleResponseItem;
import panda.rainmaker.util.ChannelReactionCache;
import panda.rainmaker.http.HttpRequest;
import panda.rainmaker.http.HttpResult;
import panda.rainmaker.util.RoleGiverCache;
import panda.rainmaker.wiki.RecordItem;
import panda.rainmaker.wiki.Records;
import panda.rainmaker.wiki.RobloxResponse;

import java.util.List;
import java.util.Map;

public class SlashCommandListener extends ListenerAdapter {

    private ObjectMapper objectMapper;

    // TODO: Store in global settings
    public static final String ARTICLE_ENDPOINT = "https://resources.robloxdevelopmentassistance.org/api/posts.json";
    public static ArticleResponse articleResponse;

    public SlashCommandListener() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
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
            case "article":
                String titleQuery = event.getOption("title").getAsString();
                OptionMapping authorQueryOption = event.getOption("author");
                String authorQuery = null;
                if (authorQueryOption != null) {
                    authorQuery = authorQueryOption.getAsString();
                }
                findArticle(event, titleQuery, authorQuery);
                break;
            case "wiki":
                String searchQuery = event.getOption("query").getAsString();
                String category = null;
                OptionMapping categoryOption = event.getOption("category");
                if (categoryOption != null) {
                    category = categoryOption.getAsString();
                }
                searchWiki(event, searchQuery, category);
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

    public void findArticle(SlashCommandInteractionEvent event, String titleQuery, String authorQuery) {
        HttpResult result = HttpRequest.getResult(ARTICLE_ENDPOINT, 0, 0);
        if (result.isFailed()) {
            articleResponse = null;
            System.out.println("Failed to parse articles.");
        }

        if (articleResponse == null) {
            try {
                List<ArticleResponseItem> items = objectMapper.readValue(result.getMessage(), new TypeReference<List<ArticleResponseItem>>() {
                });
                System.out.println("Loaded " + items.size() + " articles.");
                articleResponse = new ArticleResponse();
                articleResponse.setArticleResponse(items);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                event.reply("Failed to parse articles.").queue();
                return;
            }
        }

        List<ArticleResponseItem> queryResults = articleResponse.getArticlesFromQuery(titleQuery, authorQuery);
        if (queryResults == null) {
            event.reply("Articles were not loaded correctly.").queue();
            return;
        }
        if (queryResults.size() == 0) {
            event.reply("There were no results. [" + articleResponse.getArticleResponse().size()
                    + " articles searched.]").queue();
            return;
        }

        Member member = event.getMember();
        String author = member != null ? member.getEffectiveName() : event.getUser().getName();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Results for " + titleQuery);
        builder.setDescription(author);

        for (ArticleResponseItem item : queryResults) {
            builder.addField(
                    String.format("%s - by %s", item.getTitle(), item.getAuthor()),
                    String.format("[Link to Article](https://resources.robloxdevelopmentassistance.org%s)%n%s", item.getUrl(), item.getExcerpt()),
                    false);
        }

        event.replyEmbeds(builder.build()).queue();
    }

    public void searchWiki(SlashCommandInteractionEvent event, String searchQuery, String category) {
        // TODO: Store in global settings
        String wikiPrefix = "https://api.swiftype.com/api/v1/public/engines/search.json?callback=jQuery33104738122062067418_1647795735305&q=";
        String wikiSuffix = "&engine_key=ybGG5yhKbpKUQQW4Dwrw&fetch_fields%5Bapi-reference%5D%5B%5D=display_title&fetch_fields%5Bapi-reference%5D%5B%5D=hide_from_search&fetch_fields%5Bapi-reference%5D%5B%5D=category&fetch_fields%5Bapi-reference%5D%5B%5D=url&fetch_fields%5Bapi-reference%5D%5B%5D=segment&fetch_fields%5Bapi-reference%5D%5B%5D=summary&fetch_fields%5Bapi-reference%5D%5B%5D=api_type&fetch_fields%5Barticles%5D%5B%5D=display_title&fetch_fields%5Barticles%5D%5B%5D=hide_from_search&fetch_fields%5Barticles%5D%5B%5D=category&fetch_fields%5Barticles%5D%5B%5D=url&fetch_fields%5Barticles%5D%5B%5D=segment&fetch_fields%5Barticles%5D%5B%5D=summary&fetch_fields%5Barticles%5D%5B%5D=api_type&fetch_fields%5Brecipes%5D%5B%5D=display_title&fetch_fields%5Brecipes%5D%5B%5D=hide_from_search&fetch_fields%5Brecipes%5D%5B%5D=category&fetch_fields%5Brecipes%5D%5B%5D=url&fetch_fields%5Brecipes%5D%5B%5D=segment&fetch_fields%5Brecipes%5D%5B%5D=summary&fetch_fields%5Brecipes%5D%5B%5D=api_type&fetch_fields%5Bvideos%5D%5B%5D=display_title&fetch_fields%5Bvideos%5D%5B%5D=hide_from_search&fetch_fields%5Bvideos%5D%5B%5D=category&fetch_fields%5Bvideos%5D%5B%5D=url&fetch_fields%5Bvideos%5D%5B%5D=segment&fetch_fields%5Bvideos%5D%5B%5D=summary&fetch_fields%5Bvideos%5D%5B%5D=api_type&filters%5Bapi-reference%5D%5Blocale%5D=en-us&filters%5Barticles%5D%5Blocale%5D=en-us&filters%5Brecipes%5D%5Blocale%5D=en-us&filters%5Bvideos%5D%5Blocale%5D=en-us&per_page=10&highlight_fields%5Bapi-reference%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Barticles%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Brecipes%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Bvideos%5D%5Bbody%5D%5Bfallback%5D=false&spelling=retry&_=1647795735313";

        event.reply("Searching...").queue(msg -> {
            String urlQuery = searchQuery.replaceAll("\\s", "%20");
            String call = wikiPrefix + urlQuery + wikiSuffix;

            HttpResult result = HttpRequest.getResult(call, 45, 1);
            if (result.isFailed()) {
                msg.editOriginal(result.getMessage()).queue();
                return;
            }

            RobloxResponse robloxResult;

            try {
                robloxResult = objectMapper.readValue(result.getMessage(), RobloxResponse.class);
            } catch (JsonProcessingException e) {
                msg.editOriginal("Failed to parse results. [" + e.getMessage() + "]").queue();
                e.printStackTrace();
                return;
            }

            if (robloxResult.getRecordCount() > 0) {
                Records records = robloxResult.getRecords();
                Member member = event.getMember();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Results for " + searchQuery);
                builder.setDescription(member != null ? member.getEffectiveName() : event.getUser().getName());

                if (category == null || category.length() == 0) {
                    Map<String, RecordItem> resultList = records.getFirstOfEach();

                    for (Map.Entry<String, RecordItem> recordItem : resultList.entrySet()) {
                        RecordItem recordObject = recordItem.getValue();
                        String summary = recordObject.getSummary().replaceAll("\\<.*?\\>", "");
                        String url = recordObject.getUrl();

                        if (summary.length() == 0) {
                            summary = recordObject.getHighlight().getBody();
                            if (summary == null || summary.length() == 0) {
                                summary = "Link to Documentation";
                            } else {
                                summary = summary.replaceAll("\\<.*?\\>", "");
                            }
                        }

                        String display = String.format("[%s](%s)", summary, url);

                        builder.addField(String.format("[%s]: %s", recordItem.getKey(), recordObject.getDisplayTitle()),
                                display, false);
                    }
                } else {
                    List<RecordItem> categoryList = records.getCategory(category);

                    for (RecordItem recordItem : categoryList) {
                        String summary = recordItem.getSummary().replaceAll("\\<.*?\\>", "");
                        String url = recordItem.getUrl();

                        if (summary.length() == 0) {
                            summary = recordItem.getHighlight().getBody();
                            if (summary == null || summary.length() == 0) {
                                summary = "Link to Documentation";
                            } else {
                                summary = summary.replaceAll("\\<.*?\\>", "");
                            }
                        }

                        String display = String.format("[%s](%s)", summary, url);

                        builder.addField(String.format("[%s]: %s", category, recordItem.getDisplayTitle()),
                                display, false);
                    }
                }

                msg.editOriginalEmbeds(builder.build()).setContent("").queue();
            } else {
                msg.editOriginal("No results found.").queue();
            }
        });
    }
}
