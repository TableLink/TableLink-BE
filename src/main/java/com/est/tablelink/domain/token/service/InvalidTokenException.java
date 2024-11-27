package com.est.tablelink.domain.token.service;

public class InvalidTokenException extends Throwable {

    public InvalidTokenException(String message) {
        super(message);
    }
}
