package panda.rainmaker;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class Bot {

    public Bot(final String token) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token);

        configureMemoryUsage(builder);
        builder.addEventListeners(new MessageListener(), new SlashCommandListener());

        JDA jda = builder.build();
        jda.awaitReady();

        Guild guild = jda.getGuildById("546033322401464320");
        if (guild == null) {
            System.err.println("Failed to fetch guild");
        } else {
            CommandListUpdateAction commands = guild.updateCommands();

            // Add slash commands for channel reactions
            commands.addCommands(
                    Commands.slash("enable-reactions", "Enable a reaction for each message in a channel.")
                            .addOptions(new OptionData(OptionType.CHANNEL, "channel",
                                    "The channel to enable on.")
                                    .setRequired(true)
                                    .setChannelTypes(ChannelType.TEXT))
                            .addOptions(new OptionData(OptionType.STRING, "emote",
                                    "The reaction to add to messages.")
                                    .setRequired(true)),

                    Commands.slash("disable-reactions", "Disable a reaction for each message in a channel")
                            .addOptions(new OptionData(OptionType.CHANNEL, "channel",
                                    "The channel to enable on.")
                                    .setRequired(true)
                                    .setChannelTypes(ChannelType.TEXT))
                            .addOptions(new OptionData(OptionType.STRING, "emote",
                                    "The reaction to add to messages."))
            );

            // Add slash commands for role reactions
            commands.addCommands(
                    Commands.slash("set-role-channel", "Set the channel for all role associated messages")
                            .addOptions(new OptionData(OptionType.CHANNEL, "channel",
                                    "The channel to associate.")
                                    .setRequired(true)
                                    .setChannelTypes(ChannelType.TEXT)),

                    Commands.slash("create-role-list", "Create a new role list.")
                            .addOptions(new OptionData(OptionType.STRING, "list-name",
                                    "Name of the list.")
                                    .setRequired(true)),

                    Commands.slash("add-role-to-list", "Add a role to the list")
                            .addOptions(new OptionData(OptionType.STRING, "list-name",
                                    "The list to add the role to.")
                                    .setRequired(true))
                            .addOptions(new OptionData(OptionType.ROLE, "role",
                                    "The role to add to the list.")
                                    .setRequired(true))
                            .addOptions(new OptionData(OptionType.STRING, "emote",
                                    "The emote to associate with the role.")
                                    .setRequired(true)),

                    Commands.slash("remote-role-from-list", "Remove a role from a list")
                            .addOptions(new OptionData(OptionType.STRING, "list-name",
                                    "The list to remove the role from.")
                                    .setRequired(true))
                            .addOptions(new OptionData(OptionType.ROLE, "role",
                                    "The role to remove from the list.")
                                    .setRequired(true))
            );

            commands.queue();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing bot token.");
            System.exit(0);
        }

        try {
            new Bot(args[0]);
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
                    GatewayIntent.GUILD_EMOJIS
            )
            // Consider guilds with more than 50 members as "large".
            // Large guilds will only provide online members in the setup and thus reduce
            // bandwidth if chunking is disabled.
            .setLargeThreshold(50)
            // Set Activity to display the version.
            .setActivity(Activity.playing("v0.1_alpha"));
    }
}
