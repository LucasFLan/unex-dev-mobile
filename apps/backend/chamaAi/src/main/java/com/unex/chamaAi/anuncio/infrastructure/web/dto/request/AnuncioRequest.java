package com.unex.chamaAi.anuncio.infrastructure.web.dto.request;

import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Dados de um anúncio, usados para criar e para editar (a edição substitui todos os campos).")
public record AnuncioRequest(
        @Schema(description = "`OFERTA`: prestador oferecendo um serviço. `PEDIDO`: cliente procurando quem faça. "
                + "Aceita maiúsculas ou minúsculas.", example = "OFERTA", allowableValues = {"OFERTA", "PEDIDO"},
                requiredMode = REQUIRED)
        String tipo,

        @Schema(description = "Id de uma categoria existente (ver `GET /categorias`).",
                example = "0b9d3c4e-6f1a-4c2b-8d7e-5a4b3c2d1e0f", requiredMode = REQUIRED)
        UUID categoriaId,

        @Schema(description = "Título do anúncio. Espaços nas pontas são removidos.",
                example = "Eletricista residencial", minLength = 3, maxLength = 120, requiredMode = REQUIRED)
        String titulo,

        @Schema(description = "Descrição livre do serviço.", example = "Instalação de chuveiros, tomadas e quadros de luz.",
                maxLength = 5000, requiredMode = REQUIRED)
        String descricao,

        @Schema(description = "Valor em reais, maior que zero, com até 2 casas decimais. Ausente ou `null` = "
                + "\"a combinar\".", example = "150.00", minimum = "0.01", maximum = "99999999.99",
                requiredMode = NOT_REQUIRED, nullable = true)
        BigDecimal preco,

        @Schema(description = "Cidade onde o serviço é oferecido ou necessário.", example = "Salvador",
                maxLength = 100, requiredMode = REQUIRED)
        String cidade,

        @Schema(description = "Sigla da UF (aceita minúsculas).", example = "BA", minLength = 2, maxLength = 2,
                requiredMode = REQUIRED)
        String uf,

        @Schema(description = "CEP (com ou sem hífen). Com ele o anúncio ganha bairro, mapa da região e distância; "
                + "sem ele, fica só com cidade e UF. Consulte antes em `GET /cep/{cep}` para preencher o resto.",
                example = "40140-110", requiredMode = NOT_REQUIRED, nullable = true)
        String cep,

        @Schema(description = "Rua. Vazio = o do CEP. Só o autor (e, após o aceite, a outra pessoa) vê.",
                example = "Avenida Sete de Setembro", maxLength = 150, requiredMode = NOT_REQUIRED, nullable = true)
        String logradouro,

        @Schema(description = "Número. Só o autor (e, após o aceite, a outra pessoa) vê.", example = "123",
                maxLength = 20, requiredMode = NOT_REQUIRED, nullable = true)
        String numero,

        @Schema(description = "Complemento. Só o autor (e, após o aceite, a outra pessoa) vê.", example = "Apto 201",
                maxLength = 100, requiredMode = NOT_REQUIRED, nullable = true)
        String complemento,

        @Schema(description = "Bairro (público). Vazio = o do CEP.", example = "Barra", maxLength = 100,
                requiredMode = NOT_REQUIRED, nullable = true)
        String bairro,

        @Schema(description = "Até quantos km o profissional atende (só `OFERTA`; ignorado em `PEDIDO`).",
                example = "10", minimum = "1", maximum = "200", requiredMode = NOT_REQUIRED, nullable = true)
        Integer raioAtendimentoKm) {

    public DadosAnuncioCommand paraComando() {
        return new DadosAnuncioCommand(tipo, categoriaId, titulo, descricao, preco, cidade, uf, cep, logradouro,
                numero, complemento, bairro, raioAtendimentoKm);
    }
}
