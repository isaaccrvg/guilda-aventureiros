package br.edu.infnet.guildaaventureiros.api.dto;

import br.edu.infnet.guildaaventureiros.domain.Classe;

public class AventureiroResumoResponse {
    private Long id;
    private String nome;
    private Classe classe;
    private Integer nivel;
    private boolean ativo;

    public AventureiroResumoResponse(Long id, String nome, Classe classe, Integer nivel, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Classe getClasse() { return classe; }
    public Integer getNivel() { return nivel; }
    public boolean isAtivo() { return ativo; }
}
