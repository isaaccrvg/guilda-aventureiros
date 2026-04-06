package br.edu.infnet.guildaaventureiros.api.controller;

import br.edu.infnet.guildaaventureiros.api.dto.*;
import br.edu.infnet.guildaaventureiros.service.AventureiroService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aventureiros")
public class AventureiroController {

    private final AventureiroService service;

    public AventureiroController(AventureiroService service) {
        this.service = service;
    }

    // 1. Registrar aventureiro
    @PostMapping
    public ResponseEntity<AventureiroDetalheResponse> criar(@RequestBody AventureiroCreateRequest req) {
        AventureiroDetalheResponse created = service.criar(req);
        return ResponseEntity
            .created(URI.create("/aventureiros/" + created.getId()))
            .body(created);
    }

    // 2. Listar aventureiros com paginação/filtros
    @GetMapping
    public ResponseEntity<List<AventureiroResumoResponse>> listar(
        @RequestParam(required = false) String classe,
        @RequestParam(required = false) Boolean ativo,
        @RequestParam(name = "nivelMin", required = false) Integer nivelMin,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Integer> meta = new HashMap<>();
        List<AventureiroResumoResponse> body = service.listar(classe, ativo, nivelMin, page, size, meta);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(meta.get("X-Total-Count")));
        headers.add("X-Page", String.valueOf(meta.get("X-Page")));
        headers.add("X-Size", String.valueOf(meta.get("X-Size")));
        headers.add("X-Total-Pages", String.valueOf(meta.get("X-Total-Pages")));

        return ResponseEntity.ok().headers(headers).body(body);
    }

    // 3. Consultar por id
    @GetMapping("/{id}")
    public AventureiroDetalheResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    // 4. Atualizar nome/classe/nivel
    @PutMapping("/{id}")
    public AventureiroDetalheResponse atualizar(@PathVariable Long id, @RequestBody AventureiroUpdateRequest req) {
        return service.atualizar(id, req);
    }

    // 5. Encerrar vínculo (ativo=false)
    @PatchMapping("/{id}/encerrar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void encerrar(@PathVariable Long id) {
        service.encerrar(id);
    }

    // 6. Recrutar novamente (ativo=true)
    @PatchMapping("/{id}/recrutar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recrutar(@PathVariable Long id) {
        service.recrutar(id);
    }

    // 7. Definir/Substituir companheiro
    @PutMapping("/{id}/companheiro")
    public AventureiroDetalheResponse definirCompanheiro(@PathVariable Long id, @RequestBody CompanheiroRequest req) {
        return service.definirCompanheiro(id, req);
    }

    // 8. Remover companheiro
    @DeleteMapping("/{id}/companheiro")
    public AventureiroDetalheResponse removerCompanheiro(@PathVariable Long id) {
        return service.removerCompanheiro(id);
    }
}
