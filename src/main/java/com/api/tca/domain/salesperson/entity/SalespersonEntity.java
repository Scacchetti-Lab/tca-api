package com.api.tca.domain.salesperson.entity;

import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "salespersons")
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class SalespersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Integer totalMeetings;

    private Boolean needTraining;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    private SquadEntity squad;

    public SalespersonEntity(UserEntity user, SquadEntity squad) {
        this.user = user;
        this.squad = squad;
        this.totalMeetings = 0;
        this.needTraining = false;
    }
}
