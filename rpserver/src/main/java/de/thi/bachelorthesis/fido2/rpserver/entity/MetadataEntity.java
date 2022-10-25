package de.thi.bachelorthesis.fido2.rpserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "METADATA")
public class MetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // internal

    private String aaguid;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String timeOfLastStatusChange;

    @Column(columnDefinition = "TEXT")
    private String statusReports;
}
