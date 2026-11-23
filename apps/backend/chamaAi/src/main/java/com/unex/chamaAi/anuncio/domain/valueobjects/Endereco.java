package com.unex.chamaAi.anuncio.domain.valueobjects;

import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;

/**
 * Endereço de um anúncio a partir do CEP. Cidade e UF ficam em {@link Localizacao} (são públicas); aqui fica o
 * que só é mostrado inteiro para o autor e, depois do aceite, para a outra pessoa da proposta. As coordenadas
 * vêm da consulta do CEP e podem faltar (CEP sem ponto cadastrado ou serviço fora do ar).
 */
public record Endereco(String cep, String logradouro, String numero, String complemento, String bairro,
                       Coordenadas coordenadas) {

    public Endereco {
        cep = normalizarCep(cep);
        logradouro = opcional(logradouro, 150, "O logradouro");
        numero = opcional(numero, 20, "O número");
        complemento = opcional(complemento, 100, "O complemento");
        bairro = opcional(bairro, 100, "O bairro");
    }

    /** Só os 8 dígitos; aceita "40000-000". */
    public static String normalizarCep(String cep) {
        String digitos = cep == null ? "" : cep.replaceAll("\\D", "");
        if (digitos.length() != 8) {
            throw new DadoInvalidoException("CEP inválido. Informe os 8 dígitos.");
        }
        return digitos;
    }

    public Endereco comCoordenadas(Coordenadas novas) {
        return new Endereco(cep, logradouro, numero, complemento, bairro, novas);
    }

    private static String opcional(String valor, int maximo, String nome) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String semEspacos = valor.trim();
        if (semEspacos.length() > maximo) {
            throw new DadoInvalidoException(nome + " deve ter no máximo " + maximo + " caracteres.");
        }
        return semEspacos;
    }
}
