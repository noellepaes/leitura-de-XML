package com.noelle.leitura_de_XML.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.noelle.leitura_de_XML.domain.Cupom; 
import com.noelle.leitura_de_XML.dto.CupomDTO;

import java.util.List;


import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CupomMapper {

    private final ItemMapper itemMapper;

    public CupomMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public Cupom toEntity(CupomDTO dto) {
        if (dto == null) {
            return null;
        }

        Cupom entity = new Cupom();
        // Ignorar dataProcessamento conforme solicitado
        entity.setChaveAcesso(dto.getChaveAcesso());
        entity.setNumeroCfe(dto.getNumeroCfe());
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setValorTotalIcms(dto.getValorTotalIcms());
        entity.setValorTotalProdutos(dto.getValorTotalProdutos());
        entity.setValorTotalDescontos(dto.getValorTotalDescontos());
        entity.setValorTotalPis(dto.getValorTotalPis());
        entity.setValorTotalCofins(dto.getValorTotalCofins());
        entity.setValorTotalOutros(dto.getValorTotalOutros());

        // Mapear lista de itens
        if (dto.getItens() != null) {
            List<com.noelle.leitura_de_XML.domain.Item> itensEntity = new ArrayList<>();
            for (com.noelle.leitura_de_XML.dto.ItemDTO itemDto : dto.getItens()) {
                itensEntity.add(itemMapper.toEntity(itemDto));
            }
            entity.setItens(itensEntity);
        }

        return entity;
    }

    public CupomDTO toDto(Cupom entity) {
        if (entity == null) {
            return null;
        }

        CupomDTO dto = new CupomDTO();
        dto.setChaveAcesso(entity.getChaveAcesso());
        dto.setNumeroCfe(entity.getNumeroCfe());
        dto.setDataEmissao(entity.getDataEmissao());
        dto.setValorTotalIcms(entity.getValorTotalIcms());
        dto.setValorTotalProdutos(entity.getValorTotalProdutos());
        dto.setValorTotalDescontos(entity.getValorTotalDescontos());
        dto.setValorTotalPis(entity.getValorTotalPis());
        dto.setValorTotalCofins(entity.getValorTotalCofins());
        dto.setValorTotalOutros(entity.getValorTotalOutros());

        // Mapear lista de itens para DTOs
        if (entity.getItens() != null) {
            List<com.noelle.leitura_de_XML.dto.ItemDTO> itensDto = new ArrayList<>();
            for (com.noelle.leitura_de_XML.domain.Item item : entity.getItens()) {
                itensDto.add(itemMapper.toDto(item));
            }
            dto.setItens(itensDto);
        }

        return dto;
    }

    public List<CupomDTO> toDtoList(List<Cupom> entities) {
        if (entities == null) {
            return null;
        }

        List<CupomDTO> list = new ArrayList<>(entities.size());
        for (Cupom cupom : entities) {
            list.add(toDto(cupom));
        }
        return list;
    }
}

