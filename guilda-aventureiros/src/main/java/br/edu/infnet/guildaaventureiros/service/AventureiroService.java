package br.edu.infnet.guildaaventureiros.service;

import br.edu.infnet.guildaaventureiros.api.dto.*;
import br.edu.infnet.guildaaventureiros.api.exception.InvalidRequestException;
import br.edu.infnet.guildaaventureiros.api.exception.NotFoundException;
import br.edu.infnet.guildaaventureiros.domain.*;
import br.edu.infnet.guildaaventureiros.repository.AventureiroFakeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AventureiroService {
    private final AventureiroFakeRepository repository = new AventureiroFakeRepository();
    private final String MSG_AVENTUREIRO_NAO_ENCONTRADO = "aventureiro nao encontrado";
    private final String MSG_CLASSE_INVALIDA = "classe invalida";
    private final String MSG_ESPECIE_INVALIDA = " especie invalida";

    // ====== 1 - Criar ======
    public AventureiroDetalheResponse criar(AventureiroCreateRequest req) {
        List<String> erros = validarAventureiro(req.getNome(), req.getClasse(), req.getNivel());
        if (!erros.isEmpty()) throw new InvalidRequestException(erros);

        Classe classe = Classe.valueOf(req.getClasse().trim().toUpperCase());

        Aventureiro a = new Aventureiro();
        a.setId(null);
        a.setNome(req.getNome().trim());
        a.setClasse(classe);
        a.setNivel(req.getNivel());
        a.setAtivo(true);
        a.setCompanheiro(null);

        repository.save(a);
        return toDetalhe(a);
    }

    // ====== 2 - Listar com filtros e paginação ======
    public List<AventureiroResumoResponse> listar(String classeStr, Boolean ativo, Integer nivelMin,
                                                  int page, int size, Map<String, Integer> metaOut) {
        List<String> erros = new ArrayList<>();
        if (page < 0) erros.add("page não pode ser negativo");
        if (size < 1 || size > 50) erros.add("size deve estar entre 1 e 50");
        Classe classeParsed = null;

        if (classeStr != null) {
            try {
                classeParsed = Classe.valueOf(classeStr.trim().toUpperCase());
            } catch (Exception e) {
                erros.add(MSG_CLASSE_INVALIDA);
            }
        }

        if (nivelMin != null && nivelMin < 1) erros.add("nivelMin deve ser maior ou igual a 1");
        if (!erros.isEmpty()) throw new InvalidRequestException(erros);

        final Classe classeFiltro = classeParsed;
        final Boolean ativoFiltro = ativo;
        final Integer nivelMinFiltro = nivelMin;

        List<Aventureiro> filtrados = repository.findAll().stream()
            .filter(a -> classeFiltro == null || a.getClasse() == classeFiltro)
            .filter(a -> ativoFiltro == null || a.isAtivo() == ativoFiltro)
            .filter(a -> nivelMinFiltro == null || a.getNivel() >= nivelMinFiltro)
            .sorted(Comparator.comparingLong(Aventureiro::getId))
            .toList();


        int total = filtrados.size();
        int totalPages = (int) Math.ceil(total / (double) size);

        int from = page * size;
        int to = Math.min(from + size, total);

        List<Aventureiro> pagina = (from >= total) ? List.of() : filtrados.subList(from, to);

        metaOut.put("X-Total-Count", total);
        metaOut.put("X-Page", page);
        metaOut.put("X-Size", size);
        metaOut.put("X-Total-Pages", totalPages);

        return pagina.stream().map(this::toResumo).toList();
    }

    // ====== 3 - Buscar por id ======
    public AventureiroDetalheResponse buscar(Long id) {
        Aventureiro a = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));
        return toDetalhe(a);
    }

    // ====== 4 - Atualizar nome/classe/nivel ======
    public AventureiroDetalheResponse atualizar(Long id, AventureiroUpdateRequest req) {
        Aventureiro existente = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));

        List<String> erros = validarAventureiro(req.getNome(), req.getClasse(), req.getNivel());
        if (!erros.isEmpty()) throw new InvalidRequestException(erros);

        Classe classe = Classe.valueOf(req.getClasse().trim().toUpperCase());
        existente.setNome(req.getNome().trim());
        existente.setClasse(classe);
        existente.setNivel(req.getNivel());

        repository.save(existente);
        return toDetalhe(existente);
    }

    // ====== 5 - Encerrar vínculo (ativo = false) ======
    public void encerrar(Long id) {
        Aventureiro a = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));
        a.setAtivo(false);
        repository.save(a);
    }

    // ====== 6 - Recrutar (ativo = true) ======
    public void recrutar(Long id) {
        Aventureiro a = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));
        a.setAtivo(true);
        repository.save(a);
    }

    // ====== 7 - Definir/Substituir companheiro ======
    public AventureiroDetalheResponse definirCompanheiro(Long id, CompanheiroRequest req) {
        Aventureiro a = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));

        List<String> erros = validarCompanheiro(req.getNome(), req.getEspecie(), req.getLealdade());
        if (!erros.isEmpty()) throw new InvalidRequestException(erros);

        Especie especie = Especie.valueOf(req.getEspecie().trim().toUpperCase());
        Companheiro c = new Companheiro(req.getNome().trim(), especie, req.getLealdade());
        a.setCompanheiro(c);
        repository.save(a);
        return toDetalhe(a);
    }

    // ====== 8 - Remover companheiro ======
    public AventureiroDetalheResponse removerCompanheiro(Long id) {
        Aventureiro a = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(MSG_AVENTUREIRO_NAO_ENCONTRADO));
        a.setCompanheiro(null);
        repository.save(a);
        return toDetalhe(a);
    }

    // ====== Validadores ======
    private List<String> validarAventureiro(String nome, String classe, Integer nivel) {
        List<String> erros = new ArrayList<>();
        if (nome == null || nome.trim().isEmpty())
            erros.add("nome é obrigatório e não pode ser vazio");
        if (classe == null) {
            erros.add(MSG_CLASSE_INVALIDA);
        } else {
            try {
                Classe.valueOf(classe.trim().toUpperCase());
            } catch (Exception e) {
                erros.add(MSG_CLASSE_INVALIDA);
            }
        }
        if (nivel == null || nivel < 1)
            erros.add("nivel deve ser maior ou igual a 1");
        return erros;
    }

    private List<String> validarCompanheiro(String nome, String especie, Integer lealdade) {
        List<String> erros = new ArrayList<>();
        if (nome == null || nome.trim().isEmpty())
            erros.add("nome do companheiro é obrigatório");
        if (especie == null) {
            erros.add(MSG_ESPECIE_INVALIDA);
        } else {
            try {
                Especie.valueOf(especie.trim().toUpperCase());
            } catch (Exception e) {
                erros.add(MSG_ESPECIE_INVALIDA);
            }
        }
        if (lealdade == null || lealdade < 0 || lealdade > 100)
            erros.add("lealdade deve estar entre 0 e 100");
        return erros;
    }

    // ====== Conversores DTO ======
    private AventureiroResumoResponse toResumo(Aventureiro a) {
        return new AventureiroResumoResponse(a.getId(), a.getNome(), a.getClasse(), a.getNivel(), a.isAtivo());
    }

    private AventureiroDetalheResponse toDetalhe(Aventureiro a) {
        return new AventureiroDetalheResponse(a.getId(), a.getNome(), a.getClasse(), a.getNivel(), a.isAtivo(), a.getCompanheiro());
    }
}
