package com.koob.Koob_backend.ai;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

//    @PostMapping("/chat")
//    public String chat(@RequestBody String userMessage, @AuthenticationPrincipal User user) {
//        if (user == null) {
//            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
//        }
//        return agentService.chat(userMessage);
//    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody String userMessage,
                                  @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Not authenticated"));
        }

        String response = agentService.chat(user.getId(), userMessage);
        return ResponseEntity.ok(ApiResponse.success("successful", response));
    }

}
