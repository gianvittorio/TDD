package com.gianvittorio;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CadastroPessoasTeste {
    @Test
    @DisplayName("Deve criar o cadastro de pessoas.")
    public void deveCriarCadastro() {
        // Cenario e execucao
        CadastroPessoas cadastroPessoas = new CadastroPessoas();

        // Verificacao
        Assertions.assertThat(cadastroPessoas.getPessoas()).isEmpty();
    }

    @Test
    @DisplayName("Deve adicionar uma pessoa.")
    public void deveAdicionarUmaPessoa() {
        // Cenario
        CadastroPessoas cadastroPessoas = new CadastroPessoas();

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Wilson");

        // Execucao
        cadastroPessoas.adicionarPessoa(pessoa);

        // Verificacao
        Assertions.assertThat(cadastroPessoas.getPessoas())
                .isNotEmpty()
                .hasSize(1)
                .contains(pessoa);
    }

    //@Test(expected = PessoaSemNomeException.class)
    @Test
    @DisplayName("Nao deve adicionar pessoa sem nome.")
    public void naoDeveAdicionarPessoaComNomeVazio() {
        // Cenario
        CadastroPessoas cadastroPessoas = new CadastroPessoas();
        Pessoa pessoa = new Pessoa();

        // Execucao
        org.junit.jupiter.api.Assertions.assertThrows(
                PessoaSemNomeException.class,
                () -> cadastroPessoas.adicionarPessoa(pessoa));

        // Verificacao
    }

    @Test
    @DisplayName("Deve remover pessoa.")
    public void deveRemoverUmaPessoa() {
        // Cenario
        CadastroPessoas cadastroPessoas = new CadastroPessoas();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Wilson");
        cadastroPessoas.adicionarPessoa(pessoa);

        // Execucao
        cadastroPessoas.removerPessoa(pessoa);

        // Verificacao
        Assertions.assertThat(cadastroPessoas.getPessoas())
                .isEmpty();
    }

    //@Test(expected = CadastroVazioException.class)
    @Test
    @DisplayName("Nao deve remover pessoa, se nome nao constar na lista.")
    public void naoDeveRemoverPessoaComNomeInexistente() {
        // Cenario
        CadastroPessoas cadastroPessoas = new CadastroPessoas();

        // Execucao
        Pessoa pessoa = new Pessoa();
        org.junit.jupiter.api.Assertions.assertThrows(
                CadastroVazioException.class,
                () -> cadastroPessoas.removerPessoa(pessoa)
        );

        // Verificacao
    }
}
