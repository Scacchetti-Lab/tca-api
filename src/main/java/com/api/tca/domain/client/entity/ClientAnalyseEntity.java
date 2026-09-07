package com.api.tca.domain.client.entity;

import com.api.tca.domain.client.enums.FinancialImpact;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "client_analyses")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ClientAnalyseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private double performance;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private double closingProbability;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private double flexibility;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private double risk;

    private BigDecimal financialImpact;

    @Enumerated(EnumType.STRING)
    private FinancialImpact financialStatus;
}
