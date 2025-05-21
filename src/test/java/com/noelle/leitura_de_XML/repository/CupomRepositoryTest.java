package com.noelle.leitura_de_XML.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.noelle.leitura_de_XML.domain.Cupom;

/**
 * Testes para o repositório de cupons
 * Utilizamos @DataJpaTest para configurar um banco de dados em memória para testes
 */
@DataJpaTest
class CupomRepositoryTest {

    @Autowired
    private CupomRepository cupomRepository;
    
    @BeforeEach
    void setUp() {
        // Limpar o banco antes de cada teste
        cupomRepository.deleteAll();
    }
    
    /**
     * Teste para verificar se o repositório encontra um cupom pela chave de acesso
     * Este teste valida o critério: "Evitar duplicidade com base na chave de acesso"
     */
    @Test
    void deveBuscarCupomPorChaveAcesso() {
        // Criar e salvar um cupom
        Cupom cupom = new Cupom();
        cupom.setChaveAcesso("12345678901234567890123456789012345678901234");
        cupom.setNumeroCfe("001");
        cupom.setDataEmissao(LocalDate.now());
        cupomRepository.save(cupom);
        
        // Verificar se o cupom existe pela chave de acesso
        boolean existe = cupomRepository.existsByChaveAcesso("12345678901234567890123456789012345678901234");
        
        // Verificar se o cupom foi encontrado
        assertTrue(existe, "Deve encontrar o cupom pela chave de acesso");
    }
    
    /**
     * Teste para verificar se o repositório ordena os cupons por número
     * Este teste valida o critério: "Permitir listar os cupons ordenados por número do CF-e"
     */
   @Test
    void deveOrdenarCuponsPorNumero() {
    // Criar e salvar cupons com números diferentes
    Cupom cupom1 = new Cupom();
    cupom1.setChaveAcesso("chave1");
    cupom1.setNumeroCfe("002");
    
    Cupom cupom2 = new Cupom();
    cupom2.setChaveAcesso("chave2");
    cupom2.setNumeroCfe("001");
    
    Cupom cupom3 = new Cupom();
    cupom3.setChaveAcesso("chave3");
    cupom3.setNumeroCfe("003");
    
    // Salvar os cupons fora de ordem
    cupomRepository.saveAll(List.of(cupom1, cupom2, cupom3));
    
    // Buscar os cupons ordenados por número usando findAll com Sort
    List<Cupom> cupons = cupomRepository.findAll(Sort.by("numeroCfe").ascending());
    
    // Verificar se estão na ordem correta
    assertEquals(3, cupons.size(), "Devem ser retornados todos os cupons");
    assertEquals("001", cupons.get(0).getNumeroCfe(), "O primeiro cupom deve ter o menor número");
    assertEquals("002", cupons.get(1).getNumeroCfe(), "O segundo cupom deve ter o número intermediário");
    assertEquals("003", cupons.get(2).getNumeroCfe(), "O terceiro cupom deve ter o maior número");
}
    /**
     * Teste para verificar se o repositório ordena os cupons por valor total
     * Este teste valida o critério: "Permitir listar os cupons ordenados por valor total do cupom"
     */
    @Test
    void deveOrdenarCuponsPorValorTotal() {
        // Criar e salvar cupons com valores diferentes
        Cupom cupom1 = new Cupom();
        cupom1.setChaveAcesso("chave1");
        cupom1.setValorTotalProdutos(new BigDecimal("100.00"));
        
        Cupom cupom2 = new Cupom();
        cupom2.setChaveAcesso("chave2");
        cupom2.setValorTotalProdutos(new BigDecimal("50.00"));
        
        Cupom cupom3 = new Cupom();
        cupom3.setChaveAcesso("chave3");
        cupom3.setValorTotalProdutos(new BigDecimal("150.00"));
        
        // Salvar os cupons fora de ordem
        cupomRepository.saveAll(List.of(cupom1, cupom2, cupom3));
        
        // Buscar os cupons ordenados por valor
        List<Cupom> cupons = cupomRepository.findAllByOrderByValorTotalProdutosAsc();
        
        // Verificar se estão na ordem correta
        assertEquals(3, cupons.size(), "Devem ser retornados todos os cupons");
        assertEquals(new BigDecimal("50.00"), cupons.get(0).getValorTotalProdutos(), "O primeiro cupom deve ter o menor valor");
        assertEquals(new BigDecimal("100.00"), cupons.get(1).getValorTotalProdutos(), "O segundo cupom deve ter o valor intermediário");
        assertEquals(new BigDecimal("150.00"), cupons.get(2).getValorTotalProdutos(), "O terceiro cupom deve ter o maior valor");
    }
    
    /**
     * Teste para verificar se o repositório suporta paginação e ordenação
     * Este teste valida os critérios de ordenação com paginação
     */
    @Test
    void deveSuportarPaginacaoEOrdenacao() {
        // Criar e salvar vários cupons
        for (int i = 1; i <= 25; i++) {
            Cupom cupom = new Cupom();
            cupom.setChaveAcesso("chave" + i);
            cupom.setNumeroCfe(String.format("%03d", i));
            cupom.setValorTotalProdutos(new BigDecimal(i * 10));
            cupomRepository.save(cupom);
        }
        
        // Criar paginação para número do CF-e
        Pageable pageableNumero = PageRequest.of(0, 10, Sort.by("numeroCfe").ascending());
        Page<Cupom> paginaNumero = cupomRepository.findAll(pageableNumero);
        
        // Verificar paginação por número
        assertEquals(10, paginaNumero.getContent().size(), "A página deve conter 10 cupons");
        assertEquals("001", paginaNumero.getContent().get(0).getNumeroCfe(), "O primeiro cupom deve ter o menor número");
        
        // Criar paginação para valor total
        Pageable pageableValor = PageRequest.of(0, 10, Sort.by("valorTotalProdutos").descending());
        Page<Cupom> paginaValor = cupomRepository.findAll(pageableValor);
        
        // Verificar paginação por valor (decrescente)
        assertEquals(10, paginaValor.getContent().size(), "A página deve conter 10 cupons");
        assertEquals(new BigDecimal("250"), paginaValor.getContent().get(0).getValorTotalProdutos(), 
                "O primeiro cupom deve ter o maior valor");
    }
}
