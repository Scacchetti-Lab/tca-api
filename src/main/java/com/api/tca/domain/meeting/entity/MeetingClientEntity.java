package com.api.tca.domain.meeting.entity;

import com.api.tca.domain.client.entity.ClientEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "meeting_client_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MeetingClientEntity {
    @Id
    private UUID meetingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;
}
