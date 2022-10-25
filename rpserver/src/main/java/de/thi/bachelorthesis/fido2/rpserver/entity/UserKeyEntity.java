package de.thi.bachelorthesis.fido2.rpserver.entity;

import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USER_KEY")
public class UserKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // internal

    @ManyToOne
    private RpEntity rpEntity;

    @Column(nullable = false, length = 128)
    private String userId;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String userDisplayName;

    @Column(length = 128)
    private String userIcon;

    @Column(nullable = false, length = 36)
    private String aaguid;

    @Column(nullable = false, length = 256)
    private String credentialId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String publicKey;

    @Column(nullable = false)
    private int signatureAlgorithm;

    @Column
    Long signCounter;

    @Column
    private AttestationType attestationType;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "userKeyEntity")
    private List<AuthenticatorTransportEntity> transports = new ArrayList<>();

    @Column
    private Boolean rk;

    @Column
    private Integer credProtect;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date registeredTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date authenticatedTimestamp;
}
