package panda.rainmaker.listeners;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClient;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.SentimentScore;
import com.vdurmont.emoji.EmojiManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import panda.rainmaker.util.ChannelReactionCache;

import java.util.Set;

public class MessageListener extends ListenerAdapter {

//    private final AWSCredentialsProvider awsCreds;
//    private AmazonComprehend comprehend;
//
//    public MessageListener(AWSCredentialsProvider awsCreds) {
//        this.awsCreds = awsCreds;
//        comprehend = getComprehend();
//    }
//
//    private AmazonComprehend getComprehend() {
//        if (comprehend == null) {
//            comprehend = AmazonComprehendClientBuilder.standard()
//                    .withCredentials(awsCreds)
//                    .withRegion("us-west-2")
//                    .build();
//        }
//        return comprehend;
//    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        TextChannel channel = event.getTextChannel();
        Set<String> reactions = ChannelReactionCache.getReactionsForChannel(channel.getId());
//        String messageContent = message.getContentRaw();
//
//        AmazonComprehend comprehend = getComprehend();
//        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest()
//                .withText(messageContent)
//                .withLanguageCode("en");
//
//        DetectSentimentResult detectSentimentResult = comprehend.detectSentiment(detectSentimentRequest);
//        System.out.printf("Analyzing: \"%s\".%n", messageContent);
//        System.out.println(detectSentimentResult);
//        String sentiment = detectSentimentResult.getSentiment();
//        SentimentScore score = detectSentimentResult.getSentimentScore();
//
//        switch(sentiment) {
//            case "POSITIVE":
//                message.addReaction("\uD83D\uDC4D").queue();
//                break;
//            case "NEGATIVE":
//                if (score.getNegative() >= 0.90) {
//                    message.delete().queue();
//                } else {
//                    message.addReaction("\uD83E\uDD2C").queue();
//                }
//                break;
//            case "NEUTRAL":
//            case "MIXED":
//                message.addReaction("\uD83D\uDE10").queue();
//                break;
//        }

        if (reactions.size() > 0) {
            for (String reaction : reactions) {
                boolean isEmoji = EmojiManager.isEmoji(reaction);

                if (isEmoji) {
                    message.addReaction(reaction).queue();
                } else {
                    Emote emote = guild.getEmoteById(reaction);
                    if (emote != null) {
                        message.addReaction(emote).queue();
                    } else {
                        ChannelReactionCache.removeReactionFromChannel(channel.getId(), reaction);
                    }
                }
            }
        }
    }
}
