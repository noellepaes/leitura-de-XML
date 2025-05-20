package com.noelle.leitura_de_XML.exception;
/**
 * Exceção personalizada para indicar que houve uma duplicidade de dados.
 * Esta exceção é lançada quando um registro já existe no banco de dados
 * e não deve ser inserido novamente.
 */ 
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProcessamentoException.class )
    public ResponseEntity<String> handleProcessamentoException(ProcessamentoException e) {
        log.error("Erro de processamento: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de processamento: " + e.getMessage());
    }
    
    @ExceptionHandler(DuplicidadeException.class)
    public ResponseEntity<String> handleDuplicidadeException(DuplicidadeException e) {
        log.warn("Duplicidade detectada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicidade detectada: " + e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erro inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
    }
}