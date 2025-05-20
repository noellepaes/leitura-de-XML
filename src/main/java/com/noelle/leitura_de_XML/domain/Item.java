package com.noelle.leitura_de_XML.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_chave_acesso", nullable = false)
    private Cupom cupom;
    
    @Column(name = "codigo", nullable = false, length = 60)
    private String codigo;
    
    @Column(name = "descricao", nullable = false)
    private String descricao;
    
    @Column(name = "quantidade", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidade;
    
    @Column(name = "cfop", nullable = false, length = 4)
    private String cfop;
    
    @Column(name = "valor_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorUnitario;
    
    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(name = "valor_desconto", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDesconto;
    
    @Column(name = "cst", nullable = false, length = 3)
    private String cst;
    
    @Column(name = "aliquota_icms", precision = 5, scale = 2)
    private BigDecimal aliquotaIcms;
    
    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal valorIcms;
    
    @Column(name = "base_pis", precision = 15, scale = 2)
    private BigDecimal basePis;
    
    @Column(name = "aliquota_pis", precision = 5, scale = 2)
    private BigDecimal aliquotaPis;
    
    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal valorPis;
    
    @Column(name = "base_cofins", precision = 15, scale = 2)
    private BigDecimal baseCofins;
    
    @Column(name = "aliquota_cofins", precision = 5, scale = 2)
    private BigDecimal aliquotaCofins;
    
    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal valorCofins;
    
    @Column(name = "unidade_medida", nullable = false, length = 6)
    private String unidadeMedida;
    
    @Column(name = "ncm", nullable = false, length = 8)
    private String ncm;
    
    @Column(name = "numero_sequencial", nullable = false)
    private Integer numeroSequencial;
}
