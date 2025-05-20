package com.noelle.leitura_de_XML.services;

import com.noelle.leitura_de_XML.domain.Cupom;
import com.noelle.leitura_de_XML.domain.Item;
import com.noelle.leitura_de_XML.exception.ProcessamentoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ProcessadorXmlService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * Extrai os XMLs de um arquivo ZIP
     */
   public List<String> extrairXmlsDoZip(byte[] conteudoZip) {
    log.info("Iniciando extração de XMLs do arquivo ZIP, tamanho: {} bytes", conteudoZip.length);
    List<String> xmls = new ArrayList<>();
    
    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(conteudoZip))) {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (entry.getName().endsWith(".xml")) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                String xml = outputStream.toString("UTF-8");
                xmls.add(xml);
                log.debug("XML extraído: {}, tamanho: {} bytes", entry.getName(), xml.length());
            }
        }
        log.info("Extração concluída. Total de XMLs encontrados: {}", xmls.size());
    } catch (Exception e) {
        log.error("Erro ao extrair XMLs do arquivo ZIP: {}", e.getMessage(), e);
        throw new ProcessamentoException("Erro ao extrair XMLs do arquivo ZIP: " + e.getMessage(), e);
    }
    
    return xmls;
}

    
    /**
     * Processa um XML de CF-e SAT
     */
    public Cupom processar(String conteudoXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(conteudoXml)));
            
            // Verifica se é um cupom cancelado
            NodeList cancelamentoList = document.getElementsByTagName("CFeCanc");
            if (cancelamentoList.getLength() > 0) {
                throw new ProcessamentoException("Cupom cancelado, ignorando processamento");
            }
            
            Element infCFe = (Element) document.getElementsByTagName("infCFe").item(0);
            String chaveAcesso = infCFe.getAttribute("Id").replace("CFe", "");
            
            Element ide = (Element) infCFe.getElementsByTagName("ide").item(0);
            String numeroCfe = getElementTextContent(ide, "nCFe");
            String dataEmissaoStr = getElementTextContent(ide, "dEmi");
            LocalDate dataEmissao = LocalDate.parse(dataEmissaoStr, DATE_FORMATTER);
            
            Element total = (Element) infCFe.getElementsByTagName("total").item(0);
            Element icmsTot = (Element) total.getElementsByTagName("ICMSTot").item(0);
            
            BigDecimal valorTotalIcms = new BigDecimal(getElementTextContent(icmsTot, "vICMS"));
            BigDecimal valorTotalProdutos = new BigDecimal(getElementTextContent(icmsTot, "vProd"));
            BigDecimal valorTotalDescontos = new BigDecimal(getElementTextContent(icmsTot, "vDesc"));
            BigDecimal valorTotalPis = new BigDecimal(getElementTextContent(icmsTot, "vPIS"));
            BigDecimal valorTotalCofins = new BigDecimal(getElementTextContent(icmsTot, "vCOFINS"));
            BigDecimal valorTotalOutros = new BigDecimal(getElementTextContent(icmsTot, "vOutro"));
            
            // Em vez de usar o Builder, crie uma instância diretamente
        Cupom cupom = new Cupom();
        cupom.setChaveAcesso(chaveAcesso);
        cupom.setNumeroCfe(numeroCfe);
        cupom.setDataEmissao(dataEmissao);
        cupom.setValorTotalIcms(valorTotalIcms);
        cupom.setValorTotalProdutos(valorTotalProdutos);
        cupom.setValorTotalDescontos(valorTotalDescontos);
        cupom.setValorTotalPis(valorTotalPis);
        cupom.setValorTotalCofins(valorTotalCofins);
        cupom.setValorTotalOutros(valorTotalOutros);
        
            
            // Processa os itens
            NodeList detList = infCFe.getElementsByTagName("det");
            for (int i = 0; i < detList.getLength(); i++) {
                Element det = (Element) detList.item(i);
                int numeroSequencial = Integer.parseInt(det.getAttribute("nItem"));
                
                Element prod = (Element) det.getElementsByTagName("prod").item(0);
                String codigo = getElementTextContent(prod, "cProd");
                String descricao = getElementTextContent(prod, "xProd");
                String ncm = getElementTextContent(prod, "NCM");
                String cfop = getElementTextContent(prod, "CFOP");
                String unidadeMedida = getElementTextContent(prod, "uCom");
                // BigDecimal quantidade = new BigDecimal(getElementTextContent(prod, "qCom"));
                BigDecimal quantidade = new BigDecimal(getElementTextContent(prod, "qCom", "0.00"));
                BigDecimal valorUnitario = new BigDecimal(getElementTextContent(prod, "vUnCom"));
                BigDecimal valorTotal = new BigDecimal(getElementTextContent(prod, "vProd"));
                BigDecimal valorDesconto = new BigDecimal(getElementTextContent(prod, "vDesc", "0.00"));
                
                Element imposto = (Element) det.getElementsByTagName("imposto").item(0);
                
                // ICMS
                String cst = "";
                BigDecimal aliquotaIcms = BigDecimal.ZERO;
                BigDecimal valorIcms = BigDecimal.ZERO;
                
                NodeList icmsList = imposto.getElementsByTagName("ICMS");
                if (icmsList.getLength() > 0) {
                    Element icms = (Element) icmsList.item(0);
                    NodeList icmsChildren = icms.getChildNodes();
                    for (int j = 0; j < icmsChildren.getLength(); j++) {
                        if (icmsChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element icmsType = (Element) icmsChildren.item(j);
                            try {
                                cst = getElementTextContent(icmsType, "CST", "");
                            } catch (Exception e) {
                                log.warn("CST não encontrado para o item {}", numeroSequencial);
                            }
                            try {
                                aliquotaIcms = new BigDecimal(getElementTextContent(icmsType, "pICMS", "0.00"));
                            } catch (Exception e) {
                                log.warn("pICMS não encontrado para o item {}", numeroSequencial);
                            }
                            try {
                                valorIcms = new BigDecimal(getElementTextContent(icmsType, "vICMS", "0.00"));
                            } catch (Exception e) {
                                log.warn("vICMS não encontrado para o item {}", numeroSequencial);
                            }
                            break;
                        }
                    }
                }
                    // PIS
                BigDecimal basePis = BigDecimal.ZERO;
                BigDecimal aliquotaPis = BigDecimal.ZERO;
                BigDecimal valorPis = BigDecimal.ZERO;
                
                NodeList pisList = imposto.getElementsByTagName("PIS");
                if (pisList.getLength() > 0) {
                    Element pis = (Element) pisList.item(0);
                    NodeList pisChildren = pis.getChildNodes();
                    for (int j = 0; j < pisChildren.getLength(); j++) {
                        if (pisChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element pisType = (Element) pisChildren.item(j);
                            basePis = new BigDecimal(getElementTextContent(pisType, "vBC", "0.00"));
                            aliquotaPis = new BigDecimal(getElementTextContent(pisType, "pPIS", "0.00"));
                            valorPis = new BigDecimal(getElementTextContent(pisType, "vPIS", "0.00"));
                            break;
                        }
                    }
                }
                
                // COFINS
                BigDecimal baseCofins = BigDecimal.ZERO;
                BigDecimal aliquotaCofins = BigDecimal.ZERO;
                BigDecimal valorCofins = BigDecimal.ZERO;
                
                NodeList cofinsList = imposto.getElementsByTagName("COFINS");
                if (cofinsList.getLength() > 0) {
                    Element cofins = (Element) cofinsList.item(0);
                    NodeList cofinsChildren = cofins.getChildNodes();
                    for (int j = 0; j < cofinsChildren.getLength(); j++) {
                        if (cofinsChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element cofinsType = (Element) cofinsChildren.item(j);
                            baseCofins = new BigDecimal(getElementTextContent(cofinsType, "vBC", "0.00"));
                            aliquotaCofins = new BigDecimal(getElementTextContent(cofinsType, "pCOFINS", "0.00"));
                            valorCofins = new BigDecimal(getElementTextContent(cofinsType, "vCOFINS", "0.00"));
                            break;
                        }
                    }
                }
                
                Item item = Item.builder()
                        .codigo(codigo)
                        .descricao(descricao)
                        .quantidade(quantidade)
                        .cfop(cfop)
                        .valorUnitario(valorUnitario)
                        .valorTotal(valorTotal)
                        .valorDesconto(valorDesconto)
                        .cst(cst)
                        .aliquotaIcms(aliquotaIcms)
                        .valorIcms(valorIcms)
                        .basePis(basePis)
                        .aliquotaPis(aliquotaPis)
                        .valorPis(valorPis)
                        .baseCofins(baseCofins)
                        .aliquotaCofins(aliquotaCofins)
                        .valorCofins(valorCofins)
                        .unidadeMedida(unidadeMedida)
                        .ncm(ncm)
                        .numeroSequencial(numeroSequencial)
                        .build();
                
                cupom.adicionarItem(item);
            }
            
            return cupom;
        } catch (Exception e) {
            throw new ProcessamentoException("Erro ao processar XML: " + e.getMessage(), e);
        }
    }
    
    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        throw new ProcessamentoException("Tag " + tagName + " não encontrada");
    }
    
    private String getElementTextContent(Element parent, String tagName, String defaultValue) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return defaultValue;
    }
    
        /**
     * Valida se o XML está bem formado antes de processá-lo
     */
    public boolean validarXml(String conteudoXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(conteudoXml)));
            return true;
        } catch (Exception e) {
            log.warn("XML inválido: {}", e.getMessage());
            return false;
        }
    }
}