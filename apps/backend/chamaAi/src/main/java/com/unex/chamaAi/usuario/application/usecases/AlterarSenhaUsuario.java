package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.dto.AlterarSenhaCommand;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.SenhaAtualIncorretaException;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;

import java.util.UUID;

public class AlterarSenhaUsuario {

    private final UsuarioRepository usuarios;
    private final CriptografadorSenha criptografador;

    public AlterarSenhaUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador) {
        this.usuarios = usuarios;
        this.criptografador = criptografador;
    }

    public void executar(UUID id, AlterarSenhaCommand comando) {
        Senha novaSenha = new Senha(comando.novaSenha());
        Usuario usuario = usuarios.buscarPorId(id).orElseThrow(UsuarioNaoEncontradoException::new);

        if (!senhaAtualConfere(comando.senhaAtual(), usuario)) {
            throw new SenhaAtualIncorretaException();
        }

        usuario.alterarSenha(criptografador.criptografar(novaSenha));
        usuarios.salvar(usuario);
    }

    private boolean senhaAtualConfere(String senhaAtual, Usuario usuario) {
        try {
            return criptografador.confere(new Senha(senhaAtual), usuario.getSenha());
        } catch (DadoInvalidoException e) {
            // Uma senha fora das regras atuais nunca pode ser a senha cadastrada.
            return false;
        }
    }
}
