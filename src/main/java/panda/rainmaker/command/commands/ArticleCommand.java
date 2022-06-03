package panda.rainmaker.command.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.http.HttpRequest;
import panda.rainmaker.http.HttpResult;
import panda.rainmaker.rda_article.ArticleResponse;
import panda.rainmaker.rda_article.ArticleResponseItem;

import java.util.List;

import static panda.rainmaker.util.PandaUtil.*;

public class ArticleCommand extends CommandObject {

    public static ArticleResponse articleResponse;

    public ArticleCommand() {
        super("article", "Fetch an article from the RDA Articles site.");
        addOptionData(new OptionData(OptionType.STRING, "title",
                "The title query for the search.", true));
        addOptionData(new OptionData(OptionType.STRING, "author",
                "The author filter to apply to the result(s)."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        String titleQuery = null, authorQuery = null;

        try {
            titleQuery = getStringFromOption("Title query", event.getOption("title"));
            try {
                authorQuery = getStringFromOption("Author filter", event.getOption("author"));
            } catch (Exception ignored) {}
        } catch (Exception e) {
            failEvent(event, e.getMessage());
        }

        HttpResult result = HttpRequest.getResult(globalSettings.getRsa_link(), 0, 0);
        if (result.isFailed()) {
            articleResponse = null;
            System.out.println("Failed to parse articles.");
        }

        if (articleResponse == null) {
            try {
                List<ArticleResponseItem> items = objectMapper.readValue(result.getMessage(), new TypeReference<>() { });
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
}
