package com.gianvittorio.controller;

import com.gianvittorio.model.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class MyController {
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Client> salvar(@RequestBody Client cliente) {
        // service.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cliente);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Client> obterDadosDoClient(@PathVariable Long id) {
        Client client = new Client("Fulano", "000.000.000-00");

        return ResponseEntity.status(HttpStatus.OK)
                .body(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> atualizar(@PathVariable Long id, @RequestBody Client cliente) {
        // Client client = service.buscar(id)
        // service.update(client);

        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        // Client cliente = service.buscar(id);
        // service.delete(cliente);
    }
}
