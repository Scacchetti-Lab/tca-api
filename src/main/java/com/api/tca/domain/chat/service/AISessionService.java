package com.api.tca.domain.chat.service;

import com.api.tca.common.ai.dto.request.chat.ChatBotProviderRequest;
import com.api.tca.common.ai.dto.response.chat.ChatBotProviderResponse;
import com.api.tca.common.ai.provider.ChatBotProvider;
import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.chat.dto.request.ChatBotRequestDto;
import com.api.tca.domain.chat.dto.request.NewChatDto;
import com.api.tca.domain.chat.dto.response.ChatBotResponseDto;
import com.api.tca.domain.chat.dto.response.ChatSessionDto;
import com.api.tca.domain.chat.dto.response.ChatSessionWithOutputDto;
import com.api.tca.domain.chat.dto.response.MessageResponseDto;
import com.api.tca.domain.chat.entity.AiMessagesEntity;
import com.api.tca.domain.chat.entity.AiSessionEntity;
import com.api.tca.domain.chat.exception.InvalidTItleException;
import com.api.tca.domain.chat.exception.MessageSendFailedException;
import com.api.tca.domain.chat.exception.SessionCreateFailedException;
import com.api.tca.domain.chat.exception.SessionNotFoundException;
import com.api.tca.domain.chat.repository.SessionRepository;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AISessionService {
    @Autowired
    private SessionRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private AiSessionMessageService messageService;

    @Autowired
    private ChatBotProvider chatBotProvider;

    public AiSessionEntity getSession(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Sessão não encontrada"));
    }

    public AiSessionEntity getSession(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new SessionNotFoundException("Sessão não encontrada"));
    }

    @Transactional
    public ChatSessionWithOutputDto startNewChat(NewChatDto request, String userEmail) {
        UserEntity user = userService.getUserByEmail(userEmail);

        var response = chatBotProvider.startChat(request.input());
        System.out.println(response);
        if (!response.isValid())
            throw new SessionCreateFailedException("Ocorreu uma falha ao tentar criar um novo chat");

        var data = response.content();

        AiSessionEntity newSession = new AiSessionEntity(request, user);
        newSession.setTitle(data.title());
        newSession.setLastInteractionId(data.previousInteractId());
        repository.save(newSession);

        user.setAiTokenUsed(user.getAiTokenUsed() + data.tokensUsed());
        messageService.createConversation(request.input(), data.reply(), newSession, user.getFirstProfileName(), data.previousInteractId());

        return new ChatSessionWithOutputDto(newSession, user, request.input(), data.reply());
    }

    @Transactional
    public ChatBotResponseDto sendMessage(ChatBotRequestDto request, String userEmail, Boolean reprocess) {
        UserEntity user = userService.getUserByEmail(userEmail);
        AiSessionEntity currentSession = getSession(request.sessionId(), user.getId());

        if (reprocess) {
            String restoredInteractionId = messageService.deleteLastConversation(currentSession);
            currentSession.setLastInteractionId(restoredInteractionId);
        }

        var response = chatBotProvider.sendMessage(new ChatBotProviderRequest(request.input(), currentSession.getLastInteractionId()));
        if (!response.isValid())
            throw new MessageSendFailedException("Ocorreu uma falha ao tentar enviar a mensagem");

        var data = response.content();
        currentSession.setLastInteractionId(data.previousInteractId());
        user.setAiTokenUsed(user.getAiTokenUsed() + data.tokensUsed());
        messageService.createConversation(request.input(), data.reply(), currentSession, user.getFirstProfileName(), data.previousInteractId());

        return new ChatBotResponseDto(request.input(), data.reply(), data.previousInteractId());
    }

    public Page<ChatSessionDto> getAllSessionByUser(UUID userId, Pageable pageable) {
        return repository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
                .map(ChatSessionDto::new);
    }

    public Page<MessageResponseDto> getAllMessagesBySession(UUID sessionId, String email, Pageable pageable) {
        UUID userId = userService.getUserByEmail(email).getId();
        AiSessionEntity session = getSession(sessionId, userId);
        return messageService.listAllMessages(session, pageable);
    }

    @Transactional
    public ChatSessionDto renameSessionTitle(UUID sessionId, String email, String newTitle) {
        UUID userId = userService.getUserByEmail(email).getId();
        var session = getSession(sessionId, userId);
        if (newTitle.equals(session.getTitle()) || newTitle.isBlank())
            throw new InvalidTItleException("O título fornecido é inválido");
        session.setTitle(newTitle);

        return new ChatSessionDto(session);
    }

    @Transactional
    public void deleteSession(UUID sessionId, String email) {
        UUID userId = userService.getUserByEmail(email).getId();
        var session = getSession(sessionId, userId);
        session.setDeleted(true);
        session.setDeletedOn(BrazilRealTime.now());
    }
}
