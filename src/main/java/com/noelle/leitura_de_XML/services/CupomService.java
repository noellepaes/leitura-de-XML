package com.noelle.leitura_de_XML.services;


import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.exception.DuplicidadeException;
import com.noelle.leitura_de_XML.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CupomService {
    
    private final CupomRepository cupomRepository;
    private final ProcessadorXmlService processadorXmlService;
    
    /**
     * Processa um arquivo XML de CF-e SAT
     */
    @Transactional
    public Cupom processarXml(String conteudoXml) {
        Cupom cupom = processadorXmlService.processar(conteudoXml);
        
        // Verifica se o cupom já existe
        if (cupomRepository.existsByChaveAcesso(cupom.getChaveAcesso())) {
            throw new DuplicidadeException("Cupom com chave de acesso " + cupom.getChaveAcesso() + " já existe");
        }
        
        // Salva o cupom
        return cupomRepository.save(cupom);
    }
    
    /**
     * Processa um arquivo ZIP contendo múltiplos XMLs de CF-e SAT
     */
    @Transactional
    public int processarArquivoZip(byte[] conteudoZip) {
        List<String> xmls = processadorXmlService.extrairXmlsDoZip(conteudoZip);
        int processados = 0;
        
        for (String xml : xmls) {
            try {
                processarXml(xml);
                processados++;
                log.info("XML processado com sucesso: {}/{}", processados, xmls.size());
            } catch (DuplicidadeException e) {
                log.warn("XML ignorado (duplicado): {}", e.getMessage());
            } catch (Exception e) {
                log.error("Erro ao processar XML: {}", e.getMessage(), e);
            }
        }
        
        return processados;
    }
    
    /**
     * Lista todos os cupons ordenados pelo número do CF-e
     */
    @Transactional(readOnly = true)
    public List<Cupom> listarPorNumero() {
        return cupomRepository.findAllByOrderByNumeroCfeAsc();
    }
    
    /**
     * Lista todos os cupons ordenados pelo valor total
     */
    @Transactional(readOnly = true)
    public List<Cupom> listarPorValor() {
        return cupomRepository.findAllByOrderByValorTotalProdutosAsc();
    }
}
