package com.noelle.leitura_de_XML.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.exception.DuplicidadeException;
import com.noelle.leitura_de_XML.exception.ProcessamentoException;
import com.noelle.leitura_de_XML.repository.CupomRepository;
import com.noelle.leitura_de_XML.services.CupomService;
import com.noelle.leitura_de_XML.services.ProcessadorXmlService;

@SpringBootTest
class CupomServiceTest {

    @Autowired
    private CupomService cupomService;
    
    @MockitoBean
    private CupomRepository cupomRepository;
    
    @MockitoBean
    private ProcessadorXmlService processadorXmlService;
    
    /**
     * Configuração inicial para cada teste
     */
    @BeforeEach
    void setUp() {
    // Limpar o banco antes de cada teste
        doNothing().when(cupomRepository).deleteAll();
    }
    
    /**
     * Teste para verificar se o serviço processa corretamente um arquivo ZIP
     * Este teste valida os critérios: "Processar um .zip com diversos XMLs" e "Persistir os dados em banco relacional"
     */
    @Test
    void deveProcessarArquivoZip() throws IOException {
        // Preparar dados de teste
        byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        List<String> xmls = List.of("<xml1>", "<xml2>", "<xml3>");
        
        // Configurar mock para simular extração de XMLs
        when(processadorXmlService.extrairXmlsDoZip(conteudoZip)).thenReturn(xmls);
        
        // Configurar mock para simular validação de XML
        when(processadorXmlService.validarXml(anyString())).thenReturn(true);
        
        // Configurar mock para simular processamento de cada XML
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        
        // Configurar comportamento para o terceiro XML (lança exceção de processamento)
        when(processadorXmlService.processar(xmls.get(0))).thenReturn(cupom1);
        when(processadorXmlService.processar(xmls.get(1))).thenReturn(cupom2);
        when(processadorXmlService.processar(xmls.get(2))).thenThrow(new ProcessamentoException("Erro ao processar XML"));
        
        // Configurar mock para verificação de duplicidade
        when(cupomRepository.existsByChaveAcesso(anyString())).thenReturn(false);
        
        // Configurar mock para salvar cupom
        when(cupomRepository.save(any(Cupom.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Executar o método a ser testado
        int processados = cupomService.processarArquivoZip(conteudoZip);
        
        // Verificar se foram processados 2 cupons (o terceiro gera erro)
        assertEquals(2, processados, "Devem ser processados apenas os cupons válidos");
        
        // Verificar se o método save foi chamado duas vezes
        verify(cupomRepository, times(2)).save(any(Cupom.class));
    }
    
    /**
     * Teste para verificar se o serviço evita duplicidade de cupons
     * Este teste valida o critério: "Evitar duplicidade com base na chave de acesso"
     */
    @Test
    void deveEvitarDuplicidade() throws IOException {
        // Preparar dados de teste
        String xml = Files.readString(Paths.get("src/test/resources/cupom_exemplo.xml"));
        
        // Criar um cupom com chave de acesso
        Cupom cupom = new Cupom();
        cupom.setChaveAcesso("12345678901234567890123456789012345678901234");
        
        // Configurar mock para simular processamento do XML
        when(processadorXmlService.processar(xml)).thenReturn(cupom);
        
        // Configurar mock para simular que o cupom já existe
        when(cupomRepository.existsByChaveAcesso("12345678901234567890123456789012345678901234")).thenReturn(true);
        
        // Verificar se uma exceção de duplicidade é lançada
        assertThrows(DuplicidadeException.class, () -> {
            cupomService.processarXml(xml);
        }, "Deve lançar exceção ao tentar processar um cupom com chave duplicada");
        
        // Verificar que o método save não foi chamado
        verify(cupomRepository, never()).save(any(Cupom.class));
    }
    
    /**
     * Teste para verificar se o serviço processa corretamente um XML único
     * Este teste valida o critério: "Extrair dados dos cupons e seus itens" e "Persistir os dados"
     */
    @Test
    void deveProcessarXmlUnico() throws IOException {
        // Preparar dados de teste
        String caminhoArquivo = "src/test/resources/cupom_exemplo.xml";
        String conteudoXml = Files.readString(Paths.get(caminhoArquivo));
        
        // Criar um cupom
        Cupom cupom = new Cupom();
        cupom.setChaveAcesso("12345678901234567890123456789012345678901234");
        
        // Configurar mock para simular processamento do XML
        when(processadorXmlService.processar(conteudoXml)).thenReturn(cupom);
        
        // Configurar mock para verificação de duplicidade
        when(cupomRepository.existsByChaveAcesso(anyString())).thenReturn(false);
        
        // Configurar mock para salvar cupom
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupom);
        
        // Executar o método a ser testado
        Cupom resultado = cupomService.processarXmlUnico(caminhoArquivo);
        
        // Verificar se o cupom foi processado corretamente
        assertNotNull(resultado, "O cupom deve ser processado e retornado");
        assertEquals("12345678901234567890123456789012345678901234", resultado.getChaveAcesso(), 
                "A chave de acesso deve ser mantida");
        
        // Verificar se o método save foi chamado
        verify(cupomRepository).save(any(Cupom.class));
    }
    
    /**
     * Teste para verificar se o serviço lista os cupons ordenados por número
     * Este teste valida o critério: "Permitir listar os cupons ordenados por número do CF-e"
     */
    @Test
    void deveListarCuponsPorNumero() {
        // Criar cupons com números diferentes
        List<Cupom> cupons = new ArrayList<>();
        
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setNumeroCfe("002");
        cupons.add(cupom1);
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setNumeroCfe("001");
        cupons.add(cupom2);
        
        // Configurar mock para retornar os cupons ordenados
        when(cupomRepository.findAllByOrderByNumeroCfeAsc()).thenReturn(List.of(cupom2, cupom1));
        
        // Listar os cupons ordenados por número
        List<Cupom> resultado = cupomService.listarPorNumero();
        
        // Verificar se estão na ordem correta
        assertEquals(2, resultado.size(), "Devem ser retornados todos os cupons");
        assertEquals("001", resultado.get(0).getNumeroCfe(), "O primeiro cupom deve ter o menor número");
        assertEquals("002", resultado.get(1).getNumeroCfe(), "O segundo cupom deve ter o maior número");
    }
    
    /**
     * Teste para verificar se o serviço lista os cupons ordenados por número com paginação
     * Este teste valida o critério: "Permitir listar os cupons ordenados por número do CF-e"
     */
    @Test
    void deveListarCuponsPorNumeroPaginado() {
        // Criar cupons com números diferentes
        List<Cupom> cupons = new ArrayList<>();
        
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setNumeroCfe("002");
        cupons.add(cupom1);
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setNumeroCfe("001");
        cupons.add(cupom2);
        
        // Criar paginação
        Pageable pageable = PageRequest.of(0, 10, Sort.by("numeroCfe").ascending());
        Page<Cupom> page = new PageImpl<>(List.of(cupom2, cupom1), pageable, 2);
        
        // Configurar mock para retornar os cupons paginados
        when(cupomRepository.findAll(pageable)).thenReturn(page);
        
        // Listar os cupons ordenados por número com paginação
        Page<Cupom> resultado = cupomService.listarPorNumeroPaginado(pageable);
        
        // Verificar se estão na ordem correta
        assertEquals(2, resultado.getContent().size(), "Devem ser retornados todos os cupons");
        assertEquals("001", resultado.getContent().get(0).getNumeroCfe(), "O primeiro cupom deve ter o menor número");
        assertEquals("002", resultado.getContent().get(1).getNumeroCfe(), "O segundo cupom deve ter o maior número");
    }
    
    /**
     * Teste para verificar se o serviço lista os cupons ordenados por valor total
     * Este teste valida o critério: "Permitir listar os cupons ordenados por valor total do cupom"
     */
    @Test
    void deveListarCuponsPorValor() {
        // Criar cupons com valores diferentes
        List<Cupom> cupons = new ArrayList<>();
        
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setValorTotalProdutos(new java.math.BigDecimal("100.00"));
        cupons.add(cupom1);
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setValorTotalProdutos(new java.math.BigDecimal("50.00"));
        cupons.add(cupom2);
        
        // Configurar mock para retornar os cupons ordenados
        when(cupomRepository.findAllByOrderByValorTotalProdutosAsc()).thenReturn(List.of(cupom2, cupom1));
        
        // Listar os cupons ordenados por valor
        List<Cupom> resultado = cupomService.listarPorValor();
        
        // Verificar se estão na ordem correta
        assertEquals(2, resultado.size(), "Devem ser retornados todos os cupons");
        assertEquals(new java.math.BigDecimal("50.00"), resultado.get(0).getValorTotalProdutos(), 
                "O primeiro cupom deve ter o menor valor");
        assertEquals(new java.math.BigDecimal("100.00"), resultado.get(1).getValorTotalProdutos(), 
                "O segundo cupom deve ter o maior valor");
    }
    
    /**
     * Teste para verificar se o serviço lista os cupons ordenados por valor total com paginação
     * Este teste valida o critério: "Permitir listar os cupons ordenados por valor total do cupom"
     */
    @Test
    void deveListarCuponsPorValorPaginado() {
        // Criar cupons com valores diferentes
        List<Cupom> cupons = new ArrayList<>();
        
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setValorTotalProdutos(new java.math.BigDecimal("100.00"));
        cupons.add(cupom1);
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setValorTotalProdutos(new java.math.BigDecimal("50.00"));
        cupons.add(cupom2);
        
        // Criar paginação
        Pageable pageable = PageRequest.of(0, 10, Sort.by("valorTotalProdutos").ascending());
        Page<Cupom> page = new PageImpl<>(List.of(cupom2, cupom1), pageable, 2);
        
        // Configurar mock para retornar os cupons paginados
        when(cupomRepository.findAll(pageable)).thenReturn(page);
        
        // Listar os cupons ordenados por valor com paginação
        Page<Cupom> resultado = cupomService.listarPorValorPaginado(pageable);
        
        // Verificar se estão na ordem correta
        assertEquals(2, resultado.getContent().size(), "Devem ser retornados todos os cupons");
        assertEquals(new java.math.BigDecimal("50.00"), resultado.getContent().get(0).getValorTotalProdutos(), 
                "O primeiro cupom deve ter o menor valor");
        assertEquals(new java.math.BigDecimal("100.00"), resultado.getContent().get(1).getValorTotalProdutos(), 
                "O segundo cupom deve ter o maior valor");
    }
}
