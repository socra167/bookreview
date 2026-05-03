package com.bookreview.domain.book;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;
    private String publisher;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    private LocalDateTime createdAt;

    @Builder
    public Book(String isbn, String title, String author, String publisher, String description, String thumbnail) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.thumbnail = thumbnail;
        this.createdAt = LocalDateTime.now();
    }
}
