package com.koob.Koob_backend.library;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libraries")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
}
