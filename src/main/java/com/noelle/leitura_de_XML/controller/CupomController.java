package com.noelle.leitura_de_XML.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.dto.CupomDTO;
import com.noelle.leitura_de_XML.mapper.CupomMapper;
import com.noelle.leitura_de_XML.services.CupomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por expor endpoints REST para processamento e consulta de cupons fiscais.
 * Implementa a interface REST para consulta dos cupons conforme requisitos.
 */
@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cupons", description = "API para gerenciamento de cupons fiscais")
public class CupomController {
    
    private final CupomService cupomService;
    private final CupomMapper cupomMapper;
    
    /**
     * Processa um arquivo ZIP contendo múltiplos XMLs de CF-e SAT.
     * 
     * @param arquivo Arquivo ZIP contendo XMLs de CF-e SAT
     * @return Mensagem de sucesso com o número de cupons processados
     */
    @PostMapping(value = "/processar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Processa um arquivo ZIP contendo XMLs de CF-e SAT",
        description = "Recebe um arquivo ZIP contendo XMLs de CF-e SAT e processa todos os XMLs válidos"
    )
    @Parameter(
        name = "arquivo",
        description = "Arquivo ZIP contendo XMLs de CF-e SAT",
        required = true,
        content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
    )
    public ResponseEntity<String> processarArquivoZip(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            log.info("Recebido arquivo para processamento: {}, tamanho: {} bytes", 
                    arquivo.getOriginalFilename(), arquivo.getSize());
            
            byte[] conteudo = arquivo.getBytes();
            int processados = cupomService.processarArquivoZip(conteudo);
            
            return ResponseEntity.ok("Arquivo processado com sucesso. Total de cupons processados: " + processados);
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro ao ler arquivo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar arquivo: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro ao processar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos os cupons com opções de paginação e ordenação por número do CF-e.
     * 
     * @param page Número da página (começando em 0)
     * @param size Tamanho da página
     * @return Lista paginada de cupons ordenados por número do CF-e
     */
    @GetMapping("/por-numero")
    @Operation(
        summary = "Lista todos os cupons ordenados pelo número do CF-e",
        description = "Retorna uma lista paginada de cupons ordenados pelo número do CF-e em ordem crescente"
    )
    public ResponseEntity<Page<CupomDTO>> listarPorNumero(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("numeroCfe").ascending());
        // Usar diretamente o resultado do serviço sem atribuir a uma variável intermediária
        return ResponseEntity.ok(cupomService.listarPorNumeroPaginado(pageable).map(cupomMapper::toDto));
    }
    
    /**
     * Lista todos os cupons com opções de paginação e ordenação por valor total.
     * 
     * @param page Número da página (começando em 0)
     * @param size Tamanho da página
     * @param ascending Se true, ordena em ordem crescente; se false, em ordem decrescente
     * @return Lista paginada de cupons ordenados por valor total
     */
    @GetMapping("/por-valor")
    @Operation(
        summary = "Lista todos os cupons ordenados pelo valor total",
        description = "Retorna uma lista paginada de cupons ordenados pelo valor total dos produtos"
    )
    public ResponseEntity<Page<CupomDTO>> listarPorValor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean ascending) {
        
        Sort sort = ascending ? 
                Sort.by("valorTotalProdutos").ascending() : 
                Sort.by("valorTotalProdutos").descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        // Usar diretamente o resultado do serviço sem atribuir a uma variável intermediária
        return ResponseEntity.ok(cupomService.listarPorValorPaginado(pageable).map(cupomMapper::toDto));
    }
    
    /**
     * Processa um único arquivo XML de CF-e SAT.
     * 
     * @param caminhoArquivo Caminho do arquivo XML a ser processado
     * @return Mensagem de sucesso com a chave de acesso do cupom processado
     */
    @PostMapping("/processar-xml-unico")
    @Operation(
        summary = "Processa um único arquivo XML de CF-e SAT",
        description = "Recebe o caminho de um arquivo XML de CF-e SAT e o processa"
    )
    public ResponseEntity<String> processarXmlUnico(@RequestParam("caminho") String caminhoArquivo) {
        try {
            log.info("Processando arquivo XML único: {}", caminhoArquivo);
            Cupom cupom = cupomService.processarXmlUnico(caminhoArquivo);
            return ResponseEntity.ok("XML processado com sucesso. Chave de acesso: " + cupom.getChaveAcesso());
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro ao ler arquivo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar arquivo: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro ao processar arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/upload-form")
    public String uploadForm() {
        return "upload-form";
    }  
}
