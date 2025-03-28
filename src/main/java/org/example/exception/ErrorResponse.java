package org.example.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;
    private String path;
    private String exceptionClass;

    public ErrorResponse(ZonedDateTime now, int value, String reasonPhrase, String message, String path) {
        this.timestamp = now;
        this.status = value;
        this.error = reasonPhrase;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(ZonedDateTime now, int value, String reasonPhrase, String s, Class<? extends Exception> aClass, String message, String replace) {
        this.exceptionClass = aClass.getName();
        this.timestamp = now;
        this.status = value;
        this.error = reasonPhrase;
        this.message = message;
        this.path = path;
    }
}
