package com.bookreview.api;

import com.bookreview.domain.book.BookService;
import com.bookreview.domain.book.dto.BookSearchResult;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookSearchResult>> searchBooks(
            @RequestParam @NotBlank String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }
}
