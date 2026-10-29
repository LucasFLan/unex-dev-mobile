package com.unex.chamaAi.usuario.domain.entities;

import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.SenhaHash;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private final UUID id;
    private String nome;
    private LocalDate dataNascimento;
    private final Cpf cpf;
    private Email email;
    private SenhaHash senha;
    private String telefone;
    private boolean verificado;

    private Usuario(UUID id, String nome, LocalDate dataNascimento, Cpf cpf, Email email,
                    SenhaHash senha, String telefone, boolean verificado) {
        this.id = Objects.requireNonNull(id, "O id é obrigatório.");
        this.cpf = Objects.requireNonNull(cpf, "O CPF é obrigatório.");
        this.email = Objects.requireNonNull(email, "O email é obrigatório.");
        this.senha = Objects.requireNonNull(senha, "A senha é obrigatória.");
        this.nome = validarNome(nome);
        this.dataNascimento = validarDataNascimento(dataNascimento);
        this.telefone = telefone;
        this.verificado = verificado;
    }

    public static Usuario criar(String nome, LocalDate dataNascimento, Cpf cpf, Email email,
                                SenhaHash senha, String telefone) {
        return new Usuario(UUID.randomUUID(), nome, dataNascimento, cpf, email, senha, telefone, false);
    }

    public static Usuario reconstituir(UUID id, String nome, LocalDate dataNascimento, Cpf cpf, Email email,
                                       SenhaHash senha, String telefone, boolean verificado) {
        return new Usuario(id, nome, dataNascimento, cpf, email, senha, telefone, verificado);
    }

    public void alterarNome(String novoNome) {
        this.nome = validarNome(novoNome);
    }

    public void alterarDataNascimento(LocalDate novaDataNascimento) {
        this.dataNascimento = validarDataNascimento(novaDataNascimento);
    }

    public void alterarTelefone(String novoTelefone) {
        this.telefone = novoTelefone;
    }

    public void alterarSenha(SenhaHash novaSenha) {
        this.senha = Objects.requireNonNull(novaSenha, "A nova senha não pode ser nula.");
    }

    public void atualizarEmail(Email novoEmail) {
        Objects.requireNonNull(novoEmail, "O novo email não pode ser nulo.");
        if (novoEmail.equals(this.email)) {
            return;
        }
        this.email = novoEmail;
        this.verificado = false;
    }

    public void verificarConta() {
        if (this.verificado) {
            throw new IllegalStateException("Usuário já está verificado.");
        }
        this.verificado = true;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        return nome.trim();
    }

    private static LocalDate validarDataNascimento(LocalDate dataNascimento) {
        Objects.requireNonNull(dataNascimento, "A data de nascimento é obrigatória.");
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode estar no futuro.");
        }
        return dataNascimento;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public SenhaHash getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isVerificado() {
        return verificado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario outro)) return false;
        return id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
