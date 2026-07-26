package com.stea.controller;

import com.stea.dto.MessageRequest;
import com.stea.dto.MessageResponse;
import com.stea.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> envoyer(Authentication auth, @Valid @RequestBody MessageRequest request) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.envoyer(userId, request));
    }

    @GetMapping("/boite")
    public ResponseEntity<Page<MessageResponse>> inbox(Authentication auth,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messageService.inbox(getUserId(auth), page, size));
    }

    @GetMapping("/envoyes")
    public ResponseEntity<Page<MessageResponse>> sent(Authentication auth,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messageService.sent(getUserId(auth), page, size));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageResponse>> conversation(Authentication auth, @PathVariable Long otherUserId) {
        return ResponseEntity.ok(messageService.conversation(getUserId(auth), otherUserId));
    }

    @PatchMapping("/{id}/lire")
    public ResponseEntity<Void> marquerLu(Authentication auth, @PathVariable Long id) {
        messageService.marquerCommeLu(id, getUserId(auth));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/non-lus")
    public ResponseEntity<Long> countNonLus(Authentication auth) {
        return ResponseEntity.ok(messageService.countNonLus(getUserId(auth)));
    }

    private Long getUserId(Authentication auth) {
        return messageService.getUserIdByEmail(auth.getName());
    }
}
