package com.bookreview.domain.like;

import com.bookreview.domain.like.dto.LikeResult;
import com.bookreview.domain.member.Member;
import com.bookreview.domain.member.MemberRepository;
import com.bookreview.domain.review.Review;
import com.bookreview.domain.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    /**
     * 좋아요 토글: 이미 눌렀으면 취소, 아니면 추가.
     * Review.likeCount는 denormalized 값으로 함께 업데이트된다.
     */
    public LikeResult toggleLike(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + memberId));

        Optional<ReviewLike> existing = reviewLikeRepository.findByReviewAndMember(review, member);

        if (existing.isPresent()) {
            reviewLikeRepository.delete(existing.get());
            review.decrementLikeCount();
            return new LikeResult(false, review.getLikeCount());
        }

        reviewLikeRepository.save(ReviewLike.builder()
                .review(review)
                .member(member)
                .build());
        review.incrementLikeCount();
        return new LikeResult(true, review.getLikeCount());
    }
}
