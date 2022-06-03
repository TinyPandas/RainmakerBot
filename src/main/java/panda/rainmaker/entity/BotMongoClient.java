package panda.rainmaker.entity;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import panda.rainmaker.database.models.GlobalSettings;
import panda.rainmaker.database.models.GuildSettings;

import java.net.UnknownHostException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class BotMongoClient {

    /**
     * isConnected used a global flag for commands that require
     * a connection to the db.
     */
    public static boolean isConnected;
    public static boolean isTest;

    private static String dbUri;
    private static MongoClient mongoClient;

    public static void setupClient(final String dbUri, final boolean test) throws UnknownHostException {
        BotMongoClient.dbUri = dbUri;
        isConnected = false;
        isTest = test;

        getMongoClient();
    }

    public static MongoClient getMongoClient() throws UnknownHostException {
        if (mongoClient == null) {
            mongoClient = createMongoClient();
        }
        return mongoClient;
    }

    private static MongoClient createMongoClient() {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(dbUri))
                .build();

        return MongoClients.create(settings);
    }

    public static MongoDatabase getDatabase(final String dbName) {
        if (isTest) {
            return mongoClient.getDatabase(String.format("%s_test", dbName));
        }

        return mongoClient.getDatabase(dbName);
    }

    public static MongoCollection<GlobalSettings> getGlobalCollection(final MongoDatabase db) {
        return db.getCollection("global", GlobalSettings.class);
    }

    public static MongoCollection<GuildSettings> getGuildCollection(final MongoDatabase db) {
        return db.getCollection("guild", GuildSettings.class);
    }
}
