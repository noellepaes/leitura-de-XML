package com.noelle.leitura_de_XML.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "xml_erro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XmlErro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "conteudo_xml", columnDefinition = "TEXT")
    private String conteudoXml;
    
    @Column(name = "mensagem_erro")
    private String mensagemErro;
    
    @Column(name = "data_erro")
    private LocalDateTime dataErro;
    
    @PrePersist
    public void prePersist() {
        dataErro = LocalDateTime.now();
    }
}
