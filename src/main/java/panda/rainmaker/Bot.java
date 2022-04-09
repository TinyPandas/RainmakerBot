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

    public Bot(final String token, final String dbURI, final boolean test) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token);

        configureMemoryUsage(builder);
        try {
            connectDb(dbURI, test);
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
            boolean test = args[2] != null;
            System.out.println("Testing: " + test);

            new Bot(args[0], args[1], test);
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
            .enableCache(CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
            // Only cache members who are online or owner of the guild.
            .setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER))
            // Disable member chunking on startup.
            .setChunkingFilter(ChunkingFilter.NONE)
            // Disable All intents
            .disableIntents(Arrays.asList(GatewayIntent.values()))
            // Enable specific intents.
            .enableIntents(
                    GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES
            )
            // Consider guilds with more than 50 members as "large".
            // Large guilds will only provide online members in the setup and thus reduce
            // bandwidth if chunking is disabled.
            .setLargeThreshold(50)
            // Set Activity to display the version.
            .setActivity(Activity.playing("v0.6_alpha"));
    }

    private static void connectDb(final String dbURI, final boolean test) throws UnknownHostException {
        new BotMongoClient(dbURI, test);
    }

    private static void setupGuild(Guild guild) {
        System.out.println("Setting up commands for: " + guild.getId());
        CommandListUpdateAction commands = guild.updateCommands();

        List<CommandObject> commandObjectList = Commands.getCommands();
        List<SlashCommandData> slashCommandData = commandObjectList.stream()
                .map(CommandObject::getSlashImplementation)
                .collect(Collectors.toList());

        CommandListUpdateAction addCommandAction = commands.addCommands(slashCommandData);

        commands.queue();
    }
}
