package com.noelle.leitura_de_XML.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CupomDTO {
    private String chaveAcesso;
    private String numeroCfe;
    private LocalDate dataEmissao;
    private BigDecimal valorTotalIcms;
    private BigDecimal valorTotalProdutos;
    private BigDecimal valorTotalDescontos;
    private BigDecimal valorTotalPis;
    private BigDecimal valorTotalCofins;
    private BigDecimal valorTotalOutros;
    private List<ItemDTO> itens = new ArrayList<>();
}
