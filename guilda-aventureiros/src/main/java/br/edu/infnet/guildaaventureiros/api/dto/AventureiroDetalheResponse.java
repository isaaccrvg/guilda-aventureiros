package br.edu.infnet.guildaaventureiros.api.dto;

import br.edu.infnet.guildaaventureiros.domain.Classe;
import br.edu.infnet.guildaaventureiros.domain.Companheiro;

public class AventureiroDetalheResponse {
    private Long id;
    private String nome;
    private Classe classe;
    private Integer nivel;
    private boolean ativo;
    private Companheiro companheiro;

    public AventureiroDetalheResponse(Long id, String nome, Classe classe, Integer nivel, boolean ativo, Companheiro companheiro) {
        this.id = id;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.ativo = ativo;
        this.companheiro = companheiro;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Classe getClasse() { return classe; }
    public Integer getNivel() { return nivel; }
    public boolean isAtivo() { return ativo; }
    public Companheiro getCompanheiro() { return companheiro; }
}
