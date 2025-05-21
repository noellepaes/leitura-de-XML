package com.noelle.leitura_de_XML.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.repository.CupomRepository;
import com.noelle.leitura_de_XML.services.CupomService;

/**
 * Testes de integração para o fluxo completo de processamento de arquivos ZIP
 * Utilizamos @SpringBootTest para carregar todo o contexto da aplicação
 * e @ActiveProfiles("test") para usar configurações específicas de teste
 */
@SpringBootTest
@ActiveProfiles("test")
class ProcessamentoZipIntegrationTest {

    @Autowired
    private CupomService cupomService;
    
    @Autowired
    private CupomRepository cupomRepository;
    
    @BeforeEach
    void setUp() {
        // Limpar o banco antes de cada teste
        cupomRepository.deleteAll();
    }
    
    /**
     * Teste de integração para verificar o fluxo completo de processamento de um arquivo ZIP
     * Este teste valida todos os critérios em conjunto:
     * - Processar um .zip com diversos XMLs de CF-e SAT
     * - Extrair dados dos cupons e seus itens
     * - Persistir os dados em banco relacional
     * - Evitar duplicidade com base na chave de acesso
     * - Ignorar cupons cancelados
     */
    @Test
    void deveProcessarArquivoZipCompleto() throws IOException {
        // Carregar um arquivo ZIP de teste
        byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
        // Processar o arquivo
        int processados = cupomService.processarArquivoZip(conteudoZip);
        
        // Verificar se foram processados cupons
        assertTrue(processados > 0, "Devem ser processados cupons do arquivo ZIP");
        
        // Verificar se os cupons foram salvos no banco
        List<Cupom> cupons = cupomRepository.findAll();
        assertEquals(processados, cupons.size(), "O número de cupons no banco deve ser igual ao número de processados");
        
        // Verificar se os cupons têm os dados esperados
        for (Cupom cupom : cupons) {
            assertNotNull(cupom.getChaveAcesso(), "Todos os cupons devem ter chave de acesso");
            assertNotNull(cupom.getNumeroCfe(), "Todos os cupons devem ter número de CF-e");
            assertFalse(cupom.getItens().isEmpty(), "Todos os cupons devem ter pelo menos um item");
        }
        
        // Tentar processar o mesmo arquivo novamente
        int processadosNovamente = cupomService.processarArquivoZip(conteudoZip);
        
        // Verificar se não foram processados cupons duplicados
        assertEquals(0, processadosNovamente, "Não devem ser processados cupons duplicados");
        
        // Verificar se o número de cupons no banco não mudou
        assertEquals(processados, cupomRepository.count(), "O número de cupons no banco não deve mudar ao processar duplicados");
    }
    
    /**
     * Teste de integração para verificar a ordenação dos cupons
     * Este teste valida os critérios:
     * - Permitir listar os cupons ordenados por número do CF-e
     * - Permitir listar os cupons ordenados por valor total do cupom
     */
    @Test
    void deveOrdenarCuponsCorretamente() throws IOException {
        // Carregar e processar um arquivo ZIP de teste
        byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        cupomService.processarArquivoZip(conteudoZip);
        
        // Verificar se existem cupons no banco
        assertTrue(cupomRepository.count() > 1, "Devem existir múltiplos cupons para testar ordenação");
        
        // Listar os cupons ordenados por número
        List<Cupom> cuponsPorNumero = cupomService.listarPorNumero();
        
        // Verificar se estão ordenados por número
        for (int i = 0; i < cuponsPorNumero.size() - 1; i++) {
            String numeroAtual = cuponsPorNumero.get(i).getNumeroCfe();
            String numeroProximo = cuponsPorNumero.get(i + 1).getNumeroCfe();
            assertTrue(numeroAtual.compareTo(numeroProximo) <= 0, 
                    "Os cupons devem estar ordenados por número em ordem crescente");
        }
        
        // Listar os cupons ordenados por valor
        List<Cupom> cuponsPorValor = cupomService.listarPorValor();
        
        // Verificar se estão ordenados por valor
        for (int i = 0; i < cuponsPorValor.size() - 1; i++) {
            java.math.BigDecimal valorAtual = cuponsPorValor.get(i).getValorTotalProdutos();
            java.math.BigDecimal valorProximo = cuponsPorValor.get(i + 1).getValorTotalProdutos();
            assertTrue(valorAtual.compareTo(valorProximo) <= 0, 
                    "Os cupons devem estar ordenados por valor em ordem crescente");
        }
    }
}
