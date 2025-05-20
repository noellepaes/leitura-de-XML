// // Exemplo de teste para ProcessadorXmlService

// import java.io.IOException;
// import java.math.BigDecimal;

// import org.junit.Test;
// import org.junit.jupiter.api.BeforeEach;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.noelle.leitura_de_XML.domain.Cupom;
// import com.noelle.leitura_de_XML.repository.CupomRepository;
// import com.noelle.leitura_de_XML.services.CupomService;
// import com.noelle.leitura_de_XML.services.ProcessadorXmlService;

// @SpringBootTest
// class ProcessadorXmlServiceTest {

//     @Autowired
//     private ProcessadorXmlService processadorXmlService;
    
//     @Test
//     void deveExtrairXmlsDoZip() throws IOException {
//         // Carregar um arquivo ZIP de teste
//         byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
//         // Extrair os XMLs
//         List<String> xmls = processadorXmlService.extrairXmlsDoZip(conteudoZip);
        
//         // Verificar se foram extraídos XMLs
//         assertFalse(xmls.isEmpty());
//     }
    
//     @Test
//     void deveProcessarXmlCorretamente() throws IOException {
//         // Carregar um XML de teste
//         String xml = Files.readString(Paths.get("src/test/resources/cupom_exemplo.xml"));
        
//         // Processar o XML
//         Cupom cupom = processadorXmlService.processar(xml);
        
//         // Verificar se os dados foram extraídos corretamente
//         assertNotNull(cupom.getChaveAcesso());
//         assertNotNull(cupom.getNumeroCfe());
//         assertFalse(cupom.getItens().isEmpty());
//     }
// }

// // Exemplo de teste para CupomService
// @SpringBootTest
// class CupomServiceTest {

//     @Autowired
//     private CupomService cupomService;
    
//     @Autowired
//     private CupomRepository cupomRepository;
    
//     @BeforeEach
//     void setUp() {
//         // Limpar o banco antes de cada teste
//         cupomRepository.deleteAll();
//     }
    
//     @Test
//     void deveProcessarArquivoZip() throws IOException {
//         // Carregar um arquivo ZIP de teste
//         byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
//         // Processar o arquivo
//         int processados = cupomService.processarArquivoZip(conteudoZip);
        
//         // Verificar se foram processados cupons
//         assertTrue(processados > 0);
        
//         // Verificar se os cupons foram salvos no banco
//         List<Cupom> cupons = cupomRepository.findAll();
//         assertEquals(processados, cupons.size());
//     }
    
//     @Test
//     void deveEvitarDuplicidade() throws IOException {
//         // Carregar um XML de teste
//         String xml = Files.readString(Paths.get("src/test/resources/cupom_exemplo.xml"));
        
//         // Processar o XML pela primeira vez
//         cupomService.processarXml(xml);
        
//         // Tentar processar o mesmo XML novamente
//         assertThrows(DuplicidadeException.class, () -> {
//             cupomService.processarXml(xml);
//         });
//     }
// }

// // Exemplo de teste para CupomController
// @SpringBootTest
// @AutoConfigureMockMvc
// class CupomControllerTest {

//     @Autowired
//     private MockMvc mockMvc;
    
//     @Autowired
//     private CupomRepository cupomRepository;
    
//     @BeforeEach
//     void setUp() {
//         // Limpar o banco antes de cada teste
//         cupomRepository.deleteAll();
//     }
    
//     @Test
//     void deveProcessarArquivoZip() throws Exception {
//         // Carregar um arquivo ZIP de teste
//         byte[] conteudoZip = Files.readAllBytes(Paths.get("src/test/resources/cupons.zip"));
        
//         // Criar um MockMultipartFile
//         MockMultipartFile arquivo = new MockMultipartFile(
//             "arquivo", "cupons.zip", "application/zip", conteudoZip);
        
//         // Enviar o arquivo para o endpoint
//         mockMvc.perform(multipart("/api/cupons/processar")
//                 .file(arquivo))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(containsString("Arquivo processado com sucesso")));
        
//         // Verificar se os cupons foram salvos no banco
//         List<Cupom> cupons = cupomRepository.findAll();
//         assertFalse(cupons.isEmpty());
//     }
    
//     @Test
//     void deveListarCuponsPorNumero() throws Exception {
//         // Criar alguns cupons de teste
//         Cupom cupom1 = criarCupomTeste("12345", "001");
//         Cupom cupom2 = criarCupomTeste("67890", "002");
//         cupomRepository.saveAll(List.of(cupom1, cupom2));
        
//         // Consultar os cupons ordenados por número
//         mockMvc.perform(get("/api/cupons/por-numero"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].numeroCfe").value("001"))
//                 .andExpect(jsonPath("$[1].numeroCfe").value("002"));
//     }
    
//     private Cupom criarCupomTeste(String chaveAcesso, String numeroCfe) {
//         return Cupom.builder()
//                 .chaveAcesso(chaveAcesso)
//                 .numeroCfe(numeroCfe)
//                 .dataEmissao(LocalDate.now())
//                 .valorTotalIcms(BigDecimal.valueOf(10))
//                 .valorTotalProdutos(BigDecimal.valueOf(100))
//                 .valorTotalDescontos(BigDecimal.valueOf(5))
//                 .valorTotalPis(BigDecimal.valueOf(2))
//                 .valorTotalCofins(BigDecimal.valueOf(3))
//                 .valorTotalOutros(BigDecimal.valueOf(0))
//                 .build();
//     }
// }
