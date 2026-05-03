package com.bookreview.infrastructure.naver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
class NaverBookSearchResponse {

    private List<Item> items;

    @Getter
    @NoArgsConstructor
    static class Item {
        private String title;
        private String image;
        private String author;
        private String publisher;
        private String isbn;
        private String description;
    }
}
