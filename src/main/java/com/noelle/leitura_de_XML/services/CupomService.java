package com.noelle.leitura_de_XML.services;
import com.noelle.leitura_de_XML.domain.Cupom;

import com.noelle.leitura_de_XML.exception.DuplicidadeException;
import com.noelle.leitura_de_XML.exception.ProcessamentoException;
import com.noelle.leitura_de_XML.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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




    // Adicione este método na classe CupomService
@Transactional
public Cupom processarXmlUnico(String caminhoArquivo) throws IOException {
    // Ler o arquivo XML
    String conteudoXml = new String(Files.readAllBytes(Paths.get(caminhoArquivo)), StandardCharsets.UTF_8);
    
    // Processar o XML
    Cupom cupom = processadorXmlService.processar(conteudoXml);
    
    // Verificar se o cupom já existe
    if (cupomRepository.existsByChaveAcesso(cupom.getChaveAcesso())) {
        log.warn("Cupom com chave de acesso {} já existe, atualizando", cupom.getChaveAcesso());
        // Opcional: você pode optar por atualizar em vez de lançar exceção
        // return cupomRepository.save(cupom);
        throw new DuplicidadeException("Cupom com chave de acesso " + cupom.getChaveAcesso() + " já existe");
    }
    
    // Salvar o cupom
    Cupom cupomSalvo = cupomRepository.save(cupom);
    log.info("Cupom persistido com sucesso: chave={}, número={}, itens={}", 
             cupomSalvo.getChaveAcesso(), cupomSalvo.getNumeroCfe(), cupomSalvo.getItens().size());
    
    return cupomSalvo;
}

    








    /**
     * Processa um arquivo ZIP contendo múltiplos XMLs de CF-e SAT
     */
    @Transactional
public int processarArquivoZip(byte[] conteudoZip) {
    log.info("Iniciando processamento de arquivo ZIP, tamanho: {} bytes", conteudoZip.length);
    List<String> xmls = processadorXmlService.extrairXmlsDoZip(conteudoZip);
    log.info("Extração concluída. Total de XMLs encontrados: {}", xmls.size());
    
    int processados = 0;
    int falhas = 0;
    
    for (String xml : xmls) {
        try {
            // Validar o XML antes de processá-lo
            if (!processadorXmlService.validarXml(xml)) {
                falhas++;
                log.error("XML inválido, ignorando processamento");
                continue;
            }
            
            processarXml(xml);
            processados++;
            log.info("XML processado com sucesso: {}/{}", processados, xmls.size());
        } catch (DuplicidadeException e) {
            log.warn("XML ignorado (duplicado): {}", e.getMessage());
        } catch (ProcessamentoException e) {
            falhas++;
            log.error("Erro ao processar XML: {}", e.getMessage());
        } catch (Exception e) {
            falhas++;
            log.error("Erro inesperado ao processar XML: {}", e.getMessage(), e);
        }
    }
    
    log.info("Processamento concluído. Total: {}, Sucesso: {}, Falhas: {}", 
             xmls.size(), processados, falhas);
    
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