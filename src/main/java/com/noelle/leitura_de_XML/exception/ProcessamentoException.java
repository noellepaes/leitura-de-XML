package com.noelle.leitura_de_XML.exception;

public class ProcessamentoException extends RuntimeException {
    
    public ProcessamentoException(String message) {
        super(message);
    }
    
    public ProcessamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
