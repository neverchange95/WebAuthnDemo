package de.thi.bachelorthesis.fido2.rpserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "METADATA_YUBICO")
public class MetadataYubicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;    // internal

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}
