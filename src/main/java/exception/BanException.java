package exception;

import java.sql.Timestamp;

public class BanException extends RuntimeException {
    private final Timestamp bannedUntil;

    public BanException(Timestamp bannedUntil) {
        super("User is banned until " + bannedUntil.toString());
        this.bannedUntil = bannedUntil;
    }

    public Timestamp getBannedUntil() {
        return bannedUntil;
    }
}

