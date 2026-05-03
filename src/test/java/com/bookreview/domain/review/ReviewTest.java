package com.bookreview.domain.review;

import com.bookreview.domain.book.Book;
import com.bookreview.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Review")
class ReviewTest {

    private Review review;

    @BeforeEach
    void setUp() {
        Book book = Book.builder().isbn("9781234567890").title("테스트 책").author("저자").build();
        Member member = Member.builder().email("user@test.com").nickname("테스터").build();
        review = Review.builder().book(book).member(member).content("리뷰 내용").rating(4).build();
    }

    @Test
    @DisplayName("리뷰 생성 시 좋아요 수는 0으로 초기화된다")
    void initialLikeCount_isZero() {
        assertThat(review.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("좋아요를 누르면 좋아요 수가 1 증가한다")
    void incrementLikeCount_increasesCountByOne() {
        review.incrementLikeCount();

        assertThat(review.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요를 여러 번 누르면 누른 횟수만큼 좋아요 수가 증가한다")
    void incrementLikeCount_multipleTimes_accumulatesCorrectly() {
        review.incrementLikeCount();
        review.incrementLikeCount();
        review.incrementLikeCount();

        assertThat(review.getLikeCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요를 취소하면 좋아요 수가 1 감소한다")
    void decrementLikeCount_decreasesCountByOne() {
        review.incrementLikeCount();
        review.incrementLikeCount();

        review.decrementLikeCount();

        assertThat(review.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 수가 0일 때 취소해도 0 미만으로 내려가지 않는다")
    void decrementLikeCount_whenCountIsZero_doesNotGoBelowZero() {
        review.decrementLikeCount();

        assertThat(review.getLikeCount()).isZero();
    }
}
