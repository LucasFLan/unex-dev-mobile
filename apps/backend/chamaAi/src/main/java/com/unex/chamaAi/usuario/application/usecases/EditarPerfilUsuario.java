package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.usuario.application.dto.EditarPerfilCommand;
import com.unex.chamaAi.usuario.application.dto.PerfilUsuario;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.UsuarioNaoEncontradoException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;

import java.util.UUID;

public class EditarPerfilUsuario {

    private final UsuarioRepository usuarios;

    public EditarPerfilUsuario(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public PerfilUsuario executar(UUID id, EditarPerfilCommand comando) {
        Usuario usuario = usuarios.buscarPorId(id).orElseThrow(UsuarioNaoEncontradoException::new);

        usuario.alterarNome(comando.nome());
        usuario.alterarDataNascimento(comando.dataNascimento());
        usuario.alterarTelefone(comando.telefone());

        usuarios.salvar(usuario);
        return PerfilUsuario.de(usuario);
    }
}
