package com.noelle.leitura_de_XML.controller;

import java.io.IOException;
import java.util.List;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cupons" )
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cupons", description = "API para gerenciamento de cupons fiscais")
public class CupomController {
    
    private final CupomService cupomService;
    private final CupomMapper cupomMapper;
    
    @PostMapping("/processar")
    @Operation(summary = "Processa um arquivo ZIP contendo XMLs de CF-e SAT")
    public ResponseEntity<String> processarArquivoZip(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            log.info("Recebido arquivo para processamento: {}", arquivo.getOriginalFilename());
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
    
    @GetMapping("/por-numero")
    @Operation(summary = "Lista todos os cupons ordenados pelo número do CF-e")
    public ResponseEntity<List<CupomDTO>> listarPorNumero() {
        List<Cupom> cupons = cupomService.listarPorNumero();
        return ResponseEntity.ok(cupomMapper.toDtoList(cupons));
    }
    
    @GetMapping("/por-valor")
    @Operation(summary = "Lista todos os cupons ordenados pelo valor total")
    public ResponseEntity<List<CupomDTO>> listarPorValor() {
        List<Cupom> cupons = cupomService.listarPorValor();
        return ResponseEntity.ok(cupomMapper.toDtoList(cupons));
    }
}
