package com.api.tca.domain.chat.entity;

import com.api.tca.domain.chat.dto.request.NewChatDto;
import com.api.tca.domain.chat.enums.AiModel;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "sessions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AiSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String title;
    private LocalDateTime lastActivity;
    private LocalDateTime createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Enumerated(EnumType.STRING)
    private AiModel modelUsed;
    private String lastInteractionId;

    private boolean isDeleted;
    private LocalDateTime deletedOn;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private Set<AiMessagesEntity> messages;


    public AiSessionEntity(NewChatDto request, UserEntity user) {
        this.title = "NÃO DEFINIDO";
        this.user = user;
        this.createdOn = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isDeleted = false;
        this.modelUsed = request.model();
    }
}
