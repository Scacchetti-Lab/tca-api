package com.api.tca.domain.chat.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.domain.chat.dto.request.RenameSessionDto;
import com.api.tca.domain.chat.dto.response.ChatBotResponseDto;
import com.api.tca.domain.chat.dto.response.ChatSessionDto;
import com.api.tca.domain.chat.dto.response.ChatSessionWithOutputDto;
import com.api.tca.domain.chat.dto.response.MessageResponseDto;
import com.api.tca.domain.chat.service.AISessionService;
import jakarta.validation.Valid;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class SessionController {
    @Autowired
    private AISessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatSessionWithOutputDto>> startNewChat() {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{id}/message")
    public ResponseEntity<ApiResponse<ChatBotResponseDto>> sendMessage() {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Page<ChatSessionDto>>> listSessions(@PathVariable UUID id, Pageable pageable) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<Page<MessageResponseDto>> listMessages(@PathVariable UUID sessionId, Pageable pageable) {
        throw new RuntimeException("Not implemented yet");
    }

    @PatchMapping("/{sessionId}/title")
    public ResponseEntity<ChatSessionDto> renameSession(@PathVariable UUID sessionId, @RequestBody @Valid RenameSessionDto request) {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) {
        throw new RuntimeException("Not implemented yet");
    }
}
