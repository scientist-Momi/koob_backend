package com.koob.Koob_backend.library;

import com.koob.Koob_backend.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libraries")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_visible")
    private boolean isPrivate;
}
