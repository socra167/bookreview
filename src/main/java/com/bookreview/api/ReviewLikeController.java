package com.bookreview.api;

import com.bookreview.domain.like.ReviewLikeService;
import com.bookreview.domain.like.dto.LikeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}/likes")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    /**
     * 좋아요 토글 (누르면 추가, 이미 눌렀으면 취소)
     * X-Member-Id 헤더로 사용자를 식별한다. (인증 도입 전 임시)
     */
    @PostMapping
    public ResponseEntity<LikeResult> toggleLike(
            @PathVariable Long reviewId,
            @RequestHeader("X-Member-Id") Long memberId) {
        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, memberId));
    }
}
