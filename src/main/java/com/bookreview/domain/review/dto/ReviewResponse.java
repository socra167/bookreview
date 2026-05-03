package com.bookreview.domain.review.dto;

import com.bookreview.domain.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long bookId;
    private String memberNickname;
    private String content;
    private int rating;
    private int likeCount;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookId(review.getBook().getId())
                .memberNickname(review.getMember().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .likeCount(review.getLikeCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
