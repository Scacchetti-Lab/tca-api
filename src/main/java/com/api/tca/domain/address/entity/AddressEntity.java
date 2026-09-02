package com.api.tca.domain.address.entity;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "postalcode", columnDefinition = "CHAR(8)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String postalCode;

    private String number;
    private String complement;
    private String name;
    private String state;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(2)")
    private String uf;

    private String neighborhood;

    public AddressEntity(AddressDto dto) {
        this.postalCode = dto.cep();
        this.number = dto.numero();
        this.complement = dto.complemento();
        this.name = dto.logradouro();
        this.uf = dto.uf();
        this.state = dto.estado();
        this.neighborhood = dto.bairro();
    }

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private Set<UserEntity> userList;
}
