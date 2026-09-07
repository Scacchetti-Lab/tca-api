package com.api.tca.domain.transcript.entity;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "transcripts")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TranscriptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "raw_transcript", columnDefinition = "TEXT")
    private String rawTranscript;

    @Column(columnDefinition = "TEXT")
    private String resume;

    @Enumerated(EnumType.STRING)
    private TranscriptStatus status;

    @OneToOne(mappedBy = "transcript", fetch = FetchType.EAGER)
    private MeetingEntity meeting;
}
