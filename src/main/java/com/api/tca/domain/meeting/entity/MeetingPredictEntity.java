package com.api.tca.domain.meeting.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meeting_predicts")
@Getter
@Setter
@NoArgsConstructor
public class MeetingPredictEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @Column(columnDefinition = "TEXT")
    private String predict;

    @Enumerated(EnumType.STRING)
    PredictProcessStatus status;

    Boolean reprocess;

    LocalDateTime created_at = BrazilRealTime.now();

    public MeetingPredictEntity(MeetingEntity meeting) {
        this.meeting = meeting;
        this.predict = "EM PROCESSAMENTO";
        this.status = PredictProcessStatus.CREATED;
        this.reprocess = false;
        this.created_at = BrazilRealTime.now();
    }
}
