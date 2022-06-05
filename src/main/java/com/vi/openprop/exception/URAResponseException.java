package com.vi.openprop.exception;

import java.io.IOException;

/**
 * Custom exception for feteching DATA from URA
 */
public class URAResponseException extends IOException {
    public URAResponseException(String message) {
        super(message);
    }
}
