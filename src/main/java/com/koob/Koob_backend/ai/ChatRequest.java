package com.koob.Koob_backend.ai;

import lombok.Data;

@Data
public class ChatRequest {
    private String userMessage;
    private long boxId;
}
