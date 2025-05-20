package com.noelle.leitura_de_XML.mapper;



import com.noelle.leitura_de_XML.domain.Item;
import com.noelle.leitura_de_XML.dto.ItemDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public Item toEntity(ItemDTO dto) {
        if (dto == null) {
            return null;
        }

        Item entity = new Item();
        entity.setCodigo(dto.getCodigo());
        entity.setDescricao(dto.getDescricao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setCfop(dto.getCfop());
        entity.setValorUnitario(dto.getValorUnitario());
        entity.setValorTotal(dto.getValorTotal());
        entity.setValorDesconto(dto.getValorDesconto());
        entity.setCst(dto.getCst());
        entity.setAliquotaIcms(dto.getAliquotaIcms());
        entity.setValorIcms(dto.getValorIcms());
        entity.setBasePis(dto.getBasePis());
        entity.setAliquotaPis(dto.getAliquotaPis());
        entity.setValorPis(dto.getValorPis());
        entity.setBaseCofins(dto.getBaseCofins());
        entity.setAliquotaCofins(dto.getAliquotaCofins());
        entity.setValorCofins(dto.getValorCofins());
        entity.setUnidadeMedida(dto.getUnidadeMedida());
        entity.setNcm(dto.getNcm());
        entity.setNumeroSequencial(dto.getNumeroSequencial());

        return entity;
    }

    public ItemDTO toDto(Item entity) {
        if (entity == null) {
            return null;
        }

        ItemDTO dto = new ItemDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setDescricao(entity.getDescricao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setCfop(entity.getCfop());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setValorTotal(entity.getValorTotal());
        dto.setValorDesconto(entity.getValorDesconto());
        dto.setCst(entity.getCst());
        dto.setAliquotaIcms(entity.getAliquotaIcms());
        dto.setValorIcms(entity.getValorIcms());
        dto.setBasePis(entity.getBasePis());
        dto.setAliquotaPis(entity.getAliquotaPis());
        dto.setValorPis(entity.getValorPis());
        dto.setBaseCofins(entity.getBaseCofins());
        dto.setAliquotaCofins(entity.getAliquotaCofins());
        dto.setValorCofins(entity.getValorCofins());
        dto.setUnidadeMedida(entity.getUnidadeMedida());
        dto.setNcm(entity.getNcm());
        dto.setNumeroSequencial(entity.getNumeroSequencial());

        return dto;
    }

    public List<ItemDTO> toDtoList(List<Item> entities) {
        if (entities == null) {
            return null;
        }

        List<ItemDTO> list = new ArrayList<>(entities.size());
        for (Item item : entities) {
            list.add(toDto(item));
        }
        return list;
    }
}
