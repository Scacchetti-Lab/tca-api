package com.api.tca.domain.chat.service;

import com.api.tca.domain.chat.dto.request.ChatBotRequestDto;
import com.api.tca.domain.chat.dto.response.ChatBotResponseDto;
import com.api.tca.domain.chat.dto.response.ChatSessionDto;
import com.api.tca.domain.chat.dto.response.ChatSessionWithOutputDto;
import com.api.tca.domain.chat.dto.response.MessageResponseDto;
import com.api.tca.domain.chat.entity.AiSessionEntity;
import com.api.tca.domain.chat.exception.SessionNotFoundException;
import com.api.tca.domain.chat.repository.SessionRepository;
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

    public AiSessionEntity getSession(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException("Sessão não encontrada"));
    }

    public AiSessionEntity getSession(String title, UUID userId) {
        return repository.findByTitleAndUserIdAndIsDeletedFalse(title, userId)
                .orElseThrow(() -> new SessionNotFoundException("Sessão não encontrada"));
    }

    @Transactional
    public ChatSessionWithOutputDto startNewChat(ChatBotRequestDto request) {
        throw new RuntimeException("Not implemented yet");
    }

    @Transactional
    public ChatBotResponseDto sendMessage(ChatBotRequestDto request) {
        throw new RuntimeException("Not implemented yet");
    }

    public Page<ChatSessionDto> getAllSessionByUser(UUID userId, Pageable pageable) {
        throw new RuntimeException("Not implemented yet");
    }

    public Page<MessageResponseDto> getAllMessagesBySession(UUID sessionId, Pageable pageable) {
        throw new RuntimeException("Not implemented yet");
    }

    @Transactional
    public ChatSessionDto renameSessionTitle(UUID session, String newTitle) {
        throw new RuntimeException("Not implemented yet");
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        throw new RuntimeException("Not implemented yet");
    }
}
