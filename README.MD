
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── noelle/
│   │           └── leituradexml/
│   │               ├── LeituraDeXmlApplication.java
│   │               ├── config/
│   │               │   ├── AsyncConfig.java
│   │               │   ├── CacheConfig.java
│   │               │   └── JpaConfig.java
│   │               ├── domain/
│   │               │   ├── Cupom.java
│   │               │   └── Item.java
│   │               ├── repository/
│   │               │   ├── CupomRepository.java
│   │               │   └── ItemRepository.java
│   │               ├── service/
│   │               │   ├── ProcessadorService.java
│   │               │   ├── processor/
│   │               │   │   ├── ProcessadorFactory.java
│   │               │   │   ├── ProcessadorStrategy.java
│   │               │   │   └── impl/
│   │               │   │       └── CfeSatProcessadorStrategy.java
│   │               │   ├── extractor/
│   │               │   │   ├── DadosExtractor.java
│   │               │   │   └── impl/
│   │               │   │       └── CfeSatDadosExtractor.java
│   │               │   └── validator/
│   │               │       ├── XmlValidator.java
│   │               │       └── impl/
│   │               │           └── CfeSatXmlValidator.java
│   │               ├── controller/
│   │               │   └── CupomController.java
│   │               ├── dto/
│   │               │   ├── CupomDTO.java
│   │               │   └── ItemDTO.java
│   │               ├── mapper/
│   │               │   ├── CupomMapper.java
│   │               │   └── ItemMapper.java
│   │               ├── exception/
│   │               │   ├── ProcessamentoException.java
│   │               │   ├── DuplicidadeException.java
│   │               │   └── XmlInvalidoException.java
│   │               ├── advice/
│   │               │   └── GlobalExceptionHandler.java
│   │               └── util/
│   │                   ├── ZipExtractor.java
│   │                   └── XmlUtils.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       └── db/
│           └── migration/
│               ├── V1__criar_tabela_cupom.sql
│               └── V2__criar_tabela_item.sql
└── test/
    └── java/
        └── com/
            └── noelle/
                └── leituradexml/
                    ├── service/
                    │   ├── ProcessadorServiceTest.java
                    │   ├── processor/
                    │   │   └── CfeSatProcessadorStrategyTest.java
                    │   └── extractor/
                    │       └── CfeSatDadosExtractorTest.java
                    ├── controller/
                    │   └── CupomControllerTest.java
                    └── repository/
                        └── CupomRepositoryTest.java




implementações: http://localhost:8080/api/cupons/processar

