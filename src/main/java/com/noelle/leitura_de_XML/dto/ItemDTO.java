package com.noelle.leitura_de_XML.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String codigo;
    private String descricao;
    private BigDecimal quantidade;
    private String cfop;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorDesconto;
    private String cst;
    private BigDecimal aliquotaIcms;
    private BigDecimal valorIcms;
    private BigDecimal basePis;
    private BigDecimal aliquotaPis;
    private BigDecimal valorPis;
    private BigDecimal baseCofins;
    private BigDecimal aliquotaCofins;
    private BigDecimal valorCofins;
    private String unidadeMedida;
    private String ncm;
    private Integer numeroSequencial;
}
