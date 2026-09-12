package com.api.tca.domain.chat.service;

import com.api.tca.common.helpers.UserRoleHelper;
import com.api.tca.domain.chat.dto.response.MessageResponseDto;
import com.api.tca.domain.chat.entity.AiMessagesEntity;
import com.api.tca.domain.chat.entity.AiSessionEntity;
import com.api.tca.domain.chat.enums.UserRoles;
import com.api.tca.domain.chat.exception.MessageNotFoundException;
import com.api.tca.domain.chat.exception.MessageValidateException;
import com.api.tca.domain.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class AiSessionMessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Page<MessageResponseDto> listAllMessages(AiSessionEntity session, Pageable pageable) {
        var messages = messageRepository.findAllBySessionIdOrderByCreatedOnAsc(session.getId(), pageable);
        if (messages.isEmpty()) throw new MessageValidateException("Sessão não tem histórico");

        return messages.map(MessageResponseDto::new);
    }

    @Transactional
    public void createConversation(String input, String output, AiSessionEntity session, String profileName, String interactionId) {
        var userMsg = new AiMessagesEntity(input, session, UserRoleHelper.normalize(profileName), null);
        var totvsAiMsg = new AiMessagesEntity(output, session, UserRoles.TOTVS_AI, interactionId);

        messageRepository.saveAll(List.of(userMsg, totvsAiMsg));
    }

    @Transactional
    public String deleteLastConversation(AiSessionEntity session) {
        var ultimasTres = messageRepository.findTop3BySessionIdOrderByCreatedOnDesc(session.getId());

        if (ultimasTres.size() < 2)
            throw new MessageNotFoundException("Última conversa não encontrada");

        var last = ultimasTres.get(0);
        var nextLast = ultimasTres.get(1);

        if (last.getRole() != UserRoles.TOTVS_AI || nextLast.getRole() == UserRoles.TOTVS_AI)
            throw new MessageValidateException("As duas últimas mensagens não formam um par pergunta-resposta");

        // interaction_id de antes do par apagado -- null se era o primeiro par da sessão
        String restoredInteractionId = ultimasTres.size() >= 3 && ultimasTres.get(2).getRole() == UserRoles.TOTVS_AI
                ? ultimasTres.get(2).getInteractionId()
                : null;

        messageRepository.deleteAllById(Set.of(last.getId(), nextLast.getId()));

        return restoredInteractionId;
    }
}
