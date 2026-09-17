package com.api.tca.domain.squad.entity;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "squads")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SquadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(11)")
    private String code;

    private String name;
    private String description;

    // Manager Relationship

    @Column(name = "performance_avg")
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private double performance;

    @OneToMany(mappedBy = "squad", fetch = FetchType.LAZY)
    private Set<ClientEntity> clients;

    @OneToMany(mappedBy = "squad", fetch = FetchType.LAZY)
    private Set<SalespersonEntity> salespersons;
}
