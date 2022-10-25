package de.thi.bachelorthesis.fido2.rpserver.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@Entity
@Table(name = "AUTHENTICATOR_TRANSPORT")
public class AuthenticatorTransportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // internal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_key_id", nullable = false)
    private UserKeyEntity userKeyEntity;

    @Column
    @NotNull
    private String transport;

    public AuthenticatorTransportEntity(@NotNull String transport) {
        this.transport = transport;
    }
}
