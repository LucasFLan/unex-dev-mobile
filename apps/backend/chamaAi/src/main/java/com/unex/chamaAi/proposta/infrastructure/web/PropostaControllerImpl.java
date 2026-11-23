package com.unex.chamaAi.proposta.infrastructure.web;

import com.unex.chamaAi.proposta.application.dto.EnviarPropostaCommand;
import com.unex.chamaAi.proposta.application.usecases.EnviarProposta;
import com.unex.chamaAi.proposta.application.usecases.ListarPropostas;
import com.unex.chamaAi.proposta.application.usecases.ResponderProposta;
import com.unex.chamaAi.proposta.domain.valueobjects.FotoProposta;
import com.unex.chamaAi.proposta.infrastructure.web.dto.request.PropostaRequest;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PaginaPropostasResponse;
import com.unex.chamaAi.proposta.infrastructure.web.dto.response.PropostaResponse;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import java.util.UUID;

@RestController
public class PropostaControllerImpl implements PropostaController {

    private final EnviarProposta enviarProposta;
    private final ListarPropostas listarPropostas;
    private final ResponderProposta responderProposta;

    public PropostaControllerImpl(EnviarProposta enviarProposta, ListarPropostas listarPropostas,
                                  ResponderProposta responderProposta) {
        this.enviarProposta = enviarProposta;
        this.listarPropostas = listarPropostas;
        this.responderProposta = responderProposta;
    }

    /** Proposta e notificação são gravadas juntas: se uma falhar, nenhuma fica. */
    @Override
    @Transactional
    public PropostaResponse enviar(Jwt jwt, UUID id, PropostaRequest request) {
        return PropostaResponse.de(enviarProposta.executar(id, usuarioLogado(jwt), request.paraComando()));
    }

    @Override
    @Transactional
    public PropostaResponse enviarComFotos(Jwt jwt, UUID id, String valor, String mensagem, List<MultipartFile> fotos) {
        List<MultipartFile> anexos = fotos == null ? List.of()
                : fotos.stream().filter(foto -> foto != null && !foto.isEmpty()).toList();
        if (anexos.size() > FotoProposta.MAXIMO_POR_PROPOSTA) {
            throw new DadoInvalidoException("Anexe no máximo " + FotoProposta.MAXIMO_POR_PROPOSTA + " fotos.");
        }
        EnviarPropostaCommand comando = new EnviarPropostaCommand(valor(valor), mensagem,
                anexos.stream().map(PropostaControllerImpl::bytes).toList());
        return PropostaResponse.de(enviarProposta.executar(id, usuarioLogado(jwt), comando));
    }

    /** As fotos não mudam depois de enviadas: o navegador pode guardar por um bom tempo. */
    @Override
    public ResponseEntity<byte[]> foto(Jwt jwt, UUID id, UUID fotoId) {
        FotoProposta foto = listarPropostas.foto(id, fotoId, usuarioLogado(jwt));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePrivate())
                .header("X-Content-Type-Options", "nosniff")
                .body(foto.conteudo());
    }

    @Override
    public PaginaPropostasResponse listarRecebidas(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaPropostasResponse.de(listarPropostas.recebidas(usuarioLogado(jwt), pagina, tamanho));
    }

    @Override
    public PaginaPropostasResponse listarEnviadas(Jwt jwt, Integer pagina, Integer tamanho) {
        return PaginaPropostasResponse.de(listarPropostas.enviadas(usuarioLogado(jwt), pagina, tamanho));
    }

    /** Aceitar mexe na proposta, no anúncio, nas outras propostas e nas notificações: tudo ou nada. */
    @Override
    @Transactional
    public PropostaResponse aceitar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.aceitar(id, usuarioLogado(jwt)));
    }

    @Override
    @Transactional
    public PropostaResponse recusar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.recusar(id, usuarioLogado(jwt)));
    }

    @Override
    @Transactional
    public PropostaResponse cancelar(Jwt jwt, UUID id) {
        return PropostaResponse.de(responderProposta.cancelar(id, usuarioLogado(jwt)));
    }

    /** Valor vindo de formulário: vazio = "a combinar"; a regra de faixa e casas decimais fica no domínio. */
    private static BigDecimal valor(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("Valor inválido. Use só números, com até 2 casas decimais.");
        }
    }

    private static byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new DadoInvalidoException("Não foi possível ler uma das fotos enviadas.");
        }
    }

    /** O "sub" do token é o id do usuário (ver JwtEmissorToken). */
    private static UUID usuarioLogado(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
