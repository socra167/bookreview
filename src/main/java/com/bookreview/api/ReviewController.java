package com.bookreview.api;

import com.bookreview.domain.review.ReviewService;
import com.bookreview.domain.review.ReviewSortType;
import com.bookreview.domain.review.dto.ReviewCreateRequest;
import com.bookreview.domain.review.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     * X-Member-Id 헤더로 작성자를 식별한다. (인증 도입 전 임시)
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long bookId,
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody @Valid ReviewCreateRequest request) {
        ReviewResponse response = reviewService.createReview(bookId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 책 기준 리뷰 조회
     * sort: LATEST(최신순, 기본값) | POPULAR(인기순)
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "LATEST") ReviewSortType sort) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId, sort));
    }
}
