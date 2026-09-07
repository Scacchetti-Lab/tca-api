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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer performance;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer closingProbability;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer flexibility;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer risk;

    private BigDecimal financialImpact;

    @Enumerated(EnumType.STRING)
    private FinancialImpact financialStatus;
}
