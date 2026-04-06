package br.edu.infnet.guildaaventureiros.repository;

import br.edu.infnet.guildaaventureiros.domain.Aventureiro;
import br.edu.infnet.guildaaventureiros.domain.Classe;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class AventureiroFakeRepository {
    private final List<Aventureiro> data = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public AventureiroFakeRepository() {
        initSeed();
    }

    private void initSeed() {
        Classe[] classes = Classe.values();
        for (int i = 1; i <= 100; i++) {
            Aventureiro a = new Aventureiro();
            a.setId(sequence.getAndIncrement());
            a.setNome("Aventureiro " + i);
            a.setClasse(classes[i % classes.length]);
            a.setNivel((i % 20) + 1);
            a.setAtivo(i % 7 != 0);     // alguns inativos
            a.setCompanheiro(null);
            data.add(a);
        }
    }

    public synchronized Aventureiro save(Aventureiro aventureiro) {
        if (aventureiro.getId() == null) {
            aventureiro.setId(sequence.getAndIncrement());
            data.add(aventureiro);
        } else {
            // update se existir, add se não encontrar por id
            boolean atualizado = false;
            for (int i = 0; i < data.size(); i++) {
                if (Objects.equals(data.get(i).getId(), aventureiro.getId())) {
                    data.set(i, aventureiro);
                    atualizado = true;
                    break;
                }
            }
            if (!atualizado) {
                data.add(aventureiro);
            }
        }
        return aventureiro;
    }

    public Optional<Aventureiro> findById(Long id) {
        return data.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
    }

    public List<Aventureiro> findAll() {
        return new ArrayList<>(data);
    }
}
