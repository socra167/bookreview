package com.bookreview.domain.review;

import com.bookreview.domain.book.Book;
import com.bookreview.domain.book.BookRepository;
import com.bookreview.domain.member.Member;
import com.bookreview.domain.member.MemberRepository;
import com.bookreview.domain.review.dto.ReviewCreateRequest;
import com.bookreview.domain.review.dto.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("ReviewService")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    ReviewService reviewService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = Book.builder().isbn("9781234567890").title("테스트 책").author("저자").build();
        member = Member.builder().email("user@test.com").nickname("테스터").build();
    }

    @Test
    @DisplayName("로그인한 사용자는 책에 리뷰를 작성할 수 있다")
    void createReview_success() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(reviewRepository.save(any(Review.class))).willAnswer(inv -> inv.getArgument(0));

        ReviewCreateRequest request = new ReviewCreateRequest("정말 좋은 책입니다.", 5);
        ReviewResponse response = reviewService.createReview(1L, 1L, request);

        assertThat(response.getContent()).isEqualTo("정말 좋은 책입니다.");
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getMemberNickname()).isEqualTo("테스터");
        assertThat(response.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 책에 리뷰를 작성하면 예외가 발생한다")
    void createReview_whenBookNotFound_throwsIllegalArgumentException() {
        given(bookRepository.findById(99L)).willReturn(Optional.empty());

        ReviewCreateRequest request = new ReviewCreateRequest("내용", 3);

        assertThatThrownBy(() -> reviewService.createReview(99L, 1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 리뷰를 작성하면 예외가 발생한다")
    void createReview_whenMemberNotFound_throwsIllegalArgumentException() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(memberRepository.findById(99L)).willReturn(Optional.empty());

        ReviewCreateRequest request = new ReviewCreateRequest("내용", 3);

        assertThatThrownBy(() -> reviewService.createReview(1L, 99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("책의 리뷰를 최신순으로 조회할 수 있다")
    void getReviewsByBook_withLatestSort_returnsLatestOrderedReviews() {
        Review review1 = Review.builder().book(book).member(member).content("첫 번째 리뷰").rating(4).build();
        Review review2 = Review.builder().book(book).member(member).content("두 번째 리뷰").rating(5).build();
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(reviewRepository.findByBookOrderByCreatedAtDesc(book)).willReturn(List.of(review2, review1));

        List<ReviewResponse> responses = reviewService.getReviewsByBook(1L, ReviewSortType.LATEST);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("두 번째 리뷰");
    }

    @Test
    @DisplayName("책의 리뷰를 인기순(좋아요 수)으로 조회할 수 있다")
    void getReviewsByBook_withPopularSort_returnsLikeCountOrderedReviews() {
        Review popular = Review.builder().book(book).member(member).content("인기 리뷰").rating(5).build();
        popular.incrementLikeCount();
        popular.incrementLikeCount();
        Review recent = Review.builder().book(book).member(member).content("최신 리뷰").rating(3).build();
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(reviewRepository.findByBookOrderByLikeCountDescCreatedAtDesc(book))
                .willReturn(List.of(popular, recent));

        List<ReviewResponse> responses = reviewService.getReviewsByBook(1L, ReviewSortType.POPULAR);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 책의 리뷰를 조회하면 예외가 발생한다")
    void getReviewsByBook_whenBookNotFound_throwsIllegalArgumentException() {
        given(bookRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewsByBook(99L, ReviewSortType.LATEST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("리뷰가 없는 책을 조회하면 빈 목록을 반환할 수 있다")
    void getReviewsByBook_whenNoReviews_returnsEmptyList() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(reviewRepository.findByBookOrderByCreatedAtDesc(book)).willReturn(List.of());

        List<ReviewResponse> responses = reviewService.getReviewsByBook(1L, ReviewSortType.LATEST);

        assertThat(responses).isEmpty();
    }
}
