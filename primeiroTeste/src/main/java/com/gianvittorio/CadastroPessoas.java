package com.gianvittorio;

import java.util.*;

public class CadastroPessoas {
    private SortedSet<Pessoa> pessoas;

    public CadastroPessoas() {
        pessoas = new TreeSet<>();
    }

    public Set<Pessoa> getPessoas() {
        return Collections.unmodifiableSet(pessoas);
    }

    public void adicionarPessoa(Pessoa pessoa) {
        String nome = pessoa.getNome();
        if (nome == null) {
            throw new PessoaSemNomeException();
        }

        pessoas.add(pessoa);
    }

    public void removerPessoa(Pessoa pessoa) {
        if (!pessoas.contains(pessoa)) {
            throw new CadastroVazioException();
        }

        pessoas.remove(pessoa);
    }
}
