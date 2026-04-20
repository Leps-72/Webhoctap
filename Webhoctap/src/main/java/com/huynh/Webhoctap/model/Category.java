package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Category")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private Integer categoryId;

    @Column(name = "CategoryName", nullable = false, length = 150)
    private String categoryName;

    @Column(name = "Slug", nullable = false, unique = true, length = 200)
    private String slug;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}