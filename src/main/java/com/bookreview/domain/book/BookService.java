package com.bookreview.domain.book;

import com.bookreview.domain.book.dto.BookSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookSearchApiClient bookSearchApiClient;

    /**
     * 책 검색: DB 우선 조회 후 없으면 외부 API 호출 및 저장
     */
    @Transactional
    public List<BookSearchResult> searchBooks(String query) {
        List<Book> dbBooks = bookRepository.findByTitleContainingOrAuthorContaining(query, query);
        if (!dbBooks.isEmpty()) {
            return dbBooks.stream().map(this::toSearchResult).toList();
        }

        List<BookSearchResult> apiResults = bookSearchApiClient.search(query);
        saveNewBooks(apiResults);
        return apiResults;
    }

    /**
     * isbn 기준으로 Book 엔티티를 찾거나, 없으면 새로 저장하여 반환
     */
    @Transactional
    public Book findOrCreateBook(BookSearchResult result) {
        return bookRepository.findByIsbn(result.getIsbn())
                .orElseGet(() -> bookRepository.save(Book.builder()
                        .isbn(result.getIsbn())
                        .title(result.getTitle())
                        .author(result.getAuthor())
                        .publisher(result.getPublisher())
                        .description(result.getDescription())
                        .thumbnail(result.getThumbnail())
                        .build()));
    }

    private void saveNewBooks(List<BookSearchResult> results) {
        results.stream()
                .filter(r -> !bookRepository.existsByIsbn(r.getIsbn()))
                .map(r -> Book.builder()
                        .isbn(r.getIsbn())
                        .title(r.getTitle())
                        .author(r.getAuthor())
                        .publisher(r.getPublisher())
                        .description(r.getDescription())
                        .thumbnail(r.getThumbnail())
                        .build())
                .forEach(bookRepository::save);
    }

    @Transactional(readOnly = true)
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다. isbn=" + isbn));
    }

    private BookSearchResult toSearchResult(Book book) {
        return BookSearchResult.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .thumbnail(book.getThumbnail())
                .build();
    }
}
