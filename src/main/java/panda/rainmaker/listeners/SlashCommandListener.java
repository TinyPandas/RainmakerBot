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
import org.jetbrains.annotations.NotNull;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.command.Commands;
import panda.rainmaker.database.GlobalDao;
import panda.rainmaker.database.GuildDao;
import panda.rainmaker.database.models.GlobalSettings;
import panda.rainmaker.database.models.GuildSettings;
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

    private final ObjectMapper objectMapper;

    private static GlobalSettings globalSettings;
    public static ArticleResponse articleResponse;

    public SlashCommandListener() {
        objectMapper = new ObjectMapper();
        globalSettings = GlobalDao.retrieveGlobalSettings();
        if (globalSettings == null) {
            globalSettings = GlobalDao.loadDefaults();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        String guildId = event.getGuild().getId();
        GuildSettings guildSettings = GuildDao.fetchGuildSettings(guildId);
        if (guildSettings == null) {
            guildSettings = GuildDao.loadDefaults(guildId);
        }

        Commands.getCommand(event.getName()).execute(event, guildSettings);

//        switch(event.getName()) {
//            case "set-role-channel":
//                TextChannel channel = event.getOption("channel").getAsTextChannel();
//                setRoleChannel(event, guildSettings, channel);
//                break;
//            case "create-role-list":
//                String newListName = event.getOption("list-name").getAsString();
//                createRoleList(event, guildSettings, newListName);
//                break;
//            case "add-role-to-list":
//                String addToListName = event.getOption("list-name").getAsString();
//                Role roleToAdd = event.getOption("role").getAsRole();
//                String emote = event.getOption("emote").getAsString();
//                addRoleToList(event, guildSettings, addToListName, roleToAdd, emote);
//                break;
//            case "remove-role-from-list":
//                String removeFromListName = event.getOption("list-name").getAsString();
//                Role roleToRemove = event.getOption("role").getAsRole();
//                removeRoleFromList(event, guildSettings, removeFromListName, roleToRemove);
//                break;
//            case "delete-role-list":
//                String deleteListName = event.getOption("list-name").getAsString();
//                deleteRoleList(event, guildSettings, deleteListName);
//                break;
//            case "article":
//                String titleQuery = event.getOption("title").getAsString();
//                OptionMapping authorQueryOption = event.getOption("author");
//                String authorQuery = null;
//                if (authorQueryOption != null) {
//                    authorQuery = authorQueryOption.getAsString();
//                }
//                findArticle(event, titleQuery, authorQuery);
//                break;
//            case "wiki":
//                String searchQuery = event.getOption("query").getAsString();
//                String category = null;
//                OptionMapping categoryOption = event.getOption("category");
//                if (categoryOption != null) {
//                    category = categoryOption.getAsString();
//                }
//                searchWiki(event, searchQuery, category);
//                break;
//            default:
//                System.out.println("No event for this " + event.getName());
//                break;
//        }
    }

    public void setRoleChannel(SlashCommandInteractionEvent event, GuildSettings guildSettings, TextChannel channel) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        RoleGiverCache.setRoleChannelId(guildSettings, channel.getId());
        hook.sendMessage("Successfully set the role channel to " + channel.getAsMention()).queue();
    }

    public void createRoleList(SlashCommandInteractionEvent event, GuildSettings guildSettings, String listName) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        String createResult = RoleGiverCache.createList(guildSettings, event.getGuild(), listName);
        hook.sendMessage(createResult).queue();
    }

    public void addRoleToList(SlashCommandInteractionEvent event, GuildSettings guildSettings, String listName, Role role, String reaction) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Guild guild = event.getGuild();
        if (RoleGiverCache.isInvalidList(guildSettings, guild, listName)) {
            hook.sendMessage("`" + listName + "` does not exist.").queue();
            return;
        }

        ReactionObject reactionObject = getReactionCacheValue(guild, reaction);
        if (reactionObject == null) {
            hook.sendMessage("Failed to parse emoji/emote. Please ensure input value is correct and try again.").queue();
            return;
        }

        String reactionValue = reactionObject.getValue();
        String addRoleResult = RoleGiverCache.addRoleToList(guildSettings, guild, listName, role, reactionValue);
        hook.sendMessage(addRoleResult).queue();
    }

    public void removeRoleFromList(SlashCommandInteractionEvent event, GuildSettings guildSettings, String listName, Role role) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        Guild guild = event.getGuild();
        if (RoleGiverCache.isInvalidList(guildSettings, guild, listName)) {
            hook.sendMessage("`" + listName + "` does not exist.").queue();
            return;
        }

        RoleGiverCache.removeRoleFromList(guildSettings, guild, listName, role.getId());
        hook.sendMessage("Successfully removed " + role.getAsMention() + " from " + listName).queue();
    }

    public void deleteRoleList(SlashCommandInteractionEvent event, GuildSettings guildSettings, String listName) {
        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        hook.setEphemeral(true);
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            hook.sendMessage("You do not have permission to enable reactions in this channel.").queue();
            return;
        }

        String deleteResult = RoleGiverCache.deleteRoleList(guildSettings, event.getGuild(), listName);
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
        HttpResult result = HttpRequest.getResult(globalSettings.getRsa_link(), 0, 0);
        if (result.isFailed()) {
            articleResponse = null;
            System.out.println("Failed to parse articles.");
        }

        if (articleResponse == null) {
            try {
                List<ArticleResponseItem> items = objectMapper.readValue(result.getMessage(), new TypeReference<List<ArticleResponseItem>>() { });
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
        event.reply("Searching...").queue(msg -> {
            String urlQuery = searchQuery.replaceAll("\\s", "%20");
            String call = globalSettings.getWiki_prefix() + urlQuery + globalSettings.getWiki_suffix();

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
