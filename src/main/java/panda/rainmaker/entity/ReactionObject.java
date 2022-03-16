package panda.rainmaker.entity;

public class ReactionObject {

    private boolean isEmoji = false;
    private String value = "";

    public ReactionObject(boolean isEmoji, String value) {
        this.isEmoji = isEmoji;
        this.value = value;
    }

    public boolean isEmoji() {
        return isEmoji;
    }

    public String getValue() {
        return value;
    }
}
