package br.edu.infnet.guildaaventureiros.api.error;

import br.edu.infnet.guildaaventureiros.api.exception.InvalidRequestException;
import br.edu.infnet.guildaaventureiros.api.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalid(InvalidRequestException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mensagem", "Solicitação inválida");
        body.put("detalhes", ex.getDetalhes());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mensagem", "Recurso não encontrado");
        body.put("detalhes", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
