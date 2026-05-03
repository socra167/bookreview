package com.bookreview.domain.book;

import com.bookreview.domain.book.dto.BookSearchResult;

import java.util.List;

/**
 * 외부 도서 검색 API 클라이언트 인터페이스.
 * 현재는 네이버 책 검색 API 연동을 목적으로 하며,
 * 실제 구현체는 infrastructure 레이어에서 제공한다.
 */
public interface BookSearchApiClient {

    List<BookSearchResult> search(String query);
}
