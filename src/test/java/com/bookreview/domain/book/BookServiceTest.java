package com.bookreview.domain.book;

import com.bookreview.domain.book.dto.BookSearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("BookService")
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookSearchApiClient bookSearchApiClient;

    @InjectMocks
    BookService bookService;

    @Test
    @DisplayName("DB에 책이 있으면 외부 API를 호출하지 않고 DB 결과를 반환할 수 있다")
    void searchBooks_whenBooksExistInDb_returnsFromDbWithoutCallingApi() {
        Book book = Book.builder().isbn("9781234567890").title("테스트 책").author("저자").publisher("출판사").build();
        given(bookRepository.findByTitleContainingOrAuthorContaining("테스트", "테스트"))
                .willReturn(List.of(book));

        List<BookSearchResult> results = bookService.searchBooks("테스트");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsbn()).isEqualTo("9781234567890");
        assertThat(results.get(0).getTitle()).isEqualTo("테스트 책");
        then(bookSearchApiClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("DB에 책이 없으면 외부 API를 호출하고 결과를 저장할 수 있다")
    void searchBooks_whenNotInDb_callsApiAndSavesResults() {
        given(bookRepository.findByTitleContainingOrAuthorContaining(anyString(), anyString()))
                .willReturn(List.of());
        BookSearchResult apiResult = BookSearchResult.builder()
                .isbn("9781234567890").title("API 책").author("저자").publisher("출판사").build();
        given(bookSearchApiClient.search("테스트")).willReturn(List.of(apiResult));
        given(bookRepository.existsByIsbn("9781234567890")).willReturn(false);
        given(bookRepository.save(any(Book.class))).willAnswer(inv -> inv.getArgument(0));

        List<BookSearchResult> results = bookService.searchBooks("테스트");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsbn()).isEqualTo("9781234567890");
        then(bookRepository).should().save(any(Book.class));
    }

    @Test
    @DisplayName("API 결과 중 이미 DB에 존재하는 ISBN은 중복 저장하지 않는다")
    void searchBooks_whenApiResultAlreadyExistsInDb_doesNotSaveDuplicate() {
        given(bookRepository.findByTitleContainingOrAuthorContaining(anyString(), anyString()))
                .willReturn(List.of());
        BookSearchResult apiResult = BookSearchResult.builder()
                .isbn("9781234567890").title("기존 책").author("저자").build();
        given(bookSearchApiClient.search("테스트")).willReturn(List.of(apiResult));
        given(bookRepository.existsByIsbn("9781234567890")).willReturn(true);

        bookService.searchBooks("테스트");

        then(bookRepository).should(never()).save(any(Book.class));
    }

    @Test
    @DisplayName("API 결과가 없으면 빈 목록을 반환할 수 있다")
    void searchBooks_whenApiReturnsEmpty_returnsEmptyList() {
        given(bookRepository.findByTitleContainingOrAuthorContaining(anyString(), anyString()))
                .willReturn(List.of());
        given(bookSearchApiClient.search("없는책")).willReturn(List.of());

        List<BookSearchResult> results = bookService.searchBooks("없는책");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("ISBN이 DB에 이미 존재하면 저장 없이 기존 책을 반환할 수 있다")
    void findOrCreateBook_whenIsbnExistsInDb_returnsExistingBook() {
        BookSearchResult result = BookSearchResult.builder().isbn("9781234567890").title("기존 책").build();
        Book existing = Book.builder().isbn("9781234567890").title("기존 책").build();
        given(bookRepository.findByIsbn("9781234567890")).willReturn(Optional.of(existing));

        Book found = bookService.findOrCreateBook(result);

        assertThat(found.getIsbn()).isEqualTo("9781234567890");
        assertThat(found.getTitle()).isEqualTo("기존 책");
        then(bookRepository).should(never()).save(any(Book.class));
    }

    @Test
    @DisplayName("ISBN이 DB에 없으면 새 책을 저장하고 반환할 수 있다")
    void findOrCreateBook_whenIsbnNotInDb_savesAndReturnsNewBook() {
        BookSearchResult result = BookSearchResult.builder()
                .isbn("9781234567890").title("새 책").author("저자").publisher("출판사")
                .description("설명").thumbnail("thumb.jpg").build();
        given(bookRepository.findByIsbn("9781234567890")).willReturn(Optional.empty());
        given(bookRepository.save(any(Book.class))).willAnswer(inv -> inv.getArgument(0));

        Book created = bookService.findOrCreateBook(result);

        assertThat(created.getIsbn()).isEqualTo("9781234567890");
        assertThat(created.getTitle()).isEqualTo("새 책");
        then(bookRepository).should().save(any(Book.class));
    }
}
