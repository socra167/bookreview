package com.bookreview.infrastructure.naver;

import com.bookreview.domain.book.dto.BookSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * NaverBookSearchApiClient 단위 테스트.
 * HTTP 호출(search) 자체는 통합 테스트 대상이므로,
 * 응답 매핑 로직(mapResponse)을 package-private 메서드로 분리하여 직접 검증한다.
 */
@DisplayName("NaverBookSearchApiClient")
class NaverBookSearchApiClientTest {

    private NaverBookSearchApiClient client;

    @BeforeEach
    void setUp() {
        NaverBookProperties properties = mock(NaverBookProperties.class);
        client = new NaverBookSearchApiClient(RestClient.create(), properties);
    }

    // ── mapResponse: null / empty 처리 ──────────────────────────────────────

    @Test
    @DisplayName("API 응답이 null이면 빈 목록을 반환할 수 있다")
    void mapResponse_whenResponseIsNull_returnsEmptyList() {
        List<BookSearchResult> results = client.mapResponse(null);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("API 응답의 items가 null이면 빈 목록을 반환할 수 있다")
    void mapResponse_whenItemsIsNull_returnsEmptyList() {
        NaverBookSearchResponse response = new NaverBookSearchResponse();
        ReflectionTestUtils.setField(response, "items", null);

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("API 응답의 items가 비어 있으면 빈 목록을 반환할 수 있다")
    void mapResponse_whenItemsIsEmpty_returnsEmptyList() {
        NaverBookSearchResponse response = new NaverBookSearchResponse();
        ReflectionTestUtils.setField(response, "items", List.of());

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results).isEmpty();
    }

    // ── HTML 태그 제거 ───────────────────────────────────────────────────────

    @Test
    @DisplayName("제목에 포함된 HTML 태그를 제거할 수 있다")
    void mapResponse_stripsHtmlTagsFromTitle() {
        NaverBookSearchResponse response = buildResponse("불곰의 <b>주식</b>투자", "설명", "9788960515529");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getTitle()).isEqualTo("불곰의 주식투자");
    }

    @Test
    @DisplayName("설명에 포함된 HTML 태그를 제거할 수 있다")
    void mapResponse_stripsHtmlTagsFromDescription() {
        NaverBookSearchResponse response = buildResponse("제목", "잘못된 <b>주식</b>투자 습관을 버리고", "9788960515529");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getDescription()).isEqualTo("잘못된 주식투자 습관을 버리고");
    }

    @Test
    @DisplayName("제목과 설명에 포함된 여러 종류의 HTML 태그를 모두 제거할 수 있다")
    void mapResponse_stripsMultipleHtmlTags() {
        NaverBookSearchResponse response = buildResponse("<b>클린</b> <i>코드</i>", "<p>좋은 <strong>책</strong></p>", "9781234567890");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getTitle()).isEqualTo("클린 코드");
        assertThat(results.get(0).getDescription()).isEqualTo("좋은 책");
    }

    // ── ISBN 추출 ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ISBN10과 ISBN13이 함께 있으면 ISBN13을 우선 추출할 수 있다")
    void mapResponse_extractsIsbn13_whenBothIsbn10AndIsbn13Present() {
        NaverBookSearchResponse response = buildResponse("제목", "설명", "8960515523 9788960515529");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getIsbn()).isEqualTo("9788960515529");
    }

    @Test
    @DisplayName("ISBN10만 있으면 ISBN10을 사용할 수 있다")
    void mapResponse_fallsBackToIsbn10_whenOnlyIsbn10Present() {
        NaverBookSearchResponse response = buildResponse("제목", "설명", "8960515523");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getIsbn()).isEqualTo("8960515523");
    }

    @Test
    @DisplayName("ISBN이 비어 있으면 빈 문자열을 반환할 수 있다")
    void mapResponse_returnsEmpty_whenIsbnIsBlank() {
        NaverBookSearchResponse response = buildResponse("제목", "설명", "");

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results.get(0).getIsbn()).isEmpty();
    }

    // ── 필드 매핑 전체 검증 ──────────────────────────────────────────────────

    @Test
    @DisplayName("API 응답의 모든 필드를 BookSearchResult로 올바르게 변환할 수 있다")
    void mapResponse_mapsAllFieldsCorrectly() {
        NaverBookSearchResponse.Item item = new NaverBookSearchResponse.Item();
        ReflectionTestUtils.setField(item, "title", "불곰의 <b>주식</b>투자");
        ReflectionTestUtils.setField(item, "author", "불곰 박선목");
        ReflectionTestUtils.setField(item, "publisher", "부키");
        ReflectionTestUtils.setField(item, "isbn", "8960515523 9788960515529");
        ReflectionTestUtils.setField(item, "description", "잘못된 <b>주식</b>투자 습관");
        ReflectionTestUtils.setField(item, "image", "http://example.com/thumb.jpg");

        NaverBookSearchResponse response = new NaverBookSearchResponse();
        ReflectionTestUtils.setField(response, "items", List.of(item));

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results).hasSize(1);
        BookSearchResult result = results.get(0);
        assertThat(result.getTitle()).isEqualTo("불곰의 주식투자");
        assertThat(result.getAuthor()).isEqualTo("불곰 박선목");
        assertThat(result.getPublisher()).isEqualTo("부키");
        assertThat(result.getIsbn()).isEqualTo("9788960515529");
        assertThat(result.getDescription()).isEqualTo("잘못된 주식투자 습관");
        assertThat(result.getThumbnail()).isEqualTo("http://example.com/thumb.jpg");
    }

    @Test
    @DisplayName("여러 권의 책을 한 번에 변환할 수 있다")
    void mapResponse_returnsMultipleResults() {
        NaverBookSearchResponse.Item item1 = buildItem("책1", "9781111111111");
        NaverBookSearchResponse.Item item2 = buildItem("책2", "9782222222222");
        NaverBookSearchResponse response = new NaverBookSearchResponse();
        ReflectionTestUtils.setField(response, "items", List.of(item1, item2));

        List<BookSearchResult> results = client.mapResponse(response);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(BookSearchResult::getTitle).containsExactly("책1", "책2");
    }

    // ── 헬퍼 ────────────────────────────────────────────────────────────────

    private NaverBookSearchResponse buildResponse(String title, String description, String isbn) {
        NaverBookSearchResponse.Item item = buildItem(title, isbn);
        ReflectionTestUtils.setField(item, "description", description);

        NaverBookSearchResponse response = new NaverBookSearchResponse();
        ReflectionTestUtils.setField(response, "items", List.of(item));
        return response;
    }

    private NaverBookSearchResponse.Item buildItem(String title, String isbn) {
        NaverBookSearchResponse.Item item = new NaverBookSearchResponse.Item();
        ReflectionTestUtils.setField(item, "title", title);
        ReflectionTestUtils.setField(item, "isbn", isbn);
        ReflectionTestUtils.setField(item, "author", "저자");
        ReflectionTestUtils.setField(item, "publisher", "출판사");
        ReflectionTestUtils.setField(item, "description", "설명");
        ReflectionTestUtils.setField(item, "image", "");
        return item;
    }
}
