package com.koob.Koob_backend.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String pictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
