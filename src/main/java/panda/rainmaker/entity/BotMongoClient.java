package panda.rainmaker.entity;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import panda.rainmaker.database.GlobalDao;
import panda.rainmaker.database.GuildDao;
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

    private static String dbUri;
    private static MongoClient mongoClient;
    public BotMongoClient(final String dbUri) throws UnknownHostException {
        BotMongoClient.dbUri = dbUri;
        isConnected = false;

        getMongoClient();

        if (mongoClient != null) {
            new GlobalDao();
            new GuildDao();
        }
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
        return mongoClient.getDatabase(dbName);
    }

    public static MongoCollection<GlobalSettings> getGlobalCollection(final MongoDatabase db) {
        return db.getCollection("global", GlobalSettings.class);
    }

    public static MongoCollection<GuildSettings> getGuildCollection(final MongoDatabase db) {
        return db.getCollection("guild", GuildSettings.class);
    }
}
