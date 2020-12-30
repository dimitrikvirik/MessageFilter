package dimitrikvirik.git;

public enum MessageType {
    SAFE("safe"),
    VULGAR("vulgar"),
    DANGEROUS("dangerous");
    private String name;

    MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
