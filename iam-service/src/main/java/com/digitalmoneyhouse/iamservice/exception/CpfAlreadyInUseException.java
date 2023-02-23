package com.digitalmoneyhouse.iamservice.exception;

public class CpfAlreadyInUseException extends BusinessException {
    private String cpf;
    public CpfAlreadyInUseException(String cpf) {
        super(400, String.format("Value %s for field 'cpf' is already in use", cpf));
    }
}
