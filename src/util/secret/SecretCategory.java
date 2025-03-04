package util.secret;

public enum SecretCategory {
    SLACK_BOT_URL("SLACK_BOT_URL");
    public final String key;
    private SecretCategory(String key) {
        this.key = key;
    };
}
