package com.api.tca.domain.meeting.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.dto.request.MinimalRegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.RegisterMeetingDto;
import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.transcript.entity.TranscriptEntity;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "meetings")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MeetingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "meeting_employees",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> users;

    private String totvsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcript_id")
    private TranscriptEntity transcript;

    private String clientRepresent;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime scheduled;
    private Integer durationMin;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer performanceAvg;

    @Enumerated(EnumType.STRING)
    private MeetingPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    private Boolean isDeleted;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer rating;

    @OneToOne(mappedBy = "meeting")
    private MeetingAnalysePerformanceEntity meetingAnalysePerformance;

    @OneToOne(mappedBy = "meeting")
    private MeetingAnalyseStrategicEntity meetingAnalyseStrategic;

    @OneToOne(mappedBy = "meeting")
    private MeetingPredictEntity meetingPredict;

    public MeetingEntity(RegisterMeetingDto dto) {
        this.title = dto.title();
        this.totvsId = dto.totvsId();
        this.summary = dto.summary();
        this.scheduled = BrazilRealTime.cast(dto.scheduled());
        this.clientRepresent = dto.clientRepresent();
        this.isDeleted = false;
        this.priority = dto.priority();
        this.performanceAvg = 0;
        this.durationMin = 0;
        this.status = MeetingStatus.SCHEDULED;
        this.rating = 0;
    }

    public MeetingEntity(MinimalRegisterMeetingDto dto) {
        this.title = "Reunião TOTVS";
        this.totvsId = dto.totvsId();
        this.scheduled = BrazilRealTime.cast(dto.scheduledAt());
        this.clientRepresent = dto.clientRepresent();
        this.isDeleted = false;
        this.performanceAvg = 0;
        this.durationMin = 0;
        this.status = MeetingStatus.COMPLETED;
        this.rating = 0;
    }

    public void removeEmployee(UserEntity user) {
        this.users.remove(user);
        user.getMeetings().remove(this);
    }

    public void addMultiplesEmployee(Set<UserEntity> users) {
        this.users.addAll(users);
        users.forEach(u -> u.getMeetings().add(this));
    }
}
