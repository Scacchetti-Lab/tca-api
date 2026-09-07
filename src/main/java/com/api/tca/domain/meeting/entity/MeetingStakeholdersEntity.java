package com.api.tca.domain.meeting.entity;

import com.api.tca.domain.meeting.enums.MeetingStakeholderSide;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meeting_stakeholders")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MeetingStakeholdersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    private String name;
    private String role;

    @Enumerated(EnumType.STRING)
    private MeetingStakeholderSide side;

    private LocalDateTime createdAt;
}
