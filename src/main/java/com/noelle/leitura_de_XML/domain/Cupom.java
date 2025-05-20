// Created by Noelle on 10/10/2023
package com.noelle.leitura_de_XML.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cupom")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cupom {
    
    @Id
    @Column(name = "chave_acesso", length = 44)
    private String chaveAcesso;
    
    @Column(name = "numero_cfe", nullable = false, length = 10)
    private String numeroCfe;
    
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;
    
    @Column(name = "valor_total_icms", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalIcms;
    
    @Column(name = "valor_total_produtos", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalProdutos;
    
    @Column(name = "valor_total_descontos", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalDescontos;
    
    @Column(name = "valor_total_pis", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalPis;
    
    @Column(name = "valor_total_cofins", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalCofins;
    
    @Column(name = "valor_total_outros", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalOutros;
    
    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;
    
    @OneToMany(mappedBy = "cupom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> itens = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        dataProcessamento = LocalDateTime.now();
    }
    
    public void adicionarItem(Item item) {
        itens.add(item);
        item.setCupom(this);
    }
}
