package panda.rainmaker.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import panda.rainmaker.database.models.GuildSettings;
import panda.rainmaker.entity.BotMongoClient;

import static com.mongodb.client.model.Filters.eq;

public class GuildDao {

    // TODO: Implement cache
    public GuildDao() {}

    public static GuildSettings fetchGuildSettings(String guildId) {
        //TODO: Implement cache
        return retrieveGuildSettings(guildId);
    }

    private static GuildSettings retrieveGuildSettings(String guildId) {
        MongoDatabase db = BotMongoClient.getDatabase("rainmaker");
        MongoCollection<GuildSettings> collection = BotMongoClient.getGuildCollection(db);
        FindIterable<GuildSettings> settings = collection.find(eq("guildId", guildId));
        return settings.first();
    }

    public static boolean saveGuildSettings(GuildSettings guildSettings) {
        MongoDatabase db = BotMongoClient.getDatabase("rainmaker");
        MongoCollection<GuildSettings> collection = BotMongoClient.getGuildCollection(db);

        UpdateResult result = collection.replaceOne(eq("guildId", guildSettings.getGuildId()), guildSettings);
        if (result.getMatchedCount() > 0) {
            if (result.getModifiedCount() > 0) {
                System.out.println("Successfully updated settings for guild: " + guildSettings.getGuildId() + ".");
                return true;
            } else {
                System.out.println("No changes were made to settings.");
                return false;
            }
        } else {
            collection.insertOne(guildSettings);
            System.out.println("Successfully saved settings for guild: " + guildSettings.getGuildId() + ".");
            return true;
        }
    }

    public static GuildSettings loadDefaults(String guildId) {
        GuildSettings guildSettings = new GuildSettings();
        guildSettings.setGuildId(guildId);

        return guildSettings;
    }
}
