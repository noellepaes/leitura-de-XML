package com.noelle.leitura_de_XML.services;
        
import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.exception.DuplicidadeException;
import com.noelle.leitura_de_XML.exception.ProcessamentoException;
import com.noelle.leitura_de_XML.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Serviço responsável pelo processamento e consulta de cupons fiscais.
 * Implementa a lógica de negócio para processamento de XMLs e persistência dos dados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CupomService {
    
    private final CupomRepository cupomRepository;
    private final ProcessadorXmlService processadorXmlService;
    
    /**
     * Processa um arquivo XML de CF-e SAT e persiste no banco de dados.
     * Verifica duplicidade pela chave de acesso antes de persistir.
     * 
     * @param conteudoXml Conteúdo do XML a ser processado
     * @return Cupom processado e persistido
     * @throws DuplicidadeException se o cupom já existir no banco
     */
    @Transactional
    public Cupom processarXml(String conteudoXml) {
        log.debug("Iniciando processamento de XML");
        
        // Processa o XML e obtém o objeto Cupom
        Cupom cupom = processadorXmlService.processar(conteudoXml);
        
        // Verifica se o cupom já existe
        if (cupomRepository.existsByChaveAcesso(cupom.getChaveAcesso())) {
            log.warn("Cupom com chave de acesso {} já existe", cupom.getChaveAcesso());
            throw new DuplicidadeException("Cupom com chave de acesso " + cupom.getChaveAcesso() + " já existe");
        }
        
        // Salva o cupom
        Cupom cupomSalvo = cupomRepository.save(cupom);
        log.info("Cupom persistido com sucesso: chave={}, número={}, itens={}", 
                cupomSalvo.getChaveAcesso(), cupomSalvo.getNumeroCfe(), cupomSalvo.getItens().size());
        
        return cupomSalvo;
    }
    
    /**
     * Processa um arquivo XML de CF-e SAT a partir do caminho do arquivo.
     * 
     * @param caminhoArquivo Caminho do arquivo XML a ser processado
     * @return Cupom processado e persistido
     * @throws IOException se ocorrer erro na leitura do arquivo
     * @throws DuplicidadeException se o cupom já existir no banco
     */
    @Transactional
    public Cupom processarXmlUnico(String caminhoArquivo) throws IOException {
        // Normalizar o caminho do arquivo (substituir \ por /)
        caminhoArquivo = caminhoArquivo.replace('\\', '/');
        
        log.info("Lendo arquivo XML: {}", caminhoArquivo);
        
        // Ler o arquivo XML
        String conteudoXml = new String(Files.readAllBytes(Paths.get(caminhoArquivo)), StandardCharsets.UTF_8);
        
        // Processar o XML usando o método existente
        return processarXml(conteudoXml);
    }
    
    /**
     * Processa um arquivo ZIP contendo múltiplos XMLs de CF-e SAT.
     * Extrai os XMLs do arquivo ZIP, valida cada um e processa os válidos.
     * 
     * @param conteudoZip Conteúdo do arquivo ZIP
     * @return Número de cupons processados com sucesso
     */
    @Transactional
    public int processarArquivoZip(byte[] conteudoZip) {
        log.info("Iniciando processamento de arquivo ZIP, tamanho: {} bytes", conteudoZip.length);
        
        // Extrai os XMLs do arquivo ZIP
        List<String> xmls = processadorXmlService.extrairXmlsDoZip(conteudoZip);
        log.info("Extração concluída. Total de XMLs encontrados: {}", xmls.size());
        
        int processados = 0;
        int falhas = 0;
        
        // Processa cada XML
        for (String xml : xmls) {
            try {
                // Valida o XML antes de processá-lo
                if (!processadorXmlService.validarXml(xml)) {
                    falhas++;
                    log.error("XML inválido, ignorando processamento");
                    continue;
                }
                
                // Processa o XML
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
     * Lista todos os cupons ordenados pelo número do CF-e.
     * 
     * @return Lista de cupons ordenados por número
     */
    @Transactional(readOnly = true)
    public List<Cupom> listarPorNumero() {
        return cupomRepository.findAllByOrderByNumeroCfeAsc();
    }
    
    /**
     * Lista todos os cupons ordenados pelo número do CF-e com paginação.
     * 
     * @param pageable Informações de paginação e ordenação
     * @return Página de cupons ordenados por número
     */
    @Transactional(readOnly = true)
    public Page<Cupom> listarPorNumeroPaginado(Pageable pageable) {
        return cupomRepository.findAll(pageable);
    }
    
    /**
     * Lista todos os cupons ordenados pelo valor total.
     * 
     * @return Lista de cupons ordenados por valor
     */
    @Transactional(readOnly = true)
    public List<Cupom> listarPorValor() {
        return cupomRepository.findAllByOrderByValorTotalProdutosAsc();
    }
    
    /**
     * Lista todos os cupons ordenados pelo valor total com paginação.
     * 
     * @param pageable Informações de paginação e ordenação
     * @return Página de cupons ordenados por valor
     */
    @Transactional(readOnly = true)
    public Page<Cupom> listarPorValorPaginado(Pageable pageable) {
        return cupomRepository.findAll(pageable);
    }
}
