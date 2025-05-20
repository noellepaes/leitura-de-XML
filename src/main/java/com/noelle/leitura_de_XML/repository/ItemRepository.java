package com.noelle.leitura_de_XML.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.noelle.leitura_de_XML.domain.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Busca todos os itens de um cupom específico
     */
    List<Item> findByCupomChaveAcesso(String chaveAcesso);
}

