package panda.rainmaker;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import panda.rainmaker.command.CommandObject;
import panda.rainmaker.command.Commands;
import panda.rainmaker.entity.BotMongoClient;
import panda.rainmaker.listeners.MessageListener;
import panda.rainmaker.listeners.ReactionListener;
import panda.rainmaker.listeners.SlashCommandListener;

import javax.security.auth.login.LoginException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Bot {

//    AWSCredentialsProvider awsCreds;

    public Bot(final String token, final String dbURI) throws LoginException, InterruptedException {
//        awsCreds = DefaultAWSCredentialsProviderChain.getInstance();

        JDABuilder builder = JDABuilder.createDefault(token);

        configureMemoryUsage(builder);
        try {
            connectDb(dbURI);
        } catch (UnknownHostException uhe) {
            System.err.println("Failed to connect to DB.");
            System.exit(0);
        }

        builder.addEventListeners(new MessageListener(), new SlashCommandListener(), new ReactionListener());

        builder.addEventListeners(new ListenerAdapter() {
            @Override
            public void onGuildJoin(@NotNull GuildJoinEvent event) {
                super.onGuildJoin(event);
                setupGuild(event.getGuild());
            }
        });

        JDA jda = builder.build();
        jda.awaitReady();

        jda.getGuilds().forEach(guild -> {
            if (guild == null) {
                System.err.println("Failed to fetch guild");
            } else {
                setupGuild(guild);
            }
        });
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Missing bot token or dbUri.");
            System.exit(0);
        }

        try {
            new Bot(args[0], args[1]);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Failed to login bot.");
            System.exit(0);
        }
    }

    private void configureMemoryUsage(JDABuilder builder) {
        builder
            // Enable the bulk delete event.
            .setBulkDeleteSplittingEnabled(false)
            // Disable All CacheFlags.
            .disableCache(Arrays.asList(CacheFlag.values()))
            // Enable specific CacheFlags
            .enableCache(CacheFlag.EMOTE)
            // Only cache members who are online or owner of the guild.
            .setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER))
            // Disable member chunking on startup.
            .setChunkingFilter(ChunkingFilter.NONE)
            // Disable All intents
            .disableIntents(Arrays.asList(GatewayIntent.values()))
            // Enable specific intents.
            .enableIntents(
                    GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS
            )
            // Consider guilds with more than 50 members as "large".
            // Large guilds will only provide online members in the setup and thus reduce
            // bandwidth if chunking is disabled.
            .setLargeThreshold(50)
            // Set Activity to display the version.
            .setActivity(Activity.playing("v0.4_alpha"));
    }

    private static void connectDb(final String dbURI) throws UnknownHostException {
        new BotMongoClient(dbURI);
    }

    private static void setupGuild(Guild guild) {
        System.out.println("Setting up commands for: " + guild.getId());
        CommandListUpdateAction commands = guild.updateCommands();

        List<CommandObject> commandObjectList = Commands.getCommands();
        List<SlashCommandData> slashCommandData = commandObjectList.stream()
                .map(CommandObject::getSlashImplementation)
                .collect(Collectors.toList());

        CommandListUpdateAction addCommandAction = commands.addCommands(slashCommandData);

        // Add slash commands for channel reactions
//        commands.addCommands(
//                Commands.slash("enable-reactions", "Enable a reaction for each message in a channel.")
//                        .addOptions(
//                                new OptionData(OptionType.CHANNEL, "channel",
//                                        "The channel to enable on.")
//                                        .setRequired(true)
//                                        .setChannelTypes(ChannelType.TEXT),
//                                new OptionData(OptionType.STRING, "emote",
//                                        "The reaction to add to messages.")
//                                        .setRequired(true)),
//
//                Commands.slash("disable-reactions", "Disable a reaction for each message in a channel")
//                        .addOptions(
//                                new OptionData(OptionType.CHANNEL, "channel",
//                                        "The channel to enable on.")
//                                        .setRequired(true)
//                                        .setChannelTypes(ChannelType.TEXT),
//                                new OptionData(OptionType.STRING, "emote",
//                                        "The reaction to add to messages."))
//        );
//
//        // Add slash commands for role reactions
//        commands.addCommands(
//                Commands.slash("set-role-channel", "Set the channel for all role associated messages")
//                        .addOptions(new OptionData(OptionType.CHANNEL, "channel",
//                                "The channel to associate.")
//                                .setRequired(true)
//                                .setChannelTypes(ChannelType.TEXT)),
//
//                Commands.slash("create-role-list", "Create a new role list.")
//                        .addOptions(new OptionData(OptionType.STRING, "list-name",
//                                "The name of the list to create.")
//                                .setRequired(true)),
//
//                Commands.slash("delete-role-list", "Deletes a role list.")
//                        .addOptions(new OptionData(OptionType.STRING, "list-name",
//                                "The name of the list to delete.")
//                                .setRequired(true)),
//
//                Commands.slash("add-role-to-list", "Add a role to the list")
//                        .addOptions(
//                                new OptionData(OptionType.STRING, "list-name",
//                                        "The list to add the role to.")
//                                        .setRequired(true),
//                                new OptionData(OptionType.ROLE, "role",
//                                        "The role to add to the list.")
//                                        .setRequired(true),
//                                new OptionData(OptionType.STRING, "emote",
//                                        "The emote to associate with the role.")
//                                        .setRequired(true)),
//
//                Commands.slash("remove-role-from-list", "Remove a role from a list")
//                        .addOptions(
//                                new OptionData(OptionType.STRING, "list-name",
//                                        "The list to remove the role from.")
//                                        .setRequired(true),
//                                new OptionData(OptionType.ROLE, "role",
//                                        "The role to remove from the list.")
//                                        .setRequired(true))
//        );
//
//        // Add slash commands for wiki/article
//        commands.addCommands(
//                Commands.slash("article", "Fetch an article from the RDA Articles.")
//                        .addOptions(
//                                new OptionData(OptionType.STRING, "title",
//                                        "A title filter to apply to the search.")
//                                        .setRequired(true),
//                                new OptionData(OptionType.STRING, "author",
//                                        "An author filter to apply to the search.")),
//
//                Commands.slash("wiki", "Search the Roblox Developer Hub for an Article.")
//                        .addOptions(
//                                new OptionData(OptionType.STRING, "query",
//                                        "The query to use when searching.")
//                                        .setRequired(true),
//                                new OptionData(OptionType.STRING, "category",
//                                        "The category filter to apply to the result(s).")
//                                        .addChoice("api-reference", "API Reference")
//                                        .addChoice("articles", "Articles")
//                                        .addChoice("learn-roblox", "Learn Roblox")
//                                        .addChoice("recipes", "Recipes")
//                                        .addChoice("resources", "Resources")
//                                        .addChoice("videos", "Videos"))
//        );

        commands.queue();
    }
}
