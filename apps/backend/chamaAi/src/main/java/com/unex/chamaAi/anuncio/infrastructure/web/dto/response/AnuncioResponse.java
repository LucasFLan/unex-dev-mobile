package com.unex.chamaAi.anuncio.infrastructure.web.dto.response;

import com.unex.chamaAi.anuncio.application.dto.AnuncioDetalhado;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Anúncio com a categoria detalhada.")
public record AnuncioResponse(
        @Schema(description = "Id do anúncio.", example = "7a1c9e2b-4d3f-4b8a-9e6d-2c5f8a1b3d4e")
        UUID id,

        @Schema(description = "`OFERTA` ou `PEDIDO`.", example = "OFERTA", allowableValues = {"OFERTA", "PEDIDO"})
        String tipo,

        @Schema(description = "Título.", example = "Eletricista residencial")
        String titulo,

        @Schema(description = "Descrição.", example = "Instalação de chuveiros, tomadas e quadros de luz.")
        String descricao,

        @Schema(description = "Valor em reais. `null` = \"a combinar\".", example = "150.00", nullable = true)
        BigDecimal preco,

        @Schema(description = "Categoria do anúncio.")
        CategoriaResponse categoria,

        @Schema(description = "Cidade.", example = "Salvador")
        String cidade,

        @Schema(description = "UF, sempre em maiúsculas.", example = "BA")
        String uf,

        @Schema(description = "Bairro. `null` em anúncios sem CEP.", example = "Barra", nullable = true)
        String bairro,

        @Schema(description = "Ponto aproximado da região (arredondado a ~1 km), para o mapa e a distância. "
                + "`null` sem CEP ou sem ponto para ele.", nullable = true)
        Regiao regiao,

        @Schema(description = "Até quantos km o profissional atende (só `OFERTA`).", example = "10", nullable = true)
        Integer raioAtendimentoKm,

        @Schema(description = "Endereço completo. Só vem para o autor do anúncio.", nullable = true)
        Endereco endereco,

        @Schema(description = "Se aparece na listagem pública. Inativos só são vistos pelo autor.", example = "true")
        boolean ativo,

        @Schema(description = "Id do usuário que criou o anúncio.", example = "3f2b6c1e-8a4d-4e0b-9c7a-1d2e3f4a5b6c")
        UUID autorId,

        @Schema(description = "Quem publicou: nome, foto e reputação.", nullable = true)
        Autor autor,

        @Schema(description = "Data de criação (ISO-8601, UTC).", example = "2026-11-16T21:30:00.123456Z")
        Instant criadoEm,

        @Schema(description = "Data da última edição (ISO-8601, UTC).", example = "2026-11-16T21:30:00.123456Z")
        Instant atualizadoEm) {

    public record Regiao(@Schema(example = "-13.01") double latitude, @Schema(example = "-38.53") double longitude) {
    }

    public record Endereco(@Schema(example = "40140110") String cep,
                           @Schema(nullable = true) String logradouro,
                           @Schema(nullable = true) String numero,
                           @Schema(nullable = true) String complemento,
                           @Schema(nullable = true) String bairro,
                           @Schema(description = "Ponto exato do CEP.", nullable = true) Double latitude,
                           @Schema(description = "Ponto exato do CEP.", nullable = true) Double longitude) {
    }

    public record Autor(UUID id,
                        @Schema(example = "Maria Souza") String nome,
                        @Schema(description = "Versão da foto de perfil; `null` = sem foto.", nullable = true)
                        Instant fotoAtualizadaEm,
                        @Schema(description = "Média das avaliações (1 casa). `null` sem avaliações.", example = "4.8",
                                nullable = true)
                        Double mediaAvaliacoes,
                        @Schema(example = "12") long totalAvaliacoes) {
    }

    /** Visão pública: sem o endereço completo. */
    public static AnuncioResponse de(AnuncioDetalhado anuncio) {
        return de(anuncio, false);
    }

    /** O endereço completo só vai para o autor. */
    public static AnuncioResponse de(AnuncioDetalhado anuncio, UUID quemVe) {
        return de(anuncio, quemVe != null && quemVe.equals(anuncio.autorId()));
    }

    private static AnuncioResponse de(AnuncioDetalhado anuncio, boolean autor) {
        var endereco = anuncio.endereco();
        var ponto = endereco == null ? null : endereco.coordenadas();
        var dono = anuncio.autor();
        return new AnuncioResponse(anuncio.id(), anuncio.tipo(), anuncio.titulo(), anuncio.descricao(), anuncio.preco(),
                CategoriaResponse.de(anuncio.categoria()), anuncio.cidade(), anuncio.uf(), anuncio.bairro(),
                anuncio.regiao() == null ? null : new Regiao(anuncio.regiao().latitude(), anuncio.regiao().longitude()),
                anuncio.raioAtendimentoKm(),
                autor && endereco != null ? new Endereco(endereco.cep(), endereco.logradouro(), endereco.numero(),
                        endereco.complemento(), endereco.bairro(), ponto == null ? null : ponto.latitude(),
                        ponto == null ? null : ponto.longitude()) : null,
                anuncio.ativo(), anuncio.autorId(),
                dono == null ? null : new Autor(dono.id(), dono.nome(), dono.fotoAtualizadaEm(), dono.mediaAvaliacoes(),
                        dono.totalAvaliacoes()),
                anuncio.criadoEm(), anuncio.atualizadoEm());
    }
}
