package panda.rainmaker.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import panda.rainmaker.database.models.GlobalSettings;
import panda.rainmaker.entity.BotMongoClient;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class GlobalDao {

    public GlobalDao() {}

    public static GlobalSettings retrieveGlobalSettings() {
        MongoDatabase db = BotMongoClient.getDatabase("rainmaker");
        MongoCollection<GlobalSettings> collection = BotMongoClient.getGlobalCollection(db);

        FindIterable<GlobalSettings> settings = collection.find(GlobalSettings.class);
        return settings.first();
    }

    public static void saveGlobalSettings(GlobalSettings globalSettings) {
        MongoDatabase db = BotMongoClient.getDatabase("rainmaker");
        MongoCollection<GlobalSettings> collection = BotMongoClient.getGlobalCollection(db);

        UpdateResult result = collection.replaceOne(eq("_id", globalSettings.getId()), globalSettings);
        if (result.getMatchedCount() > 0) {
            if (result.getModifiedCount() > 0) {
                System.out.println("Successfully updated global settings.");
            } else {
                System.out.println("No changes were made to settings.");
            }
        } else {
            collection.insertOne(globalSettings);
            System.out.println("Successfully saved global settings.");
        }
    }

    public static GlobalSettings loadDefaults(GlobalSettings globalSettings) {
        boolean missingRSA = globalSettings.getRsa_link()==null;
        boolean missingPrefix = globalSettings.getWiki_prefix()==null;
        boolean missingSuffix = globalSettings.getWiki_suffix()==null;
        boolean missingPerms = globalSettings.getCanChangeValues()==null || globalSettings.getCanChangeValues().isEmpty();

        if (missingRSA)
            globalSettings.setRsa_link("https://resources.robloxdevelopmentassistance.org/api/posts.json");

        if (missingPrefix)
            globalSettings.setWiki_prefix("https://api.swiftype.com/api/v1/public/engines/search.json?callback=jQuery33104738122062067418_1647795735305&q=");

        if (missingSuffix)
            globalSettings.setWiki_suffix("&engine_key=ybGG5yhKbpKUQQW4Dwrw&fetch_fields%5Bapi-reference%5D%5B%5D=display_title&fetch_fields%5Bapi-reference%5D%5B%5D=hide_from_search&fetch_fields%5Bapi-reference%5D%5B%5D=category&fetch_fields%5Bapi-reference%5D%5B%5D=url&fetch_fields%5Bapi-reference%5D%5B%5D=segment&fetch_fields%5Bapi-reference%5D%5B%5D=summary&fetch_fields%5Bapi-reference%5D%5B%5D=api_type&fetch_fields%5Barticles%5D%5B%5D=display_title&fetch_fields%5Barticles%5D%5B%5D=hide_from_search&fetch_fields%5Barticles%5D%5B%5D=category&fetch_fields%5Barticles%5D%5B%5D=url&fetch_fields%5Barticles%5D%5B%5D=segment&fetch_fields%5Barticles%5D%5B%5D=summary&fetch_fields%5Barticles%5D%5B%5D=api_type&fetch_fields%5Brecipes%5D%5B%5D=display_title&fetch_fields%5Brecipes%5D%5B%5D=hide_from_search&fetch_fields%5Brecipes%5D%5B%5D=category&fetch_fields%5Brecipes%5D%5B%5D=url&fetch_fields%5Brecipes%5D%5B%5D=segment&fetch_fields%5Brecipes%5D%5B%5D=summary&fetch_fields%5Brecipes%5D%5B%5D=api_type&fetch_fields%5Bvideos%5D%5B%5D=display_title&fetch_fields%5Bvideos%5D%5B%5D=hide_from_search&fetch_fields%5Bvideos%5D%5B%5D=category&fetch_fields%5Bvideos%5D%5B%5D=url&fetch_fields%5Bvideos%5D%5B%5D=segment&fetch_fields%5Bvideos%5D%5B%5D=summary&fetch_fields%5Bvideos%5D%5B%5D=api_type&filters%5Bapi-reference%5D%5Blocale%5D=en-us&filters%5Barticles%5D%5Blocale%5D=en-us&filters%5Brecipes%5D%5Blocale%5D=en-us&filters%5Bvideos%5D%5Blocale%5D=en-us&per_page=10&highlight_fields%5Bapi-reference%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Barticles%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Brecipes%5D%5Bbody%5D%5Bfallback%5D=false&highlight_fields%5Bvideos%5D%5Bbody%5D%5Bfallback%5D=false&spelling=retry&_=1647795735313");

        if (missingPerms)
            globalSettings.setCanChangeValues(List.of("169208961533345792"));

        saveGlobalSettings(globalSettings);

        return globalSettings;
    }

    public static GlobalSettings loadDefaults() {
        return loadDefaults(new GlobalSettings());
    }
}