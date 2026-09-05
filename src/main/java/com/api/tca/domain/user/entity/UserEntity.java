package com.api.tca.domain.user.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.user.dto.user.RegisterRequestDto;
import com.api.tca.domain.user.enums.UserStatus;
import com.api.tca.domain.user.service.UserService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    private String fullName;

    @Column(name = "username")
    private String username;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(11)")
    private String cpf;

    private String mobilePhone;

    private LocalDate birthDate;
    private String email;
    private String password;

    private String profilePhotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    private int score;

    @ManyToMany
    @JoinTable(
            name = "user_profiles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<ProfileEntity> profiles = new HashSet<>();

    private int aiTokenUsed;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    private Boolean useMfa;
    private String mfaToken;

    private UUID createdBy;


    public UserEntity(RegisterRequestDto dto, ProfileEntity profile, AddressEntity address) {
        this.fullName = dto.fullName();
        this.username = UserService.createUserName(dto.fullName());
        this.address = address;
        this.aiTokenUsed = 0;
        this.cpf = dto.cpf().replace("-", "").replace(".", "");
        this.profilePhotoUrl = dto.profilePhoto();
        this.email = dto.email();
        this.mobilePhone = dto.mobilePhone();
        this.score = 0;
        this.password = UserService.encryptPassword(dto.password());
        this.birthDate = dto.birthDate();
        this.profiles.add(profile);
        this.useMfa = false;
        this.status = UserStatus.FIRST_LOGIN;
        this.createdOn = BrazilRealTime.now();
        this.modifiedOn = BrazilRealTime.now();
    }

    public void addProfile(ProfileEntity profile) {
        this.profiles.add(profile);
    }

    public String getFirstProfileName() {
        return this.profiles.stream().findFirst().get().getName();
    }
}
