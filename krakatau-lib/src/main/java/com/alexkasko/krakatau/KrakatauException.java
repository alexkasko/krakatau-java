package com.alexkasko.krakatau;

/**
 * Library-specific exception
 *
 * @author alexkasko
 * Date: 9/29/13
 */
public class KrakatauException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message error message
     */
    public KrakatauException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message error message
     * @param cause cause error
     */
    public KrakatauException(String message, Throwable cause) {
        super(message, cause);
    }
}
