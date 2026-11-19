package com.unex.chamaAi.anuncio.infrastructure.web;

import com.unex.chamaAi.anuncio.application.dto.ListarAnunciosQuery;
import com.unex.chamaAi.anuncio.application.usecases.AlterarAtivacaoAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.BuscarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.CriarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.EditarAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ExcluirAnuncio;
import com.unex.chamaAi.anuncio.application.usecases.ListarAnuncios;
import com.unex.chamaAi.anuncio.application.usecases.ListarMeusAnuncios;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.request.AnuncioRequest;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.AnuncioResponse;
import com.unex.chamaAi.anuncio.infrastructure.web.dto.response.PaginaAnunciosResponse;
import com.unex.chamaAi.config.SecurityConfig;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AnuncioControllerImpl implements AnuncioController {

    private final CriarAnuncio criarAnuncio;
    private final BuscarAnuncio buscarAnuncio;
    private final ListarAnuncios listarAnuncios;
    private final ListarMeusAnuncios listarMeusAnuncios;
    private final EditarAnuncio editarAnuncio;
    private final ExcluirAnuncio excluirAnuncio;
    private final AlterarAtivacaoAnuncio alterarAtivacaoAnuncio;

    public AnuncioControllerImpl(CriarAnuncio criarAnuncio, BuscarAnuncio buscarAnuncio, ListarAnuncios listarAnuncios,
                                 ListarMeusAnuncios listarMeusAnuncios, EditarAnuncio editarAnuncio,
                                 ExcluirAnuncio excluirAnuncio, AlterarAtivacaoAnuncio alterarAtivacaoAnuncio) {
        this.criarAnuncio = criarAnuncio;
        this.buscarAnuncio = buscarAnuncio;
        this.listarAnuncios = listarAnuncios;
        this.listarMeusAnuncios = listarMeusAnuncios;
        this.editarAnuncio = editarAnuncio;
        this.excluirAnuncio = excluirAnuncio;
        this.alterarAtivacaoAnuncio = alterarAtivacaoAnuncio;
    }

    @Override
    public AnuncioResponse criar(Jwt jwt, AnuncioRequest request) {
        return AnuncioResponse.de(criarAnuncio.executar(usuarioLogado(jwt), request.paraComando()));
    }

    @Override
    public PaginaAnunciosResponse listar(String tipo, UUID categoriaId, String cidade, String uf, Integer pagina,
                                         Integer tamanho) {
        return PaginaAnunciosResponse.de(listarAnuncios.executar(
                new ListarAnunciosQuery(tipo, categoriaId, cidade, uf, pagina, tamanho)));
    }

    @Override
    public AnuncioResponse buscar(Jwt jwt, UUID id) {
        return AnuncioResponse.de(buscarAnuncio.executar(id, jwt == null ? null : usuarioLogado(jwt)));
    }

    @Override
    public AnuncioResponse editar(Jwt jwt, UUID id, AnuncioRequest request) {
        return AnuncioResponse.de(editarAnuncio.executar(id, usuarioLogado(jwt), request.paraComando()));
    }

    @Override
    public void excluir(Jwt jwt, UUID id) {
        excluirAnuncio.executar(id, usuarioLogado(jwt), ehAdmin(jwt));
    }

    @Override
    public AnuncioResponse ativar(Jwt jwt, UUID id) {
        return AnuncioResponse.de(alterarAtivacaoAnuncio.executar(id, usuarioLogado(jwt), true));
    }

    @Override
    public AnuncioResponse desativar(Jwt jwt, UUID id) {
        return AnuncioResponse.de(alterarAtivacaoAnuncio.executar(id, usuarioLogado(jwt), false));
    }

    @Override
    public PaginaAnunciosResponse listarMeus(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaAnunciosResponse.de(listarMeusAnuncios.executar(usuarioLogado(jwt), pagina, tamanho));
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    private static boolean ehAdmin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString(SecurityConfig.CLAIM_PAPEL));
    }
}
