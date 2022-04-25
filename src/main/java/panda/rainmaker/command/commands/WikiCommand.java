package panda.rainmaker.command.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.EventData;
import panda.rainmaker.http.HttpRequest;
import panda.rainmaker.http.HttpResult;
import panda.rainmaker.wiki.RecordItem;
import panda.rainmaker.wiki.Records;
import panda.rainmaker.wiki.RobloxResponse;

import java.util.List;
import java.util.Map;

import static panda.rainmaker.util.PandaUtil.*;

public class WikiCommand extends CommandObject {

    public WikiCommand() {
        super("wiki", "Search the Roblox Developer Hub for an Article.");
        addOptionData(new OptionData(OptionType.STRING, "query",
                "The query to use when searching.", true));
        addOptionData(new OptionData(OptionType.STRING, "category",
                "The category filter to apply to the result(s).")
                        .addChoice("api-reference", "API Reference")
                        .addChoice("articles", "Articles")
                        .addChoice("learn-roblox", "Learn Roblox")
                        .addChoice("recipes", "Recipes")
                        .addChoice("resources", "Resources")
                        .addChoice("videos", "Videos"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, GuildSettings guildSettings) {
        EventData eventData = super.validate(event);
        String searchQuery = (String) eventData.getOption("query").getValue();
        String category = (String) eventData.getOption("category").getValue();

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
                        String summary = recordObject.getSummary().replaceAll("<.*?>", "");
                        String url = recordObject.getUrl();

                        if (summary.length() == 0) {
                            summary = recordObject.getHighlight().getBody();
                            if (summary == null || summary.length() == 0) {
                                summary = "Link to Documentation";
                            } else {
                                summary = summary.replaceAll("<.*?>", "");
                            }
                        }

                        String display = String.format("[%s](%s)", summary, url);

                        builder.addField(String.format("[%s]: %s", recordItem.getKey(), recordObject.getDisplayTitle()),
                                display, false);
                    }
                } else {
                    List<RecordItem> categoryList = records.getCategory(category);

                    for (RecordItem recordItem : categoryList) {
                        String summary = recordItem.getSummary().replaceAll("<.*?>", "");
                        String url = recordItem.getUrl();

                        if (summary.length() == 0) {
                            summary = recordItem.getHighlight().getBody();
                            if (summary == null || summary.length() == 0) {
                                summary = "Link to Documentation";
                            } else {
                                summary = summary.replaceAll("<.*?>", "");
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
