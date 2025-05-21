package com.noelle.leitura_de_XML.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.dto.CupomDTO;
import com.noelle.leitura_de_XML.mapper.CupomMapper;
import com.noelle.leitura_de_XML.services.CupomService;

/**
 * Testes para o controlador REST de cupons
 * Utilizamos @WebMvcTest para testar apenas a camada de controller
 */
@WebMvcTest(CupomController.class )
class CupomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private CupomService cupomService;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private CupomMapper cupomMapper;
    
    /**
     * Teste para verificar se o endpoint de processamento de arquivo ZIP funciona corretamente
     * Este teste valida o critério: "Expor uma interface (REST) para consulta dos cupons"
     */
    @Test
    void deveProcessarArquivoZip() throws Exception {
        // Preparar dados de teste
        byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
        // Configurar mock para simular processamento do arquivo
        when(cupomService.processarArquivoZip(any(byte[].class))).thenReturn(5);
        
        // Criar um MockMultipartFile para simular o upload
        MockMultipartFile arquivo = new MockMultipartFile(
            "arquivo", "cupons.zip", "application/zip", conteudoZip);
        
        // Enviar o arquivo para o endpoint e verificar a resposta
        mockMvc.perform(multipart("/api/cupons/processar")
                .file(arquivo))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Arquivo processado com sucesso")))
                .andExpect(content().string(containsString("5")));
    }
    
    /**
     * Teste para verificar se o endpoint de processamento de XML único funciona corretamente
     * Este teste valida o critério: "Processar um XML de CF-e SAT"
     */
    @Test
    void deveProcessarXmlUnico() throws Exception {
        // Preparar dados de teste
        String caminhoArquivo = "src/test/resources/cupom_exemplo.xml";
        
        // Criar um cupom
        Cupom cupom = new Cupom();
        cupom.setChaveAcesso("12345678901234567890123456789012345678901234");
        
        // Configurar mock para simular processamento do XML
        when(cupomService.processarXmlUnico(caminhoArquivo)).thenReturn(cupom);
        
        // Enviar o caminho para o endpoint e verificar a resposta
        mockMvc.perform(post("/api/cupons/processar-xml-unico")
                .param("caminho", caminhoArquivo)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("XML processado com sucesso")))
                .andExpect(content().string(containsString("12345678901234567890123456789012345678901234")));
    }
    
    /**
     * Teste para verificar se o endpoint de listagem por número funciona corretamente
     * Este teste valida o critério: "Permitir listar os cupons ordenados por número do CF-e"
     */
    @Test
    void deveListarCuponsPorNumero() throws Exception {
        // Preparar dados de teste
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setNumeroCfe("001");
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setNumeroCfe("002");
        
        List<Cupom> cupons = List.of(cupom1, cupom2);
        
        // Preparar DTOs
        CupomDTO dto1 = new CupomDTO();
        dto1.setChaveAcesso("chave1");
        dto1.setNumeroCfe("001");
        
        CupomDTO dto2 = new CupomDTO();
        dto2.setChaveAcesso("chave2");
        dto2.setNumeroCfe("002");
        
        List<CupomDTO> dtos = List.of(dto1, dto2);
        
        // Criar paginação
        Pageable pageable = PageRequest.of(0, 20, Sort.by("numeroCfe").ascending());
        Page<Cupom> page = new PageImpl<>(cupons, pageable, 2);
        
        // Configurar mocks
        when(cupomService.listarPorNumeroPaginado(any(Pageable.class))).thenReturn(page);
        when(cupomMapper.toDto(cupom1)).thenReturn(dto1);
        when(cupomMapper.toDto(cupom2)).thenReturn(dto2);
        
        // Consultar o endpoint e verificar a resposta
        mockMvc.perform(get("/api/cupons/por-numero")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].numeroCfe").value("001"))
                .andExpect(jsonPath("$.content[1].numeroCfe").value("002"));
    }
    
    /**
     * Teste para verificar se o endpoint de listagem por valor funciona corretamente
     * Este teste valida o critério: "Permitir listar os cupons ordenados por valor total do cupom"
     */
    @Test
    void deveListarCuponsPorValor() throws Exception {
        // Preparar dados de teste
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setValorTotalProdutos(new BigDecimal("50.00"));
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setValorTotalProdutos(new BigDecimal("100.00"));
        
        List<Cupom> cupons = List.of(cupom1, cupom2);
        
        // Preparar DTOs
        CupomDTO dto1 = new CupomDTO();
        dto1.setChaveAcesso("chave1");
        dto1.setValorTotalProdutos(new BigDecimal("50.00"));
        
        CupomDTO dto2 = new CupomDTO();
        dto2.setChaveAcesso("chave2");
        dto2.setValorTotalProdutos(new BigDecimal("100.00"));
        
        List<CupomDTO> dtos = List.of(dto1, dto2);
        
        // Criar paginação
        Pageable pageable = PageRequest.of(0, 20, Sort.by("valorTotalProdutos").ascending());
        Page<Cupom> page = new PageImpl<>(cupons, pageable, 2);
        
        // Configurar mocks
        when(cupomService.listarPorValorPaginado(any(Pageable.class))).thenReturn(page);
        when(cupomMapper.toDto(cupom1)).thenReturn(dto1);
        when(cupomMapper.toDto(cupom2)).thenReturn(dto2);
        
        // Consultar o endpoint e verificar a resposta
        mockMvc.perform(get("/api/cupons/por-valor")
                .param("page", "0")
                .param("size", "20")
                .param("ascending", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].valorTotalProdutos", closeTo(50.0, 0.001)))
                .andExpect(jsonPath("$.content[1].valorTotalProdutos", closeTo(100.0, 0.001)));
    }
}
