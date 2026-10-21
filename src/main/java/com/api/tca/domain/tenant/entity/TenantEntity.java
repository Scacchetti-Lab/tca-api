package com.api.tca.domain.tenant.entity;

import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.tenant.enums.TenantStatus;
import com.api.tca.domain.user.entity.UserEntity;
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
@Table(name = "tenants")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String fantasyName;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 14)
    private String cnpj;

    private String email;
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    private Set<MeetingEntity> meetings;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    private Boolean isDeleted;
}