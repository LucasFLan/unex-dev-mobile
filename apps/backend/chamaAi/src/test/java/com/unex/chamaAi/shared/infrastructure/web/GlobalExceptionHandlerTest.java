package com.unex.chamaAi.shared.infrastructure.web;

import com.unex.chamaAi.shared.domain.exceptions.ConflitoException;
import com.unex.chamaAi.shared.domain.exceptions.DadoInvalidoException;
import com.unex.chamaAi.shared.domain.exceptions.DominioException;
import com.unex.chamaAi.shared.domain.exceptions.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ControllerDeTeste())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void dadoInvalidoRetorna400() throws Exception {
        mockMvc.perform(get("/invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("inválido"));
    }

    @Test
    void recursoNaoEncontradoRetorna404() throws Exception {
        mockMvc.perform(get("/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("não encontrado"));
    }

    @Test
    void conflitoRetorna409() throws Exception {
        mockMvc.perform(get("/conflito"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("conflito"));
    }

    @Test
    void outraRegraDeNegocioRetorna422() throws Exception {
        mockMvc.perform(get("/regra"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("regra"));
    }

    @Test
    void erroInesperadoRetorna500SemExporDetalhes() throws Exception {
        mockMvc.perform(get("/inesperado"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Erro interno do servidor."))
                .andExpect(content().string(not(containsString("segredo"))));
    }

    @Test
    void metodoNaoSuportadoContinuaRetornando405() throws Exception {
        mockMvc.perform(post("/invalido")).andExpect(status().isMethodNotAllowed());
    }

    @RestController
    private static class ControllerDeTeste {

        @GetMapping("/invalido")
        void invalido() {
            throw new DadoInvalidoException("inválido");
        }

        @GetMapping("/nao-encontrado")
        void naoEncontrado() {
            throw new RecursoNaoEncontradoException("não encontrado") {
            };
        }

        @GetMapping("/conflito")
        void conflito() {
            throw new ConflitoException("conflito") {
            };
        }

        @GetMapping("/regra")
        void regra() {
            throw new DominioException("regra") {
            };
        }

        @GetMapping("/inesperado")
        void inesperado() {
            throw new IllegalStateException("segredo");
        }
    }
}
