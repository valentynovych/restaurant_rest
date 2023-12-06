package com.restaurant_rest.exception;

import java.io.Serial;

public class ForbiddenUpdateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenUpdateException(String message) {
        super(message);
    }

}
