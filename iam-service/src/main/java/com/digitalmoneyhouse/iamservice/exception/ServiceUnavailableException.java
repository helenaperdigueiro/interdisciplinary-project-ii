package com.digitalmoneyhouse.iamservice.exception;

public class ServiceUnavailableException extends BusinessException {
    public ServiceUnavailableException(String serviceName) {
        super(500, String.format("The %s Service is unavailable.", serviceName));
    }
}
