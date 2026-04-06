package br.edu.infnet.guildaaventureiros.api.exception;

import java.util.List;

public class InvalidRequestException extends RuntimeException {
    private final List<String> detalhes;

    public InvalidRequestException(List<String> detalhes) {
        super("Solicitação inválida");
        this.detalhes = detalhes;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}
