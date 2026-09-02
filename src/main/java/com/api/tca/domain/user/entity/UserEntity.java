package com.api.tca.domain.user.entity;

import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.user.enums.ScoreType;
import com.api.tca.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "username")
    private String username;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(11)")
    private String cpf;

    @Column(name = "mobilephone")
    private String mobilePhone;

    @Column(name = "birthdate")
    private LocalDate birthDate;
    private String email;
    private String password;

    @Column(name = "profilephoto")
    private String profilePhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_type")
    private ScoreType scoreType;

    @Column(name = "ai_token_used")
    private int aiTokenUser;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    private UUID createdBy;
}
