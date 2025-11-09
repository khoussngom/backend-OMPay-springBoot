package com.khouss.UsersMicroservice.exception;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCompteNotFound(CompteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(OMPayResponse.error(OMPayMessages.COMPTE_INEXISTANT));
    }

    @ExceptionHandler(CompteAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCompteExists(CompteAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(OMPayResponse.error(OMPayMessages.COMPTE_DEJA_EXISTANT));
    }

    @ExceptionHandler(DestinataireNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDestinataire(DestinataireNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(OMPayResponse.error(OMPayMessages.COMPTE_DESTINATAIRE_INEXISTANT));
    }

    @ExceptionHandler(CodeMarchandNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCodeMarchand(CodeMarchandNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(OMPayResponse.error(OMPayMessages.CODE_MARCHAND_INEXISTANT));
    }

    @ExceptionHandler(SoldeInsuffisantException.class)
    public ResponseEntity<Map<String, Object>> handleSolde(SoldeInsuffisantException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OMPayResponse.error(OMPayMessages.SOLDE_INSUFFISANT));
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClientNotFound(ClientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(OMPayResponse.error(OMPayMessages.CLIENT_INEXISTANT));
    }

    @ExceptionHandler(NumeroInvalideException.class)
    public ResponseEntity<Map<String, Object>> handleNumeroInvalide(NumeroInvalideException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OMPayResponse.error(OMPayMessages.NUMERO_INVALIDE));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegal(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }
}
