package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CpfJaCadastradoException;
import com.unex.chamaAi.usuario.domain.exceptions.EmailJaCadastradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Cpf;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;

public class CadastrarUsuario {

    private final UsuarioRepository usuarios;
    private final CriptografadorSenha criptografador;

    public CadastrarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador) {
        this.usuarios = usuarios;
        this.criptografador = criptografador;
    }

    public UsuarioCadastrado executar(CadastrarUsuarioCommand comando) {
        Cpf cpf = new Cpf(comando.cpf());
        Email email = new Email(comando.email());
        Senha senha = new Senha(comando.senha());

        if (usuarios.existePorCpf(cpf)) {
            throw new CpfJaCadastradoException();
        }
        if (usuarios.existePorEmail(email)) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = Usuario.criar(comando.nome(), comando.dataNascimento(), cpf, email,
                criptografador.criptografar(senha), comando.telefone());

        usuarios.salvar(usuario);
        return new UsuarioCadastrado(usuario.getId(), usuario.getNome(), usuario.getEmail().valor());
    }
}
