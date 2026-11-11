package com.unex.chamaAi.usuario.application.usecases;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.usuario.application.dto.LoginCommand;
import com.unex.chamaAi.usuario.application.dto.TokenAcesso;
import com.unex.chamaAi.usuario.application.ports.EmissorToken;
import com.unex.chamaAi.usuario.domain.entities.Usuario;
import com.unex.chamaAi.usuario.domain.exceptions.CredenciaisInvalidasException;
import com.unex.chamaAi.usuario.domain.repositories.UsuarioRepository;
import com.unex.chamaAi.usuario.domain.services.CriptografadorSenha;
import com.unex.chamaAi.usuario.domain.valueobjects.Email;
import com.unex.chamaAi.usuario.domain.valueobjects.Senha;

import java.util.Optional;
import java.util.function.Supplier;

public class AutenticarUsuario {

    private final UsuarioRepository usuarios;
    private final CriptografadorSenha criptografador;
    private final EmissorToken emissorToken;

    public AutenticarUsuario(UsuarioRepository usuarios, CriptografadorSenha criptografador,
                             EmissorToken emissorToken) {
        this.usuarios = usuarios;
        this.criptografador = criptografador;
        this.emissorToken = emissorToken;
    }

    public TokenAcesso executar(LoginCommand comando) {
        Optional<Senha> senha = converter(() -> new Senha(comando.senha()));
        Optional<Usuario> usuario = converter(() -> new Email(comando.email())).flatMap(usuarios::buscarPorEmail);

        if (usuario.isEmpty() || senha.isEmpty()) {
            senha.ifPresent(criptografador::criptografar);
            throw new CredenciaisInvalidasException();
        }
        if (!criptografador.confere(senha.get(), usuario.get().getSenha())) {
            throw new CredenciaisInvalidasException();
        }
        return emissorToken.emitir(usuario.get());
    }

    private static <T> Optional<T> converter(Supplier<T> conversao) {
        try {
            return Optional.of(conversao.get());
        } catch (DadoInvalidoException e) {
            return Optional.empty();
        }
    }
}
