package com.api.tca.domain.client.entity;

import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.squad.entity.SquadEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String fantasyName;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(14)")
    private String cnpj;

    private String email;
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    private SquadEntity squad;

    private BigDecimal revenue;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    private boolean isDeleted;

    @OneToOne(mappedBy = "client")
    private ClientAnalyseEntity analyse;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<MeetingEntity> meetings;
}
