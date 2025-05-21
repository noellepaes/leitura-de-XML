package com.noelle.leitura_de_XML.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.domain.Item;
import com.noelle.leitura_de_XML.exception.ProcessamentoException;
import com.noelle.leitura_de_XML.services.ProcessadorXmlService;

@SpringBootTest
class ProcessadorXmlServiceTest {

    @Autowired
    private ProcessadorXmlService processadorXmlService;
    
    /**
     * Teste para verificar se o serviço consegue extrair XMLs de um arquivo ZIP
     * Este teste valida o critério: "Processar um .zip com diversos XMLs de CF-e SAT"
     */
    @Test
    void deveExtrairXmlsDoZip() throws IOException {
        // Carregar um arquivo ZIP de teste
        byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
        // Extrair os XMLs
        List<String> xmls = processadorXmlService.extrairXmlsDoZip(conteudoZip);
        
        // Verificar se foram extraídos XMLs
        assertFalse(xmls.isEmpty(), "O ZIP deve conter pelo menos um XML");
        
        // Verificar se o conteúdo extraído parece ser um XML de CF-e SAT
        assertTrue(xmls.get(0).contains("<CFe>"), "O conteúdo extraído deve ser um XML de CF-e");
    }
    
    /**
     * Teste para verificar se o serviço consegue processar um XML e extrair os dados corretamente
     * Este teste valida o critério: "Extrair dados dos cupons e seus itens"
     */
    @Test
    void deveProcessarXmlCorretamente() throws IOException {
        // Carregar um XML de teste
        String xml = Files.readString(Paths.get("src/test/resources/cupom_exemplo.xml"));
        
        // Processar o XML
        Cupom cupom = processadorXmlService.processar(xml);
        
        // Verificar se os dados foram extraídos corretamente
        assertNotNull(cupom.getChaveAcesso(), "A chave de acesso deve ser extraída");
        assertNotNull(cupom.getNumeroCfe(), "O número do CF-e deve ser extraído");
        assertFalse(cupom.getItens().isEmpty(), "O cupom deve ter pelo menos um item");
        
        // Verificar dados específicos do cupom
        assertNotNull(cupom.getDataEmissao(), "A data de emissão deve ser extraída");
        assertNotNull(cupom.getValorTotalProdutos(), "O valor total dos produtos deve ser extraído");
        
        // Verificar dados do primeiro item
        Item primeiroItem = cupom.getItens().get(0);
        assertNotNull(primeiroItem.getCodigo(), "O código do item deve ser extraído");
        assertNotNull(primeiroItem.getDescricao(), "A descrição do item deve ser extraída");
        assertTrue(primeiroItem.getQuantidade().compareTo(BigDecimal.ZERO) > 0, "A quantidade deve ser maior que zero");
    }
    
    /**
     * Teste para verificar se o serviço identifica XMLs inválidos
     * Este teste valida a robustez do processamento
     */
    @Test
    void deveIdentificarXmlInvalido() {
        // XML mal formado
        String xmlInvalido = "<CFe><infCFe>Dados incompletos</infCFe>";
        
        // Verificar se o XML é identificado como inválido
        boolean valido = processadorXmlService.validarXml(xmlInvalido);
        
        // Confirmar que o XML foi identificado como inválido
        assertFalse(valido, "O XML mal formado deve ser identificado como inválido");
    }
    
    /**
     * Teste para verificar se o serviço lança exceção ao processar cupom cancelado
     * Este teste valida o critério: "Ignorar cupons cancelados"
     */
    @Test
    void deveLancarExcecaoAoProcessarCupomCancelado() throws IOException {
        // Carregar um XML de cupom cancelado
        String xmlCancelado = Files.readString(Paths.get("src/test/resources/cupom_cancelado.xml"));
        
        // Verificar se uma exceção é lançada ao processar o cupom cancelado
        assertThrows(ProcessamentoException.class, () -> {
            processadorXmlService.processar(xmlCancelado);
        }, "Deve lançar exceção ao processar um cupom cancelado");
    }
}
