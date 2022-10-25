package de.thi.bachelorthesis.fido2.rpserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RP")
public class RpEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Column
    private String description;
}
