package re.com.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.com.dto.ChatRequest;
import re.com.dto.ChatResponse;
import re.com.service.AIChatService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ChatController {

    private final AIChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("REST request to chat: {}", request);
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        ChatResponse response = chatService.chat(request.getMessage());
        return ResponseEntity.ok(response);
    }
}
