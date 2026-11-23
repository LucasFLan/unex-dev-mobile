package com.unex.chamaAi.anuncio.application.usecases;

import com.unex.chamaAi.anuncio.application.dto.DadosAnuncioCommand;
import com.unex.chamaAi.anuncio.application.ports.ConsultaCep;
import com.unex.chamaAi.anuncio.application.ports.ConsultaCep.EnderecoCep;
import com.unex.chamaAi.anuncio.domain.exceptions.CategoriaInvalidaException;
import com.unex.chamaAi.anuncio.domain.repositories.CategoriaRepository;
import com.unex.chamaAi.anuncio.domain.valueobjects.Endereco;
import com.unex.chamaAi.anuncio.domain.valueobjects.Localizacao;
import com.unex.chamaAi.anuncio.domain.valueobjects.Preco;
import com.unex.chamaAi.anuncio.domain.valueobjects.TipoAnuncio;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.exceptions.ServicoExternoIndisponivelException;

import java.util.Optional;
import java.util.UUID;

/**
 * Converte o comando em value objects e confere se a categoria existe. Com CEP, consulta o serviço de CEP para
 * pegar as coordenadas (e completar bairro e logradouro que vierem vazios). Se o serviço estiver fora do ar, o
 * anúncio é salvo mesmo assim, só sem o ponto no mapa.
 */
class ValidadorDadosAnuncio {

    record DadosValidados(TipoAnuncio tipo, UUID categoriaId, String titulo, String descricao,
                          Preco preco, Localizacao localizacao, Endereco endereco, Integer raioAtendimentoKm) {
    }

    private final CategoriaRepository categorias;
    private final ConsultaCep consultaCep;

    ValidadorDadosAnuncio(CategoriaRepository categorias, ConsultaCep consultaCep) {
        this.categorias = categorias;
        this.consultaCep = consultaCep;
    }

    DadosValidados validar(DadosAnuncioCommand comando) {
        if (comando.categoriaId() == null) {
            throw new DadoInvalidoException("A categoria é obrigatória.");
        }
        TipoAnuncio tipo = TipoAnuncio.de(comando.tipo());
        Preco preco = Preco.deOuNulo(comando.preco());
        Localizacao localizacao = new Localizacao(comando.cidade(), comando.uf());
        Endereco endereco = endereco(comando);
        categorias.buscarPorId(comando.categoriaId()).orElseThrow(CategoriaInvalidaException::new);
        return new DadosValidados(tipo, comando.categoriaId(), comando.titulo(), comando.descricao(), preco,
                localizacao, endereco, comando.raioAtendimentoKm());
    }

    private Endereco endereco(DadosAnuncioCommand comando) {
        if (comando.cep() == null || comando.cep().isBlank()) {
            return null;
        }
        Endereco informado = new Endereco(comando.cep(), comando.logradouro(), comando.numero(), comando.complemento(),
                comando.bairro(), null);

        Optional<EnderecoCep> encontrado;
        try {
            encontrado = consultaCep.buscar(informado.cep());
        } catch (ServicoExternoIndisponivelException e) {
            return informado;
        }
        EnderecoCep cep = encontrado.orElseThrow(() -> new DadoInvalidoException("CEP não encontrado."));
        return new Endereco(informado.cep(), primeiro(informado.logradouro(), cep.logradouro()), informado.numero(),
                informado.complemento(), primeiro(informado.bairro(), cep.bairro()), cep.coordenadas());
    }

    private static String primeiro(String informado, String doCep) {
        return informado != null ? informado : doCep;
    }
}
