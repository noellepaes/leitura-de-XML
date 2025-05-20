package com.noelle.leitura_de_XML.xml;


import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.services.ProcessadorXmlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;


@Component
@Profile("!test") // Não executar durante testes
public class TestRunner implements CommandLineRunner {

    @Autowired
    private ProcessadorXmlService processadorXmlService;

    @Override
    public void run(String... args) throws Exception {
        // Teste de processamento de um único XML
        ClassPathResource resource = new ClassPathResource("exemplos/CFe35241260892858000130590014453660039237378714.xml");
        String conteudoXml = new String(Files.readAllBytes(resource.getFile().toPath()));
        
        System.out.println("=== TESTANDO PROCESSAMENTO DE XML ===");
        try {
            Cupom cupom = processadorXmlService.processar(conteudoXml);
            System.out.println("XML processado com sucesso!");
            System.out.println("Chave de Acesso: " + cupom.getChaveAcesso());
            System.out.println("Número CF-e: " + cupom.getNumeroCfe());
            System.out.println("Quantidade de Itens: " + cupom.getItens().size());
        } catch (Exception e) {
            System.err.println("Erro ao processar XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
