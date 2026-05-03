package com.bookreview.domain.book.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookSearchResult {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String thumbnail;
}
