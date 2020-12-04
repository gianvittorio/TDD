package com.gianvittorio;

public class Pessoa implements Comparable<Pessoa> {
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int compareTo(Pessoa other) {
        return nome.compareTo(other.nome);
    }
}
