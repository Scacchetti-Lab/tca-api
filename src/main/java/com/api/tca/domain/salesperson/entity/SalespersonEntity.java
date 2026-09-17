package com.api.tca.domain.salesperson.entity;

import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(name = "salesperson")
@Entity
@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "squad_id")
    private SquadEntity squad;
}
