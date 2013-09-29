package com.alexkasko.krakatau;

/**
 * User: alexkasko
 * Date: 9/29/13
 */
public class KrakatauException extends RuntimeException {
    public KrakatauException(String message) {
        super(message);
    }

    public KrakatauException(String message, Throwable cause) {
        super(message, cause);
    }

    public KrakatauException(Throwable cause) {
        super(cause);
    }
}
