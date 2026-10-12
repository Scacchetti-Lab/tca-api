package com.api.tca.domain.meeting.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.dto.request.MinimalRegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.RegisterMeetingDto;
import com.api.tca.domain.meeting.enums.AudioTranscriptType;
import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingScope;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import com.api.tca.domain.score.entity.StrategicScoreEntity;
import com.api.tca.domain.tenant.entity.TenantEntity;
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

    @OneToOne(mappedBy = "meeting")
    private MeetingClientEntity mtClient;

    private Boolean isDeleted;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private MeetingScope scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;

    @OneToOne(mappedBy = "meeting")
    private MeetingAnalysePerformanceEntity meetingAnalysePerformance;

    @OneToOne(mappedBy = "meeting")
    private MeetingAnalyseStrategicEntity meetingAnalyseStrategic;

    @OneToOne(mappedBy = "meeting")
    private MeetingPredictEntity meetingPredict;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY)
    private Set<StrategicScoreEntity> strategicScore;

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY)
    private Set<PerformanceScoreEntity> performanceScores;

    public ClientEntity getClient() {
        return mtClient != null ? mtClient.getClient() : null;
    }
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
        this.scope = MeetingScope.EXTERNAL;
    }

    public MeetingEntity(MinimalRegisterMeetingDto dto) {
        this.title = dto.title();
        this.totvsId = dto.totvsId();
        this.scheduled = BrazilRealTime.cast(dto.scheduledAt());
        this.clientRepresent = dto.clientRepresent();
        this.isDeleted = false;
        this.performanceAvg = 0;
        this.durationMin = 0;
        this.status = MeetingStatus.COMPLETED;
        this.rating = 0;
        this.scope = MeetingScope.EXTERNAL;
    }

    public void removeEmployee(UserEntity user) {
        if (this.users != null) {
            this.users.remove(user);
        }
        if (user.getMeetings() != null) {
            user.getMeetings().remove(this);
        }
    }

    public void setScope(AudioTranscriptType type) {
        switch (type) {
            case AudioTranscriptType.INTERNAL, AudioTranscriptType.STREAM ->  this.scope = MeetingScope.INTERNAL;
            case AudioTranscriptType.EXTERNAL ->  this.scope = MeetingScope.EXTERNAL;
        }
    }
}
