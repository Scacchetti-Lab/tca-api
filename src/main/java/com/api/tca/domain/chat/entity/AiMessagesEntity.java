package com.api.tca.domain.chat.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.chat.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class AiMessagesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private AiSessionEntity session;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdOn;

    private String interactionId;

    public AiMessagesEntity(String content, AiSessionEntity session, UserRoles role,  String interactionId) {
        this.content = content;
        this.session = session;
        this.role = role;
        this.createdOn = BrazilRealTime.now();
        this.interactionId = interactionId;
    }

}
