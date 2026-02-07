package com.poi.yow_point.application.exceptions;

import java.time.OffsetDateTime;

public class AccountLockedException extends RuntimeException {
    private final OffsetDateTime lockedUntil;

    public AccountLockedException(String message, OffsetDateTime lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }

    public OffsetDateTime getLockedUntil() {
        return lockedUntil;
    }
}
