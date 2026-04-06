package br.edu.infnet.guildaaventureiros.api.exception;

public class ApiExceptionHandler extends RuntimeException {
    public ApiExceptionHandler(String message) {
        super(message);
    }
}
