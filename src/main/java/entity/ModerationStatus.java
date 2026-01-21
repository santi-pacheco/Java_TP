package entity;

public enum ModerationStatus {
    PENDING_MODERATION("PENDING_MODERATION"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    SPOILER("SPOILER");
    
    private final String value;
    
    ModerationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ModerationStatus fromString(String value) {
        for (ModerationStatus status : ModerationStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de moderación desconocido: " + value);
    }
}