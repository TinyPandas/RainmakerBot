package panda.rainmaker.database.models;

import org.bson.types.ObjectId;

import java.util.List;

public class GlobalSettings {

    private ObjectId _id;

    private String rsa_link;
    private String wiki_prefix;
    private String wiki_suffix;
    private List<String> canChangeValues;

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getRsa_link() {
        return rsa_link;
    }

    public void setRsa_link(String rsa_link) {
        this.rsa_link = rsa_link;
    }

    public String getWiki_prefix() {
        return wiki_prefix;
    }

    public void setWiki_prefix(String wiki_prefix) {
        this.wiki_prefix = wiki_prefix;
    }

    public String getWiki_suffix() {
        return wiki_suffix;
    }

    public void setWiki_suffix(String wiki_suffix) {
        this.wiki_suffix = wiki_suffix;
    }

    public List<String> getCanChangeValues() {
        return canChangeValues;
    }

    public void setCanChangeValues(List<String> canChangeValues) {
        this.canChangeValues = canChangeValues;
    }
}
