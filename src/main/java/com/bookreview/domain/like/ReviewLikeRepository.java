package com.bookreview.domain.like;

import com.bookreview.domain.member.Member;
import com.bookreview.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByReviewAndMember(Review review, Member member);

    boolean existsByReviewAndMember(Review review, Member member);
}
