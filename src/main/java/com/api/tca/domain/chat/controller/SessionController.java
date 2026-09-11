package com.api.tca.domain.chat.controller;

import com.api.tca.common.ai.dto.request.chat.ChatBotProviderRequest;
import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.chat.dto.request.ChatBotRequestDto;
import com.api.tca.domain.chat.dto.request.NewChatDto;
import com.api.tca.domain.chat.dto.request.RenameSessionDto;
import com.api.tca.domain.chat.dto.response.ChatBotResponseDto;
import com.api.tca.domain.chat.dto.response.ChatSessionDto;
import com.api.tca.domain.chat.dto.response.ChatSessionWithOutputDto;
import com.api.tca.domain.chat.dto.response.MessageResponseDto;
import com.api.tca.domain.chat.service.AISessionService;
import com.api.tca.domain.user.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class SessionController {

    @Autowired
    private AISessionService sessionService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatSessionWithOutputDto>> startNewChat(@RequestBody @Valid NewChatDto request, HttpServletRequest httpRequest) {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var result = sessionService.startNewChat(request, tokenData.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult<>(HttpStatus.CREATED, "Sessão criada", result));
    }

    @PostMapping("/{id}/message")
    public ResponseEntity<ApiResponse<ChatBotResponseDto>> sendMessage(
            @RequestParam("reprocess") Boolean reprocess,
            @RequestBody @Valid ChatBotRequestDto request,
            HttpServletRequest httpRequest) {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var result = sessionService.sendMessage(request, tokenData.email(), reprocess);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResult<>("Sessão criada", result));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Page<ChatSessionDto>>> listSessions(@PathVariable UUID id, Pageable pageable) {
        var sessions = sessionService.getAllSessionByUser(id, pageable);
        return ResponseEntity.ok(new SuccessResult<>("Encontramos " + sessions.stream().count() + "sessões", sessions));
    }

    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponseDto>>> listMessages(@PathVariable UUID sessionId, Pageable pageable, HttpServletRequest httpRequest) {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var messages = sessionService.getAllMessagesBySession(sessionId, tokenData.email(), pageable);
        return ResponseEntity.ok(new SuccessResult<>("Encontramos " + messages.stream().count() + " mensagens na sessão", messages));
    }

    @PatchMapping("/{sessionId}/title")
    public ResponseEntity<ApiResponse<ChatSessionDto>> renameSession(@PathVariable UUID sessionId, @RequestBody @Valid RenameSessionDto request, HttpServletRequest httpRequest) {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var updatedSession = sessionService.renameSessionTitle(sessionId, request.title(), tokenData.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new SuccessResult<>("Título atualizado", updatedSession));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId,HttpServletRequest httpRequest) {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        sessionService.deleteSession(sessionId, tokenData.email());
        return ResponseEntity.noContent().build();
    }
}
