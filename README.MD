
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
│       ├── static
│           └── html
│               
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




implementações:  http://localhost:8080/upload-form.html
http://localhost:8080/swagger-ui/index.html#/
