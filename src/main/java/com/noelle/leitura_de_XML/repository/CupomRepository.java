package com.noelle.leitura_de_XML.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.noelle.leitura_de_XML.domain.Cupom;

import java.util.List;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, String> {
    
    /**
     * Busca todos os cupons ordenados pelo número do CF-e
     */
    List<Cupom> findAllByOrderByNumeroCfeAsc();
    
    /**
     * Busca todos os cupons ordenados pelo valor total dos produtos
     */
    List<Cupom> findAllByOrderByValorTotalProdutosAsc();
    
    /**
     * Verifica se já existe um cupom com a chave de acesso informada
     */
    boolean existsByChaveAcesso(String chaveAcesso);
}

