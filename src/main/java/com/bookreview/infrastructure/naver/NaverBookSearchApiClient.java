package com.bookreview.infrastructure.naver;

import com.bookreview.domain.book.BookSearchApiClient;
import com.bookreview.domain.book.dto.BookSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class NaverBookSearchApiClient implements BookSearchApiClient {

    private static final String NAVER_BOOK_SEARCH_URL =
            "https://openapi.naver.com/v1/search/book.json?query={query}&display=10";
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");

    private final RestClient restClient;
    private final NaverBookProperties properties;

    public NaverBookSearchApiClient(NaverBookProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    @Override
    public List<BookSearchResult> search(String query) {
        NaverBookSearchResponse response = restClient.get()
                .uri(NAVER_BOOK_SEARCH_URL, query)
                .header("X-Naver-Client-Id", properties.getClientId())
                .header("X-Naver-Client-Secret", properties.getClientSecret())
                .retrieve()
                .body(NaverBookSearchResponse.class);

        if (response == null || response.getItems() == null) {
            return List.of();
        }

        return response.getItems().stream()
                .map(this::toBookSearchResult)
                .toList();
    }

    private BookSearchResult toBookSearchResult(NaverBookSearchResponse.Item item) {
        return BookSearchResult.builder()
                .isbn(extractIsbn13(item.getIsbn()))
                .title(stripHtml(item.getTitle()))
                .author(item.getAuthor())
                .publisher(item.getPublisher())
                .description(stripHtml(item.getDescription()))
                .thumbnail(item.getImage())
                .build();
    }

    /**
     * 네이버 API는 ISBN10과 ISBN13을 공백으로 구분하여 반환한다.
     * ISBN13(978/979로 시작, 13자리)을 우선 사용하고, 없으면 첫 번째 값을 사용한다.
     */
    private String extractIsbn13(String isbnField) {
        if (isbnField == null || isbnField.isBlank()) {
            return "";
        }
        String[] parts = isbnField.trim().split("\\s+");
        for (String part : parts) {
            if (part.length() == 13) {
                return part;
            }
        }
        return parts[0];
    }

    private String stripHtml(String text) {
        if (text == null) {
            return null;
        }
        return HTML_TAG.matcher(text).replaceAll("");
    }
}
